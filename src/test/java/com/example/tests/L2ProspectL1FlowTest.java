package com.example.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.managers.PageObjectManager;
import com.example.pages.L2InfoActions;
import com.example.testdata.TestConfig;

public class L2ProspectL1FlowTest   extends BaseTest {

    // Credentials loaded from TestConfig (testdata/config.properties)

    private PageObjectManager pageObjectManager;

    @BeforeMethod
    public void initializePageObjects() {
        pageObjectManager = new PageObjectManager(driver);
    }

    @Test(groups = "l2Info")
    public void runL2ProspectL1DetailsFlow() throws Exception {
        var permissionPage = pageObjectManager.getPermissionPage();
        var homePage = pageObjectManager.getHomePage();
        var onboardingPage = pageObjectManager.getOnboardingPage();

        permissionPage.handlePermissions();

        if (!homePage.waitForLoginScreenReady()) {
            System.err.println("Login screen did not become ready within wait; proceeding with attempts for debugging.");
        }

        homePage.enterUserId(TestConfig.USERNAME);
        homePage.enterPassword(TestConfig.PASSWORD);
        homePage.clickSignInButton();

        homePage.waitForJLGVisible();
        homePage.clickJLG();

        boolean onboardingLaunched = onboardingPage.openCustomerDetailsScreen();
        if (!onboardingLaunched) {
            throw new IllegalStateException("Onboarding APK did not launch within timeout.");
        }
        onboardingPage.clickCaptureCustomerConsent();
        String submittedMobile = onboardingPage.enterMobileNumberAndSubmit();
        System.out.println("Submitted onboarding mobile number: " + submittedMobile);

        onboardingPage.completeVoterIdCaptureFlow();

        L2InfoActions l2Actions = new L2InfoActions(driver);
        boolean clicked = l2Actions.clickContinuationDirectly();
        if (!clicked) {
            throw new IllegalStateException("Failed to click the L2 post-success continuation control.");
        }

        String chosenCustomer = l2Actions.selectFirstCustomerSkippingAlerts();
        System.out.println("[L2ProspectL1FlowTest] Selected customer: " + chosenCustomer);

        boolean flowOk = l2Actions.fillL2ProspectL1DetailsFlow();
        if (!flowOk) {
            throw new IllegalStateException("L2 Prospect L1 details flow did not complete successfully.");
        }
    }

    @Test(groups = "l2Info")
    public void runBankDetailsOnly() {
        L2InfoActions l2Actions = new L2InfoActions(driver);
        boolean bankFlowOk = l2Actions.fillBankDetailsAndCaptureProof();
        if (!bankFlowOk) {
            throw new IllegalStateException("Bank details workflow did not complete successfully .");
        }
    }
}

