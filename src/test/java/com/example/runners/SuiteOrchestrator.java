package com.example.runners;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.config.ConfigurationReader;
import com.example.config.models.SuiteExecution;
import com.example.config.models.TestConfig;
import com.example.config.models.TestDataSet;
import com.example.config.models.TestFlow;
import com.example.reporting.TestReport;

import io.appium.java_client.android.AndroidDriver;

/**
 * Orchestrates execution of multiple test suites sequentially with reset and reporting
 */
public class SuiteOrchestrator {
    
    private final AndroidDriver driver;
    private final ConfigurationReader configReader;
    private final List<TestReport> reports;
    private final String reportDirectory;

    public SuiteOrchestrator(AndroidDriver driver) {
        this.driver = driver;
        this.configReader = ConfigurationReader.getInstance();
        this.reports = new ArrayList<>();
        this.reportDirectory = System.getProperty("user.dir") + "/test-reports";
    }

    /**
     * Execute all enabled test suites sequentially
     */
    public void executeAllSuites() {
        TestConfig config = configReader.getTestConfig();
        
        if (config.getSuiteExecutions() == null || config.getSuiteExecutions().isEmpty()) {
            System.err.println("[SuiteOrchestrator] No suite executions defined");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("[SuiteOrchestrator] Starting execution of " + config.getSuiteExecutions().size() + " test suites");
        System.out.println("=".repeat(100) + "\n");

        for (SuiteExecution suiteExec : config.getSuiteExecutions()) {
            if (!suiteExec.isEnabled()) {
                System.out.println("[SuiteOrchestrator] Suite '" + suiteExec.getSuiteName() + "' is disabled. Skipping.");
                continue;
            }

            executeSuite(suiteExec);

            // Reset app if configured
            if (suiteExec.isResetAfter()) {
                resetApp();
            }
        }

        // Generate consolidated report
        generateConsolidatedReport();
    }

    /**
     * Execute a single test suite
     */
    private void executeSuite(SuiteExecution suiteExec) {
        TestReport report = new TestReport(suiteExec.getSuiteName());
        reports.add(report);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("[SuiteOrchestrator] Executing Suite: " + suiteExec.getSuiteName());
        if (suiteExec.getDescription() != null) {
            System.out.println("[SuiteOrchestrator] Description: " + suiteExec.getDescription());
        }
        System.out.println("=".repeat(80));

        try {
            // Get test data
            TestDataSet dataSet = getDataSet(suiteExec.getDataSetRef());
            if (dataSet == null) {
                throw new IllegalStateException("DataSet not found: " + suiteExec.getDataSetRef());
            }

            System.out.println("[SuiteOrchestrator] Using DataSet: " + dataSet.getDataSetName());

            // Create flow executor with test data
            FlowExecutor flowExecutor = new FlowExecutor(driver, dataSet.getData());

            // Execute each flow in sequence
            for (String flowRef : suiteExec.getFlowRefs()) {
                TestFlow flow = getFlow(flowRef);
                if (flow == null) {
                    throw new IllegalStateException("Flow not found: " + flowRef);
                }

                System.out.println("\n[SuiteOrchestrator] Executing Flow: " + flow.getFlowName());
                
                try {
                    flowExecutor.executeFlow(flow);
                    report.addStepResult(flow.getFlowName(), true, "Flow completed successfully");
                } catch (Exception e) {
                    report.addStepResult(flow.getFlowName(), false, e.getMessage());
                    throw e; // Re-throw to mark suite as failed
                }
            }

            report.markCompleted("PASSED");
            System.out.println("\n[SuiteOrchestrator] Suite '" + suiteExec.getSuiteName() + "' PASSED");

        } catch (Exception e) {
            report.markCompleted("FAILED");
            report.setFailure(
                suiteExec.getSuiteName(),
                e.getMessage(),
                getStackTraceAsString(e)
            );
            
            System.err.println("\n[SuiteOrchestrator] Suite '" + suiteExec.getSuiteName() + "' FAILED");
            System.err.println("[SuiteOrchestrator] Failure Reason: " + e.getMessage());
            e.printStackTrace();
        }

        // Print report
        System.out.println(report);
    }

    /**
     * Reset the app to clean state
     */
    private void resetApp() {
        System.out.println("\n[SuiteOrchestrator] Resetting app to clean state...");
        try {
            driver.terminateApp(driver.getCapabilities().getCapability("appPackage").toString());
            Thread.sleep(2000);
            driver.activateApp(driver.getCapabilities().getCapability("appPackage").toString());
            Thread.sleep(3000);
            System.out.println("[SuiteOrchestrator] App reset completed");
        } catch (Exception e) {
            System.err.println("[SuiteOrchestrator] Failed to reset app: " + e.getMessage());
        }
    }

    /**
     * Generate consolidated HTML/JSON report
     */
    private void generateConsolidatedReport() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("[SuiteOrchestrator] CONSOLIDATED TEST EXECUTION REPORT");
        System.out.println("=".repeat(100));

        int passed = 0;
        int failed = 0;
        int total = reports.size();

        for (TestReport report : reports) {
            if ("PASSED".equals(report.getStatus())) {
                passed++;
            } else if ("FAILED".equals(report.getStatus())) {
                failed++;
            }
        }

        System.out.println("Total Suites: " + total);
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Success Rate: " + (total > 0 ? (passed * 100 / total) : 0) + "%");
        System.out.println("=".repeat(100));

        // Save to file
        saveReportToFile();
    }

    /**
     * Save report to JSON file
     */
    private void saveReportToFile() {
        try {
            java.io.File dir = new java.io.File(reportDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = reportDirectory + "/test-report-" + timestamp + ".txt";

            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("TEST EXECUTION REPORT\n");
                writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
                writer.write("=".repeat(100) + "\n\n");

                for (TestReport report : reports) {
                    writer.write(report.toString());
                    writer.write("\n");
                }
            }

            System.out.println("\n[SuiteOrchestrator] Report saved to: " + filename);

        } catch (IOException e) {
            System.err.println("[SuiteOrchestrator] Failed to save report: " + e.getMessage());
        }
    }

    /**
     * Get test data set by name
     */
    private TestDataSet getDataSet(String name) {
        TestConfig config = configReader.getTestConfig();
        if (config.getTestDataSets() != null) {
            for (TestDataSet dataSet : config.getTestDataSets()) {
                if (dataSet.getDataSetName().equals(name)) {
                    return dataSet;
                }
            }
        }
        return null;
    }

    /**
     * Get flow by name
     */
    private TestFlow getFlow(String name) {
        TestConfig config = configReader.getTestConfig();
        if (config.getFlows() != null) {
            for (TestFlow flow : config.getFlows()) {
                if (flow.getFlowName().equals(name)) {
                    return flow;
                }
            }
        }
        return null;
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
