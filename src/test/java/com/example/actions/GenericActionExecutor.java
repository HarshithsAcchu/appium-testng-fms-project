package com.example.actions;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.config.ConfigurationReader;
import com.example.config.models.ActionStep;

import io.appium.java_client.android.AndroidDriver;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic executor for common mobile automation actions
 * All actions are driven by ActionStep objects from JSON
 */
public class GenericActionExecutor {
    
    private final AndroidDriver driver;
    private final ConfigurationReader configReader;
    private final WebDriverWait defaultWait;
    private Map<String, String> testData;
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{testData\\.([^}]+)\\}");

    public GenericActionExecutor(AndroidDriver driver) {
        this.driver = driver;
        this.configReader = ConfigurationReader.getInstance();
        this.defaultWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Set test data for placeholder resolution
     */
    public void setTestData(Map<String, String> testData) {
        this.testData = testData;
    }

    /**
     * Main entry point - executes an action based on ActionStep
     */
    public void executeAction(ActionStep actionStep) {
        String actionType = actionStep.getActionType();
        
        System.out.println("[GenericActionExecutor] Executing action: " + actionType + 
            (actionStep.getDescription() != null ? " - " + actionStep.getDescription() : ""));

        try {
            switch (actionType.toLowerCase()) {
                case "click":
                    performClick(actionStep);
                    break;
                case "sendkeys":
                case "entertext":
                    performSendKeys(actionStep);
                    break;
                case "clear":
                    performClear(actionStep);
                    break;
                case "scroll":
                    performScroll(actionStep);
                    break;
                case "scrolltoelement":
                    performScrollToElement(actionStep);
                    break;
                case "swipe":
                    performSwipe(actionStep);
                    break;
                case "wait":
                    performWait(actionStep);
                    break;
                case "waitforvisible":
                    performWaitForVisible(actionStep);
                    break;
                case "waitforclickable":
                    performWaitForClickable(actionStep);
                    break;
                case "tap":
                    performTap(actionStep);
                    break;
                case "longtap":
                    performLongTap(actionStep);
                    break;
                case "hidekeyboard":
                    driver.hideKeyboard();
                    break;
                case "goback":
                    driver.navigate().back();
                    break;
                default:
                    System.err.println("[GenericActionExecutor] Unknown action type: " + actionType);
            }
        } catch (Exception e) {
            if (actionStep.isOptional()) {
                System.out.println("[GenericActionExecutor] Optional action failed (continuing): " + e.getMessage());
            } else {
                throw new RuntimeException("Action failed: " + actionType + " - " + e.getMessage(), e);
            }
        }
    }

    /**
     * Click on an element
     */
    private void performClick(ActionStep step) {
        try {
            int timeout = step.getParamAsInt("timeout", 10);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            wait.pollingEvery(Duration.ofMillis(500)).ignoring(NoSuchElementException.class);
            
            By locator = getLocator(step);
            WebElement element;
            
            try {
                element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            } catch (TimeoutException te) {
                // Try with presence if clickable fails
                element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            }
            
            element.click();
            System.out.println("[GenericActionExecutor] Clicked element");
        } catch (Exception e) {
            System.err.println("[GenericActionExecutor] Click failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Enter text into an element
     */
    private void performSendKeys(ActionStep step) {
        WebElement element = findElement(step);
        String text = step.getValue();
        
        if (text == null || text.isEmpty()) {
            System.err.println("[GenericActionExecutor] No text provided for sendKeys");
            return;
        }
        
        // Resolve placeholder if present
        text = resolvePlaceholder(text);
        
        element.clear();
        element.sendKeys(text);
        System.out.println("[GenericActionExecutor] Entered text: " + text);
    }

    /**
     * Clear an element
     */
    private void performClear(ActionStep step) {
        WebElement element = findElement(step);
        element.clear();
        System.out.println("[GenericActionExecutor] Cleared element");
    }

    /**
     * Scroll down/up by specified amount
     */
    private void performScroll(ActionStep step) {
        String direction = step.getParamAsString("direction", "down");
        int distance = step.getParamAsInt("distance", 500);
        
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = size.height / 2;
        int endY = direction.equalsIgnoreCase("down") ? startY - distance : startY + distance;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 1);
        
        scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Collections.singletonList(scroll));
        System.out.println("[GenericActionExecutor] Scrolled " + direction + " by " + distance + "px");
    }

    /**
     * Scroll until element is visible
     */
    private void performScrollToElement(ActionStep step) {
        int maxScrolls = step.getParamAsInt("maxScrolls", 10);
        String direction = step.getParamAsString("direction", "down");
        By locator = getLocator(step);
        
        for (int i = 0; i < maxScrolls; i++) {
            try {
                // Check if element is visible in viewport (not just present in DOM)
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                WebElement element = shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                
                // Double-check it's actually displayed
                if (element != null && element.isDisplayed()) {
                    System.out.println("[GenericActionExecutor] Element '" + step.getLocatorName() + "' is visible after " + i + " scrolls");
                    return;
                }
            } catch (TimeoutException | NoSuchElementException e) {
                // Element not visible yet, continue scrolling
                System.out.println("[GenericActionExecutor] Element '" + step.getLocatorName() + "' not visible yet, scroll attempt " + (i + 1) + "/" + maxScrolls);
            }
            
            // Create temp action for scroll
            ActionStep scrollAction = new ActionStep();
            scrollAction.setActionType("scroll");
            scrollAction.setParams(step.getParams());
            performScroll(scrollAction);
        }
        
        throw new RuntimeException("Element '" + step.getLocatorName() + "' not found after " + maxScrolls + " scrolls");
    }

    /**
     * Swipe gesture
     */
    private void performSwipe(ActionStep step) {
        int startX = step.getParamAsInt("startX", 500);
        int startY = step.getParamAsInt("startY", 1000);
        int endX = step.getParamAsInt("endX", 500);
        int endY = step.getParamAsInt("endY", 500);
        int duration = step.getParamAsInt("duration", 600);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(duration), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Collections.singletonList(swipe));
        System.out.println("[GenericActionExecutor] Swiped from (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");
    }

    /**
     * Simple wait (sleep)
     */
    private void performWait(ActionStep step) {
        int milliseconds = step.getParamAsInt("milliseconds", 1000);
        try {
            Thread.sleep(milliseconds);
            System.out.println("[GenericActionExecutor] Waited " + milliseconds + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Wait for element to be visible
     */
    private void performWaitForVisible(ActionStep step) {
        int timeout = step.getParamAsInt("milliseconds", 10);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(timeout));
        By locator = getLocator(step);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        System.out.println("[GenericActionExecutor] Element became visible");
    }

    /**
     * Wait for element to be clickable
     */
    private void performWaitForClickable(ActionStep step) {
        int timeout = step.getParamAsInt("milliseconds", 10);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(timeout));
        By locator = getLocator(step);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        System.out.println("[GenericActionExecutor] Element became clickable");
    }

    /**
     * Tap at specific coordinates or on element
     */
    private void performTap(ActionStep step) {
        WebElement element = findElement(step);
        Point location = element.getLocation();
        Dimension size = element.getSize();
        
        int centerX = location.getX() + (size.getWidth() / 2);
        int centerY = location.getY() + (size.getHeight() / 2);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Collections.singletonList(tap));
        System.out.println("[GenericActionExecutor] Tapped at (" + centerX + "," + centerY + ")");
    }

    /**
     * Long tap (press and hold)
     */
    private void performLongTap(ActionStep step) {
        WebElement element = findElement(step);
        Point location = element.getLocation();
        Dimension size = element.getSize();
        
        int centerX = location.getX() + (size.getWidth() / 2);
        int centerY = location.getY() + (size.getHeight() / 2);
        int duration = step.getParamAsInt("duration", 2000);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longTap = new Sequence(finger, 1);
        
        longTap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
        longTap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longTap.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(duration)));
        longTap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Collections.singletonList(longTap));
        System.out.println("[GenericActionExecutor] Long tapped at (" + centerX + "," + centerY + ") for " + duration + "ms");
    }

    /**
     * Find element using locator from JSON
     */
    private WebElement findElement(ActionStep step) {
        return findElement(step, Duration.ofSeconds(10));
    }

    private WebElement findElement(ActionStep step, Duration timeout) {
        By locator = getLocator(step);
        System.out.println("[GenericActionExecutor] Finding element with locator " + locator);
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.pollingEvery(Duration.ofMillis(500));
        wait.ignoring(NoSuchElementException.class);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Get locator from configuration
     */
    private By getLocator(ActionStep step) {
        String category = step.getLocatorCategory();
        String name = step.getLocatorName();
        
        if (category == null || name == null) {
            throw new IllegalArgumentException("Locator category and name must be specified");
        }
        System.out.println("[GenericActionExecutor] Getting locator for " + category + ":" + name);
        return configReader.getLocator(category, name);
    }

    /**
     * Resolve ${testData.key} placeholders in values
     */
    private String resolvePlaceholder(String value) {
        if (value == null || !value.contains("${")) {
            return value;
        }
        
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = testData != null ? testData.get(key) : null;
            
            if (replacement != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
                System.out.println("[GenericActionExecutor] Resolved ${testData." + key + "} -> " + replacement);
            } else {
                System.err.println("[GenericActionExecutor] WARNING: No testData found for key: " + key);
                matcher.appendReplacement(result, Matcher.quoteReplacement(value));
            }
        }
        
        matcher.appendTail(result);
        return result.toString();
    }
}
