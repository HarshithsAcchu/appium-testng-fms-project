package com.example.runners;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.config.ConfigurationReader;
import com.example.config.TestDataProvider;
import com.example.config.models.TestSuite;
import com.example.reporting.TestReport;

import io.appium.java_client.android.AndroidDriver;

/**
 * Data-driven test runner that executes a test suite multiple times
 * Once for each row of test data from external test-data.json file
 * Generates individual reports for each data iteration
 */
public class DataDrivenTestRunner {
    
    private final AndroidDriver driver;
    private final ConfigurationReader configReader;
    private final TestDataProvider dataProvider;
    private final List<TestReport> reports;
    private final String reportDirectory;

    public DataDrivenTestRunner(AndroidDriver driver) {
        this.driver = driver;
        this.configReader = ConfigurationReader.getInstance();
        this.dataProvider = TestDataProvider.getInstance();
        this.reports = new ArrayList<>();
        this.reportDirectory = System.getProperty("user.dir") + "/test-reports";
    }

    /**
     * Execute a test suite with all data rows from external file
     * For each data row: run suite -> generate report -> reset app
     * 
     * @param suiteName Name of the test suite to execute
     */
    public void executeSuiteWithAllData(String suiteName) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("[DataDrivenTestRunner] Starting Data-Driven Execution for Suite: " + suiteName);
        System.out.println("=".repeat(100));

        // Get test suite configuration
        TestSuite suite = configReader.getTestConfig().getTestSuite(suiteName);
        if (suite == null) {
            System.err.println("[DataDrivenTestRunner] Test suite not found: " + suiteName);
            return;
        }

        if (!suite.isEnabled()) {
            System.out.println("[DataDrivenTestRunner] Test suite '" + suiteName + "' is disabled. Skipping.");
            return;
        }

        // Get all test data rows for this suite
        List<Map<String, String>> dataRows = dataProvider.getTestDataForSuite(suiteName);
        
        if (dataRows == null || dataRows.isEmpty()) {
            System.err.println("[DataDrivenTestRunner] No test data found for suite: " + suiteName);
            System.err.println("[DataDrivenTestRunner] Please add data in test-data.json under key: \"" + suiteName + "\"");
            return;
        }

        System.out.println("[DataDrivenTestRunner] Found " + dataRows.size() + " data row(s) for suite: " + suiteName);
        System.out.println("=".repeat(100) + "\n");

        // Execute suite for each data row
        int iteration = 1;
        for (Map<String, String> dataRow : dataRows) {
            String dataId = dataRow.getOrDefault("dataId", "Data_" + iteration);
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("[DataDrivenTestRunner] Iteration " + iteration + "/" + dataRows.size());
            System.out.println("[DataDrivenTestRunner] Suite: " + suiteName);
            System.out.println("[DataDrivenTestRunner] Data ID: " + dataId);
            System.out.println("=".repeat(80));

            executeSuiteWithData(suite, dataRow, dataId, iteration);

            // Reset app after each iteration (except last one)
            if (iteration < dataRows.size()) {
                resetApp();
            }

            iteration++;
        }

        // Generate consolidated report
        generateConsolidatedReport(suiteName);
    }

    /**
     * Execute suite with specific data row
     */
    private void executeSuiteWithData(TestSuite suite, Map<String, String> dataRow, String dataId, int iteration) {
        String reportName = suite.getSuiteName() + "_" + dataId;
        TestReport report = new TestReport(reportName);
        reports.add(report);

        try {
            // Create parameterized test runner with this data
            ParameterizedTestRunner runner = new ParameterizedTestRunner(driver, suite.getSuiteName());
            
            // Override the suite's test data with current data row
            suite.setTestData(dataRow);
            
            // Execute the suite
            System.out.println("[DataDrivenTestRunner] Executing suite with data: " + dataId);
            runner.executeTestSuite();
            
            report.markCompleted("PASSED");
            report.addStepResult("Suite Execution", true, "Completed successfully with data: " + dataId);
            
            System.out.println("\n[DataDrivenTestRunner] ✓ Iteration " + iteration + " PASSED (Data: " + dataId + ")");

        } catch (Exception e) {
            report.markCompleted("FAILED");
            report.setFailure(
                suite.getSuiteName(),
                e.getMessage(),
                getStackTraceAsString(e)
            );
            report.addStepResult("Suite Execution", false, "Failed with data: " + dataId);
            
            System.err.println("\n[DataDrivenTestRunner] ✗ Iteration " + iteration + " FAILED (Data: " + dataId + ")");
            System.err.println("[DataDrivenTestRunner] Failure Reason: " + e.getMessage());
            e.printStackTrace();
        }

        // Print individual report
        System.out.println(report);
    }

    /**
     * Reset the app to clean state
     */
    private void resetApp() {
        System.out.println("\n[DataDrivenTestRunner] Resetting app for next iteration...");
        try {
            String appPackage = driver.getCapabilities().getCapability("appPackage").toString();
            driver.terminateApp(appPackage);
            Thread.sleep(2000);
            driver.activateApp(appPackage);
            Thread.sleep(3000);
            System.out.println("[DataDrivenTestRunner] App reset completed\n");
        } catch (Exception e) {
            System.err.println("[DataDrivenTestRunner] Failed to reset app: " + e.getMessage());
        }
    }

    /**
     * Generate consolidated report for all iterations
     */
    private void generateConsolidatedReport(String suiteName) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("[DataDrivenTestRunner] CONSOLIDATED REPORT - Suite: " + suiteName);
        System.out.println("=".repeat(100));

        int passed = 0;
        int failed = 0;
        int total = reports.size();

        System.out.println("\nITERATION SUMMARY:");
        System.out.println("-".repeat(100));
        
        for (int i = 0; i < reports.size(); i++) {
            TestReport report = reports.get(i);
            String status = report.getStatus();
            String icon = "PASSED".equals(status) ? "✓" : "✗";
            
            System.out.printf("  %s Iteration %d: %s - %s%n", 
                icon, (i + 1), report.getSuiteName(), status);
            
            if ("PASSED".equals(status)) {
                passed++;
            } else if ("FAILED".equals(status)) {
                failed++;
                System.out.println("      Reason: " + report.getFailureReason());
            }
        }

        System.out.println("-".repeat(100));
        System.out.println("\nOVERALL STATISTICS:");
        System.out.println("  Total Iterations: " + total);
        System.out.println("  Passed: " + passed + " (" + (total > 0 ? (passed * 100 / total) : 0) + "%)");
        System.out.println("  Failed: " + failed + " (" + (total > 0 ? (failed * 100 / total) : 0) + "%)");
        System.out.println("=".repeat(100));

        // Save to file
        saveReportToFile(suiteName);
    }

    /**
     * Save consolidated report to file
     */
    private void saveReportToFile(String suiteName) {
        try {
            java.io.File dir = new java.io.File(reportDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = reportDirectory + "/data-driven-report-" + suiteName + "-" + timestamp + ".txt";

            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("DATA-DRIVEN TEST EXECUTION REPORT\n");
                writer.write("Suite: " + suiteName + "\n");
                writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
                writer.write("=".repeat(100) + "\n\n");

                for (int i = 0; i < reports.size(); i++) {
                    writer.write("ITERATION " + (i + 1) + ":\n");
                    writer.write(reports.get(i).toString());
                    writer.write("\n");
                }

                // Summary
                long passed = reports.stream().filter(r -> "PASSED".equals(r.getStatus())).count();
                long failed = reports.stream().filter(r -> "FAILED".equals(r.getStatus())).count();
                
                writer.write("\n" + "=".repeat(100) + "\n");
                writer.write("SUMMARY:\n");
                writer.write("Total Iterations: " + reports.size() + "\n");
                writer.write("Passed: " + passed + "\n");
                writer.write("Failed: " + failed + "\n");
                writer.write("=".repeat(100) + "\n");
            }

            System.out.println("\n[DataDrivenTestRunner] Report saved to: " + filename);

        } catch (IOException e) {
            System.err.println("[DataDrivenTestRunner] Failed to save report: " + e.getMessage());
        }
    }

    /**
     * Convert stack trace to string
     */
    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    public List<TestReport> getReports() {
        return reports;
    }
}
