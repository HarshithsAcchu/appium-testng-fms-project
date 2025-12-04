# JSON-Driven Test Automation Framework

## Overview

This framework enables **data-driven and configuration-driven test automation** where test flows, locators, test data, and execution sequences are externalized to JSON configuration files. This allows non-technical users to modify test behavior without changing Java code.

## Architecture

```
src/test/
├── java/com/example/
│   ├── config/
│   │   ├── ConfigurationReader.java       # Singleton to read JSON config
│   │   └── models/                        # POJO models for JSON structure
│   │       ├── TestConfig.java
│   │       ├── TestSuite.java
│   │       ├── ExecutionStep.java
│   │       ├── Credentials.java
│   │       ├── LocatorConfig.java
│   │       └── GlobalSettings.java
│   ├── runners/
│   │   └── ParameterizedTestRunner.java   # Executes tests from JSON config
│   └── tests/
│       └── L2ProspectL1FlowTest.java      # Test class using JSON runner
└── resources/
    └── test-config.json                   # Main configuration file
```

---

## JSON Configuration Structure

### 1. Test Suites

Define multiple test suites with execution flows:

```json
{
  "testSuites": [
    {
      "suiteName": "L2ProspectL1Flow",
      "enabled": true,
      "credentials": {
        "username": "us-sh-shc-60507",
        "password": "Nst@1234"
      },
      "testData": {
        "alternateMobile": "8105928245",
        "bankAccountNumber": "10990200087021",
        "ifscCode": "ICIC0002121"
      },
      "executionFlow": [
        {
          "stepName": "handlePermissions",
          "enabled": true,
          "page": "PermissionPage",
          "action": "handlePermissions"
        }
      ]
    }
  ]
}
```

### 2. Locators

Centralize all element locators:

```json
{
  "locators": {
    "login": {
      "userId": {
        "type": "ID",
        "value": "com.nst.profile.qa:id/editUserId"
      },
      "password": {
        "type": "XPATH",
        "value": "//android.widget.EditText[@password='true']"
      }
    },
    "l2Info": {
      "prospectL1DetailsCard": {
        "type": "XPATH",
        "value": "//androidx.cardview.widget.CardView[@resource-id='mifix.io.qa:id/cardL1Details']"
      }
    }
  }
}
```

**Supported Locator Types:**
- `ID` - By.id()
- `XPATH` - By.xpath()
- `CLASS_NAME` - By.className()
- `ACCESSIBILITY_ID` - MobileBy.AccessibilityId()
- `ANDROID_UIAUTOMATOR` - MobileBy.AndroidUIAutomator()

### 3. Global Settings

Configure Appium capabilities:

```json
{
  "globalSettings": {
    "appiumUrl": "http://127.0.0.1:4723",
    "platformName": "Android",
    "deviceName": "emulator-5554",
    "appPackage": "com.nst.profile.qa",
    "appActivity": "com.nst.profile.feature_splash.ui.SplashScreenActivity",
    "autoGrantPermissions": true,
    "noReset": false,
    "fullReset": false
  }
}
```

---

## Execution Flow Configuration

Each test suite has an `executionFlow` array defining steps to execute:

### Single Action Step

```json
{
  "stepName": "handlePermissions",
  "enabled": true,
  "page": "PermissionPage",
  "action": "handlePermissions"
}
```

### Multiple Actions Step

```json
{
  "stepName": "enterCredentials",
  "enabled": true,
  "page": "HomePage",
  "actions": ["enterUserId", "enterPassword", "clickSignInButton"],
  "useCredentials": true
}
```

**Supported Pages:**
- `PermissionPage` - Android permission handling
- `HomePage` - Login and navigation
- `OnboardingPage` - Customer onboarding flows
- `L2InfoActions` - L2 customer information workflows

---

## How to Use

### 1. Run Tests with JSON Configuration

```java
@Test
public void runL2ProspectL1DetailsFlow() throws Exception {
    ParameterizedTestRunner testRunner = new ParameterizedTestRunner(driver, "L2ProspectL1Flow");
    testRunner.executeTestSuite();
}
```

### 2. Access Configuration in Code

```java
ConfigurationReader config = ConfigurationReader.getInstance();

// Get locators
By userIdField = config.getLocator("login", "userId");

// Get test data
String mobile = config.getTestData("L2ProspectL1Flow", "alternateMobile");

// Get credentials
String username = config.getUsername("L2ProspectL1Flow");
String password = config.getPassword("L2ProspectL1Flow");
```

### 3. Enable/Disable Tests

Disable entire suite:
```json
{
  "suiteName": "L2ProspectL1Flow",
  "enabled": false
}
```

Disable specific step:
```json
{
  "stepName": "completeVoterIdFlow",
  "enabled": false,
  "page": "OnboardingPage",
  "action": "completeVoterIdCaptureFlow"
}
```

---

## Running Tests

### Via Maven

```bash
# Run all tests
mvn clean test

# Run specific test group
mvn test -Dgroups=l2Info

# Run with custom config
mvn test -Dconfig.path=custom-config.json
```

### Via TestNG XML

```xml
<suite name="JSON-Driven Tests">
  <test name="L2 Flow">
    <classes>
      <class name="com.example.tests.L2ProspectL1FlowTest">
        <methods>
          <include name="runL2ProspectL1DetailsFlow"/>
        </methods>
      </class>
    </classes>
  </test>
</suite>
```

---

## Advantages

✅ **No Code Changes** - Modify flows via JSON without recompiling  
✅ **Reusable** - Share locators and test data across tests  
✅ **Maintainable** - Single source of truth for locators  
✅ **Flexible** - Enable/disable steps dynamically  
✅ **Version Control Friendly** - Easy to track config changes  
✅ **Non-Technical Friendly** - QA can modify flows  

---

## Adding New Test Suites

1. **Add suite to `test-config.json`:**

```json
{
  "suiteName": "MyNewFlow",
  "enabled": true,
  "credentials": {...},
  "testData": {...},
  "executionFlow": [...]
}
```

2. **Create test method:**

```java
@Test
public void runMyNewFlow() throws Exception {
    ParameterizedTestRunner runner = new ParameterizedTestRunner(driver, "MyNewFlow");
    runner.executeTestSuite();
}
```

---

## Adding New Page Actions

1. **Add action handler in `ParameterizedTestRunner.java`:**

```java
private void executeMyNewPageAction(ExecutionStep step) {
    MyNewPage page = new MyNewPage(driver);
    
    switch (step.getAction()) {
        case "myNewAction":
            page.performAction();
            break;
    }
}
```

2. **Update `executeStep()` method:**

```java
case "MyNewPage":
    executeMyNewPageAction(step, suite);
    break;
```

3. **Add to JSON config:**

```json
{
  "stepName": "performMyAction",
  "enabled": true,
  "page": "MyNewPage",
  "action": "myNewAction"
}
```

---

## Best Practices

1. **Keep locators in JSON** - Never hardcode locators in page classes
2. **Use meaningful step names** - Helps with debugging
3. **Version control** - Track `test-config.json` changes
4. **Environment-specific configs** - Create `test-config-dev.json`, `test-config-prod.json`
5. **Validate JSON** - Use online validators before running tests
6. **Comment complex flows** - Add description fields to steps

---

## Troubleshooting

### Configuration not loading

```
[ConfigurationReader] Configuration loaded from classpath: test-config.json
```

If you don't see this message:
- Check `test-config.json` is in `src/test/resources/`
- Verify JSON syntax (use JSONLint.com)
- Rebuild project: `mvn clean compile`

### Locator not found

```
IllegalArgumentException: Locator not found: login.userId
```

Solution: Verify locator exists in JSON under correct category.

### Step execution failed

```
[TestRunner] Unknown HomePage action: invalidAction
```

Solution: Check action name matches implemented actions in `ParameterizedTestRunner`.

---

## Example: Complete L2 Flow

```json
{
  "suiteName": "L2ProspectL1Flow",
  "enabled": true,
  "credentials": {
    "username": "testuser",
    "password": "testpass"
  },
  "executionFlow": [
    {"stepName": "permissions", "enabled": true, "page": "PermissionPage", "action": "handlePermissions"},
    {"stepName": "login", "enabled": true, "page": "HomePage", "actions": ["waitForLoginScreenReady", "enterUserId", "enterPassword", "clickSignInButton"], "useCredentials": true},
    {"stepName": "jlg", "enabled": true, "page": "HomePage", "actions": ["waitForJLGVisible", "clickJLG"]},
    {"stepName": "onboard", "enabled": true, "page": "OnboardingPage", "action": "openCustomerDetailsScreen"},
    {"stepName": "consent", "enabled": true, "page": "OnboardingPage", "action": "clickCaptureCustomerConsent"},
    {"stepName": "mobile", "enabled": true, "page": "OnboardingPage", "action": "enterMobileNumberAndSubmit"},
    {"stepName": "voterId", "enabled": true, "page": "OnboardingPage", "action": "completeVoterIdCaptureFlow"},
    {"stepName": "l2Continue", "enabled": true, "page": "L2InfoActions", "action": "clickContinuationDirectly"},
    {"stepName": "selectCustomer", "enabled": true, "page": "L2InfoActions", "action": "selectFirstCustomerSkippingAlerts"},
    {"stepName": "fillDetails", "enabled": true, "page": "L2InfoActions", "action": "fillL2ProspectL1DetailsFlow"}
  ]
}
```

---

## Migration Guide

### Before (Hardcoded)

```java
homePage.enterUserId("us-sh-shc-60507");
homePage.enterPassword("Nst@1234");
```

### After (JSON-driven)

```java
ParameterizedTestRunner runner = new ParameterizedTestRunner(driver, "L2ProspectL1Flow");
runner.executeTestSuite();
```

All credentials and flow come from JSON!

---

## Contact & Support

For issues or enhancements, update:
- `src/test/resources/test-config.json` - Configuration
- `com.example.runners.ParameterizedTestRunner` - Execution logic
- `com.example.config.ConfigurationReader` - Config reading

**Happy Testing! 🚀**
