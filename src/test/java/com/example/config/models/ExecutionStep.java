package com.example.config.models;

import java.util.List;

public class ExecutionStep {
    private String stepName;
    private boolean enabled;
    private String page;
    private String action;
    private List<String> actions;
    private boolean useCredentials;
    
    // NEW: Generic action-driven approach
    private List<ActionStep> actionSteps;  // List of generic actions

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public boolean isUseCredentials() {
        return useCredentials;
    }

    public void setUseCredentials(boolean useCredentials) {
        this.useCredentials = useCredentials;
    }

    public List<ActionStep> getActionSteps() {
        return actionSteps;
    }

    public void setActionSteps(List<ActionStep> actionSteps) {
        this.actionSteps = actionSteps;
    }
}
