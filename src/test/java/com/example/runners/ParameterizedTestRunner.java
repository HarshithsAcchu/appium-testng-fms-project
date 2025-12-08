package com.example.runners;

import com.example.actions.GenericActionExecutor;
import com.example.config.ConfigurationReader;
import com.example.config.models.ActionStep;
import com.example.config.models.ExecutionStep;
import com.example.config.models.TestSuite;
import com.example.managers.PageObjectManager;
import com.example.pages.HomePage;
import com.example.pages.L2InfoActions;
import com.example.pages.OnboardingPage;
import com.example.pages.PermissionPage;

import io.appium.java_client.android.AndroidDriver;

/**
 * Parameterized test runner that executes test flows based on JSON configuration
 */
public class ParameterizedTestRunner {
    
    private final AndroidDriver driver;
    private final PageObjectManager pageObjectManager;
    private final ConfigurationReader configReader;
    private final GenericActionExecutor actionExecutor;
    private final String suiteName;

    public ParameterizedTestRunner(AndroidDriver driver, String suiteName) {
        this.driver = driver;
        this.suiteName = suiteName;
        this.pageObjectManager = new PageObjectManager(driver);
        this.configReader = ConfigurationReader.getInstance();
        this.actionExecutor = new GenericActionExecutor(driver);
    }

    /**
     * Execute test suite based on JSON configuration
     */
    public void executeTestSuite() throws Exception {
        TestSuite suite = configReader.getTestConfig().getTestSuite(suiteName);
        
        if (suite == null) {
            throw new IllegalArgumentException("Test suite not found: " + suiteName);
        }

        if (!suite.isEnabled()) {
            System.out.println("[TestRunner] Test suite '" + suiteName + "' is disabled. Skipping.");
            return;
        }

        System.out.println("[TestRunner] Executing test suite: " + suiteName);

        for (ExecutionStep step : suite.getExecutionFlow()) {
            if (!step.isEnabled()) {
                System.out.println("[TestRunner] Step '" + step.getStepName() + "' is disabled. Skipping.");
                continue;
            }

            System.out.println("[TestRunner] Executing step: " + step.getStepName());
            executeStep(step, suite);
        }

        System.out.println("[TestRunner] Test suite '" + suiteName + "' completed successfully.");
    }

    /**
     * Execute individual test step
     */
    private void executeStep(ExecutionStep step, TestSuite suite) throws Exception {
        // NEW: Check if step uses generic action steps (JSON-driven actions)
        if (step.getActionSteps() != null && !step.getActionSteps().isEmpty()) {
            executeGenericActions(step);
            return;
        }
        
        // LEGACY: Old hardcoded page-action approach
        String page = step.getPage();
        
        switch (page) {
            case "PermissionPage":
                executePermissionPageAction(step);
                break;
            case "HomePage":
                executeHomePageAction(step, suite);
                break;
            case "OnboardingPage":
                executeOnboardingPageAction(step);
                break;
            case "L2InfoActions":
                executeL2InfoAction(step);
                break;
            default:
                throw new IllegalArgumentException("Unknown page: " + page);
        }
    }

    /**
     * Execute generic actions from actionSteps array
     */
    private void executeGenericActions(ExecutionStep step) {
        for (ActionStep actionStep : step.getActionSteps()) {
            actionExecutor.executeAction(actionStep);
        }
    }

    private void executePermissionPageAction(ExecutionStep step) {
        PermissionPage permissionPage = pageObjectManager.getPermissionPage();
        
        if ("handlePermissions".equals(step.getAction())) {
            permissionPage.handlePermissions();
        }
    }

    private void executeHomePageAction(ExecutionStep step, TestSuite suite) {
        HomePage homePage = pageObjectManager.getHomePage();
        String action = step.getAction();
        
        if (action != null) {
            switch (action) {
                case "waitForLoginScreenReady":
                    if (!homePage.waitForLoginScreenReady()) {
                        System.err.println("[TestRunner] Login screen did not become ready within wait.");
                    }
                    break;
                default:
                    System.err.println("[TestRunner] Unknown HomePage action: " + action);
            }
        } else if (step.getActions() != null) {
            // Handle multiple actions
            for (String multiAction : step.getActions()) {
                switch (multiAction) {
                    case "enterUserId":
                        if (step.isUseCredentials() && suite.getCredentials() != null) {
                            homePage.enterUserId(suite.getCredentials().getUsername());
                        }
                        break;
                    case "enterPassword":
                        if (step.isUseCredentials() && suite.getCredentials() != null) {
                            homePage.enterPassword(suite.getCredentials().getPassword());
                        }
                        break;
                    case "clickSignInButton":
                        homePage.clickSignInButton();
                        break;
                    case "waitForJLGVisible":
                        homePage.waitForJLGVisible();
                        break;
                    case "clickJLG":
                        homePage.clickJLG();
                        break;
                    default:
                        System.err.println("[TestRunner] Unknown HomePage multi-action: " + multiAction);
                }
            }
        }
    }

    private void executeOnboardingPageAction(ExecutionStep step) {
        OnboardingPage onboardingPage = pageObjectManager.getOnboardingPage();
        String action = step.getAction();
        
        switch (action) {
            case "openCustomerDetailsScreen":
                boolean launched = onboardingPage.openCustomerDetailsScreen();
                if (!launched) {
                    throw new IllegalStateException("Onboarding APK did not launch within timeout.");
                }
                break;
            case "clickCaptureCustomerConsent":
                onboardingPage.clickCaptureCustomerConsent();
                break;
            case "enterMobileNumberAndSubmit":
                String mobile = onboardingPage.enterMobileNumberAndSubmit();
                System.out.println("[TestRunner] Submitted mobile: " + mobile);
                break;
            case "completeVoterIdCaptureFlow":
                onboardingPage.completeVoterIdCaptureFlow();
                break;
            default:
                System.err.println("[TestRunner] Unknown OnboardingPage action: " + action);
        }
    }

    private void executeL2InfoAction(ExecutionStep step) {
        L2InfoActions l2Actions = new L2InfoActions(driver);
        String action = step.getAction();
        
        switch (action) {
            case "clickContinuationAfterSuccess":
                boolean clicked1 = l2Actions.clickContinuationAfterSuccess();
                if (!clicked1) {
                    System.err.println("[TestRunner] Failed to click continuation after success.");
                }
                break;
            case "clickContinuationDirectly":
                boolean clicked2 = l2Actions.clickContinuationDirectly();
                if (!clicked2) {
                    throw new IllegalStateException("Failed to click L2 continuation directly.");
                }
                break;
            case "selectFirstCustomerSkippingAlerts":
                String customer = l2Actions.selectFirstCustomerSkippingAlerts();
                System.out.println("[TestRunner] Selected customer: " + customer);
                break;
            case "fillL2ProspectL1DetailsFlow":
                boolean success = l2Actions.fillL2ProspectL1DetailsFlow();
                if (!success) {
                    throw new IllegalStateException("L2 Prospect L1 details flow failed.");
                }
                break;
            case "fillBankDetailsAndCaptureProof":
                boolean bankSuccess = l2Actions.fillBankDetailsAndCaptureProof();
                if (!bankSuccess) {
                    throw new IllegalStateException("Bank details flow failed.");
                }
                break;
            default:
                System.err.println("[TestRunner] Unknown L2InfoActions action: " + action);
        }
    }

    /**
     * Get test data from suite configuration
     */
    public String getTestData(String key) {
        return configReader.getTestData(suiteName, key);
    }
}
