package com.example.tests;

import java.net.URL;
import java.time.Duration;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.example.config.ConfigurationReader;
import com.example.config.models.GlobalSettings;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;

public class BaseTest {
    protected AndroidDriver driver;

    public BaseTest() {
    }

    @BeforeClass
    public void setUp() {
        try {
            // Load global settings from JSON config
            ConfigurationReader configReader = ConfigurationReader.getInstance();
            GlobalSettings globalSettings = configReader.getTestConfig().getGlobalSettings();
            
            if (globalSettings == null) {
                throw new IllegalStateException("Global settings not found in test-config.json");
            }
            
            System.out.println("[BaseTest] Loading capabilities from globalSettings...");
            
            // Initialize UiAutomator2Options with capabilities from JSON
            UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(globalSettings.getPlatformName())
                .setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2)
                .setDeviceName(globalSettings.getDeviceName())
                .setAppPackage(globalSettings.getAppPackage())
                .setAppActivity(globalSettings.getAppActivity())
                .setNewCommandTimeout(Duration.ofSeconds(60000))
                .setAutoGrantPermissions(globalSettings.isAutoGrantPermissions())
                .setNoReset(globalSettings.isNoReset())
                .setFullReset(globalSettings.isFullReset());
            
            System.out.println("[BaseTest] AppPackage: " + globalSettings.getAppPackage());
            System.out.println("[BaseTest] AppActivity: " + globalSettings.getAppActivity());
            System.out.println("[BaseTest] DeviceName: " + globalSettings.getDeviceName());

            // Override with environment variables if set
            String deviceName = System.getenv("DEVICE_NAME");
            if (deviceName != null && !deviceName.isEmpty()) {
                options.setDeviceName(deviceName);
            }

            String udid = System.getenv("DEVICE_UDID");
            if (udid != null && !udid.isEmpty()) {
                options.setUdid(udid);
            }

            // Initialize driver with options (prefer JSON config, fallback to env var)
            String serverUrl = globalSettings.getAppiumUrl();
            if (serverUrl == null || serverUrl.isEmpty()) {
                serverUrl = System.getenv().getOrDefault("APPIUM_URL", "http://127.0.0.1:4723");
            }
            System.out.println("[BaseTest] Connecting to Appium server at: " + serverUrl);
            
            driver = new AndroidDriver(new URL(serverUrl), options);
            System.out.println("Appium session started successfully");
            
            // Set implicit wait
          //  driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            
            
        } catch (Exception e) {
            System.err.println("Error initializing Appium driver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Appium driver: " + e.getMessage(), e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            try {
                System.out.println("Closing Appium session...");
              //  driver.close();
                System.out.println("Appium session closed successfully");
            } catch (Exception e) {
                System.err.println("Error while closing the driver: " + e.getMessage());
                e.printStackTrace();
            } finally {
                driver = null;
            }
        }
    }
}