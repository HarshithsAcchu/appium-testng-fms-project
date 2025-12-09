package com.example.config.models;

import java.util.List;

/**
 * Represents a test suite execution plan with flow and data references
 */
public class SuiteExecution {
    private String suiteName;
    private boolean enabled;
    private String description;
    private String dataSetRef;  // Reference to testDataSet
    private List<String> flowRefs;  // References to flows to execute
    private boolean resetAfter;  // Whether to reset app after this suite

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataSetRef() {
        return dataSetRef;
    }

    public void setDataSetRef(String dataSetRef) {
        this.dataSetRef = dataSetRef;
    }

    public List<String> getFlowRefs() {
        return flowRefs;
    }

    public void setFlowRefs(List<String> flowRefs) {
        this.flowRefs = flowRefs;
    }

    public boolean isResetAfter() {
        return resetAfter;
    }

    public void setResetAfter(boolean resetAfter) {
        this.resetAfter = resetAfter;
    }
}
