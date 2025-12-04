package com.example.config.models;

import java.util.List;
import java.util.Map;

public class TestSuite {
    private String suiteName;
    private boolean enabled;
    private Credentials credentials;
    private Map<String, String> testData;
    private List<ExecutionStep> executionFlow;

    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Map<String, String> getTestData() {
        return testData;
    }

    public void setTestData(Map<String, String> testData) {
        this.testData = testData;
    }

    public List<ExecutionStep> getExecutionFlow() {
        return executionFlow;
    }

    public void setExecutionFlow(List<ExecutionStep> executionFlow) {
        this.executionFlow = executionFlow;
    }

    public String getTestData(String key) {
        return testData != null ? testData.get(key) : null;
    }
}
