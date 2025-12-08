package com.example.config.models;

import java.util.Map;

/**
 * Represents a single action to perform in the test flow
 * Examples: click, sendKeys, scroll, wait, swipe, etc.
 */
public class ActionStep {
    private String actionType;           // click, sendKeys, scroll, wait, swipe, etc.
    private String locatorCategory;      // login, home, onboarding, l2Info
    private String locatorName;          // userId, password, submitButton
    private String value;                // Text to enter or other value
    private Map<String, Object> params;  // Additional parameters (timeout, direction, etc.)
    private boolean optional;            // If true, failure won't stop execution
    private String description;          // Human-readable description

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getLocatorCategory() {
        return locatorCategory;
    }

    public void setLocatorCategory(String locatorCategory) {
        this.locatorCategory = locatorCategory;
    }

    public String getLocatorName() {
        return locatorName;
    }

    public void setLocatorName(String locatorName) {
        this.locatorName = locatorName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Helper to get typed parameter
    public Object getParam(String key) {
        return params != null ? params.get(key) : null;
    }

    public String getParamAsString(String key, String defaultValue) {
        Object val = getParam(key);
        return val != null ? val.toString() : defaultValue;
    }

    public int getParamAsInt(String key, int defaultValue) {
        Object val = getParam(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return defaultValue;
    }

    public boolean getParamAsBoolean(String key, boolean defaultValue) {
        Object val = getParam(key);
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        return defaultValue;
    }
}
