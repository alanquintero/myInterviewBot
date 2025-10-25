/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.myinterviewbot.utils.Utils;
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
public class AIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AIService.class);

    private static final int QUESTION_MAX_NUMBER_OF_WORDS = 20;
    private static final int FEEDBACK_MAX_NUMBER_OF_WORDS = 200;
    private static final int MAX_NUMBER_OF_ATTEMPTS = 3;

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
            prompt = "Give me another behavioral interview question for a " + profession + ". Remember that the question must be less than 15 words. Only output the question.";
        }

        /*
            Sometimes the model response with a very long question, the next code will try to avoid returning a long question by asking the model to generate another question.
            This process will be repeated a maximum of three time, hope the model can generate a good question.
        */
        String question = runOllama(prompt);
        int words = Utils.countWords(question);
        if (words > QUESTION_MAX_NUMBER_OF_WORDS) {
            LOGGER.warn("⚠︎⚠︎⚠︎ Question has more than " + QUESTION_MAX_NUMBER_OF_WORDS + " words, asking model to generate another question...");
            int requestNewAnswer = 0;
            while (requestNewAnswer < MAX_NUMBER_OF_ATTEMPTS) {
                requestNewAnswer++;
                prompt = "Please provide the next behavioral interview question in " + QUESTION_MAX_NUMBER_OF_WORDS + " words or less: " + question;
                question = runOllama(prompt);
                words = Utils.countWords(question);
                if (words <= QUESTION_MAX_NUMBER_OF_WORDS) {
                    break;
                } else {
                    LOGGER.warn("⚠︎⚠︎⚠︎ New generated question has more than " + QUESTION_MAX_NUMBER_OF_WORDS + " words, asking model to generate another question...");
                }
            }
        }
        words = Utils.countWords(question);
        if (words > QUESTION_MAX_NUMBER_OF_WORDS) {
            LOGGER.warn("⚠︎⚠︎⚠︎ The question has more than " + QUESTION_MAX_NUMBER_OF_WORDS + " words!");
        } else {
            LOGGER.info("Number of words in the question: {}", words);
        }

        return question;
    }

    public String generateFeedback(final String transcript, final String profession, final String question) {
        String prompt = "You are a technical hiring manager. Evaluate the following interview answer, focusing on clarity, structure, relevance, and communication style. "
                + "Provide actionable feedback in 3–4 concise sentences, output only the feedback, no extra commentary. "
                + "Candidate profession: " + profession + ". "
                + "Question: " + question + ". "
                + "Candidate answer: " + transcript;

        String feedback = runOllama(prompt);

        /*
            Sometimes the model response with a very long feedback, the next code will try to avoid returning a long feedback by asking the model to generate another feedback.
            This process will be repeated a maximum of three time, hope the model can generate a good and short feedback.
        */
        int words = Utils.countWords(feedback);
        if (words > FEEDBACK_MAX_NUMBER_OF_WORDS) {
            LOGGER.warn("⚠︎⚠︎⚠︎ Feedback has more than " + FEEDBACK_MAX_NUMBER_OF_WORDS + " words, asking model to generate another feedback...");
            int requestNewFeedback = 0;
            while (requestNewFeedback < MAX_NUMBER_OF_ATTEMPTS) {
                requestNewFeedback++;
                prompt = "Please provide the next feedback in " + FEEDBACK_MAX_NUMBER_OF_WORDS + " words or less: " + feedback;

                feedback = runOllama(prompt);
                words = Utils.countWords(feedback);
                if (words <= FEEDBACK_MAX_NUMBER_OF_WORDS) {
                    break;
                } else {
                    LOGGER.warn("⚠︎⚠︎⚠︎ New generated feedback has more than " + FEEDBACK_MAX_NUMBER_OF_WORDS + "  words, asking model to generate another feedback...");
                }
            }
        }
        words = Utils.countWords(feedback);
        if (words > FEEDBACK_MAX_NUMBER_OF_WORDS) {
            LOGGER.warn("⚠︎⚠︎⚠︎ The feedback has more than " + FEEDBACK_MAX_NUMBER_OF_WORDS + " words!");
        } else {
            LOGGER.info("Number of words in the feedback: {}", words);
        }
        return feedback;
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
            final ProcessBuilder pb;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                LOGGER.info("Creating Windows command");
                // Windows execution using cmd.exe
                final String windowsCommand = "echo " + prompt.replace("\"", "\\\"") + " | ollama run " + defaultModel;
                final String[] cmdArgs = {"cmd.exe", "/c", windowsCommand};
                pb = new ProcessBuilder(cmdArgs);
                pb.environment().put("OLLAMA_NO_COLOR", "1");
                pb.environment().put("OLLAMA_SILENT", "1");
                LOGGER.info("Executing Windows command: {}", windowsCommand);
            } else {
                LOGGER.info("Creating Unix/Linux/Mac command");
                // Unix/Linux/Mac execution using bash
                final String command = "echo \"" + prompt.replace("\"", "\\\"") + "\" | OLLAMA_NO_COLOR=1 OLLAMA_SILENT=1 ollama run " + defaultModel;
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
