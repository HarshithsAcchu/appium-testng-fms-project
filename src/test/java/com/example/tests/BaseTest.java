package com.example.tests;

import java.net.URL;
import java.time.Duration;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.example.testdata.TestConfig;

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
            // Print loaded configuration
            TestConfig.printConfig();

            UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2)
                .setDeviceName(TestConfig.DEVICE_NAME)
                .setAppPackage(TestConfig.APP_PACKAGE)
                .setAppActivity(TestConfig.APP_ACTIVITY)
                .setNewCommandTimeout(Duration.ofSeconds(TestConfig.TIMEOUT_COMMAND))
                .setAutoGrantPermissions(true)
                .setNoReset(false)
                .setFullReset(false);

            // Set UDID if configured
            if (TestConfig.DEVICE_UDID != null && !TestConfig.DEVICE_UDID.isEmpty()) {
                options.setUdid(TestConfig.DEVICE_UDID);
            }

            // Initialize driver with options
            String serverUrl = TestConfig.APPIUM_SERVER_URL;
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