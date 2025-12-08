# Generic Action-Driven Test Framework

## Overview

This framework allows you to write **100% JSON-driven tests** where even low-level actions (click, scroll, sendKeys, swipe) come from JSON configuration. No hardcoded test logic in Java!

## Key Concepts

### Before (Hardcoded Approach)
```java
// Java code with hardcoded logic
public void fillL2ProspectL1DetailsFlow() {
    driver.findElement(By.id("card")).click();
    driver.findElement(By.xpath("//input")).sendKeys("8105928245");
    // ... 50 more lines of hardcoded actions
}
```

### After (Generic JSON-Driven Approach)
```json
{
  "stepName": "fillL2Details",
  "actionSteps": [
    {
      "actionType": "click",
      "locatorCategory": "l2Info",
      "locatorName": "prospectL1DetailsCard"
    },
    {
      "actionType": "sendKeys",
      "locatorCategory": "l2Info",
      "locatorName": "alternateMobileInput",
      "value": "8105928245"
    }
  ]
}
```

---

## Supported Action Types

### 1. **click** - Click on element
```json
{
  "actionType": "click",
  "locatorCategory": "login",
  "locatorName": "signInButton",
  "description": "Click sign in button"
}
```

### 2. **sendKeys / enterText** - Enter text
```json
{
  "actionType": "sendKeys",
  "locatorCategory": "login",
  "locatorName": "userId",
  "value": "testuser",
  "description": "Enter username"
}
```

### 3. **clear** - Clear input field
```json
{
  "actionType": "clear",
  "locatorCategory": "login",
  "locatorName": "userId"
}
```

### 4. **scroll** - Scroll in direction
```json
{
  "actionType": "scroll",
  "params": {
    "direction": "down",
    "distance": 500
  },
  "description": "Scroll down 500px"
}
```

**Parameters:**
- `direction`: "down" (default) or "up"
- `distance`: Pixels to scroll (default: 500)

### 5. **scrollToElement** - Scroll until element visible
```json
{
  "actionType": "scrollToElement",
  "locatorCategory": "l2Info",
  "locatorName": "submitButton",
  "params": {
    "maxScrolls": 10,
    "direction": "down",
    "distance": 400
  },
  "description": "Scroll until submit button is visible"
}
```

**Parameters:**
- `maxScrolls`: Maximum scroll attempts (default: 10)
- `direction`: Scroll direction (default: "down")
- `distance`: Scroll distance per attempt (default: 500)

### 6. **swipe** - Custom swipe gesture
```json
{
  "actionType": "swipe",
  "params": {
    "startX": 500,
    "startY": 1500,
    "endX": 500,
    "endY": 500,
    "duration": 800
  },
  "description": "Swipe up from bottom"
}
```

**Parameters:**
- `startX`, `startY`: Start coordinates
- `endX`, `endY`: End coordinates
- `duration`: Swipe duration in milliseconds (default: 600)

### 7. **wait** - Simple delay
```json
{
  "actionType": "wait",
  "params": {
    "milliseconds": 2000
  },
  "description": "Wait 2 seconds"
}
```

### 8. **waitForVisible** - Wait for element to appear
```json
{
  "actionType": "waitForVisible",
  "locatorCategory": "home",
  "locatorName": "jlgText",
  "params": {
    "timeout": 15
  },
  "description": "Wait for JLG to appear"
}
```

**Parameters:**
- `timeout`: Wait timeout in seconds (default: 10)

### 9. **waitForClickable** - Wait for element to be clickable
```json
{
  "actionType": "waitForClickable",
  "locatorCategory": "l2Info",
  "locatorName": "submitButton",
  "params": {
    "timeout": 10
  }
}
```

### 10. **tap** - Tap at element center
```json
{
  "actionType": "tap",
  "locatorCategory": "onboarding",
  "locatorName": "composeButton",
  "description": "Tap compose button"
}
```

### 11. **longTap** - Long press
```json
{
  "actionType": "longTap",
  "locatorCategory": "onboarding",
  "locatorName": "cameraButton",
  "params": {
    "duration": 2000
  },
  "description": "Long press camera for 2s"
}
```

**Parameters:**
- `duration`: Press duration in milliseconds (default: 2000)

### 12. **hideKeyboard** - Hide on-screen keyboard
```json
{
  "actionType": "hideKeyboard",
  "optional": true
}
```

### 13. **goBack** - Navigate back
```json
{
  "actionType": "goBack",
  "description": "Press back button"
}
```

---

## Action Step Properties

### Required
- **`actionType`**: Type of action to perform (see list above)

### Optional (depends on action)
- **`locatorCategory`**: Category in locators JSON (e.g., "login", "home")
- **`locatorName`**: Specific locator name within category
- **`value`**: Text value for sendKeys actions
- **`params`**: Additional parameters (Map<String, Object>)
- **`optional`**: If true, failure won't stop execution (default: false)
- **`description`**: Human-readable description for logging

---

## Complete Example: Login Flow

### JSON Configuration
```json
{
  "suiteName": "GenericLoginFlow",
  "enabled": true,
  "credentials": {
    "username": "testuser",
    "password": "testpass"
  },
  "executionFlow": [
    {
      "stepName": "performLogin",
      "enabled": true,
      "description": "Login using generic actions",
      "actionSteps": [
        {
          "actionType": "waitForVisible",
          "locatorCategory": "login",
          "locatorName": "userId",
          "params": {"timeout": 10},
          "description": "Wait for login screen"
        },
        {
          "actionType": "click",
          "locatorCategory": "login",
          "locatorName": "userId"
        },
        {
          "actionType": "sendKeys",
          "locatorCategory": "login",
          "locatorName": "userId",
          "value": "testuser"
        },
        {
          "actionType": "click",
          "locatorCategory": "login",
          "locatorName": "password"
        },
        {
          "actionType": "sendKeys",
          "locatorCategory": "login",
          "locatorName": "password",
          "value": "testpass"
        },
        {
          "actionType": "hideKeyboard",
          "optional": true
        },
        {
          "actionType": "click",
          "locatorCategory": "login",
          "locatorName": "signInButton"
        }
      ]
    }
  ]
}
```

### Run the Test
```java
@Test
public void runGenericLoginFlow() throws Exception {
    ParameterizedTestRunner runner = new ParameterizedTestRunner(driver, "GenericLoginFlow");
    runner.executeTestSuite();
}
```

---

## Migration Guide

### Step 1: Identify Hardcoded Logic
Look for methods with hardcoded actions:
```java
public void fillForm() {
    driver.findElement(By.id("input1")).sendKeys("value");
    driver.findElement(By.id("button1")).click();
}
```

### Step 2: Convert to Generic Actions
Replace with JSON:
```json
{
  "stepName": "fillForm",
  "actionSteps": [
    {
      "actionType": "sendKeys",
      "locatorCategory": "myPage",
      "locatorName": "input1",
      "value": "value"
    },
    {
      "actionType": "click",
      "locatorCategory": "myPage",
      "locatorName": "button1"
    }
  ]
}
```

### Step 3: Add Locators to JSON
```json
{
  "locators": {
    "myPage": {
      "input1": {
        "type": "ID",
        "value": "input1"
      },
      "button1": {
        "type": "ID",
        "value": "button1"
      }
    }
  }
}
```

---

## Hybrid Approach (Old + New)

You can mix old hardcoded steps with new generic steps:

```json
{
  "executionFlow": [
    {
      "stepName": "handlePermissions",
      "enabled": true,
      "page": "PermissionPage",
      "action": "handlePermissions"
    },
    {
      "stepName": "loginWithGenericActions",
      "enabled": true,
      "actionSteps": [
        {
          "actionType": "sendKeys",
          "locatorCategory": "login",
          "locatorName": "userId",
          "value": "testuser"
        }
      ]
    }
  ]
}
```

Runner checks:
1. If `actionSteps` exists → use `GenericActionExecutor`
2. Otherwise → use old page/action approach

---

## Benefits

✅ **No code changes** - Update test logic via JSON  
✅ **Reusable actions** - Same actions work everywhere  
✅ **Easy debugging** - Each action logs what it does  
✅ **Optional actions** - Mark actions as optional to continue on failure  
✅ **Parameterized** - Pass custom params to each action  
✅ **Self-documenting** - Description field explains intent  

---

## Advanced Features

### Optional Actions
```json
{
  "actionType": "click",
  "locatorCategory": "popup",
  "locatorName": "closeButton",
  "optional": true,
  "description": "Close popup if it appears"
}
```

### Parameterized Scrolling
```json
{
  "actionType": "scrollToElement",
  "locatorCategory": "form",
  "locatorName": "submitButton",
  "params": {
    "maxScrolls": 15,
    "direction": "down",
    "distance": 300
  }
}
```

### Custom Waits
```json
{
  "actionType": "waitForVisible",
  "locatorCategory": "dashboard",
  "locatorName": "welcomeMessage",
  "params": {
    "timeout": 30
  },
  "description": "Wait up to 30s for dashboard"
}
```

---

## File Structure

```
src/test/
├── java/com/example/
│   ├── actions/
│   │   └── GenericActionExecutor.java     ← Executes all generic actions
│   ├── config/
│   │   ├── ConfigurationReader.java
│   │   └── models/
│   │       ├── ActionStep.java            ← Model for action definition
│   │       ├── ExecutionStep.java         ← Updated with actionSteps
│   │       └── ...
│   └── runners/
│       └── ParameterizedTestRunner.java   ← Routes to GenericActionExecutor
└── resources/
    ├── test-config.json                   ← Main config (old + new)
    └── test-config-generic-example.json   ← Pure generic examples
```

---

## Troubleshooting

### Action fails immediately
Check:
1. Locator exists in JSON under correct category/name
2. Element is visible/present before action
3. Add `waitForVisible` before click/sendKeys

### Element not found after scroll
Increase `maxScrolls`:
```json
{
  "actionType": "scrollToElement",
  "params": {
    "maxScrolls": 20
  }
}
```

### Keyboard blocks element
Add hideKeyboard:
```json
{
  "actionType": "hideKeyboard",
  "optional": true
}
```

### Need to debug
Set `description` on each action:
```json
{
  "actionType": "click",
  "description": "This is step 5: clicking submit",
  ...
}
```

---

## Next Steps

1. ✅ Start with simple flows (login, navigation)
2. ✅ Add generic actions incrementally
3. ✅ Keep complex flows in Java initially
4. ✅ Gradually migrate to 100% JSON-driven

## Summary

🎉 **You now have a fully generic, JSON-driven test framework!**

- All actions (click, scroll, swipe, etc.) come from JSON
- No need to write Java code for test flows
- Just define `actionSteps` array in your test suite
- Framework handles execution automatically

**See `test-config-generic-example.json` for complete examples!**
