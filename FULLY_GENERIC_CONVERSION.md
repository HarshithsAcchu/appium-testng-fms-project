# 100% Generic JSON-Driven Test Framework

## Overview

Your test framework is now **completely JSON-driven** with **ZERO hardcoded page/action methods**. Every test step uses generic `actionSteps` that are executed by `GenericActionExecutor`.

---

## What Was Converted

### ✅ Before (Legacy - Hardcoded)
```json
{
  "stepName": "openOnboarding",
  "enabled": true,
  "page": "OnboardingPage",
  "action": "openCustomerDetailsScreen"
}
```

### ✅ After (Generic - JSON-Driven)
```json
{
  "stepName": "openCustomerDetails",
  "enabled": true,
  "description": "Open customer details screen",
  "actionSteps": [
    {
      "actionType": "waitForVisible",
      "locatorCategory": "onboarding",
      "locatorName": "customerDetailsText",
      "params": {"milliseconds": 10000},
      "description": "Wait for customer details text"
    },
    {
      "actionType": "click",
      "locatorCategory": "onboarding",
      "locatorName": "customerDetailsText",
      "description": "Click customer details"
    },
    {
      "actionType": "wait",
      "params": {"milliseconds": 1000}
    }
  ]
}
```

---

## Complete Conversion List

| Old Step (Legacy) | New Step (Generic) | Actions Count |
|-------------------|-------------------|---------------|
| `handlePermissions` | `handlePermissions` | 1 action (wait) |
| `waitForLoginScreen` | ✅ Merged into `loginWithGenericActions` | - |
| `enterCredentials` | ✅ Merged into `loginWithGenericActions` | - |
| `loginWithGenericActions` | `loginWithGenericActions` | 8 actions |
| `navigateToJLG` | `navigateToJLG` | 3 actions |
| `openOnboarding` | `openCustomerDetails` | 3 actions |
| `captureConsent` | `captureConsent` | 3 actions |
| `submitMobile` | `enterMobileNumber` | 7 actions |
| `completeVoterIdFlow` | `completeVoterIdCapture` | 22 actions |
| `clickL2Continuation` | `clickL2Continuation` | 4 actions |
| `selectL2Customer` | `selectL2Customer` | 5 actions |
| `fillL2ProspectDetails` | `fillL2ProspectDetails` | 56 actions |
| `fillBankDetails` | `fillBankDetails` | 24 actions |

**Total: 136 generic actions** replacing 13 hardcoded methods!

---

## All Supported Action Types

Your framework now supports these generic actions:

1. ✅ **click** - Click element
2. ✅ **sendKeys** - Enter text
3. ✅ **clear** - Clear input
4. ✅ **scroll** - Scroll by distance
5. ✅ **scrollToElement** - Scroll until element visible
6. ✅ **swipe** - Custom swipe gesture
7. ✅ **wait** - Simple delay
8. ✅ **waitForVisible** - Wait for element to appear
9. ✅ **waitForClickable** - Wait for element to be clickable
10. ✅ **tap** - Tap at element center
11. ✅ **longTap** - Long press
12. ✅ **hideKeyboard** - Hide on-screen keyboard
13. ✅ **goBack** - Navigate back

---

## Test Suites

### 1. L2ProspectL1Flow (Main Flow)

**Steps:**
1. **handlePermissions** - Wait for permission dialogs (1 action)
2. **loginWithGenericActions** - Complete login flow (8 actions)
3. **navigateToJLG** - Navigate to JLG screen (3 actions)
4. **openCustomerDetails** - Open customer details (3 actions)
5. **captureConsent** - Capture customer consent (3 actions)
6. **enterMobileNumber** - Enter and validate mobile (7 actions)
7. **completeVoterIdCapture** - Complete voter ID capture with images (22 actions)
8. **clickL2Continuation** - Continue to L2 section (4 actions)
9. **selectL2Customer** - Select customer and dismiss alerts (5 actions)
10. **fillL2ProspectDetails** - Fill complete L2 form with document capture (56 actions)

**Total: 112 actions**

### 2. BankDetailsOnly

**Steps:**
1. **fillBankDetails** - Fill bank account details and capture proof (24 actions)

**Total: 24 actions**

---

## How It Works Now

### Execution Flow

```
Test Class (L2ProspectL1FlowTest.java)
    ↓
ParameterizedTestRunner.executeTestSuite()
    ↓
For each ExecutionStep:
    ↓
    Check if step has "actionSteps"?
        ↓ YES
        GenericActionExecutor.executeAction() for each action
            ↓
            Switch on actionType (click, scroll, sendKeys, etc.)
                ↓
                Resolve locator from JSON
                ↓
                Perform action on driver
        ↓ NO (legacy)
        Call hardcoded page method (deprecated)
```

### Example: Login Flow

```json
{
  "stepName": "loginWithGenericActions",
  "actionSteps": [
    {"actionType": "waitForVisible", "locatorCategory": "login", "locatorName": "userId"},
    {"actionType": "click", "locatorCategory": "login", "locatorName": "userId"},
    {"actionType": "sendKeys", "locatorCategory": "login", "locatorName": "userId", "value": "us-sh-shc-60507"},
    {"actionType": "click", "locatorCategory": "login", "locatorName": "password"},
    {"actionType": "sendKeys", "locatorCategory": "login", "locatorName": "password", "value": "Nst@1234"},
    {"actionType": "hideKeyboard", "optional": true},
    {"actionType": "click", "locatorCategory": "login", "locatorName": "signInButton"},
    {"actionType": "wait", "params": {"milliseconds": 2000}}
  ]
}
```

**Execution:**
1. Wait for userId field to be visible (15s timeout)
2. Click userId field
3. Enter username "us-sh-shc-60507"
4. Click password field
5. Enter password "Nst@1234"
6. Hide keyboard (optional - won't fail if keyboard not present)
7. Click sign-in button
8. Wait 2 seconds for navigation

---

## Benefits of Full Generic Approach

### 1. **Zero Code Changes for Test Modifications**
- Change test flow → Edit JSON only
- Add new steps → Add JSON actions
- Update locators → Update JSON locators
- Adjust waits/scrolls → Update JSON params

### 2. **Complete Flexibility**
```json
// Want to add a scroll before clicking?
{
  "actionType": "scroll",
  "params": {"direction": "down", "distance": 300}
},
{
  "actionType": "click",
  "locatorCategory": "myPage",
  "locatorName": "myButton"
}
```

### 3. **Easy Debugging**
Every action has a `description` field:
```json
{
  "actionType": "click",
  "locatorCategory": "l2Info",
  "locatorName": "submitButton",
  "description": "Click submit button to save L2 details"
}
```

Logs show:
```
[GenericActionExecutor] Executing action: click - Click submit button to save L2 details
```

### 4. **Optional Actions**
Mark actions as optional to continue on failure:
```json
{
  "actionType": "click",
  "locatorCategory": "popup",
  "locatorName": "closeButton",
  "optional": true,
  "description": "Close popup if it appears"
}
```

### 5. **Parameterized Actions**
Customize behavior via params:
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

---

## Running Tests

### Run Main Flow
```bash
mvn test -Dtest=L2ProspectL1FlowTest
```

### Run Bank Details Only
```bash
mvn test -Dtest=L2ProspectL1FlowTest -DsuiteName=BankDetailsOnly
```

---

## Modifying Test Flow

### Example: Change Mobile Number

**Before (required Java code change):**
```java
public void enterMobileNumber() {
    element.sendKeys("9876543210"); // Hardcoded
}
```

**After (JSON only):**
```json
{
  "actionType": "sendKeys",
  "locatorCategory": "onboarding",
  "locatorName": "mobileInput",
  "value": "8888888888",  ← Just change this!
  "description": "Enter mobile number"
}
```

### Example: Add Extra Wait

```json
{
  "actionType": "click",
  "locatorCategory": "l2Info",
  "locatorName": "submitButton"
},
{
  "actionType": "wait",  ← Add this action
  "params": {
    "milliseconds": 5000
  },
  "description": "Wait for server processing"
}
```

### Example: Skip a Step

```json
{
  "stepName": "captureConsent",
  "enabled": false,  ← Set to false to skip
  "description": "Capture customer consent",
  "actionSteps": [...]
}
```

---

## Advanced Features

### 1. Conditional Execution
Use `optional: true` for actions that may or may not be needed:
```json
{
  "actionType": "click",
  "locatorCategory": "onboarding",
  "locatorName": "cropButton",
  "optional": true,
  "description": "Click crop if present"
}
```

### 2. Dynamic Scrolling
Scroll until element found:
```json
{
  "actionType": "scrollToElement",
  "locatorCategory": "l2Info",
  "locatorName": "submitButton",
  "params": {
    "maxScrolls": 10,
    "direction": "down",
    "distance": 400
  }
}
```

### 3. Custom Gestures
Precise swipe control:
```json
{
  "actionType": "swipe",
  "params": {
    "startX": 500,
    "startY": 1500,
    "endX": 500,
    "endY": 500,
    "duration": 800
  }
}
```

---

## Troubleshooting

### Action Fails
1. Check locator exists in JSON under correct category/name
2. Verify element is visible before action
3. Add `waitForVisible` or `waitForClickable` before action
4. Increase timeout in params

### Element Not Found After Scroll
```json
{
  "actionType": "scrollToElement",
  "params": {
    "maxScrolls": 20  ← Increase this
  }
}
```

### Keyboard Blocking Element
```json
{
  "actionType": "hideKeyboard",
  "optional": true
},
{
  "actionType": "wait",
  "params": {"milliseconds": 500}
}
```

---

## Summary

🎉 **Your framework is now 100% JSON-driven!**

- ✅ **136 generic actions** across 2 test suites
- ✅ **Zero hardcoded page methods** in execution flow
- ✅ **13 action types** supported
- ✅ **Complete flexibility** via JSON configuration
- ✅ **Easy maintenance** - no Java code changes needed
- ✅ **Self-documenting** - descriptions on every action

**All test logic is now in `test-config.json`!**

To modify tests:
1. Open `test-config.json`
2. Edit `actionSteps` arrays
3. Save
4. Run tests

No Java compilation required! 🚀
