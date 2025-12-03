package com.example.pages;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.locators.AppLocators;

import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;

public class OnboardingPage extends BasePage {
    private static final Duration LONG = Duration.ofSeconds(20);
    private static final Duration MEDIUM = Duration.ofSeconds(8);
    private static final Duration SHORT = Duration.ofSeconds(4);
    private static final Duration TINY = Duration.ofMillis(300);
    private static final int MAX_SCROLL_ATTEMPTS = 6;

    private static final By[] PRIMARY_LOCATORS = {
            AppLocators.Onboarding.VALIDATE_PARENT,
            AppLocators.Onboarding.VALIDATE_PARENT_FUZZY,
            AppLocators.Onboarding.VALIDATE_ACCESSIBILITY,
            AppLocators.Onboarding.VALIDATE_CLICKABLE,
            AppLocators.Onboarding.VALIDATE_RESOURCE,
            AppLocators.Onboarding.SUBMIT_RESOURCE,
            AppLocators.Onboarding.BOUNDS_FALLBACK,
            AppLocators.Onboarding.FIRST_CLICKABLE,
            AppLocators.Onboarding.VALIDATE_TEXT_GENERIC
    };

    private static final By[] RIGHT_ARROW_LOCATORS = {
            AppLocators.Onboarding.RIGHT_ARROW_LOCATOR,
            AppLocators.Onboarding.RIGHT_ARROW_LOCATOR_FUZZY,
            AppLocators.Onboarding.RIGHT_ARROW_IMAGE_FUZZY
    };

    private static final String[] SCROLL_CUES = {
            "Validate",
            "Validate Number",
            "SUBMIT"
    };

    private By lastUsedLocator = null;

    public OnboardingPage(AndroidDriver driver) {
        super(driver);
    }

    /*
    -------------------------
    Wait helpers
    ------------------------- */
    private WebElement waitVisible(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitClickable(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    private void tinySleep() {
        try {
            Thread.sleep(TINY.toMillis());
        } catch (InterruptedException ignored) {
        }
    }

    private WebElement locateEditTextWithoutAdditionalScroll(By locator) {
        String targetLabel = null;
        if (locator.equals(AppLocators.Onboarding.SCROLL_EDITTEXT_FIVE)) {
            targetLabel = "Enter Father Name";
        } else if (locator.equals(AppLocators.Onboarding.SCROLL_EDITTEXT_SIX)) {
            targetLabel = "Enter Mother Name";
        }
        if (targetLabel == null) {
            return null;
        }

        try {
            WebElement label = locateLabelElement(targetLabel);
            if (label != null) {
                WebElement resolved = resolveEditTextCandidate(locator, label);
                if (resolved != null) {
                    return resolved;
                }
            }
        } catch (Exception e) {
            System.err.println("[OnboardingPage] locateEditTextWithoutAdditionalScroll failed for '" + targetLabel + "': " + e.getMessage());
        }

        tinySleep();
        return firstDisplayed(driver.findElements(locator));
    }

    /*
    -------------------------
    Public flows
    ------------------------- */
    public boolean openCustomerDetailsScreen() {
        try {
            WebElement el = waitVisible(AppLocators.Onboarding.CUSTOMER_DETAILS_TEXT, LONG);
            el.click();
            return true;
        } catch (Exception e) {
            captureDebugArtifacts("open_customer_details_failure");
            return false;
        }
    }

    /**
     * Updated performCoordinateScroll: performs two swipes that end at the points
     * you requested. Interpretation: callers supply the two *end* coordinates as
     * (end1X, end1Y) and (end2X, end2Y) respectively. The method computes a
     * sensible start point near the bottom of the screen so the swipe moves
     * upward toward those targets. This avoids assuming a fixed device height.
     *
     * It logs progress and swallows exceptions (to avoid test crash) while
     * still printing helpful diagnostics.
     */
    private void performCoordinateScroll(int end1X, int end1Y, int end2X, int end2Y, Duration moveDuration) {
        try {
            Dimension size = driver.manage().window().getSize();
            int screenHeight = size.getHeight();
            int screenWidth = size.getWidth();

            // Choose start X near center to keep swipes natural unless end X is far off-screen
            int startX = Math.max(20, Math.min(screenWidth - 20, screenWidth / 2));
            // Start Y near bottom (85% of screen) to produce an upward swipe
            int startY = (int) (screenHeight * 0.85);

            // First swipe: from (startX, startY) -> (end1X, end1Y)
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe1 = new Sequence(finger, 1);
            swipe1.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            swipe1.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe1.addAction(finger.createPointerMove(moveDuration, PointerInput.Origin.viewport(), end1X, end1Y));
            swipe1.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(swipe1));
            System.out.println(String.format("[OnboardingPage] Performed coordinate scroll #1 to (%d,%d)", end1X, end1Y));

            // brief pause to let UI settle
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {
            }

            // Second swipe: from (startX, startY) -> (end2X, end2Y)
            Sequence swipe2 = new Sequence(finger, 1);
            swipe2.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            swipe2.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe2.addAction(finger.createPointerMove(moveDuration, PointerInput.Origin.viewport(), end2X, end2Y));
            swipe2.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(swipe2));
            System.out.println(String.format("[OnboardingPage] Performed coordinate scroll #2 to (%d,%d)", end2X, end2Y));

            // tiny settle
            try {
                Thread.sleep(350);
            } catch (InterruptedException ignored) {
            }

        } catch (Exception e) {
            System.err.println("[OnboardingPage] Coordinate scroll failed: " + e.getMessage());
        }
    }

    public void clickCaptureCustomerConsent() {
        try {
            WebElement consent = waitVisible(AppLocators.Onboarding.CAPTURE_CUSTOMER_CONSENT, LONG);
            if (!consent.isEnabled()) {
                captureDebugArtifacts("consent_disabled");
                throw new IllegalStateException("CAPTURE CUSTOMER CONSENT is disabled");
            }
            consent.click();
        } catch (Exception e) {
            captureDebugArtifacts("consent_click_failure");
            throw new IllegalStateException("Failed to click CAPTURE CUSTOMER CONSENT", e);
        }
    }

    public void completeVoterIdCaptureFlow() {
        try {
            // assume permissions already handled at test level; this focuses on capture & document steps
            // First capture attempt and crop
            clickWhenClickable(AppLocators.Onboarding.CAMERA_CAPTURE_BUTTON, MEDIUM);
            clickWhenClickable(AppLocators.Onboarding.CROP_BUTTON, MEDIUM);
            // Cancel and recapture
            clickWhenClickable(AppLocators.Onboarding.CAMERA_CANCEL_BUTTON, MEDIUM);
            clickWhenClickable(AppLocators.Onboarding.CAMERA_CAPTURE_BUTTON, MEDIUM);
            // Upload recent image
            clickWhenClickable(AppLocators.Onboarding.UPLOAD_RECENT_IMAGE_TEXT, MEDIUM);
            // Wait until third Compose button is enabled/visible
            WebElement composeBtn = waitClickable(AppLocators.Onboarding.COMPOSE_THIRD_BUTTON, LONG);
            composeBtn.click();
            // Select document type
            clickWhenClickable(AppLocators.Onboarding.SELECT_DOCUMENT_SPINNER, MEDIUM);
            clickWhenClickable(AppLocators.Onboarding.DOCUMENT_FIRST_OPTION, MEDIUM);
            // Choose "Capture Front image"
            clickWhenClickable(AppLocators.Onboarding.CAPTURE_FRONT_IMAGE_TEXT, MEDIUM);
            // Two generic Compose clicks
            clickWhenClickable(AppLocators.Onboarding.COMPOSE_GENERIC_BUTTON, MEDIUM);
            clickWhenClickable(AppLocators.Onboarding.COMPOSE_GENERIC_BUTTON, MEDIUM);
            // Enter voter id
            WebElement voterField = waitVisible(AppLocators.Onboarding.VOTER_ID_INPUT, LONG);
            String voterId = buildRandomVoterId();
            voterField.click();
            voterField.clear();
            voterField.sendKeys(voterId);
            System.out.println("[OnboardingPage] Entered voter id: " + voterId);
            clickWithLogging(AppLocators.Onboarding.SUBMIT_SECTION_BUTTON, "Voter details SUBMIT button", MEDIUM, false);
            WebElement composeSecond = waitUntilEnabledWithLogging(AppLocators.Onboarding.COMPOSE_SECOND_BUTTON, LONG, "Compose button (index 2)");
            if (composeSecond != null) {
                clickElementWithLogging(composeSecond, "Compose button (index 2)");
            }
            tinySleep();

            // Perform the two specific coordinate swipes you requested and then attempt to scroll-to-field
            // Interpret the call as: end1=(472,705), end2=(467,893)
            performCoordinateScroll(472, 705, 467, 893, Duration.ofMillis(450));
            tinySleep();

            boolean fatherFilled = enterTextWithLogging(AppLocators.Onboarding.SCROLL_EDITTEXT_FIVE, "Father name field", "darshan", false);
            boolean motherFilled = enterTextWithLogging(AppLocators.Onboarding.SCROLL_EDITTEXT_SIX, "Mother name field", "saroja", false);
            if (!fatherFilled) {
                throw new IllegalStateException("Failed to populate father name field");
            }
            if (!motherFilled) {
                throw new IllegalStateException("Failed to populate mother name field");
            }

            clickWithLogging(AppLocators.Onboarding.SCROLL_RADIO_BUTTON_THREE, "Radio option (view[3])", MEDIUM, true);
            clickWithLogging(AppLocators.Onboarding.COMPOSE_GENERIC_BUTTON, "Compose submit button (1)", MEDIUM, false);
            clickWithLogging(AppLocators.Onboarding.SCROLL_VIEW2_INNER, "Scroll view[2] inner", MEDIUM, true);
            clickWithLogging(AppLocators.Onboarding.COMPOSE_GENERIC_BUTTON, "Compose submit button (2)", MEDIUM, false);
            clickWithLogging(AppLocators.Onboarding.COMPOSE_SECOND_BUTTON, "Compose button (index 2) repeat", MEDIUM, false);
            clickWithLogging(AppLocators.Onboarding.COMPOSE_GENERIC_BUTTON, "Compose submit button (3)", MEDIUM, false);
            clickWithLogging(AppLocators.Onboarding.SCROLL_VIEW3_INNER, "Scroll view[3] inner", MEDIUM, true);
            clickWithLogging(AppLocators.Onboarding.COMPOSE_GENERIC_BUTTON, "Compose submit button (4)", MEDIUM, false);
            clickWithLogging(AppLocators.Onboarding.COMPOSE_GENERIC_BUTTON, "Compose submit button (5)", MEDIUM, false);
            clickWithLogging(AppLocators.Onboarding.COMPOSE_GENERIC_BUTTON, "Compose submit button (6)", MEDIUM, false);
            clickWithLogging(AppLocators.Onboarding.SCROLL_VIEW4_SECTION, "Scroll view[4] section", MEDIUM, true);
            clickWithLogging(AppLocators.Onboarding.COMPOSE_NESTED_SECOND_BUTTON, "Compose nested button", MEDIUM, true);
            clickWithLogging(AppLocators.Onboarding.SCROLL_VIEW7_BUTTON, "Scroll view[7] button", MEDIUM, true);

            if (waitForOptionalVisibility(AppLocators.Onboarding.SUCCESSFULLY_CAPTURED_MESSAGE, Duration.ofSeconds(5))) {
                System.out.println("[OnboardingPage] Successfully Captured message detected.");
                clickWithLogging(AppLocators.Onboarding.COMPOSE_GENERIC_BUTTON, "Compose button after success message", MEDIUM, false);
            } else {
                System.out.println("[OnboardingPage] Successfully Captured message not shown; proceeding without extra click.");
            }
        } catch (Exception e) {
            captureDebugArtifacts("voter_id_capture_flow_failure");
            throw new IllegalStateException("Failed during voter-id capture flow", e);
        }
    }

    @SuppressWarnings("deprecation")
    public String enterMobileNumberAndSubmit() {
        try {
            String mobile = generateRandomMobileNumber();
            System.out.println("[OnboardingPage] Generated mobile = " + mobile);
            WebElement input = waitVisible(AppLocators.Onboarding.MOBILE_INPUT_BY_LABEL, LONG);
            System.out.println("[OnboardingPage] Mobile EditText found. entering number.");
            input.click();
            input.clear();
            input.sendKeys(mobile);
            ensureValueRetained(input);
            hideKeyboardIfVisible();
            tinySleep();
            clickPrimaryButtonOrThrow();
            if (!clickRightArrowIfPresent()) {
                System.out.println("[OnboardingPage] Right arrow not available after Validate Number; continuing.");
            }
            return mobile;
        } catch (Exception e) {
            captureDebugArtifacts("enter_mobile_failure");
            throw new IllegalStateException("Could not enter mobile number", e);
        }
    }

    /*
    -------------------------
    Primary button handling
    ------------------------- */
    private void clickPrimaryButtonOrThrow() {
        WebElement button = locatePrimaryButton();
        if (button == null) {
            captureDebugArtifacts("primary_button_not_found");
            throw new IllegalStateException("Primary button not found");
        }
        if (!button.isDisplayed()) {
            captureDebugArtifacts("primary_button_not_displayed");
            throw new IllegalStateException("Primary button not displayed");
        }
        if (!button.isEnabled()) {
            captureDebugArtifacts("primary_button_disabled");
            throw new IllegalStateException("Primary button disabled");
        }
        boolean ok = clickWithFallbacks(button);
        if (!ok) {
            captureDebugArtifacts("primary_button_click_failed");
            throw new IllegalStateException("Primary button click failed after retries");
        }
    }

    private WebElement locatePrimaryButton() {
        lastUsedLocator = null;
        // 1) Try direct locators
        for (By loc : PRIMARY_LOCATORS) {
            try {
                WebElement e = waitClickable(loc, MEDIUM);
                if (e != null) {
                    lastUsedLocator = loc;
                    return e;
                }
            } catch (Exception ignored) {
            }
        }

        // 2) Try scrolling cues and re-check locators
        for (String cue : SCROLL_CUES) {
            if (scrollIntoView(cue)) {
                for (By loc : PRIMARY_LOCATORS) {
                    try {
                        WebElement e = waitClickable(loc, SHORT);
                        if (e != null) {
                            lastUsedLocator = loc;
                            return e;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // 3) Last resort: first clickable element
        try {
            WebElement e = waitClickable(AppLocators.Onboarding.FIRST_CLICKABLE, SHORT);
            lastUsedLocator = AppLocators.Onboarding.FIRST_CLICKABLE;
            return e;
        } catch (Exception ignored) {
        }
        return null;
    }

    public boolean clickRightArrowIfPresent() {
        hideKeyboardIfVisible();
        tinySleep();
        WebElement arrow = locateRightArrowElement();
        if (arrow == null && scrollIntoView("Right Arrow")) {
            arrow = locateRightArrowElement();
        }
        if (arrow == null) {
            return false;
        }
        if (clickWithFallbacks(arrow)) {
            waitPostClick(arrow);
            return true;
        }
        System.err.println("[OnboardingPage] right-arrow click fallbacks failed");
        return false;
    }

    private WebElement locateRightArrowElement() {
        Duration[] timeouts = {Duration.ofSeconds(7), Duration.ofSeconds(5), Duration.ofSeconds(3)};
        for (int i = 0; i < RIGHT_ARROW_LOCATORS.length; i++) {
            try {
                WebElement element = waitClickable(RIGHT_ARROW_LOCATORS[i], timeouts[i]);
                if (element != null) {
                    lastUsedLocator = RIGHT_ARROW_LOCATORS[i];
                    return element;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private boolean waitForOptionalVisibility(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .pollingEvery(Duration.ofMillis(300))
                    .ignoring(NoSuchElementException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void clickWhenClickable(By locator, Duration timeout) {
        WebElement element = waitClickable(locator, timeout);
        clickWithFallbacks(element);
    }

    private boolean tryValidateBoundsFallback() {
        try {
            WebElement fallback = waitClickable(AppLocators.Onboarding.BOUNDS_FALLBACK, Duration.ofSeconds(1));
            if (fallback != null) {
                System.out.println("[OnboardingPage] clicking clickable view in validate bounds as fallback.");
                clickWithFallbacks(fallback);
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private void handleDialogsIfPresent() {
        try {
            if (isElementPresent(AppLocators.Onboarding.DIALOG_GENERIC)) {
                System.out.println("[OnboardingPage] dialog/popup detected - capturing artifacts and trying to accept.");
                captureDebugArtifacts("otp_dialog_detected");
                tryClickByTextFuzzy("ALLOW");
                tryClickByTextFuzzy("OK");
                tryClickByTextFuzzy("YES");
            }
        } catch (Exception ignored) {
        }
    }

    private void logToastIfPresent() {
        try {
            if (isElementPresent(AppLocators.Onboarding.TOAST_GENERIC)) {
                System.out.println("[OnboardingPage] toast present - capture page source");
                captureDebugArtifacts("otp_toast_present");
            }
        } catch (Exception ignored) {
        }
    }

    private boolean isElementPresent(By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void tryClickByTextFuzzy(String text) {
        String upper = text.toUpperCase();
        By by = By.xpath("//*[contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'" + upper + "') "
                + "or contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'" + upper + "')]");
        try {
            List<WebElement> elements = driver.findElements(by);
            for (WebElement el : elements) {
                if (el.isDisplayed() && el.isEnabled()) {
                    clickWithFallbacks(el);
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void smallSwipeUp() {
        try {
            WebElement scrollContainer = firstDisplayed(driver.findElements(By.className("android.widget.ScrollView")));
            int startX;
            int startY;
            int endY;
            if (scrollContainer != null) {
                Point origin = scrollContainer.getLocation();
                Dimension size = scrollContainer.getSize();
                startX = origin.getX() + size.getWidth() / 2;
                startY = origin.getY() + (int) (size.getHeight() * 0.75);
                endY = origin.getY() + (int) (size.getHeight() * 0.35);
            } else {
                int width = driver.manage().window().getSize().getWidth();
                int height = driver.manage().window().getSize().getHeight();
                startX = width / 2;
                startY = (int) (height * 0.7);
                endY = (int) (height * 0.4);
            }
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);
            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), startX, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(swipe));
        } catch (Exception ignored) {
        }
    }

    private void smallSwipeDown() {
        try {
            WebElement scrollContainer = firstDisplayed(driver.findElements(By.className("android.widget.ScrollView")));
            int startX;
            int startY;
            int endY;
            if (scrollContainer != null) {
                Point origin = scrollContainer.getLocation();
                Dimension size = scrollContainer.getSize();
                startX = origin.getX() + size.getWidth() / 2;
                startY = origin.getY() + (int) (size.getHeight() * 0.35);
                endY = origin.getY() + (int) (size.getHeight() * 0.75);
            } else {
                int width = driver.manage().window().getSize().getWidth();
                int height = driver.manage().window().getSize().getHeight();
                startX = width / 2;
                startY = (int) (height * 0.4);
                endY = (int) (height * 0.7);
            }
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);
            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), startX, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(swipe));
        } catch (Exception ignored) {
        }
    }

    private boolean clickWithLogging(By locator, String label, Duration timeout, boolean attemptScroll) {
        try {
            WebElement element = null;
            if (attemptScroll) {
                element = scrollToElement(locator, MAX_SCROLL_ATTEMPTS);
            }
            if (element == null) {
                element = findElementForInteraction(locator, timeout);
            }
            if (element == null) {
                logActionResult(label, false);
                return false;
            }
            boolean ok = clickWithFallbacks(element);
            logActionResult(label, ok);
            return ok;
        } catch (Exception e) {
            System.err.println("[OnboardingPage] Click failed for " + label + ": " + e.getMessage());
            logActionResult(label, false);
            return false;
        }
    }

    private WebElement findElementForInteraction(By locator, Duration timeout) {
        try {
            return waitClickable(locator, timeout);
        } catch (Exception ignored) {
            try {
                return waitVisible(locator, timeout);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private void logActionResult(String label, boolean success) {
        String prefix = success ? "[OnboardingPage] ✅ " : "[OnboardingPage] ❌ ";
        if (success) {
            System.out.println(prefix + label);
        } else {
            System.err.println(prefix + label);
        }
    }

    private WebElement waitUntilEnabledWithLogging(By locator, Duration timeout, String label) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            try {
                WebElement candidate = firstDisplayed(driver.findElements(locator));
                if (candidate != null && candidate.isEnabled()) {
                    System.out.println("[OnboardingPage] " + label + " enabled; proceeding.");
                    return candidate;
                }
            } catch (Exception ignored) {
            }
            sleepMillis(300);
        }
        System.err.println("[OnboardingPage] " + label + " did not enable within timeout.");
        return null;
    }

    private boolean clickElementWithLogging(WebElement element, String label) {
        if (element == null) {
            logActionResult(label, false);
            return false;
        }
        try {
            boolean ok = clickWithFallbacks(element);
            logActionResult(label, ok);
            return ok;
        } catch (Exception e) {
            System.err.println("[OnboardingPage] Click failed for " + label + ": " + e.getMessage());
            logActionResult(label, false);
            return false;
        }
    }

    private boolean enterTextWithLogging(By locator, String label, String value) {
        return enterTextWithLogging(locator, label, value, true);
    }

    private boolean enterTextWithLogging(By locator, String label, String value, boolean allowAutoScroll) {
        try {
            WebElement field = firstDisplayed(driver.findElements(locator));
            if (field == null && !allowAutoScroll) {
                field = locateEditTextWithoutAdditionalScroll(locator);
            }
            if (field == null) {
                if (allowAutoScroll) {
                    field = scrollToElement(locator, MAX_SCROLL_ATTEMPTS);
                }
                if (field == null) {
                    try {
                        field = waitVisible(locator, MEDIUM);
                    } catch (Exception ignored) {
                    }
                }
            }
            if (field == null) {
                System.err.println("[OnboardingPage] Unable to locate " + label + " for text entry.");
                logActionResult(label + " text entry", false);
                return false;
            }

            String focusedAttr = field.getAttribute("focused");
            boolean alreadyFocused = focusedAttr != null && focusedAttr.equalsIgnoreCase("true");
            if (!alreadyFocused) {
                field.click();
                tinySleep();
            }

            field.clear();
            field.sendKeys(value);
            hideKeyboardIfVisible();
            tinySleep();
            System.out.println("[OnboardingPage] Entered '" + value + "' into " + label + ".");
            logActionResult(label + " text entry", true);
            return true;
        } catch (Exception e) {
            System.err.println("[OnboardingPage] Failed to enter text into " + label + ": " + e.getMessage());
            logActionResult(label + " text entry", false);
            return false;
        }
    }

    private WebElement scrollToElement(By locator, int maxScrolls) {
        // Special-case: handle the two "Enter Father/Mother Name" edittexts more reliably.
        if (locator.equals(AppLocators.Onboarding.SCROLL_EDITTEXT_FIVE)
            || locator.equals(AppLocators.Onboarding.SCROLL_EDITTEXT_SIX)) {
            String labelText = locator.equals(AppLocators.Onboarding.SCROLL_EDITTEXT_FIVE)
                ? "Enter Father Name"
                : "Enter Mother Name";
            try {
                WebElement resolved = scrollToEditTextByLabel(labelText, locator, maxScrolls);
                if (resolved != null) {
                    return resolved;
                }
            } catch (Exception e) {
                System.err.println("[OnboardingPage] special-case scroll attempt failed: " + e.getMessage());
            }
            System.err.println("[OnboardingPage] Unable to surface field '" + labelText + "' after special-case attempts. Falling back to generic scroll heuristics.");
        }

        // Generic existing behavior (keeps your heuristics)
        Dimension screen = driver.manage().window().getSize();
        int height = screen.getHeight();

        boolean coordinateFallbackUsed = false;
        for (int i = 0; i < maxScrolls; i++) {
            List<WebElement> elements = driver.findElements(locator);
            WebElement displayed = firstDisplayed(elements);
            if (displayed != null) {
                if (isElementWithinViewport(displayed, height)) {
                    return displayed;
                }
                int centerY = displayed.getLocation().getY() + displayed.getSize().getHeight() / 2;
                if (centerY > height * 0.85) {
                    // element is below viewport - do a small swipe up to bring it into view
                    smallSwipeUp();
                } else if (centerY < height * 0.15) {
                    // element is above viewport - swipe down
                    smallSwipeDown();
                } else {
                    return displayed;
                }
            } else {
                // Prefer a gentle swipe before resorting to coordinate fallback
                smallSwipeUp();
                if (!coordinateFallbackUsed) {
                    performCoordinateScroll(472, 705, 467, 893, Duration.ofMillis(450));
                    coordinateFallbackUsed = true;
                }
            }
            tinySleep();
        }
        return null;
    }

    // Attempts multiple strategies to bring the EditText labeled by `labelText` into view.
    // Returns true if it either found the EditText in DOM or at least detected the label so caller can re-query.
    private WebElement scrollToEditTextByLabel(String labelText, By editLocator, int maxScrolls) {
        WebElement fromLocator = firstDisplayed(driver.findElements(editLocator));
        if (fromLocator != null) {
            return fromLocator;
        }

        WebElement viaUiScrollable = tryUiScrollableForText(labelText, editLocator);
        if (viaUiScrollable != null) {
            return viaUiScrollable;
        }

        WebElement label = locateLabelElement(labelText);
        if (label != null) {
            try {
                int labelY = label.getLocation().getY();
                int screenHeight = driver.manage().window().getSize().getHeight();
                if (labelY > screenHeight * 0.85) {
                    smallSwipeUp();
                } else if (labelY < screenHeight * 0.15) {
                    smallSwipeDown();
                } else {
                    tapAtLabelCenter(label);
                }

                WebElement resolved = resolveEditTextCandidate(editLocator, label);
                if (resolved != null) {
                    return resolved;
                }
            } catch (Exception e) {
                System.err.println("[OnboardingPage] attempt to act on label coordinates failed: " + e.getMessage());
            }
        }

        int coordinateAttempts = Math.max(1, Math.min(2, maxScrolls));
        for (int i = 0; i < coordinateAttempts; i++) {
            performCoordinateScroll(472, 705, 467, 893, Duration.ofMillis(450));
            tinySleep();
            WebElement resolved = resolveEditTextCandidate(editLocator, label);
            if (resolved != null) {
                return resolved;
            }
        }

        String[] tokens = labelText.split(" ");
        if (tokens.length > 0) {
            WebElement fuzzy = tryUiScrollableForText(tokens[0], editLocator);
            if (fuzzy != null) {
                return fuzzy;
            }
        }

        return null;
    }

    private WebElement resolveEditTextCandidate(By editLocator, WebElement label) {
        WebElement fromLocator = firstDisplayed(driver.findElements(editLocator));
        if (fromLocator != null) {
            return fromLocator;
        }
        if (label == null) {
            return null;
        }
        List<String> relativePaths = Arrays.asList(
            "following-sibling::android.widget.EditText[1]",
            "ancestor::android.view.ViewGroup[1]//android.widget.EditText[1]",
            "following::android.widget.EditText[1]");
        for (String path : relativePaths) {
            try {
                WebElement candidate = label.findElement(By.xpath(path));
                if (candidate != null) {
                    System.out.println("[OnboardingPage] Located edit field via label-relative path: " + path);
                    return candidate;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private void tapAtLabelCenter(WebElement label) {
        Point loc = label.getLocation();
        int centerX = loc.getX() + label.getSize().getWidth() / 2;
        int centerY = loc.getY() + label.getSize().getHeight() / 2;
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(60)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(java.util.Collections.singletonList(tap));
        tinySleep();
    }

    private WebElement tryUiScrollableForText(String targetText, By editLocator) {
        if (targetText == null || targetText.isBlank()) {
            return null;
        }
        try {
            String ui = String.format(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains(\"%s\"))",
                targetText);
            driver.findElement(MobileBy.AndroidUIAutomator(ui));
            tinySleep();
            return firstDisplayed(driver.findElements(editLocator));
        } catch (Exception e) {
            System.err.println("[OnboardingPage] UiScrollable scrollIntoView failed for '" + targetText + "': " + e.getMessage());
            return null;
        }
    }

    private WebElement locateLabelElement(String labelText) {
        List<By> labelCandidates = new ArrayList<>();
        String upper = labelText.toUpperCase(Locale.ROOT);
        labelCandidates.add(By.xpath("//android.widget.TextView[@text='" + labelText + "']"));
        labelCandidates.add(By.xpath("//android.widget.TextView[contains(@text,'" + labelText + "')]"));
        labelCandidates.add(By.xpath("//android.widget.TextView[contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'" + upper + "')]"));
        labelCandidates.add(By.xpath("//android.widget.TextView[contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'" + upper + "')]"));

        for (By candidate : labelCandidates) {
            try {
                WebElement label = firstDisplayed(driver.findElements(candidate));
                if (label != null) {
                    return label;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private boolean isElementWithinViewport(WebElement element, int viewportHeight) {
        try {
            int top = element.getLocation().getY();
            int bottom = top + element.getSize().getHeight();
            return top >= 0 && bottom <= viewportHeight;
        } catch (Exception e) {
            System.err.println("[OnboardingPage] Unable to compute element viewport bounds: " + e.getMessage());
            return false;
        }
    }

    private WebElement firstDisplayed(List<WebElement> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        for (WebElement element : candidates) {
            try {
                if (element != null && element.isDisplayed()) {
                    return element;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    private String describeElement(WebElement element) {
        try {
            return String.format("class=%s text='%s'\ndesc='%s' bounds=%s",
                    element.getAttribute("class"),
                    element.getText(),
                    element.getAttribute("content-desc"),
                    element.getAttribute("bounds"));
        } catch (Exception ex) {
            return "<unavailable>";
        }
    }

    private String buildRandomVoterId() {
        // Format: 3 uppercase letters + 7 digits, e.g. ABC1234567
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < 3; i++) {
            sb.append(letters.charAt(rnd.nextInt(letters.length())));
        }
        int digits = rnd.nextInt(1_000_000, 10_000_000); // 7 digits
        sb.append(digits);
        return sb.toString();
    }

    private void ensureValueRetained(WebElement input) {
        new WebDriverWait(driver, SHORT).until(d -> {
            String value = input.getText();
            return value != null && !value.isBlank();
        });
    }

    private WebElement refreshIfPossible(WebElement fallback) {
        if (lastUsedLocator != null) {
            try {
                return waitClickable(lastUsedLocator, SHORT);
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }

    private boolean scrollIntoView(String text) {
        String ui = String.format("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains(\"%s\"))", text);
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(ui));
            // stabilize
            tinySleep();
            // verify presence of any matching text node
            waitVisible(buildCaseInsensitiveTextLocator(text), SHORT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean scrollIntoView(By locator) {
        return scrollToElement(locator, MAX_SCROLL_ATTEMPTS) != null;
    }

    /* -------------------------
       Click fallbacks (re-find before each fallback)
    ------------------------- */
    private boolean clickWithFallbacks(WebElement original) {
        hideKeyboardIfVisible();
        tinySleep();
        // Attempt 1: standard click (try re-finding the element)
        try {
            WebElement current = refreshIfPossible(original);
            if (current == null)
                current = original;
            new WebDriverWait(driver, SHORT).until(ExpectedConditions.elementToBeClickable(current));
            current.click();
            waitPostClick(current);
            return true;
        } catch (Exception e) {
            System.err.println("standard click failed: " + e.getMessage());
        }
        // Attempt 2: JS click (re-find)
        try {
            WebElement current = refreshIfPossible(original);
            if (current == null)
                return false;
            if (driver instanceof JavascriptExecutor) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", current);
                waitPostClick(current);
                return true;
            }
        } catch (Exception e) {
            System.err.println("js click failed: " + e.getMessage());
        }
        // Attempt 3: pointer tap at center (re-find)
        try {
            WebElement current = refreshIfPossible(original);
            if (current == null) return false;
            Point loc = current.getLocation();
            int centerX = loc.getX() + current.getSize().getWidth() / 2;
            int centerY = loc.getY() + current.getSize().getHeight() / 2;
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(new Pause(finger, Duration.ofMillis(80)));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            List<Sequence> actions = new ArrayList<>();
            actions.add(tap);
            driver.perform(actions);
            waitPostClick(current);
            return true;
        } catch (Exception e) {
            System.err.println("pointer tap failed: " + e.getMessage());
        }
        return false;
    }

    private void waitPostClick(WebElement element) {
        try {
            new WebDriverWait(driver, SHORT)
                    .until(ExpectedConditions.or(
                            ExpectedConditions.invisibilityOf(element),
                            ExpectedConditions.stalenessOf(element)
                    ));
        } catch (TimeoutException ignored) {
            // not fatal — continue
        }
    }

    private By buildCaseInsensitiveTextLocator(String cue) {
        String u = cue.toUpperCase();
        return By.xpath(String.format(
                "//*[contains(translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'%s') or contains(translate(@content-desc,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'%s')]",
                u, u));
    }

    private void hideKeyboardIfVisible() {
        try {
            driver.hideKeyboard();
            // try a brief wait for keyboard to go away
            new WebDriverWait(driver, SHORT)
                    .until(d -> {
                        try {
                            return !driver.isKeyboardShown();
                        } catch (Exception ex) {
                            return true;
                        }
                    });
        } catch (Exception ignored) {
        }
    }

    private String generateRandomMobileNumber() {
        long base = 8_000_000_000L;
        long offset = ThreadLocalRandom.current().nextLong(0, 1_000_000_000L);
        return String.valueOf(base + offset);
    }

    private void captureDebugArtifacts(String prefix) {
        try {
            Path dir = Paths.get("target");
            Files.createDirectories(dir);
            String ps = prefix + "_pagesource_" + UUID.randomUUID() + ".xml";
            Files.writeString(dir.resolve(ps), driver.getPageSource(), StandardCharsets.UTF_8);
            File screenshot = driver.getScreenshotAs(OutputType.FILE);
            String sc = prefix + "_screenshot_" + UUID.randomUUID() + ".png";
            Files.copy(screenshot.toPath(), dir.resolve(sc));
            System.out.println("[OnboardingPage] Debug artifacts saved: " + ps + ", " + sc);
        } catch (Exception ex) {
            System.err.println("[OnboardingPage] Failed to save debug artifacts: " + ex.getMessage());
        }
    }
}
