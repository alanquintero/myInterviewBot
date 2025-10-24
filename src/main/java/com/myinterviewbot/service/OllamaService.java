/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import jakarta.servlet.http.HttpSession;
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
 * <p>This service formats candidate answers, sends them to Ollama, and
 * returns the AI-generated evaluation or feedback.</p>
 *
 * @author Alan Quintero
 */
@Service
public class OllamaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OllamaService.class);


    @Value("${interviewbot.default-model}")
    private String defaultModel;

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(final String model) {
        this.defaultModel = model;
    }

    public String generateQuestion(final String profession, final HttpSession session) {
        LOGGER.info("Generating question for profession: {}", profession);

        String lastProfession = (String) session.getAttribute("currentProfession");
        Boolean firstQuestion = (Boolean) session.getAttribute("firstQuestion");

        // If profession changed, reset session info
        if (lastProfession == null || !lastProfession.equals(profession)) {
            session.setAttribute("currentProfession", profession);
            session.setAttribute("firstQuestion", true);
            firstQuestion = true;
        }

        String prompt;
        if (firstQuestion == null || firstQuestion) {
            prompt = "You are a concise behavioral interview coach. Generate a single, realistic behavioral interview question for a " + profession + ". The question must be less than 15 words. Only output the question.";
            session.setAttribute("firstQuestion", false);
        } else {
            prompt = "Give me another behavioral interview question for a " + profession + ".";
        }

        return runOllama(prompt);
    }

    public String generateFeedback(final String transcript, final String profession, final String question) {
        final String prompt = "You are a technical hiring manager. Evaluate the following interview answer, focusing on clarity, structure, relevance, and communication style. "
                + "Provide actionable feedback in 3–4 concise sentences, output only the feedback, no extra commentary. "
                + "Candidate profession: " + profession + ". "
                + "Question: " + question + ". "
                + "Candidate answer: " + transcript;
        return runOllama(prompt);
    }

    /**
     * Calls the Ollama AI model with a given prompt and returns the response.
     *
     * @param prompt the text prompt to send to Ollama
     * @return the AI-generated response
     */
    private String runOllama(final String prompt) {
        LOGGER.info("Running Ollama with model: {}", defaultModel);
        LOGGER.info("Calling Ollama with the prompt: {}", prompt);
        try {
            final String command = "echo \"" + prompt.replace("\"", "\\\"") + "\" | OLLAMA_NO_COLOR=1 OLLAMA_SILENT=1 ollama run " + defaultModel;
            LOGGER.info("Executing command: {}", command);
            final ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
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

            if (result.isEmpty()) {
                return "No response received from Ollama.";
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
            return "Error running Ollama: " + e.getMessage();
        }
    }
}
