/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to store the prompt stats.
 *
 * @author Alan Quintero
 */
public class PromptStats {

    private boolean executedSuccessfully;

    private boolean slowPromptResponse;

    private boolean exceptionDetected;

    private String reasonExecutionFailed;

    private double secondsTakenToRespondPrompt;

    public PromptStats() {
    }

    public PromptStats(final boolean executedSuccessfully, final boolean slowPromptResponse, final boolean exceptionDetected) {
        this.executedSuccessfully = executedSuccessfully;
        this.slowPromptResponse = slowPromptResponse;
        this.exceptionDetected = exceptionDetected;
    }

    public boolean isExecutedSuccessfully() {
        return executedSuccessfully;
    }

    public void setExecutedSuccessfully(final boolean executedSuccessfully) {
        this.executedSuccessfully = executedSuccessfully;
    }

    public String getReasonExecutionFailed() {
        return reasonExecutionFailed;
    }

    public void setReasonExecutionFailed(final String reasonExecutionFailed) {
        this.reasonExecutionFailed = reasonExecutionFailed;
    }

    public boolean isSlowPromptResponse() {
        return slowPromptResponse;
    }

    public void setSlowPromptResponse(final boolean slowPromptResponse) {
        this.slowPromptResponse = slowPromptResponse;
    }

    public double getSecondsTakenToRespondPrompt() {
        return secondsTakenToRespondPrompt;
    }

    public void setSecondsTakenToRespondPrompt(final double secondsTakenToRespondPrompt) {
        this.secondsTakenToRespondPrompt = secondsTakenToRespondPrompt;
    }

    public boolean isExceptionDetected() {
        return exceptionDetected;
    }

    public void setExceptionDetected(final boolean exceptionDetected) {
        this.exceptionDetected = exceptionDetected;
    }

    @Override
    public String toString() {
        return "{" +
                "slowPromptResponse=" + slowPromptResponse +
                ", executedSuccessfully=" + executedSuccessfully +
                ", exceptionDetected=" + exceptionDetected +
                '}';
    }
}
