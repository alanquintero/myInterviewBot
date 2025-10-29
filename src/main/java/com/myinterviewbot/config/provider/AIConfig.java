/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.config.provider;

import com.myinterviewbot.service.ai.model.AIService;
import com.myinterviewbot.service.ai.model.OllamaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for initializing and providing the appropriate AI service
 * implementation based on the application settings.
 *
 * @author Alan Quintero
 */
@Configuration
public class AIConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AIConfig.class);

    @Value("${ai.provider}")
    private String aiProvider;

    /**
     * Creates and returns the appropriate AI service implementation based on the
     * {@code ai.provider} property.
     *
     * @return an instance of the corresponding AI service
     */
    @Bean
    public AIService aiService() {
        switch (aiProvider.toLowerCase()) {
            case "ollama":
                LOGGER.info("Using ollama AI");
                return new OllamaService();
            default:
                LOGGER.warn("AI Provider not supported yet! Using ollama AI");
                return new OllamaService();
        }
    }
}
