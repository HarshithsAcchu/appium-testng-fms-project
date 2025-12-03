package com.example.pages;

import java.util.List;

import org.openqa.selenium.WebElement;

import io.appium.java_client.android.AndroidDriver;

import com.example.locators.AppLocators;

public class PermissionPage extends BasePage {
    public PermissionPage(AndroidDriver driver) {
        super(driver);
    }

    private static final int MAX_PERMISSIONS = 6;

    public void handlePermissions() {
        int dismissed = 0;
        for (int i = 0; i < MAX_PERMISSIONS; i++) {
            WebElement allowElement = findAllowButton();
            if (allowElement == null) {
                if (dismissed == 0) {
                    System.out.println("[PermissionPage] No permission dialogs detected; continuing.");
                }
                break;
            }

            try {
                allowElement.click();
                dismissed++;
                Thread.sleep(300);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[PermissionPage] Failed to click permission dialog: " + e.getMessage());
                break;
            }
        }

        if (dismissed > 0) {
            System.out.println("[PermissionPage] Dismissed " + dismissed + " permission dialog(s).");
        }
    }

    private WebElement findAllowButton() {
        for (String id : AppLocators.Permissions.ALLOW_BUTTON_IDS) {
            WebElement candidate = firstDisplayed(driver.findElements(AppLocators.byId(id)));
            if (candidate != null) {
                return candidate;
            }
        }

        for (String text : AppLocators.Permissions.ALLOW_BUTTON_TEXTS) {
            WebElement candidate = firstDisplayed(driver.findElements(AppLocators.permissionsButtonByText(text)));
            if (candidate != null) {
                return candidate;
            }
        }

        WebElement fallback = firstDisplayed(driver.findElements(AppLocators.Permissions.ALLOW_BUTTON_RESOURCE_CONTAINS));
        return fallback;
    }

    private WebElement firstDisplayed(List<WebElement> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        for (WebElement element : candidates) {
            try {
                if (element != null && element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
            } catch (Exception ignored) { }
        }
        return candidates.get(0);
    }
}
