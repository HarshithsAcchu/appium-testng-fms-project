package com.example.config.models;

import java.util.List;
import java.util.Map;

public class TestConfig {
    // Legacy support - keep for backward compatibility
    private List<TestSuite> testSuites;
    
    // New structure - centralized data and flows
    private List<TestDataSet> testDataSets;
    private List<TestFlow> flows;
    private List<SuiteExecution> suiteExecutions;
    
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

    // New getters and setters
    public List<TestDataSet> getTestDataSets() {
        return testDataSets;
    }

    public void setTestDataSets(List<TestDataSet> testDataSets) {
        this.testDataSets = testDataSets;
    }

    public List<TestFlow> getFlows() {
        return flows;
    }

    public void setFlows(List<TestFlow> flows) {
        this.flows = flows;
    }

    public List<SuiteExecution> getSuiteExecutions() {
        return suiteExecutions;
    }

    public void setSuiteExecutions(List<SuiteExecution> suiteExecutions) {
        this.suiteExecutions = suiteExecutions;
    }
}
