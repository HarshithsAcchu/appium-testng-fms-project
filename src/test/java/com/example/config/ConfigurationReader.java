package com.example.config;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.openqa.selenium.By;

import com.example.config.models.LocatorConfig;
import com.example.config.models.TestConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.appium.java_client.MobileBy;

public class ConfigurationReader {
    private static ConfigurationReader instance;
    private TestConfig testConfig;
    private static final String DEFAULT_CONFIG_PATH = "test-config.json";

    private ConfigurationReader() {
        loadConfiguration(DEFAULT_CONFIG_PATH);
    }

    public static synchronized ConfigurationReader getInstance() {
        if (instance == null) {
            instance = new ConfigurationReader();
        }
        return instance;
    }

    private void loadConfiguration(String configPath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        try {
            // Try loading from classpath first
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configPath);
            if (inputStream != null) {
                try (Reader reader = new InputStreamReader(inputStream)) {
                    testConfig = gson.fromJson(reader, TestConfig.class);
                    System.out.println("[ConfigurationReader] Configuration loaded from classpath: " + configPath);
                }
            } else {
                // Fallback to file system
                try (Reader reader = new FileReader(configPath)) {
                    testConfig = gson.fromJson(reader, TestConfig.class);
                    System.out.println("[ConfigurationReader] Configuration loaded from file: " + configPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test configuration from: " + configPath, e);
        }
    }

    public TestConfig getTestConfig() {
        return testConfig;
    }

    /**
     * Get a locator by category and name from JSON configuration
     * 
     * @param category Locator category (e.g., "login", "home", "l2Info")
     * @param name Locator name (e.g., "userId", "password")
     * @return Selenium By locator
     */
    public By getLocator(String category, String name) {
        if (testConfig == null || testConfig.getLocators() == null) {
            throw new IllegalStateException("Test configuration not loaded");
        }

        var categoryLocators = testConfig.getLocators().get(category);
        if (categoryLocators == null) {
            throw new IllegalArgumentException("Locator category not found: " + category);
        }

        LocatorConfig locatorConfig = categoryLocators.get(name);
        if (locatorConfig == null) {
            throw new IllegalArgumentException("Locator not found: " + category + "." + name);
        }

        return buildLocator(locatorConfig);
    }

    /**
     * Build Selenium By locator from LocatorConfig
     */
    private By buildLocator(LocatorConfig config) {
        String value = config.getValue();
        
        switch (config.getLocatorType()) {
            case ID:
                return By.id(value);
            case XPATH:
                return By.xpath(value);
            case CLASS_NAME:
                return By.className(value);
            case ACCESSIBILITY_ID:
                return MobileBy.AccessibilityId(value);
            case ANDROID_UIAUTOMATOR:
                return MobileBy.AndroidUIAutomator(value);
            default:
                return By.xpath(value);
        }
    }

    /**
     * Get test data value by key from a specific test suite
     */
    public String getTestData(String suiteName, String key) {
        var suite = testConfig.getTestSuite(suiteName);
        if (suite != null) {
            return suite.getTestData(key);
        }
        return null;
    }

    /**
     * Get credentials from a test suite
     */
    public String getUsername(String suiteName) {
        var suite = testConfig.getTestSuite(suiteName);
        if (suite != null && suite.getCredentials() != null) {
            return suite.getCredentials().getUsername();
        }
        return null;
    }

    public String getPassword(String suiteName) {
        var suite = testConfig.getTestSuite(suiteName);
        if (suite != null && suite.getCredentials() != null) {
            return suite.getCredentials().getPassword();
        }
        return null;
    }

    /**
     * Get global setting value
     */
    public String getGlobalSetting(String key) {
        if (testConfig == null || testConfig.getGlobalSettings() == null) {
            return null;
        }

        switch (key.toLowerCase()) {
            case "appiumurl":
                return testConfig.getGlobalSettings().getAppiumUrl();
            case "platformname":
                return testConfig.getGlobalSettings().getPlatformName();
            case "devicename":
                return testConfig.getGlobalSettings().getDeviceName();
            case "apppackage":
                return testConfig.getGlobalSettings().getAppPackage();
            case "appactivity":
                return testConfig.getGlobalSettings().getAppActivity();
            default:
                return null;
        }
    }
}
