/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.config.provider;

import com.myinterviewbot.service.whisper.OpenAiWhisper;
import com.myinterviewbot.service.whisper.Whisper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for initializing and providing the appropriate Whisper service implementation based on the application settings.
 *
 * @author Alan Quintero
 */
@Configuration
public class WhisperConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhisperConfig.class);

    @Value("${whisper.provider}")
    private String whisperProvider;

    /**
     * Creates and returns the appropriate Whisper service implementation based on the
     * {@code whisper.provider} property.
     *
     * @return an instance of the corresponding Whisper service
     */
    @Bean
    public Whisper whisper() {
        switch (whisperProvider.toLowerCase()) {
            case "openai-whisper":
                LOGGER.info("Using openai-whisper");
                return new OpenAiWhisper();
            default:
                LOGGER.warn("Whisper Provider not supported yet! Using openai-whisper");
                return new OpenAiWhisper();
        }
    }
}
