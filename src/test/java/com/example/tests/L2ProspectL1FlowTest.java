package com.example.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.runners.ParameterizedTestRunner;

public class L2ProspectL1FlowTest extends BaseTest {

    private static final String SUITE_NAME = "L2ProspectL1Flow";
    
    private ParameterizedTestRunner testRunner;

    @BeforeMethod
    public void initializePageObjects() {
        testRunner = new ParameterizedTestRunner(driver, SUITE_NAME);
    }

    /**
     * JSON-driven test execution
     * Reads test flow from test-config.json and executes accordingly
     */
    @Test(groups = "l2Info", priority = 1)
    public void runL2ProspectL1DetailsFlow() throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("[L2ProspectL1FlowTest] Starting JSON-driven test execution");
        System.out.println("[L2ProspectL1FlowTest] Suite: " + SUITE_NAME);
        System.out.println("=".repeat(80));
        
        // Execute entire flow from JSON configuration
        testRunner.executeTestSuite();
        
        System.out.println("=".repeat(80));
        System.out.println("[L2ProspectL1FlowTest] JSON-    driven test completed successfully");
        System.out.println("=".repeat(80));
    }

    /**
     * JSON-driven test for bank details only
     * Executes the BankDetailsOnly suite from test-config.json
     */
    @Test(groups = "l2Info", priority = 2)
    public void runBankDetailsOnly() throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("[L2ProspectL1FlowTest] Starting Bank Details Only flow");
        System.out.println("=".repeat(80));
        
        ParameterizedTestRunner bankRunner = new ParameterizedTestRunner(driver, "BankDetailsOnly");
        bankRunner.executeTestSuite();
        
        System.out.println("=".repeat(80));
        System.out.println("[L2ProspectL1FlowTest] Bank Details flow completed successfully");
        System.out.println("=".repeat(80));
    }
}
