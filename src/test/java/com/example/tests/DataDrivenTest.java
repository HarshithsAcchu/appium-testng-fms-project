package com.example.tests;

import org.testng.annotations.Test;

import com.example.runners.DataDrivenTestRunner;

/**
 * Data-driven test execution
 * Reads test data from external test-data.json file
 * Executes suite once for each data row
 * Generates individual reports for each iteration
 */
public class DataDrivenTest extends BaseTest {

    /**
     * Execute L2ProspectL1Flow with all data rows from test-data.json
     * Each data row will run the complete flow and generate a report
     * App resets between each iteration
     */
    @Test(groups = "dataDriven", priority = 1)
    public void runL2ProspectFlowWithAllData() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("[DataDrivenTest] Starting Data-Driven Test Execution");
        System.out.println("[DataDrivenTest] Suite: L2ProspectL1Flow");
        System.out.println("=".repeat(100) + "\n");

        DataDrivenTestRunner runner = new DataDrivenTestRunner(driver);
        
        try {
            // This will execute the suite for each data row in test-data.json
            runner.executeSuiteWithAllData("L2ProspectL1Flow");
            
            // Check if any iteration failed
            long failed = runner.getReports().stream()
                .filter(r -> "FAILED".equals(r.getStatus()))
                .count();
            
            if (failed > 0) {
                throw new AssertionError(failed + " iteration(s) failed. Check reports for details.");
            }
            
            System.out.println("\n[DataDrivenTest] All iterations completed successfully!");
            
        } catch (Exception e) {
            System.err.println("[DataDrivenTest] Data-driven execution failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Execute BankDetailsOnly with all data rows from test-data.json
     */
    @Test(groups = "dataDriven", priority = 2, enabled = false)
    public void runBankDetailsWithAllData() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("[DataDrivenTest] Starting Data-Driven Test Execution");
        System.out.println("[DataDrivenTest] Suite: BankDetailsOnly");
        System.out.println("=".repeat(100) + "\n");

        DataDrivenTestRunner runner = new DataDrivenTestRunner(driver);
        
        try {
            runner.executeSuiteWithAllData("BankDetailsOnly");
            
            long failed = runner.getReports().stream()
                .filter(r -> "FAILED".equals(r.getStatus()))
                .count();
            
            if (failed > 0) {
                throw new AssertionError(failed + " iteration(s) failed. Check reports for details.");
            }
            
            System.out.println("\n[DataDrivenTest] All iterations completed successfully!");
            
        } catch (Exception e) {
            System.err.println("[DataDrivenTest] Data-driven execution failed: " + e.getMessage());
            throw e;
        }
    }
}
