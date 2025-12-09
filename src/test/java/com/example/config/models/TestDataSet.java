package com.example.config.models;

import java.util.Map;

/**
 * Represents a named test data set that can be referenced by test suites
 */
public class TestDataSet {
    private String dataSetName;
    private String description;
    private Map<String, String> data;

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getValue(String key) {
        return data != null ? data.get(key) : null;
    }
}
