/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.builder;

import com.myinterviewbot.model.PromptExecutionResult;
import com.myinterviewbot.model.PromptStats;

/**
 * Builder class for creating instances of {@link PromptStats}.
 * <p>
 * This builder encapsulates the logic for calculating derived fields
 * (e.g., {@code slowPromptResponse}) so that service classes don't need
 * to duplicate that logic.
 * </p>
 *
 * @author Alan Quintero
 */
public class PromptStatsBuilder {

    private static final int SLOW_RESPONSE_SECONDS_THRESHOLD = 90;

    private boolean executedSuccessfully;
    private String reasonExecutionFailed;
    private double secondsTakenToRespondPrompt;

    public PromptStatsBuilder executedSuccessfully(final boolean executedSuccessfully) {
        this.executedSuccessfully = executedSuccessfully;
        return this;
    }

    public PromptStatsBuilder secondsTakenToRespondPrompt(final double secondsTakenToRespondPrompt) {
        this.secondsTakenToRespondPrompt = secondsTakenToRespondPrompt;
        return this;
    }

    public PromptStatsBuilder reasonExecutionFailed(final String reasonExecutionFailed) {
        this.reasonExecutionFailed = reasonExecutionFailed;
        return this;
    }

    public PromptStats build() {
        PromptStats stats = new PromptStats();
        stats.setExecutedSuccessfully(executedSuccessfully);
        stats.setSecondsTakenToRespondPrompt(secondsTakenToRespondPrompt);
        if (!executedSuccessfully) {
            stats.setReasonExecutionFailed(reasonExecutionFailed);
            if (!PromptExecutionResult.EMPTY_RESULT.equals(reasonExecutionFailed)) {
                stats.setExceptionDetected(true);
            }
        }

        // auto-compute derived flag
        stats.setSlowPromptResponse(secondsTakenToRespondPrompt >= SLOW_RESPONSE_SECONDS_THRESHOLD);
        return stats;
    }
}
