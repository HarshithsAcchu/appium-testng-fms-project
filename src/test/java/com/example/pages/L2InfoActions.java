package com.example.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.locators.L2_Info_Locators;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

/**
 * Minimal helper focused on the post "Successfully Captured" portion of the L2 screen.
 * It lets tests run that tail segment independently of the wider onboarding journey.
 */
public class L2InfoActions extends BasePage {

    private static final Duration MEDIUM = Duration.ofSeconds(8);
    private static final Duration LONG = Duration.ofSeconds(60);

    public L2InfoActions(AndroidDriver driver) {
        super(driver);
    }

    private boolean waitForAnyClickable(By locator, Duration timeout) {
        try {
            WebDriverWait waitClickable = new WebDriverWait(driver, timeout);
            waitClickable.pollingEvery(Duration.ofMillis(250));
            return Boolean.TRUE.equals(waitClickable.until(d -> {
                List<WebElement> candidates = d.findElements(locator);
                for (WebElement candidate : candidates) {
                    if (candidate == null) {
                        continue;
                    }
                    if (isDisplayed(candidate) && isCandidateClickable(candidate)) {
                        return true;
                    }
                }
                return null;
            }));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Waits for the success toast/label and clicks the follow-up compose view.
     *
     * @return true when the continuation view was clicked, false if absent or disabled
     */
    public boolean clickContinuationAfterSuccess() {
        WebDriverWait localWait = new WebDriverWait(driver, MEDIUM);
        localWait.pollingEvery(Duration.ofMillis(300));

        try {
            localWait.until(ExpectedConditions.visibilityOfElementLocated(L2_Info_Locators.SUCCESS_MESSAGE));
            WebElement continuation = localWait.until(ExpectedConditions.elementToBeClickable(L2_Info_Locators.POST_SUCCESS_COMPOSE_VIEW));
            continuation.click();
            System.out.println("[L2InfoActions] Clicked post-success compose view after success message.");
            tinySleep();
            return true;
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Unable to click continuation after success: " + e.getMessage());
            return false;
        }
    }

    public boolean clickContinuationDirectly() {
        WebDriverWait localWait = new WebDriverWait(driver, MEDIUM);
        localWait.pollingEvery(Duration.ofMillis(300));

        try {
            WebElement continuation = localWait.until(ExpectedConditions.elementToBeClickable(L2_Info_Locators.POST_SUCCESS_COMPOSE_VIEW));
            continuation.click();
            System.out.println("[L2InfoActions] Directly clicked post-success compose view.");
            tinySleep();
            return true;
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Failed to click continuation directly: " + e.getMessage());
            return false;
        }
    }

    /**
     * Iterates over the L2 customer cards, skipping any that fire the alert dialog.
     *
     * @return the name of the customer whose card was successfully selected
     */
    public String selectFirstCustomerSkippingAlerts() {
        WebDriverWait localWait = new WebDriverWait(driver, MEDIUM);
        localWait.pollingEvery(Duration.ofMillis(300));

        List<WebElement> initialCards = localWait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(L2_Info_Locators.LISTVIEW_OF_L2));

        List<String> discoveredLabels = new ArrayList<>();
        for (WebElement card : initialCards) {
            String label = extractPrimaryText(card);
            if (label != null && !label.isEmpty()) {
                discoveredLabels.add(label);
            }
        }
        System.out.println("[L2InfoActions] L2 customer cards discovered: " + discoveredLabels);

        for (int index = 0; index < initialCards.size(); index++) {
            List<WebElement> currentCards = driver.findElements(L2_Info_Locators.LISTVIEW_OF_L2);
            if (currentCards.isEmpty() || index >= currentCards.size()) {
                break;
            }

            WebElement card = currentCards.get(index);
            String label = extractPrimaryText(card);

            try {
                localWait.until(ExpectedConditions.elementToBeClickable(card)).click();
            } catch (Exception e) {
                System.err.println("[L2InfoActions] Failed to click L2 card index " + index + ": " + e.getMessage());
                continue;
            }

            tinySleep();

            if (waitForAlertIfPresent()) {
                System.out.println("[L2InfoActions] Alert detected for customer card " + (label != null ? label : index));
                dismissAlertIfPresent();
                tinySleep();
                continue;
            }

            return label != null ? label : "";
        }

        throw new IllegalStateException("No selectable L2 customer card was found without showing an alert.");
    }

    private String extractPrimaryText(WebElement card) {
        try {
            List<WebElement> textNodes = card.findElements(By.className("android.widget.TextView"));
            for (WebElement node : textNodes) {
                String text = node.getText();
                if (text != null && !text.isBlank()) {
                    return text.trim();
                }
            }
            String contentDesc = card.getAttribute("content-desc");
            if (contentDesc != null && !contentDesc.isBlank()) {
                return contentDesc.trim();
            }
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Unable to extract text from customer card: " + e.getMessage());
        }
        return null;
    }

    private boolean waitForAlertIfPresent() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        shortWait.pollingEvery(Duration.ofMillis(200));
        try {
            return Boolean.TRUE.equals(shortWait.until(d ->
                d.findElements(L2_Info_Locators.ALERT_MESSAGE)
                    .stream()
                    .anyMatch(WebElement::isDisplayed)));
        } catch (TimeoutException ignored) {
            return false;
        }
    }

    private void dismissAlertIfPresent() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        shortWait.pollingEvery(Duration.ofMillis(200));

        try {
            WebElement okText = shortWait.until(
                ExpectedConditions.elementToBeClickable(L2_Info_Locators.ALERT_OK_TEXT));
            okText.click();
            return;
        } catch (TimeoutException ignored) {
            // TextView-based OK not shown; fall through to button fallback.
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Failed to tap alert OK text: " + e.getMessage());
        }

        try {
            WebElement dismissButton = shortWait.until(
                ExpectedConditions.elementToBeClickable(L2_Info_Locators.ALERT_DISMISS_BUTTON));
            dismissButton.click();
        } catch (TimeoutException ignored) {
            // No dismiss button either.
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Failed to dismiss alert dialog: " + e.getMessage());
        }
    }

    public boolean fillL2ProspectL1DetailsFlow() {
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(12));
        localWait.pollingEvery(Duration.ofMillis(300));
        try {
            WebElement l1Card = localWait.until(
                ExpectedConditions.elementToBeClickable(L2_Info_Locators.PROSPECT_L1_DETAILS_CARD));
            l1Card.click();
            tinySleep();

            WebElement altInput = localWait.until(
                ExpectedConditions.visibilityOfElementLocated(L2_Info_Locators.ALTERNATE_MOBILE_INPUT));
            try {
                altInput.clear();
            } catch (Exception ignored) {
            }
            altInput.click();
            altInput.sendKeys("8105928245");
            tinySleep();
            hideKeyboardIfVisible();

            if (areTargetValuesAlreadyPresent()) {
                System.out.println("[L2InfoActions] Target values already filled; proceeding directly to final submit.");
                if (!waitForAnyClickable(L2_Info_Locators.FINAL_SUBMIT_TEXT, LONG)) {
                    System.err.println("[L2InfoActions] Final SUBMIT button did not become enabled (prefilled path).");
                    return false;
                }
                if (!clickFirstVisible(L2_Info_Locators.FINAL_SUBMIT_TEXT, Duration.ofSeconds(8))) {
                    System.err.println("[L2InfoActions] Unable to click final SUBMIT button (prefilled path).");
                    return false;
                }
                tinySleep();
                return true;
            }

            if (!isElementDisplayed(L2_Info_Locators.CURRENT_ADDRESS_YES_TOGGLE, Duration.ofMillis(600))) {
                for (int i = 0; i < 3; i++) {
                    swipeToCoordinates(490, 390, Duration.ofMillis(350));
                    tinySleep();
                    if (isElementDisplayed(L2_Info_Locators.CURRENT_ADDRESS_YES_TOGGLE, Duration.ofMillis(400))) {
                        break;
                    }
                }
            }

            try {
                WebElement yesToggle = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.CURRENT_ADDRESS_YES_TOGGLE));
                yesToggle.click();
            } catch (Exception e) {
                System.err.println("[L2InfoActions] Unable to click current address YES toggle: " + e.getMessage());
            }
            tinySleep();

            for (int i = 0; i < 2; i++) {
                swipeToCoordinates(490, 390, Duration.ofMillis(350));
                if (isElementDisplayed(L2_Info_Locators.RELIGION_SPINNER, Duration.ofMillis(500))) {
                    break;
                }
            }

            try {
                WebElement religionSpinner = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.RELIGION_SPINNER));
                religionSpinner.click();
                WebElement hindu = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.RELIGION_OPTION_HINDU));
                hindu.click();
            } catch (Exception e) {
                System.err.println("[L2InfoActions] Religion selection failed: " + e.getMessage());
            }
            tinySleep();

            try {
                WebElement eduSpinner = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.EDUCATION_SPINNER));
                eduSpinner.click();
                WebElement engGrad = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.EDUCATION_OPTION_ENGINEERING_GRADUATE));
                engGrad.click();
                String selectedEdu = "";
                try {
                    selectedEdu = driver.findElement(L2_Info_Locators.EDUCATION_SPINNER).getText();
                } catch (Exception ignored) {
                    selectedEdu = "Engineering Graduate";
                }
                System.out.println("[L2InfoActions] Selected education qualification: " + selectedEdu);
            } catch (Exception e) {
                System.err.println("[L2InfoActions] Education selection failed: " + e.getMessage());
            }
            tinySleep();

            try {
                WebElement resSpinner = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.RESIDENCE_SPINNER));
                resSpinner.click();
                WebElement ownHouse = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.RESIDENCE_OPTION_OWN_HOUSE));
                ownHouse.click();
            } catch (Exception e) {
                System.err.println("[L2InfoActions] Residence selection failed: " + e.getMessage());
            }
            tinySleep();

            swipeToCoordinates(490, 1200, Duration.ofMillis(350));

            try {
                WebElement ownershipSpinner = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.OWNERSHIP_PROOF_SPINNER));
                ownershipSpinner.click();
                WebElement katha = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.OWNERSHIP_OPTION_KATHA));
                katha.click();
            } catch (Exception e) {
                System.err.println("[L2InfoActions] Ownership proof selection failed: " + e.getMessage());
            }
            tinySleep();

            try {
                WebElement docSection = localWait.until(
                    ExpectedConditions.elementToBeClickable(L2_Info_Locators.DOCUMENT_CAPTURE_SECTION));
                docSection.click();
            } catch (Exception e) {
                System.err.println("[L2InfoActions] Could not click document capture section: " + e.getMessage());
            }
            tinySleep();

            if (!clickWhenClickable(L2_Info_Locators.CAPTURE_FRONT_IMAGE_TEXT, Duration.ofSeconds(8))) {
                System.err.println("[L2InfoActions] Failed to trigger 'Capture Front image'.");
                return false;
            }
            tinySleep();

            if (!clickWhenClickable(L2_Info_Locators.CAPTURE_BACK_IMAGE_TEXT, Duration.ofSeconds(8))) {
                System.err.println("[L2InfoActions] Failed to trigger 'Capture Back image'.");
                return false;
            }
            tinySleep();

            if (!waitForAnyClickable(L2_Info_Locators.SUBMIT_TEXT_BUTTON, Duration.ofSeconds(60))) {
                System.err.println("[L2InfoActions] Submit button (text) did not become enabled.");
                return false;
            }
            if (!clickFirstVisible(L2_Info_Locators.SUBMIT_TEXT_BUTTON, Duration.ofSeconds(8))) {
                System.err.println("[L2InfoActions] Unable to click Submit button (text).");
                return false;
            }
            sleepSeconds(30);

            if (!waitForAnyClickable(L2_Info_Locators.GENERIC_COMPOSE_BUTTON, LONG)) {
                System.err.println("[L2InfoActions] Compose submit button did not become enabled after text submit.");
                return false;
            }
            if (!clickFirstVisible(L2_Info_Locators.GENERIC_COMPOSE_BUTTON, Duration.ofSeconds(8))) {
                System.err.println("[L2InfoActions] Failed to click compose submit button.");
                return false;
            }
            sleepSeconds(15);

            if (!waitForAnyClickable(L2_Info_Locators.FINAL_SUBMIT_TEXT, LONG)) {
                System.err.println("[L2InfoActions] Final SUBMIT button did not become enabled.");
                return false;
            }
            if (!clickFirstVisible(L2_Info_Locators.FINAL_SUBMIT_TEXT, Duration.ofSeconds(8))) {
                System.err.println("[L2InfoActions] Unable to click final SUBMIT button.");
                return false;
            }
            sleepSeconds(15);

            if (!waitForAnyClickable(L2_Info_Locators.FINAL_OKAY_TEXT, LONG)) {
                System.err.println("[L2InfoActions] Final OKAY acknowledgement did not appear.");
                return false;
            }
            if (!clickFirstVisible(L2_Info_Locators.FINAL_OKAY_TEXT, Duration.ofSeconds(8))) {
                System.err.println("[L2InfoActions] Unable to click final OKAY acknowledgement.");
                return false;
            }
            tinySleep();

            if (!fillBankDetailsAndCaptureProof()) {
                System.err.println("[L2InfoActions] Bank details workflow failed.");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("[L2InfoActions] L2 Prospect L1 details flow failed: " + e.getMessage());
            return false;
        }
    }

    public boolean fillBankDetailsAndCaptureProof() {
        try {
            if (!clickWhenClickable(L2_Info_Locators.ADD_BANK_ACCOUNT_BUTTON, Duration.ofSeconds(12))) {
                System.err.println("[L2InfoActions] Add bank account button not clickable.");
                return false;
            }
            tinySleep();

            if (!enterBankAccountNumber()) {
                return false;
            }

            if (!enterIfscAndValidateBranch()) {
                return false;
            }

            if (!captureBankProofImage()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Bank details flow threw exception: " + e.getMessage());
            return false;
        }
    }

    private boolean enterBankAccountNumber() {
        try {
            WebElement accountNumber = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.visibilityOfElementLocated(L2_Info_Locators.BANK_ACCOUNT_NUMBER_INPUT));
            accountNumber.click();
            accountNumber.clear();
            accountNumber.sendKeys("10990200087021");
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Unable to enter primary account number: " + e.getMessage());
            return false;
        }

        tinySleep();

        try {
            WebElement reenter = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.visibilityOfElementLocated(L2_Info_Locators.BANK_ACCOUNT_REENTER_INPUT));
            reenter.click();
            reenter.clear();
            reenter.sendKeys("10990200087021");
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Unable to re-enter account number: " + e.getMessage());
            return false;
        }

        hideKeyboardIfVisible();
        tinySleep();
        return true;
    }

    private boolean enterIfscAndValidateBranch() {
        try {
            WebElement ifscInput = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.visibilityOfElementLocated(L2_Info_Locators.BANK_IFSC_INPUT));
            ifscInput.click();
            ifscInput.clear();
            ifscInput.sendKeys("ICIC0002121");
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Unable to enter IFSC: " + e.getMessage());
            return false;
        }

        hideKeyboardIfVisible();
        tinySleep();

        if (!clickWhenClickable(L2_Info_Locators.BANK_IFSC_SEARCH_BUTTON, Duration.ofSeconds(8))) {
            System.err.println("[L2InfoActions] Unable to click IFSC search button.");
            return false;
        }

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
            wait.until(driver1 -> {
                String branch = safeGetText(L2_Info_Locators.BANK_BRANCH_NAME_INPUT, Duration.ofSeconds(2));
                String address = safeGetText(L2_Info_Locators.BANK_BRANCH_ADDRESS_INPUT, Duration.ofSeconds(2));
                return !branch.isBlank() && !address.isBlank();
            });

            String branchName = safeGetText(L2_Info_Locators.BANK_BRANCH_NAME_INPUT, Duration.ofSeconds(2));
            String branchAddress = safeGetText(L2_Info_Locators.BANK_BRANCH_ADDRESS_INPUT, Duration.ofSeconds(2));
            System.out.println("[L2InfoActions] IFSC resolved branch: " + branchName + " | Address: " + branchAddress);
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Branch details did not populate after IFSC search: " + e.getMessage());
            return false;
        }

        return true;
    }

    private boolean captureBankProofImage() {
        if (!clickWhenClickable(L2_Info_Locators.BANK_PROOF_IMAGE_TOGGLE, Duration.ofSeconds(8))) {
            System.err.println("[L2InfoActions] Unable to open bank proof capture toggle.");
            return false;
        }

        tinySleep();

        if (!clickFirstVisible(L2_Info_Locators.BANK_PROOF_COMPOSE_CAPTURE, Duration.ofSeconds(8))) {
            System.err.println("[L2InfoActions] Unable to trigger bank proof compose capture option.");
            return false;
        }

        tinySleep();

        if (!clickWhenClickable(L2_Info_Locators.CAMERA_CAPTURE_BUTTON, Duration.ofSeconds(10))) {
            System.err.println("[L2InfoActions] Unable to click camera capture button for bank proof.");
            return false;
        }

        tinySleep();
        return true;
    }

    private boolean clickFirstVisible(By locator, Duration timeout) {
        try {
            WebDriverWait w = new WebDriverWait(driver, timeout);
            w.pollingEvery(Duration.ofMillis(250));
            return Boolean.TRUE.equals(w.until(d -> {
                List<WebElement> elements = d.findElements(locator);
                if (elements.isEmpty()) {
                    return null;
                }
                List<WebElement> ordered = elements.stream()
                    .filter(this::isDisplayed)
                    .filter(this::isCandidateClickable)
                    .sorted(Comparator.comparingInt(this::getElementCenterY).reversed())
                    .collect(Collectors.toList());

                for (WebElement candidate : ordered) {
                    try {
                        new WebDriverWait(driver, Duration.ofMillis(800))
                            .pollingEvery(Duration.ofMillis(150))
                            .until(ExpectedConditions.visibilityOf(candidate));
                    } catch (Exception ignored) {
                    }

                    if (clickWithFallbacks(candidate)) {
                        return true;
                    }
                }

                return null;
            }));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isElementDisplayed(By locator, Duration timeout) {
        try {
            WebDriverWait w = new WebDriverWait(driver, timeout);
            w.pollingEvery(Duration.ofMillis(200));
            return Boolean.TRUE.equals(w.until(d -> {
                List<WebElement> els = d.findElements(locator);
                for (WebElement el : els) {
                    try {
                        if (el.isDisplayed()) return true;
                    } catch (Exception ignored) {}
                }
                return false;
            }));
        } catch (Exception e) {
            return false;
        }
    }

    

    private boolean areTargetValuesAlreadyPresent() {
        try {
            String religionValue = safeGetText(L2_Info_Locators.RELIGION_SPINNER, Duration.ofSeconds(2));
            String educationValue = safeGetText(L2_Info_Locators.EDUCATION_SPINNER, Duration.ofSeconds(2));
            String residenceValue = safeGetText(L2_Info_Locators.RESIDENCE_SPINNER, Duration.ofSeconds(2));
            String ownershipValue = safeGetText(L2_Info_Locators.OWNERSHIP_PROOF_SPINNER, Duration.ofSeconds(2));

            boolean captureButtonsHidden = !isElementDisplayed(L2_Info_Locators.CAPTURE_FRONT_IMAGE_TEXT, Duration.ofMillis(500))
                && !isElementDisplayed(L2_Info_Locators.CAPTURE_BACK_IMAGE_TEXT, Duration.ofMillis(500));

            return textMatches(religionValue, "Hindu")
                && textMatches(educationValue, "Engineering Graduate")
                && textMatches(residenceValue, "Own House")
                && textMatches(ownershipValue, "Katha")
                && captureButtonsHidden;
        } catch (Exception e) {
            return false;
        }
    }

    private String safeGetText(By locator, Duration timeout) {
        try {
            WebElement element = new WebDriverWait(driver, timeout)
                .pollingEvery(Duration.ofMillis(250))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
            String text = element.getText();
            return text != null ? text.trim() : "";
        } catch (Exception ignored) {
            try {
                WebElement fallback = driver.findElement(locator);
                String text = fallback.getText();
                return text != null ? text.trim() : "";
            } catch (Exception inner) {
                return "";
            }
        }
    }

    private boolean textMatches(String actual, String expected) {
        if (actual == null || expected == null) {
            return false;
        }
        return actual.trim().equalsIgnoreCase(expected.trim());
    }

    private void swipeToCoordinates(int endX, int endY, Duration moveDuration) {
        try {
            Dimension size = driver.manage().window().getSize();
            int screenWidth = size.getWidth();
            int screenHeight = size.getHeight();
            int startX = Math.max(20, Math.min(screenWidth - 20, screenWidth / 2));
            int startY = (int) (screenHeight * 0.85);

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);
            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(moveDuration, PointerInput.Origin.viewport(), endX, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(swipe));
        } catch (Exception e) {
            System.err.println("[L2InfoActions] Coordinate swipe failed: " + e.getMessage());
        }
    }

    private boolean clickWithFallbacks(WebElement element) {
        try {
            element.click();
            return true;
        } catch (Exception primary) {
            try {
                int centerX = getElementCenterX(element);
                int centerY = getElementCenterY(element);
                PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
                Sequence tap = new Sequence(finger, 1);
                tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
                tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                tap.addAction(new Pause(finger, Duration.ofMillis(140)));
                tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                driver.perform(Collections.singletonList(tap));
                tinySleep();

                try {
                    new WebDriverWait(driver, Duration.ofMillis(700))
                        .ignoring(Exception.class)
                        .until(d -> {
                            try {
                                return !isDisplayed(element);
                            } catch (Exception ex) {
                                return true;
                            }
                        });
                } catch (Exception ignored) {
                }

                return true;
            } catch (Exception ignored) {
                return false;
            }
        }
    }

    private boolean isDisplayed(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCandidateClickable(WebElement element) {
        try {
            if (element == null) {
                return false;
            }

            String clickableAttr = element.getAttribute("clickable");
            String enabledAttr = element.getAttribute("enabled");

            boolean clickableAttrTrue = clickableAttr == null || clickableAttr.isEmpty() || clickableAttr.equalsIgnoreCase("true");
            boolean enabledAttrTrue = enabledAttr == null || enabledAttr.isEmpty() || enabledAttr.equalsIgnoreCase("true");

            boolean seleniumEnabled;
            try {
                seleniumEnabled = element.isEnabled();
            } catch (Exception inner) {
                seleniumEnabled = false;
            }

            return clickableAttrTrue && enabledAttrTrue && seleniumEnabled;
        } catch (Exception e) {
            return false;
        }
    }

    private int getElementCenterX(WebElement element) {
        org.openqa.selenium.Point loc = element.getLocation();
        return loc.getX() + element.getSize().getWidth() / 2;
    }

    private int getElementCenterY(WebElement element) {
        org.openqa.selenium.Point loc = element.getLocation();
        return loc.getY() + element.getSize().getHeight() / 2;
    }

    private boolean clickWhenClickable(By locator, Duration timeout) {
        try {
            WebElement element = new WebDriverWait(driver, timeout)
                .pollingEvery(Duration.ofMillis(250))
                .until(ExpectedConditions.elementToBeClickable(locator));
            return clickWithFallbacks(element);
        } catch (Exception e) {
            return false;
        }
    }

    private void hideKeyboardIfVisible() {
        boolean keyboardInitiallyVisible = isKeyboardCurrentlyShown();
        if (!keyboardInitiallyVisible) {
            return;
        }

        boolean hidden = attemptHideKeyboard();
        if (!hidden) {
            hidden = sendBackKeyToHideKeyboard();
        }

        if (!hidden) {
            System.err.println("[L2InfoActions] Warning: keyboard remained visible after hide attempts.");
        }
    }

    private boolean attemptHideKeyboard() {
        try {
            driver.hideKeyboard();
            WebDriverWait waitForDismiss = new WebDriverWait(driver, Duration.ofSeconds(2));
            waitForDismiss.pollingEvery(Duration.ofMillis(200));
            return Boolean.TRUE.equals(waitForDismiss.until(d -> !isKeyboardCurrentlyShown()));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean sendBackKeyToHideKeyboard() {
        try {
            driver.pressKey(new KeyEvent(AndroidKey.BACK));
            tinySleep();
            return !isKeyboardCurrentlyShown();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isKeyboardCurrentlyShown() {
        try {
            return driver.isKeyboardShown();
        } catch (Exception e) {
            return false;
        }
    }

    private void sleepSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ignored) {
        }
    }

    private void tinySleep() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException ignored) {
        }
    }
}
