package com.example.tests;

import org.testng.annotations.Test;

import com.example.runners.SuiteOrchestrator;

/**
 * Test class for executing multiple test suites sequentially with reporting
 * Uses the new flow-based structure with centralized test data
 */
public class MultiSuiteExecutionTest extends BaseTest {

    /**
     * Execute all enabled test suites from configuration
     * Each suite will run with its own data set and flows
     * App will be reset between suites if configured
     * Detailed reports will be generated for each suite
     */
    @Test(groups = "multiSuite", priority = 1)
    public void executeAllTestSuites() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("[MultiSuiteExecutionTest] Starting Multi-Suite Execution");
        System.out.println("=".repeat(100) + "\n");

        SuiteOrchestrator orchestrator = new SuiteOrchestrator(driver);
        
        try {
            orchestrator.executeAllSuites();
            
            // Print summary
            long passed = orchestrator.getReports().stream()
                .filter(r -> "PASSED".equals(r.getStatus()))
                .count();
            long failed = orchestrator.getReports().stream()
                .filter(r -> "FAILED".equals(r.getStatus()))
                .count();
            
            System.out.println("\n" + "=".repeat(100));
            System.out.println("[MultiSuiteExecutionTest] Execution Summary:");
            System.out.println("  Total Suites: " + orchestrator.getReports().size());
            System.out.println("  Passed: " + passed);
            System.out.println("  Failed: " + failed);
            System.out.println("=".repeat(100) + "\n");
            
            // Fail the test if any suite failed
            if (failed > 0) {
                throw new AssertionError("One or more test suites failed. Check reports for details.");
            }
            
        } catch (Exception e) {
            System.err.println("[MultiSuiteExecutionTest] Multi-suite execution failed: " + e.getMessage());
            throw e;
        }
    }
}
