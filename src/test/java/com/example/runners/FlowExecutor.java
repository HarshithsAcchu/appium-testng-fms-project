package com.example.runners;

import java.util.Map;

import com.example.actions.GenericActionExecutor;
import com.example.config.models.ExecutionStep;
import com.example.config.models.TestFlow;

import io.appium.java_client.android.AndroidDriver;

/**
 * Executes a test flow with given test data
 */
public class FlowExecutor {
    
    private final AndroidDriver driver;
    private final GenericActionExecutor actionExecutor;
    private final Map<String, String> testData;

    public FlowExecutor(AndroidDriver driver, Map<String, String> testData) {
        this.driver = driver;
        this.testData = testData;
        this.actionExecutor = new GenericActionExecutor(driver);
        this.actionExecutor.setTestData(testData);
    }

    /**
     * Execute a test flow
     */
    public void executeFlow(TestFlow flow) throws Exception {
        if (flow == null || flow.getSteps() == null) {
            throw new IllegalArgumentException("Invalid flow");
        }

        System.out.println("[FlowExecutor] Starting flow: " + flow.getFlowName());
        if (flow.getDescription() != null) {
            System.out.println("[FlowExecutor] Description: " + flow.getDescription());
        }

        for (ExecutionStep step : flow.getSteps()) {
            if (!step.isEnabled()) {
                System.out.println("[FlowExecutor] Step '" + step.getStepName() + "' is disabled. Skipping.");
                continue;
            }

            System.out.println("[FlowExecutor] Executing step: " + step.getStepName());
            executeStep(step);
        }

        System.out.println("[FlowExecutor] Flow '" + flow.getFlowName() + "' completed successfully");
    }

    /**
     * Execute individual step
     */
    private void executeStep(ExecutionStep step) throws Exception {
        if (step.getActionSteps() != null && !step.getActionSteps().isEmpty()) {
            step.getActionSteps().forEach(actionExecutor::executeAction);
        } else {
            System.out.println("[FlowExecutor] No action steps defined for: " + step.getStepName());
        }
    }
}
