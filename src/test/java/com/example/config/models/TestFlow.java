package com.example.config.models;

import java.util.List;

/**
 * Represents a reusable test flow (sequence of execution steps)
 */
public class TestFlow {
    private String flowName;
    private String description;
    private List<ExecutionStep> steps;

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ExecutionStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ExecutionStep> steps) {
        this.steps = steps;
    }
}
