package com.example.tests;

import java.util.Locale;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.example.managers.PageObjectManager;
import com.example.pages.L2InfoActions;
import com.example.testdata.TestConfig;

public class PermissionAndJLGTest extends BaseTest {

    // Credentials and settings loaded from TestConfig (testdata/config.properties)

    private PageObjectManager pageObjectManager;
    
    @BeforeMethod
    public void initializePageObjects() {
        pageObjectManager = new PageObjectManager(driver);
    }
    
   @Parameters({"resumeFrom"})
    @Test
    public void openAppHandlePermissionsAndOpenJLG(@Optional("FRESH") String resumeFromValue) throws Exception {
        ResumePoint resumePoint = ResumePoint.from(resumeFromValue);
        System.out.println("[PermissionAndJLGTest] resumeFrom=" + resumePoint.name());

        var permissionPage = pageObjectManager.getPermissionPage();
        var homePage = pageObjectManager.getHomePage();
        var onboardingPage = pageObjectManager.getOnboardingPage();

        if (resumePoint.shouldRunPermissions()) {
            permissionPage.handlePermissions();
        } else {
            logSkippedStage("Permission dialogs", resumePoint);
        }

        if (TestConfig.DEBUG_LOGS_ENABLED) {
            System.out.println("Contexts: " + driver.getContextHandles());
            System.out.println("Current context: " + driver.getContext());
            System.out.println("Current activity: " + driver.currentActivity());
            System.out.println("Current package: " + driver.getCurrentPackage());
        }

        boolean onboardingReady = true;

        if (resumePoint.shouldRunLogin()) {
            if (!homePage.waitForLoginScreenReady()) {
                System.err.println("Login screen did not become ready within wait; proceeding with attempts for debugging.");
            }

            homePage.enterUserId(TestConfig.USERNAME);
            homePage.enterPassword(TestConfig.PASSWORD);
            homePage.clickSignInButton();

            homePage.waitForJLGVisible();
            homePage.clickJLG();
        } else {
            logSkippedStage("Login & JLG navigation", resumePoint);
        }

        if (resumePoint.shouldRunOnboardingPrep()) {
            boolean onboardingLaunched = onboardingPage.openCustomerDetailsScreen();
            if (!onboardingLaunched) {
                System.err.println("Onboarding APK did not launch within timeout.");
                onboardingReady = false;
            } else {
                onboardingPage.clickCaptureCustomerConsent();
                String submittedMobile = onboardingPage.enterMobileNumberAndSubmit();
                System.out.println("Submitted onboarding mobile number: " + submittedMobile);
            }
        } else {
            logSkippedStage("Onboarding launch & mobile submission", resumePoint);
        }

        if (resumePoint.shouldRunDocumentFlow()) {
            if (!onboardingReady) {
                System.err.println("[PermissionAndJLGTest] Skipping voter-id capture because onboarding screen was unavailable.");
            } else {
                System.out.println("[PermissionAndJLGTest] Starting voter-id capture flow.");
                onboardingPage.completeVoterIdCaptureFlow();
                L2InfoActions l2Actions = new L2InfoActions(driver);
                boolean l2Clicked = l2Actions.clickContinuationAfterSuccess();
                if (!l2Clicked) {
                    System.err.println("[PermissionAndJLGTest] L2 post-success continuation control not available after document flow.");
                } else {
                    try {
                        String chosenCustomer = l2Actions.selectFirstCustomerSkippingAlerts();
                        System.out.println("[PermissionAndJLGTest] Selected L2 customer: " + chosenCustomer);
                        boolean flowOk = l2Actions.fillL2ProspectL1DetailsFlow();
                        if (!flowOk) {
                            System.err.println("[PermissionAndJLGTest] L2 Prospect L1 details flow did not complete successfully.");
                        }
                    } catch (Exception selectionError) {
                        System.err.println("[PermissionAndJLGTest] Unable to select L2 customer: " + selectionError.getMessage());
                    }
                }
            }
        } else {
            logSkippedStage("Voter-id capture flow", resumePoint);
        }
    }

    @Parameters({"resumeFrom"})
    @Test(groups = "l2Info")
    public void runL2InfoContinuationOnly(@Optional("AFTER_MOBILE_SUBMIT") String resumeFromValue) throws Exception {
        ResumePoint resumePoint = ResumePoint.from(resumeFromValue);
        System.out.println("[PermissionAndJLGTest] (L2 only) resumeFrom=" + resumePoint.name());

        var permissionPage = pageObjectManager.getPermissionPage();
        var homePage = pageObjectManager.getHomePage();
        var onboardingPage = pageObjectManager.getOnboardingPage();

        permissionPage.handlePermissions();

        boolean onboardingReady = true;

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
            System.err.println("Onboarding APK did not launch within timeout.");
            onboardingReady = false;
        } else {
            onboardingPage.clickCaptureCustomerConsent();
            String submittedMobile = onboardingPage.enterMobileNumberAndSubmit();
            System.out.println("Submitted onboarding mobile number: " + submittedMobile);
        }

        if (!onboardingReady) {
            throw new IllegalStateException("Cannot run L2 continuation because onboarding screen was unavailable.");
        }
        System.out.println("[PermissionAndJLGTest] Preparing L2 continuation by executing voter-id capture flow.");
        onboardingPage.completeVoterIdCaptureFlow();

        L2InfoActions l2Actions = new L2InfoActions(driver);
        boolean clicked = l2Actions.clickContinuationDirectly();
        if (!clicked) {
            throw new IllegalStateException("Failed to click the L2 post-success continuation control.");
        }

        String chosenCustomer = l2Actions.selectFirstCustomerSkippingAlerts();
        System.out.println("[PermissionAndJLGTest] (L2 only) Selected customer: " + chosenCustomer);
        boolean flowOk = l2Actions.fillL2ProspectL1DetailsFlow();
        if (!flowOk) {
            throw new IllegalStateException("L2 Prospect L1 details flow did not complete successfully.");
        }
    }

    private void logSkippedStage(String stage, ResumePoint resumePoint) {
        System.out.println("[PermissionAndJLGTest] Skipping " + stage + " due to resumeFrom=" + resumePoint.name());
    }

    private enum ResumePoint {
        FRESH(true, true, true, true),
        AFTER_PERMISSIONS(false, true, true, true),
        AFTER_LOGIN(false, false, true, true),
       
        AFTER_MOBILE_SUBMIT(false, false, false, true);

        private final boolean runPermissions;
        private final boolean runLogin;
        private final boolean runOnboardingPrep;
        private final boolean runDocumentFlow;

        ResumePoint(boolean runPermissions, boolean runLogin, boolean runOnboardingPrep, boolean runDocumentFlow) {
            this.runPermissions = runPermissions;
            this.runLogin = runLogin;
            this.runOnboardingPrep = runOnboardingPrep;
            this.runDocumentFlow = runDocumentFlow;
        }

        boolean shouldRunPermissions() {
            return runPermissions;
        }

        boolean shouldRunLogin() {
            return runLogin;
        }

        boolean shouldRunOnboardingPrep() {
            return runOnboardingPrep;
        }

        boolean shouldRunDocumentFlow() {
            return runDocumentFlow;
        }

        static ResumePoint from(String value) {
            if (value == null) {
                return FRESH;
            }
            String normalized = value.trim().toUpperCase(Locale.ROOT);
            for (ResumePoint point : values()) {
                if (point.name().equals(normalized)) {
                    return point;
                }
            }
            System.err.println("[PermissionAndJLGTest] Unknown resumeFrom='" + value + "'. Defaulting to FRESH.");
            return FRESH;
        }
    }
}