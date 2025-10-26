/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for customizing Spring MVC settings.
 * <p>
 * This configuration exposes static resources located in the local
 * {@code uploads/} directory so they can be accessed via HTTP requests.
 * For example, a file stored at {@code uploads/interviews/video.webm}
 * will be accessible at {@code http://localhost:8080/uploads/interviews/video.webm}.
 * </p>
 *
 * <p>
 * This is useful for serving uploaded media files (such as recorded interview videos)
 * directly from the file system without needing a dedicated controller.
 * </p>
 *
 * @author Alan Quintero
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Adds a custom resource handler to serve uploaded files from the local file system.
     *
     * @param registry the {@link ResourceHandlerRegistry} used to register the resource handler
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
