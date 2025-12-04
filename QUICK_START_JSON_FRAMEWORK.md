# Quick Start Guide - JSON-Driven Test Framework

## What Was Created

### ✅ New Files Created

1. **Configuration File**
   - `src/test/resources/test-config.json` - Main JSON configuration with locators, flows, and test data

2. **Model Classes** (`com.example.config.models`)
   - `TestConfig.java` - Root configuration model
   - `TestSuite.java` - Test suite definition
   - `ExecutionStep.java` - Individual step configuration
   - `Credentials.java` - Login credentials model
   - `LocatorConfig.java` - Element locator configuration
   - `GlobalSettings.java` - Appium capabilities

3. **Utility Classes**
   - `com.example.config.ConfigurationReader` - Singleton JSON reader
   - `com.example.runners.ParameterizedTestRunner` - JSON-driven test executor

4. **Updated Tests**
   - `L2ProspectL1FlowTest.java` - Now uses JSON configuration

5. **Documentation**
   - `JSON_TEST_FRAMEWORK_README.md` - Complete documentation
   - `QUICK_START_JSON_FRAMEWORK.md` - This file

---

## How It Works

### Before (Hardcoded)

```java
@Test
public void runL2ProspectL1DetailsFlow() throws Exception {
    var permissionPage = pageObjectManager.getPermissionPage();
    var homePage = pageObjectManager.getHomePage();
    
    permissionPage.handlePermissions();
    
    homePage.waitForLoginScreenReady();
    homePage.enterUserId("us-sh-shc-60507");  // ❌ Hardcoded
    homePage.enterPassword("Nst@1234");        // ❌ Hardcoded
    homePage.clickSignInButton();
    
    // ... 50 more lines of hardcoded steps
}
```

### After (JSON-Driven)

```java
@Test
public void runL2ProspectL1DetailsFlow() throws Exception {
    ParameterizedTestRunner testRunner = new ParameterizedTestRunner(driver, "L2ProspectL1Flow");
    testRunner.executeTestSuite();  // ✅ Reads everything from JSON!
}
```

**All test flow, credentials, and locators are now in `test-config.json`!**

---

## Quick Test

### 1. Build Project

```bash
mvn clean compile
```

### 2. Run JSON-Driven Test

```bash
mvn test -Dtest=L2ProspectL1FlowTest#runL2ProspectL1DetailsFlow
```

### 3. Expected Console Output

```
================================================================================
[L2ProspectL1FlowTest] Starting JSON-driven test execution
[L2ProspectL1FlowTest] Suite: L2ProspectL1Flow
================================================================================
[TestRunner] Executing test suite: L2ProspectL1Flow
[TestRunner] Executing step: handlePermissions
[PermissionPage] Dismissed 3 permission dialog(s).
[TestRunner] Executing step: waitForLoginScreen
[HomePage] Login screen ready (ms): 2341
[TestRunner] Executing step: enterCredentials
[TestRunner] Executing step: navigateToJLG
[TestRunner] Executing step: openOnboarding
...
[TestRunner] Test suite 'L2ProspectL1Flow' completed successfully.
================================================================================
[L2ProspectL1FlowTest] JSON-driven test completed successfully
================================================================================
```

---

## Customization Examples

### Example 1: Change Credentials

**Edit `test-config.json`:**

```json
{
  "suiteName": "L2ProspectL1Flow",
  "credentials": {
    "username": "newuser",    ← Change here
    "password": "newpass123"  ← Change here
  }
}
```

**No Java code changes needed!**

---

### Example 2: Skip Steps

Disable voter ID capture flow:

```json
{
  "stepName": "completeVoterIdFlow",
  "enabled": false,  ← Set to false
  "page": "OnboardingPage",
  "action": "completeVoterIdCaptureFlow"
}
```

---

### Example 3: Change Test Data

```json
{
  "testData": {
    "alternateMobile": "9876543210",     ← Update mobile
    "bankAccountNumber": "11223344556",  ← Update account
    "ifscCode": "HDFC0001234"            ← Update IFSC
  }
}
```

---

### Example 4: Add New Locator

```json
{
  "locators": {
    "myNewPage": {
      "submitButton": {
        "type": "XPATH",
        "value": "//android.widget.Button[@text='Submit']"
      }
    }
  }
}
```

**Access in code:**

```java
By submitBtn = ConfigurationReader.getInstance().getLocator("myNewPage", "submitButton");
```

---

## File Structure Overview

```
appium-testng-fms-project/
│
├── src/test/
│   ├── java/com/example/
│   │   ├── config/                    ← NEW: Configuration package
│   │   │   ├── ConfigurationReader.java
│   │   │   └── models/
│   │   │       ├── TestConfig.java
│   │   │       ├── TestSuite.java
│   │   │       ├── ExecutionStep.java
│   │   │       ├── Credentials.java
│   │   │       ├── LocatorConfig.java
│   │   │       └── GlobalSettings.java
│   │   ├── runners/                   ← NEW: Test runners
│   │   │   └── ParameterizedTestRunner.java
│   │   ├── tests/
│   │   │   ├── BaseTest.java
│   │   │   └── L2ProspectL1FlowTest.java  ← UPDATED
│   │   ├── pages/
│   │   │   ├── HomePage.java
│   │   │   ├── OnboardingPage.java
│   │   │   └── L2InfoActions.java
│   │   └── locators/
│   │       └── AppLocators.java       ← Still used, but JSON overrides
│   │
│   └── resources/
│       ├── test-config.json           ← NEW: Main configuration
│       └── testng.xml
│
├── pom.xml                             ← UPDATED: Added Gson
├── JSON_TEST_FRAMEWORK_README.md       ← NEW: Full documentation
└── QUICK_START_JSON_FRAMEWORK.md       ← NEW: This file
```

---

## Key Benefits

| Feature | Before | After |
|---------|--------|-------|
| **Change credentials** | Edit Java code, recompile | Edit JSON, no recompile |
| **Update locators** | Edit Java class, rebuild | Edit JSON, done |
| **Skip test steps** | Comment code, rebuild | Set `enabled: false` |
| **Add test data** | Hardcode in test, rebuild | Add to JSON testData |
| **Share configs** | Copy-paste code | Share JSON file |

---

## Next Steps

1. ✅ **Run test** - Verify it works with JSON config
2. 📝 **Customize** - Edit `test-config.json` for your needs
3. 🔄 **Version control** - Commit JSON changes
4. 📊 **Create new suites** - Add more test suites to JSON
5. 🚀 **CI/CD** - Different configs per environment

---

## Testing the Framework

### Test 1: Verify JSON Loading

```java
@Test
public void verifyConfigLoads() {
    ConfigurationReader config = ConfigurationReader.getInstance();
    String username = config.getUsername("L2ProspectL1Flow");
    System.out.println("Username from JSON: " + username);
    Assert.assertNotNull(username);
}
```

### Test 2: Verify Locators

```java
@Test
public void verifyLocator() {
    ConfigurationReader config = ConfigurationReader.getInstance();
    By userIdField = config.getLocator("login", "userId");
    System.out.println("Locator: " + userIdField);
    Assert.assertNotNull(userIdField);
}
```

---

## Common Use Cases

### Use Case 1: Environment-Specific Tests

Create separate configs:
- `test-config-dev.json`
- `test-config-qa.json`
- `test-config-prod.json`

Load dynamically:
```java
String env = System.getProperty("env", "dev");
String configFile = "test-config-" + env + ".json";
```

### Use Case 2: Data-Driven Tests

Use TestNG DataProvider with JSON:

```java
@DataProvider(name = "testSuites")
public Object[][] getTestSuites() {
    TestConfig config = ConfigurationReader.getInstance().getTestConfig();
    return config.getTestSuites().stream()
        .map(suite -> new Object[]{suite.getSuiteName()})
        .toArray(Object[][]::new);
}

@Test(dataProvider = "testSuites")
public void runDynamicTest(String suiteName) throws Exception {
    ParameterizedTestRunner runner = new ParameterizedTestRunner(driver, suiteName);
    runner.executeTestSuite();
}
```

### Use Case 3: Parallel Execution

```xml
<suite name="Parallel Tests" parallel="methods" thread-count="3">
  <test name="L2 Flow Test">
    <classes>
      <class name="com.example.tests.L2ProspectL1FlowTest"/>
    </classes>
  </test>
</suite>
```

---

## Troubleshooting

### Issue: JSON not loading

**Solution:**
```bash
mvn clean compile test-compile
```

### Issue: Locator not found

**Check:**
1. JSON syntax is valid (use jsonlint.com)
2. Category and name match exactly
3. File is in `src/test/resources/`

### Issue: Step not executing

**Verify:**
1. Step `enabled` is `true`
2. Suite `enabled` is `true`
3. Action name matches implemented actions

---

## Summary

✅ **Created:** Complete JSON-driven test framework  
✅ **Updated:** `L2ProspectL1FlowTest` to use JSON runner  
✅ **Added:** 10+ new Java classes for config management  
✅ **Benefit:** Change test behavior without recompiling code  

**Your test now runs from JSON configuration!** 🎉

Run it:
```bash
mvn test -Dtest=L2ProspectL1FlowTest
```

---

**Questions? Check `JSON_TEST_FRAMEWORK_README.md` for detailed documentation!**
