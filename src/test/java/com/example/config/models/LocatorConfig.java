package com.example.config.models;

public class LocatorConfig {
    private String type;
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocatorType getLocatorType() {
        if (type == null) {
            return LocatorType.XPATH;
        }
        try {
            return LocatorType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LocatorType.XPATH;
        }
    }

    public enum LocatorType {
        ID,
        XPATH,
        CLASS_NAME,
        ACCESSIBILITY_ID,
        ANDROID_UIAUTOMATOR
    }
}
