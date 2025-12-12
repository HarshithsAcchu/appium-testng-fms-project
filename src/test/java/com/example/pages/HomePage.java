package com.example.pages;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.locators.AppLocators;

import io.appium.java_client.android.AndroidDriver;

public class HomePage extends BasePage {

    private static final Duration LOGIN_WAIT = Duration.ofSeconds(15);   // slightly higher to allow animations
    private static final Duration ELEMENT_WAIT = Duration.ofSeconds(8);
    private static final Duration POLL_INTERVAL = Duration.ofMillis(300);

    private static final By USER_ID_LOCATOR = AppLocators.Login.USER_ID;
    private static final By PASSWORD_LOCATOR = AppLocators.Login.PASSWORD;
    private static final By SIGN_IN_LOCATOR = AppLocators.Login.SIGN_IN;
    private static final By SIGN_IN_FALLBACK = AppLocators.Login.SIGN_IN_FALLBACK;
    private static final By JLG_LOCATOR = AppLocators.Home.JLG_TEXT;

    public HomePage(AndroidDriver driver) {
        super(driver);
    }

    /**
     * Waits for the login screen to be actually visible and clickable.
     * Returns true when ready, false otherwise (and captures debug artifacts).
     */

    
    public boolean waitForLoginScreenReady() {
        long start = System.currentTimeMillis();
        try {
            WebDriverWait wait = new WebDriverWait(driver, LOGIN_WAIT);
            wait.pollingEvery(POLL_INTERVAL).ignoring(NoSuchElementException.class);

            wait.until(ExpectedConditions.visibilityOfElementLocated(USER_ID_LOCATOR));

            WebDriverWait shortWait = new WebDriverWait(driver, ELEMENT_WAIT);
            shortWait.pollingEvery(POLL_INTERVAL).ignoring(NoSuchElementException.class);
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_LOCATOR));

            try {
                shortWait.until(ExpectedConditions.elementToBeClickable(SIGN_IN_LOCATOR));
            } catch (TimeoutException te) {
                shortWait.until(ExpectedConditions.elementToBeClickable(SIGN_IN_FALLBACK));
            }

            long elapsed = System.currentTimeMillis() - start;
            System.out.println("[HomePage] Login screen ready (ms): " + elapsed);
            return true;

        } catch (Exception e) {
            System.err.println("[HomePage] waitForLoginScreenReady FAILED: " + e.getMessage());
            captureDebugArtifacts("login_screen_not_ready");
            return false;
        }
    }

    public void waitForJLGVisible() {
        new WebDriverWait(driver, ELEMENT_WAIT)
            .pollingEvery(POLL_INTERVAL)
            .ignoring(NoSuchElementException.class)
            .until(ExpectedConditions.visibilityOfElementLocated(JLG_LOCATOR));
    }

    public void clickJLG() {
        try {
            WebElement jlg = new WebDriverWait(driver, ELEMENT_WAIT)
                .pollingEvery(POLL_INTERVAL)
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(JLG_LOCATOR));
            jlg.click();
        } catch (Exception e) {
            captureDebugArtifacts("jlg_click_failed");
            throw new IllegalStateException("Failed to click JLG: " + e.getMessage(), e);
        }
    }

    public void enterUserId(String userId) {
        WebElement userField = new WebDriverWait(driver, ELEMENT_WAIT)
            .pollingEvery(POLL_INTERVAL)
            .ignoring(NoSuchElementException.class)
            .until(ExpectedConditions.visibilityOfElementLocated(USER_ID_LOCATOR));

        userField.clear();
        userField.sendKeys(userId);
    }

    public void enterPassword(String password) {
        WebElement pwdField = new WebDriverWait(driver, ELEMENT_WAIT)
            .pollingEvery(POLL_INTERVAL)
            .ignoring(NoSuchElementException.class)
            .until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_LOCATOR));

        pwdField.clear();
        pwdField.sendKeys(password);
    }

    public void clickSignInButton() {
        try {
            WebElement signIn;
            WebDriverWait shortWait = new WebDriverWait(driver, ELEMENT_WAIT);
            shortWait.pollingEvery(POLL_INTERVAL).ignoring(NoSuchElementException.class);

            try {
                signIn = shortWait.until(ExpectedConditions.elementToBeClickable(SIGN_IN_LOCATOR));
            } catch (TimeoutException te) {
                signIn = shortWait.until(ExpectedConditions.elementToBeClickable(SIGN_IN_FALLBACK));
            }

            signIn.click();
        } catch (Exception e) {
            captureDebugArtifacts("click_signin_failed");
            throw new IllegalStateException("Failed to click Sign In: " + e.getMessage(), e);
        }
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

            System.out.println("[HomePage] Debug artifacts saved: " + ps + ", " + sc);
        } catch (Exception ex) {
            System.err.println("[HomePage] Failed to save debug artifacts: " + ex.getMessage());
        }
    }
}
