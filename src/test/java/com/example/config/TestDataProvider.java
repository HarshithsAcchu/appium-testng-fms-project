package com.example.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Provides test data from external JSON file (test-data.json)
 * Data is organized by suite name, each suite can have multiple data rows
 */
public class TestDataProvider {
    
    private static TestDataProvider instance;
    private Map<String, List<Map<String, String>>> testDataMap;
    private static final String DEFAULT_DATA_FILE = "test-data.json";

    private TestDataProvider() {
        loadTestData(DEFAULT_DATA_FILE);
    }

    public static synchronized TestDataProvider getInstance() {
        if (instance == null) {
            instance = new TestDataProvider();
        }
        return instance;
    }

    /**
     * Load test data from JSON file
     */
    private void loadTestData(String dataFile) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dataFile);
            if (inputStream == null) {
                System.err.println("[TestDataProvider] Test data file not found: " + dataFile);
                testDataMap = new HashMap<>();
                return;
            }

            try (Reader reader = new InputStreamReader(inputStream)) {
                // Parse JSON into Map<String, List<Map<String, String>>>
                TypeToken<Map<String, List<Map<String, String>>>> typeToken = 
                    new TypeToken<Map<String, List<Map<String, String>>>>() {};
                testDataMap = gson.fromJson(reader, typeToken.getType());
                
                System.out.println("[TestDataProvider] Test data loaded successfully from: " + dataFile);
                if (testDataMap != null) {
                    testDataMap.forEach((suiteName, dataList) -> {
                        System.out.println("[TestDataProvider]   Suite '" + suiteName + "' has " + dataList.size() + " data row(s)");
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("[TestDataProvider] Failed to load test data: " + e.getMessage());
            e.printStackTrace();
            testDataMap = new HashMap<>();
        }
    }

    /**
     * Get all test data rows for a specific suite
     * @param suiteName Name of the test suite
     * @return List of data maps, each map represents one data row
     */
    public List<Map<String, String>> getTestDataForSuite(String suiteName) {
        if (testDataMap == null || !testDataMap.containsKey(suiteName)) {
            System.out.println("[TestDataProvider] No test data found for suite: " + suiteName);
            return new ArrayList<>();
        }
        return testDataMap.get(suiteName);
    }

    /**
     * Get a specific data row by index
     * @param suiteName Name of the test suite
     * @param index Index of the data row (0-based)
     * @return Data map for that row, or null if not found
     */
    public Map<String, String> getTestDataRow(String suiteName, int index) {
        List<Map<String, String>> dataList = getTestDataForSuite(suiteName);
        if (dataList == null || index < 0 || index >= dataList.size()) {
            return null;
        }
        return dataList.get(index);
    }

    /**
     * Get count of data rows for a suite
     * @param suiteName Name of the test suite
     * @return Number of data rows
     */
    public int getDataRowCount(String suiteName) {
        List<Map<String, String>> dataList = getTestDataForSuite(suiteName);
        return dataList != null ? dataList.size() : 0;
    }

    /**
     * Check if suite has test data
     * @param suiteName Name of the test suite
     * @return true if data exists
     */
    public boolean hasTestData(String suiteName) {
        return testDataMap != null && testDataMap.containsKey(suiteName) && 
               !testDataMap.get(suiteName).isEmpty();
    }

    /**
     * Get all suite names that have test data
     * @return List of suite names
     */
    public List<String> getAllSuiteNames() {
        if (testDataMap == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(testDataMap.keySet());
    }
}
