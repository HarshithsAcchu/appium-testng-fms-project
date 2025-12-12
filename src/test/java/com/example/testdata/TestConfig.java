package com.example.testdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized test configuration loader.
 * Reads values from config.properties and provides static access to all test data.
 * Environment variables override properties file values when set.
 */
public final class TestConfig {

    private static final Properties props = new Properties();
    private static boolean loaded = false;

    // ===========================================
    // Login Credentials
    // ===========================================
    public static String USERNAME;
    public static String PASSWORD;

    // ===========================================
    // Appium Server Configuration
    // ===========================================
    public static String APPIUM_SERVER_URL;

    // ===========================================
    // Device Configuration
    // ===========================================
    public static String DEVICE_NAME;
    public static String DEVICE_UDID;

    // ===========================================
    // App Configuration
    // ===========================================
    public static String APP_PACKAGE;
    public static String APP_ACTIVITY;

    // ===========================================
    // Timeout Configuration
    // ===========================================
    public static int TIMEOUT_COMMAND;
    public static int TIMEOUT_IMPLICIT;

    // ===========================================
    // Test Data - Bank Details
    // ===========================================
    public static String BANK_ACCOUNT_NUMBER;
    public static String BANK_IFSC;

    // ===========================================
    // Test Data - Swipe Coordinates
    // ===========================================
    public static int SWIPE_X;
    public static int SWIPE_Y;

    // ===========================================
    // Debug Settings
    // ===========================================
    public static boolean DEBUG_LOGS_ENABLED;

    static {
        loadConfig();
    }

    private TestConfig() {
        // Prevent instantiation
    }

    /**
     * Loads configuration from properties file.
     * Environment variables take precedence over properties file values.
     */
    public static synchronized void loadConfig() {
        if (loaded) {
            return;
        }

        try (InputStream input = TestConfig.class.getClassLoader()
                .getResourceAsStream("testdata/config.properties")) {
            if (input == null) {
                System.err.println("[TestConfig] Unable to find config.properties, using defaults.");
            } else {
                props.load(input);
                System.out.println("[TestConfig] Loaded config.properties successfully.");
            }
        } catch (IOException e) {
            System.err.println("[TestConfig] Error loading config.properties: " + e.getMessage());
        }

        // Login Credentials (using TEST_ prefix to avoid conflict with Windows USERNAME env var)
        USERNAME = getEnvOrProperty("TEST_USERNAME", "username", "us-sh-shc-60507");
        PASSWORD = getEnvOrProperty("TEST_PASSWORD", "password", "Nst@1234");

        // Appium Server
        APPIUM_SERVER_URL = getEnvOrProperty("APPIUM_URL", "appium.server.url", "http://127.0.0.1:4723");

        // Device
        DEVICE_NAME = getEnvOrProperty("DEVICE_NAME", "device.name", "emulator-5554");
        DEVICE_UDID = getEnvOrProperty("DEVICE_UDID", "device.udid", "");

        // App
        APP_PACKAGE = getEnvOrProperty("APP_PACKAGE", "app.package", "com.nst.profile.qa");
        APP_ACTIVITY = getEnvOrProperty("APP_ACTIVITY", "app.activity", "com.nst.profile.feature_splash.ui.SplashScreenActivity");

        // Timeouts
        TIMEOUT_COMMAND = getEnvOrPropertyInt("TIMEOUT_COMMAND", "timeout.command", 60000);
        TIMEOUT_IMPLICIT = getEnvOrPropertyInt("TIMEOUT_IMPLICIT", "timeout.implicit", 10);

        // Bank Details
        BANK_ACCOUNT_NUMBER = getEnvOrProperty("BANK_ACCOUNT_NUMBER", "bank.account.number", "10990200087021");
        BANK_IFSC = getEnvOrProperty("BANK_IFSC", "bank.ifsc", "ICIC0002121");

        // Swipe Coordinates
        SWIPE_X = getEnvOrPropertyInt("SWIPE_X", "swipe.x", 500);
        SWIPE_Y = getEnvOrPropertyInt("SWIPE_Y", "swipe.y", 1400);

        // Debug
        DEBUG_LOGS_ENABLED = getEnvOrPropertyBoolean("DEBUG_LOGS", "debug.logs.enabled", false);

        loaded = true;
    }

    /**
     * Gets value from environment variable first, then properties file, then default.
     */
    private static String getEnvOrProperty(String envKey, String propKey, String defaultValue) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        return props.getProperty(propKey, defaultValue);
    }

    /**
     * Gets integer value from environment variable first, then properties file, then default.
     */
    private static int getEnvOrPropertyInt(String envKey, String propKey, int defaultValue) {
        String value = getEnvOrProperty(envKey, propKey, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Gets boolean value from environment variable first, then properties file, then default.
     */
    private static boolean getEnvOrPropertyBoolean(String envKey, String propKey, boolean defaultValue) {
        String value = getEnvOrProperty(envKey, propKey, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    /**
     * Reloads configuration (useful for tests that modify properties).
     */
    public static void reload() {
        loaded = false;
        loadConfig();
    }

    /**
     * Prints current configuration (masks password).
     */
    public static void printConfig() {
        System.out.println("=== Test Configuration ===");
        System.out.println("USERNAME: " + USERNAME);
        System.out.println("PASSWORD: ****");
        System.out.println("APPIUM_SERVER_URL: " + APPIUM_SERVER_URL);
        System.out.println("DEVICE_NAME: " + DEVICE_NAME);
        System.out.println("DEVICE_UDID: " + (DEVICE_UDID.isEmpty() ? "(not set)" : DEVICE_UDID));
        System.out.println("APP_PACKAGE: " + APP_PACKAGE);
        System.out.println("APP_ACTIVITY: " + APP_ACTIVITY);
        System.out.println("BANK_ACCOUNT_NUMBER: " + BANK_ACCOUNT_NUMBER);
        System.out.println("BANK_IFSC: " + BANK_IFSC);
        System.out.println("DEBUG_LOGS_ENABLED: " + DEBUG_LOGS_ENABLED);
        System.out.println("==========================");
    }
}
