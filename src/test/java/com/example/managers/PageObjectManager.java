package com.example.managers;

import com.example.pages.HomePage;
import com.example.pages.OnboardingPage;
import com.example.pages.PermissionPage;

import io.appium.java_client.android.AndroidDriver;

public class PageObjectManager {
    private final AndroidDriver driver;
    private PermissionPage permissionPage;
    private HomePage homePage;
    private OnboardingPage onboardingPage;

    public PageObjectManager(AndroidDriver driver) {
        this.driver = driver;
    }

    public PermissionPage getPermissionPage() {
        return (permissionPage == null) ? permissionPage = new PermissionPage(driver) : permissionPage;
    }

    public HomePage getHomePage() {
        return (homePage == null) ? homePage = new HomePage(driver) : homePage;
    }

    public OnboardingPage getOnboardingPage() {
        return (onboardingPage == null) ? onboardingPage = new OnboardingPage(driver) : onboardingPage;
    }
}
