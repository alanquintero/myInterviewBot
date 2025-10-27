/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service.ai.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for sending prompts to the Ollama AI model and retrieving responses.
 *
 * @author Alan Quintero
 */
@Service
public class OllamaService implements AIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OllamaService.class);

    @Value("${ai.model}")
    private String aiModel;

    /**
     * Calls the Ollama AI model with a given prompt and returns the response.
     *
     * @param prompt the text prompt to send to Ollama
     * @return the AI-generated response
     */
    @Override
    public String executePrompt(final String prompt) {
        LOGGER.info("Running Ollama with model: {}", aiModel);
        LOGGER.info("Calling Ollama with the prompt: {}", prompt);
        try {
            final ProcessBuilder pb;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                LOGGER.info("Creating Windows command");
                // Windows execution using cmd.exe
                final String windowsCommand = "echo " + prompt.replace("\"", "\\\"") + " | ollama run " + aiModel;
                final String[] cmdArgs = {"cmd.exe", "/c", windowsCommand};
                pb = new ProcessBuilder(cmdArgs);
                pb.environment().put("OLLAMA_NO_COLOR", "1");
                pb.environment().put("OLLAMA_SILENT", "1");
                LOGGER.info("Executing Windows command: {}", windowsCommand);
            } else {
                LOGGER.info("Creating Unix/Linux/Mac command");
                // Unix/Linux/Mac execution using bash
                final String command = "echo \"" + prompt.replace("\"", "\\\"") + "\" | OLLAMA_NO_COLOR=1 OLLAMA_SILENT=1 ollama run " + aiModel;
                final String[] bashArgs = {"bash", "-c", command};
                pb = new ProcessBuilder(bashArgs);
                pb.redirectErrorStream(true);
                LOGGER.info("Executing command: {}", command);
            }
            pb.redirectErrorStream(true);

            final Process process = pb.start();
            LOGGER.info("Ollama process started successfully.");

            // Reader to capture process output
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            final StringBuilder output = new StringBuilder();
            final long startTime = System.currentTimeMillis();
            boolean finished = false;

            // Read asynchronously to avoid blocking indefinitely
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<?> readerFuture = executor.submit(() -> {
                try {
                    int ch;
                    while ((ch = reader.read()) != -1) {
                        char c = (char) ch;
                        output.append(c);
                        if (output.length() % 300 == 0) {
                            LOGGER.debug("Ollama partial output: {} chars", output.length());
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error("Error reading Ollama output", e);
                }
            });

            // Wait for process to finish (max 90 seconds)
            if (process.waitFor(90, TimeUnit.SECONDS)) {
                finished = true;
            } else {
                process.destroyForcibly();
                LOGGER.error("Ollama process timed out after 90 seconds");
            }

            // Stop the reader
            readerFuture.cancel(true);
            executor.shutdownNow();

            LOGGER.info("Ollama process exited with code: {}; process finished: {}", process.exitValue(), finished);
            final long duration = System.currentTimeMillis() - startTime;
            LOGGER.info("Ollama call completed in {} ms", duration);

            final String result = output.toString().trim();
            LOGGER.debug("Full Ollama output:\n{}", result);

            if (result.isEmpty() || process.exitValue() != 0) {
                return "";
            }

            /*
             * Remove any special characters from the text, including those inserted
             * by the AI during "thinking" or processing, which may appear in the output.
             */
            final String cleanedResult = output.toString()
                    // Remove ANSI color codes and cursor control sequences
                    .replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "")
                    // Remove other terminal control sequences (like [?25l, [?25h)
                    .replaceAll("\\[\\?\\d+[hl]", "")
                    // Remove non-printable characters
                    .replaceAll("[^\\p{Print}]", "")
                    .trim();

            LOGGER.info("Clean Ollama output:\n{}", cleanedResult);
            return cleanedResult;

        } catch (Exception e) {
            LOGGER.error("Error running Ollama", e);
            return "";
        }
    }
}
