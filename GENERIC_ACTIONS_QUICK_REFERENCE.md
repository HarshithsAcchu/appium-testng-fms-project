# Generic Actions Quick Reference Card

## Action Types Cheat Sheet

### 1. CLICK
```json
{
  "actionType": "click",
  "locatorCategory": "category",
  "locatorName": "elementName",
  "description": "Click description"
}
```

### 2. SEND KEYS (Enter Text)
```json
{
  "actionType": "sendKeys",
  "locatorCategory": "category",
  "locatorName": "elementName",
  "value": "text to enter",
  "description": "Enter text description"
}
```

### 3. CLEAR INPUT
```json
{
  "actionType": "clear",
  "locatorCategory": "category",
  "locatorName": "elementName"
}
```

### 4. SCROLL (Fixed Distance)
```json
{
  "actionType": "scroll",
  "params": {
    "direction": "down",  // or "up"
    "distance": 500       // pixels
  },
  "description": "Scroll down 500px"
}
```

### 5. SCROLL TO ELEMENT
```json
{
  "actionType": "scrollToElement",
  "locatorCategory": "category",
  "locatorName": "elementName",
  "params": {
    "maxScrolls": 10,     // max attempts
    "direction": "down",  // or "up"
    "distance": 400       // pixels per scroll
  },
  "description": "Scroll until element visible"
}
```

### 6. SWIPE (Custom Gesture)
```json
{
  "actionType": "swipe",
  "params": {
    "startX": 500,
    "startY": 1500,
    "endX": 500,
    "endY": 500,
    "duration": 800  // milliseconds
  },
  "description": "Swipe up"
}
```

### 7. WAIT (Simple Delay)
```json
{
  "actionType": "wait",
  "params": {
    "milliseconds": 2000
  },
  "description": "Wait 2 seconds"
}
```

### 8. WAIT FOR VISIBLE
```json
{
  "actionType": "waitForVisible",
  "locatorCategory": "category",
  "locatorName": "elementName",
  "params": {
    "milliseconds": 15000  // timeout in ms
  },
  "description": "Wait for element to appear"
}
```

### 9. WAIT FOR CLICKABLE
```json
{
  "actionType": "waitForClickable",
  "locatorCategory": "category",
  "locatorName": "elementName",
  "params": {
    "milliseconds": 10000  // timeout in ms
  },
  "description": "Wait for element to be clickable"
}
```

### 10. TAP (At Element Center)
```json
{
  "actionType": "tap",
  "locatorCategory": "category",
  "locatorName": "elementName",
  "description": "Tap element"
}
```

### 11. LONG TAP (Press and Hold)
```json
{
  "actionType": "longTap",
  "locatorCategory": "category",
  "locatorName": "elementName",
  "params": {
    "duration": 2000  // press duration in ms
  },
  "description": "Long press for 2s"
}
```

### 12. HIDE KEYBOARD
```json
{
  "actionType": "hideKeyboard",
  "optional": true,
  "description": "Hide on-screen keyboard"
}
```

### 13. GO BACK
```json
{
  "actionType": "goBack",
  "description": "Press back button"
}
```

---

## Common Patterns

### Login Flow
```json
{
  "stepName": "login",
  "actionSteps": [
    {"actionType": "waitForVisible", "locatorCategory": "login", "locatorName": "username", "params": {"milliseconds": 10000}},
    {"actionType": "click", "locatorCategory": "login", "locatorName": "username"},
    {"actionType": "sendKeys", "locatorCategory": "login", "locatorName": "username", "value": "myuser"},
    {"actionType": "click", "locatorCategory": "login", "locatorName": "password"},
    {"actionType": "sendKeys", "locatorCategory": "login", "locatorName": "password", "value": "mypass"},
    {"actionType": "hideKeyboard", "optional": true},
    {"actionType": "click", "locatorCategory": "login", "locatorName": "loginButton"},
    {"actionType": "wait", "params": {"milliseconds": 2000}}
  ]
}
```

### Form Fill with Scroll
```json
{
  "stepName": "fillForm",
  "actionSteps": [
    {"actionType": "scrollToElement", "locatorCategory": "form", "locatorName": "field1", "params": {"maxScrolls": 5}},
    {"actionType": "click", "locatorCategory": "form", "locatorName": "field1"},
    {"actionType": "sendKeys", "locatorCategory": "form", "locatorName": "field1", "value": "value1"},
    {"actionType": "hideKeyboard", "optional": true},
    {"actionType": "scroll", "params": {"direction": "down", "distance": 300}},
    {"actionType": "click", "locatorCategory": "form", "locatorName": "field2"},
    {"actionType": "sendKeys", "locatorCategory": "form", "locatorName": "field2", "value": "value2"},
    {"actionType": "scrollToElement", "locatorCategory": "form", "locatorName": "submitButton", "params": {"maxScrolls": 5}},
    {"actionType": "click", "locatorCategory": "form", "locatorName": "submitButton"}
  ]
}
```

### Dropdown Selection
```json
{
  "stepName": "selectDropdown",
  "actionSteps": [
    {"actionType": "click", "locatorCategory": "form", "locatorName": "dropdown", "description": "Open dropdown"},
    {"actionType": "wait", "params": {"milliseconds": 500}},
    {"actionType": "click", "locatorCategory": "form", "locatorName": "dropdownOption", "description": "Select option"},
    {"actionType": "wait", "params": {"milliseconds": 500}}
  ]
}
```

### Image Capture Flow
```json
{
  "stepName": "captureImage",
  "actionSteps": [
    {"actionType": "scrollToElement", "locatorCategory": "capture", "locatorName": "captureButton", "params": {"maxScrolls": 5}},
    {"actionType": "click", "locatorCategory": "capture", "locatorName": "captureButton"},
    {"actionType": "wait", "params": {"milliseconds": 2000}},
    {"actionType": "click", "locatorCategory": "camera", "locatorName": "shutterButton"},
    {"actionType": "wait", "params": {"milliseconds": 1500}},
    {"actionType": "click", "locatorCategory": "camera", "locatorName": "cropButton", "optional": true},
    {"actionType": "wait", "params": {"milliseconds": 1000}}
  ]
}
```

---

## Optional Actions

Mark any action as optional to continue on failure:

```json
{
  "actionType": "click",
  "locatorCategory": "popup",
  "locatorName": "closeButton",
  "optional": true,  ← Won't fail test if element not found
  "description": "Close popup if present"
}
```

---

## Parameter Reference

### scroll
- `direction`: "down" | "up" (default: "down")
- `distance`: number (pixels, default: 500)

### scrollToElement
- `maxScrolls`: number (max attempts, default: 10)
- `direction`: "down" | "up" (default: "down")
- `distance`: number (pixels per scroll, default: 500)

### swipe
- `startX`: number (start X coordinate)
- `startY`: number (start Y coordinate)
- `endX`: number (end X coordinate)
- `endY`: number (end Y coordinate)
- `duration`: number (swipe duration in ms, default: 600)

### wait
- `milliseconds`: number (delay in ms, default: 1000)

### waitForVisible / waitForClickable
- `milliseconds`: number (timeout in ms, default: 10000)

### longTap
- `duration`: number (press duration in ms, default: 2000)

---

## Locator Structure

Locators are defined in the `locators` section:

```json
{
  "locators": {
    "categoryName": {
      "elementName": {
        "type": "ID" | "XPATH" | "CLASS_NAME" | "ACCESSIBILITY_ID",
        "value": "locator value"
      }
    }
  }
}
```

**Example:**
```json
{
  "locators": {
    "login": {
      "username": {
        "type": "ID",
        "value": "com.app:id/username"
      },
      "password": {
        "type": "XPATH",
        "value": "//input[@type='password']"
      }
    }
  }
}
```

---

## Tips & Best Practices

### 1. Always Wait Before Click
```json
{"actionType": "waitForClickable", "locatorCategory": "page", "locatorName": "button", "params": {"milliseconds": 10000}},
{"actionType": "click", "locatorCategory": "page", "locatorName": "button"}
```

### 2. Hide Keyboard After Text Entry
```json
{"actionType": "sendKeys", "locatorCategory": "form", "locatorName": "input", "value": "text"},
{"actionType": "hideKeyboard", "optional": true}
```

### 3. Use Descriptions for Debugging
```json
{
  "actionType": "click",
  "locatorCategory": "form",
  "locatorName": "submit",
  "description": "Step 5: Submit the registration form"  ← Helps in logs
}
```

### 4. Add Waits After Navigation
```json
{"actionType": "click", "locatorCategory": "nav", "locatorName": "nextButton"},
{"actionType": "wait", "params": {"milliseconds": 2000}}  ← Wait for screen transition
```

### 5. Make Popups Optional
```json
{
  "actionType": "click",
  "locatorCategory": "popup",
  "locatorName": "dismiss",
  "optional": true  ← Won't fail if popup doesn't appear
}
```

---

## Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| Element not found | Add `waitForVisible` before action |
| Element not clickable | Add `waitForClickable` before click |
| Keyboard blocks element | Add `hideKeyboard` action |
| Element below fold | Use `scrollToElement` |
| Action too fast | Add `wait` action after |
| Popup sometimes appears | Mark action as `optional: true` |
| Need more scroll attempts | Increase `maxScrolls` param |
| Timeout too short | Increase `milliseconds` in params |

---

## Example: Complete Test Step

```json
{
  "stepName": "completeRegistration",
  "enabled": true,
  "description": "Fill and submit registration form",
  "actionSteps": [
    {
      "actionType": "waitForVisible",
      "locatorCategory": "registration",
      "locatorName": "nameField",
      "params": {"milliseconds": 10000},
      "description": "Wait for registration form"
    },
    {
      "actionType": "click",
      "locatorCategory": "registration",
      "locatorName": "nameField"
    },
    {
      "actionType": "sendKeys",
      "locatorCategory": "registration",
      "locatorName": "nameField",
      "value": "John Doe"
    },
    {
      "actionType": "hideKeyboard",
      "optional": true
    },
    {
      "actionType": "scrollToElement",
      "locatorCategory": "registration",
      "locatorName": "emailField",
      "params": {"maxScrolls": 5}
    },
    {
      "actionType": "click",
      "locatorCategory": "registration",
      "locatorName": "emailField"
    },
    {
      "actionType": "sendKeys",
      "locatorCategory": "registration",
      "locatorName": "emailField",
      "value": "john@example.com"
    },
    {
      "actionType": "hideKeyboard",
      "optional": true
    },
    {
      "actionType": "scrollToElement",
      "locatorCategory": "registration",
      "locatorName": "submitButton",
      "params": {"maxScrolls": 5}
    },
    {
      "actionType": "click",
      "locatorCategory": "registration",
      "locatorName": "submitButton",
      "description": "Submit registration"
    },
    {
      "actionType": "wait",
      "params": {"milliseconds": 3000},
      "description": "Wait for confirmation"
    }
  ]
}
```

---

**Remember:** All actions are case-insensitive (`click` = `CLICK` = `Click`)

**File Location:** `src/test/resources/test-config.json`

**Executor:** `GenericActionExecutor.java`
