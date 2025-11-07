/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.factory;

import com.myinterviewbot.builder.PromptStatsBuilder;
import com.myinterviewbot.model.PromptResponse;
import com.myinterviewbot.model.PromptStats;

/**
 * Factory class for creating {@link PromptResponse} instances.
 * <p>
 * This factory centralizes the creation of prompt responses,
 * including the construction of {@link PromptStats} with
 * computed metrics.
 * </p>
 *
 * @author Alan Quintero
 */
public class PromptResponseFactory {

    public static PromptResponse createSuccessResponse(final Object response, final long durationMs) {
        final PromptStats stats = new PromptStatsBuilder()
                .executedSuccessfully(true)
                .secondsTakenToRespondPrompt(durationMs / 1000.0)
                .build();

        return new PromptResponse(stats, response);
    }

    public static PromptResponse createFailedResponse(final Object response, final String reasonExecutionFailed, final long durationMs) {
        final PromptStats stats = new PromptStatsBuilder()
                .executedSuccessfully(false)
                .reasonExecutionFailed(reasonExecutionFailed)
                .secondsTakenToRespondPrompt(durationMs / 1000.0)
                .build();

        return new PromptResponse(stats, response);
    }
}
