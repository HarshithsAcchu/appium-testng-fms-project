package com.example.config.models;

import java.util.List;
import java.util.Map;

public class TestConfig {
    private List<TestSuite> testSuites;
    private Map<String, Map<String, LocatorConfig>> locators;
    private GlobalSettings globalSettings;

    public List<TestSuite> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(List<TestSuite> testSuites) {
        this.testSuites = testSuites;
    }

    public Map<String, Map<String, LocatorConfig>> getLocators() {
        return locators;
    }

    public void setLocators(Map<String, Map<String, LocatorConfig>> locators) {
        this.locators = locators;
    }

    public GlobalSettings getGlobalSettings() {
        return globalSettings;
    }

    public void setGlobalSettings(GlobalSettings globalSettings) {
        this.globalSettings = globalSettings;
    }

    public TestSuite getTestSuite(String suiteName) {
        if (testSuites == null) {
            return null;
        }
        return testSuites.stream()
                .filter(suite -> suite.getSuiteName().equals(suiteName))
                .findFirst()
                .orElse(null);
    }
}
