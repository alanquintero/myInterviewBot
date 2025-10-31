/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service.ai.model;

import com.myinterviewbot.model.PromptResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

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
     * The prompt is written directly to the process's standard input (STDIN)
     * to avoid shell piping overhead, which is particularly slow on Windows.
     *
     * @param prompt the text prompt to send to Ollama
     * @return the AI-generated response
     */
    @Override
    public PromptResponse executePrompt(final String prompt) {
        LOGGER.info("Running Ollama with model: {}", aiModel);
        LOGGER.info("Calling Ollama with the prompt: {}", prompt);
        boolean insufficientSystemRequirements = false;

        // Define the common executable name for the platform
        final String executable = System.getProperty("os.name").toLowerCase().contains("win") ? "ollama.exe" : "ollama";
        final ProcessBuilder pb = new ProcessBuilder(executable, "run", aiModel);

        // Set environment variables directly on the ProcessBuilder
        pb.environment().put("OLLAMA_NO_COLOR", "1");
        pb.environment().put("OLLAMA_SILENT", "1");
        pb.redirectErrorStream(true); // Redirect stderr to stdout

        // The ExecutorService is used to handle asynchronous reading of the output
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        Process process = null;

        try {
            // 1. Start the Ollama process
            process = pb.start();
            LOGGER.info("Ollama process started successfully using direct execution.");

            // 2. Write the prompt directly to the process's STDIN
            try (OutputStream os = process.getOutputStream()) {
                os.write(prompt.getBytes(StandardCharsets.UTF_8));
                os.flush();
            } // os.close() is called automatically here

            // 3. Set up the asynchronous reader
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );
            final StringBuilder output = new StringBuilder();
            final long startTime = System.currentTimeMillis();

            // Task to read the output asynchronously
            final Future<?> readerFuture = executor.submit(() -> {
                try {
                    int ch;
                    while ((ch = reader.read()) != -1) {
                        output.append((char) ch);
                        if (output.length() % 500 == 0) {
                            LOGGER.debug("Ollama partial output: {} chars", output.length());
                        }
                    }
                } catch (IOException e) {
                    // This catch block handles the expected closure/cancellation when process exits
                    if (!executor.isShutdown()) {
                        LOGGER.error("Error reading Ollama output", e);
                    }
                }
            });

            // 4. Wait for process to finish
            boolean finished = false;
            if (process.waitFor(90, TimeUnit.SECONDS)) {
                finished = true;
            } else {
                // If timeout, forcibly terminate the process
                process.destroyForcibly();
                LOGGER.error("Ollama process timed out after 90 seconds. Process forcibly destroyed.");
            }

            // 5. Cleanup and Logging
            final int exitValue = process.exitValue();
            LOGGER.info("Ollama process exited with code: {}; process finished: {}", exitValue, finished);
            final long duration = System.currentTimeMillis() - startTime;
            LOGGER.info("Ollama call completed in {} ms", duration);

            // Cancel the reader task, giving it a small moment to complete reading the buffer
            readerFuture.cancel(true);

            // Wait for the reader thread to actually stop (optional, but robust)
            try {
                readerFuture.get(100, TimeUnit.MILLISECONDS);
            } catch (TimeoutException | CancellationException e) {
                // Ignore if it takes too long to stop, as we are shutting down the executor next
                insufficientSystemRequirements = true;
            }

            final String rawResult = output.toString();
            LOGGER.debug("Full Ollama output (RAW):\n{}", rawResult);

            if (rawResult.isEmpty() || exitValue != 0) {
                if (exitValue != 0) {
                    LOGGER.error("Ollama process exited with non-zero code: {}", exitValue);
                } else {
                    LOGGER.warn("Ollama returned empty output.");
                }
                return new PromptResponse(insufficientSystemRequirements, "");
            }

            /*
             * Remove terminal and non-printable characters from the text.
             */
            final String cleanedResult = rawResult
                    // Remove ANSI color codes and cursor control sequences
                    .replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "")
                    // Remove other terminal control sequences (like [?25l, [?25h)
                    .replaceAll("\\[\\?\\d+[hl]", "")
                    // Remove non-printable characters (excluding common whitespace)
                    .replaceAll("[^\\p{Print}\\s]", "")
                    .trim();

            LOGGER.info("Clean Ollama output:\n{}", cleanedResult);
            return new PromptResponse(insufficientSystemRequirements, cleanedResult);

        } catch (InterruptedException e) {
            // Re-assert the interrupt flag
            Thread.currentThread().interrupt();
            LOGGER.error("Ollama process execution was interrupted", e);
            return new PromptResponse(insufficientSystemRequirements, "");
        } catch (Exception e) {
            LOGGER.error("Error running Ollama", e);
            return new PromptResponse(insufficientSystemRequirements, "");
        } finally {
            // Ensure the ExecutorService is always shut down
            if (executor != null) {
                executor.shutdownNow();
            }
            // Ensure process resources are closed if it was started
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }
}