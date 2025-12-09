package com.example.reporting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Test execution report
 */
public class TestReport {
    private String suiteName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // PASSED, FAILED, SKIPPED
    private String failureReason;
    private String failureStep;
    private String stackTrace;
    private List<StepResult> stepResults;

    public TestReport(String suiteName) {
        this.suiteName = suiteName;
        this.startTime = LocalDateTime.now();
        this.stepResults = new ArrayList<>();
    }

    public void markCompleted(String status) {
        this.endTime = LocalDateTime.now();
        this.status = status;
    }

    public void setFailure(String step, String reason, String stackTrace) {
        this.failureStep = step;
        this.failureReason = reason;
        this.stackTrace = stackTrace;
        this.status = "FAILED";
    }

    public void addStepResult(String stepName, boolean passed, String message) {
        stepResults.add(new StepResult(stepName, passed, message));
    }

    public String getSuiteName() {
        return suiteName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getFailureStep() {
        return failureStep;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public List<StepResult> getStepResults() {
        return stepResults;
    }

    public long getDurationMs() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).toMillis();
        }
        return 0;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(80)).append("\n");
        sb.append("TEST SUITE REPORT: ").append(suiteName).append("\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Start Time: ").append(startTime.format(formatter)).append("\n");
        if (endTime != null) {
            sb.append("End Time: ").append(endTime.format(formatter)).append("\n");
            sb.append("Duration: ").append(getDurationMs()).append(" ms\n");
        }
        
        if ("FAILED".equals(status)) {
            sb.append("\nFAILURE DETAILS:\n");
            sb.append("Failed Step: ").append(failureStep).append("\n");
            sb.append("Reason: ").append(failureReason).append("\n");
            if (stackTrace != null) {
                sb.append("\nStack Trace:\n").append(stackTrace).append("\n");
            }
        }
        
        sb.append("\nSTEP RESULTS:\n");
        for (StepResult result : stepResults) {
            sb.append("  [").append(result.isPassed() ? "PASS" : "FAIL").append("] ")
              .append(result.getStepName());
            if (result.getMessage() != null) {
                sb.append(" - ").append(result.getMessage());
            }
            sb.append("\n");
        }
        
        sb.append("=".repeat(80)).append("\n");
        return sb.toString();
    }

    public static class StepResult {
        private String stepName;
        private boolean passed;
        private String message;
        private LocalDateTime timestamp;

        public StepResult(String stepName, boolean passed, String message) {
            this.stepName = stepName;
            this.passed = passed;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        public String getStepName() {
            return stepName;
        }

        public boolean isPassed() {
            return passed;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
