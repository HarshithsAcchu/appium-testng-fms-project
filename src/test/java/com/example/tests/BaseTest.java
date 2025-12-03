package com.example.tests;

import java.net.URL;
import java.time.Duration;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

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
            // Initialize UiAutomator2Options with required capabilities
            UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2)
                .setDeviceName("emulator-5554") // Default, can be overridden by environment variable
                .setAppPackage("com.nst.profile.qa")
                .setAppActivity("com.nst.profile.feature_splash.ui.SplashScreenActivity")
                .setNewCommandTimeout(Duration.ofSeconds(60000))
                .setAutoGrantPermissions(true)
                .setNoReset(false)  // This is the correct method in newer versions
                .setFullReset(false);
                // Add any additional capabilities here if needed

            // Override with environment variables if set
            String deviceName = System.getenv("DEVICE_NAME");
            if (deviceName != null && !deviceName.isEmpty()) {
                options.setDeviceName(deviceName);
            }

            String udid = System.getenv("DEVICE_UDID");
            if (udid != null && !udid.isEmpty()) {
                options.setUdid(udid);
            }

            // Initialize driver with options
            String serverUrl = System.getenv().getOrDefault("APPIUM_URL", "http://127.0.0.1:4723");
            System.out.println("Connecting to Appium server at: " + serverUrl);
            
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