/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myinterviewbot.model.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class providing helper methods for handling interview video files.
 *
 * @author Alan Quintero
 */
public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public final static String INTERVIEWS_DIR = "uploads/interviews/";

    /**
     * Saves an uploaded video file to a timestamped directory under "uploads/interviews/".
     *
     * <p>The method generates a directory based on the current timestamp and the original
     * filename (without the ".webm" extension), creates the directory if it does not exist,
     * and then writes the contents of the uploaded file into this directory.</p>
     *
     * <p>Example:</p>
     * <pre>
     * Uploaded file: "answer.webm"
     * Saved path: "uploads/interviews/1699999999999-answer/1699999999999-answer.webm"
     * </pre>
     *
     * @param file the uploaded video file
     * @return the {@link File} object pointing to the saved video, or {@code null} if
     * the directory could not be created.
     */
    public static File saveVideo(final MultipartFile file) {
        try {
            // Ensure base upload directory exists
            final File uploadsDir = new File(INTERVIEWS_DIR);
            if (!uploadsDir.exists() && !uploadsDir.mkdirs()) {
                throw new IOException("Failed to create uploads/interviews directory");
            }

            // Generate timestamped filename and directory
            final String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            final String baseName = filename.replaceFirst("\\.webm$", "");
            final File recordDir = new File(INTERVIEWS_DIR + baseName);
            if (!recordDir.exists() && !recordDir.mkdirs()) {
                throw new IOException("Failed to create record directory: " + recordDir.getAbsolutePath());
            }

            // Save video file
            final File videoFile = new File(recordDir, filename);
            try (FileOutputStream fos = new FileOutputStream(videoFile)) {
                fos.write(file.getBytes());
            }
            return videoFile;
        } catch (Exception e) {
            LOGGER.error("Failed while saving file.", e);
        }
        return null;
    }


    /**
     * Deletes all interview folders inside the uploads/interviews directory.
     */
    public static String clearAllInterviews() {
        final File baseDir = new File(INTERVIEWS_DIR);

        if (!baseDir.exists()) {
            LOGGER.warn("Base directory does not exist: {}", INTERVIEWS_DIR);
            return "No interviews found to delete.";
        }

        final boolean success = deleteDirectoryContents(baseDir);
        if (success) {
            LOGGER.info("All interviews cleared successfully.");
            return "All interviews have been deleted successfully.";
        } else {
            LOGGER.error("Failed to delete some or all interview files.");
            return "Failed to delete all interviews. Some files may remain.";
        }
    }

    /**
     * Recursively deletes all files and subdirectories inside a directory,
     * but keeps the base directory itself.
     *
     * @param directory the parent directory to clear
     * @return true if everything was deleted successfully, false otherwise
     */
    private static boolean deleteDirectoryContents(final File directory) {
        final File[] files = directory.listFiles();
        if (files == null) {
            return false;
        }

        boolean allDeleted = true;
        for (final File file : files) {
            if (file.isDirectory()) {
                allDeleted &= deleteDirectoryContents(file);
            }
            if (!file.delete()) {
                LOGGER.warn("Could not delete: {}", file.getAbsolutePath());
                allDeleted = false;
            }
        }
        return allDeleted;
    }

    /**
     * Gets the timestamp from the given filename.
     *
     * @param filename the filename that has a timestamp in the name.
     * @return the timestamp
     */
    public static Long getTimestamp(final String filename) {
        // Remove extension
        final int dotIndex = filename.indexOf('.');
        final String nameWithoutExt = (dotIndex != -1) ? filename.substring(0, dotIndex) : filename;

        // Keep only digits (timestamp)
        final String timestampStr = nameWithoutExt.replaceAll("\\D+", ""); // removes non-digit chars

        try {
            return Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Constructs and return the video url of the given filename
     *
     * @param filename the filename
     * @return the video url
     */
    public static String getVideoUrl(final String filename) {
        // Remove extension
        final int dotIndex = filename.indexOf('.');
        final String nameWithoutExt = (dotIndex != -1) ? filename.substring(0, dotIndex) : filename;

        return INTERVIEWS_DIR + nameWithoutExt + "/" + filename;
    }

    public static int countWords(final String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int wordCount = 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                wordCount++;
            }
        }
        return wordCount;
    }

    /**
     * Removes all double quotes from the given response string.
     *
     * @param response The input string possibly containing double quotes.
     * @return A new string with all double quotes removed.
     */
    public static String removeQuotes(String response) {
        if (response == null) {
            return null;
        }
        return response.replace("\"", "");
    }

    /**
     * Removes all double quotes and adds a newline before numbered items (e.g., "1.", "2.", etc.).
     * <p>
     * Example:
     * Input:  "Here is a list: 1. Do this 2. Do that"
     * Output: "Here is a list:\n1. Do this\n2. Do that"
     *
     * @param response The input string possibly containing double quotes and numbered items.
     * @return A formatted string without quotes and with newlines before numbers.
     */
    public static String removeQuotesAndFormatList(String response) {
        if (response == null) {
            return null;
        }

        // Remove quotes
        String cleaned = removeQuotes(response);

        // Add newline before a number with dot (e.g., "1.") or a special character preceded by space
        cleaned = cleaned.replaceAll("(?<=\\s)((\\d+\\.)|[-*•])", "\n$1");

        // Add newline between a dot possibly inside quotes and a following special character
        // Matches: .-   or .'-
        cleaned = cleaned.replaceAll("(\\.'?|\\.)\\s*([\\-*•])", "$1\n$2");

        return cleaned;
    }

    /**
     * Extracts the JSON from the given String (if valid).
     *
     * @param input string with JSON
     * @return JSON
     */
    public static String extractJson(final String input) {
        int start = input.indexOf('{');
        int end = input.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return input.substring(start, end + 1).trim();
        }
        return null;
    }

    public static String extractQuestion(final String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        int dotIndex = text.indexOf(".");
        int questionIndex = text.indexOf("?");

        // Take the first occurring punctuation
        int endIndex = -1;
        if (dotIndex >= 0 && questionIndex >= 0) {
            endIndex = Math.min(dotIndex, questionIndex);
        } else if (dotIndex >= 0) {
            endIndex = dotIndex;
        } else if (questionIndex >= 0) {
            endIndex = questionIndex;
        }

        if (endIndex >= 0) {
            return text.substring(0, endIndex + 1).trim();
        }

        // fallback: return full text
        return text.trim();
    }

    /**
     * Returns the name of the operating system the user is currently using.
     *
     * @return A String containing the operating system name (e.g., "Windows 10", "Mac OS X", "Linux").
     */
    public static String getOperatingSystemName() {
        // The "os.name" system property contains the full name of the operating system.
        return System.getProperty("os.name");
    }

    public static Evaluation generateEvaluation(final String evaluationTxt) {
        final String evaluationJson = Utils.extractJson(evaluationTxt);

        LOGGER.info("Evaluation JSON: {}", evaluationJson);
        if (evaluationJson == null) {
            LOGGER.warn("Evaluation JSON not found in evaluation output: {}", evaluationTxt);
            return null;
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final Evaluation evaluation = mapper.readValue(evaluationJson, Evaluation.class);
            validateFeedback(evaluation);
            return evaluation;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.warn("Evaluation failed. Please try again later.");
        return null;
    }

    private static void validateFeedback(final Evaluation evaluation) {
        if (evaluation == null) {
            return;
        }
        // Clarity
        if (evaluation.getClarityFeedback() == null || evaluation.getClarityFeedback().isEmpty()) {
            evaluation.setClarityScore(0);
        }
        // Structure
        if (evaluation.getStructureFeedback() == null || evaluation.getStructureFeedback().isEmpty()) {
            evaluation.setStructureScore(0);
        }
        // Relevance
        if (evaluation.getRelevanceFeedback() == null || evaluation.getRelevanceFeedback().isEmpty()) {
            evaluation.setRelevanceScore(0);
        }
        // Communication
        if (evaluation.getCommunicationFeedback() == null || evaluation.getCommunicationFeedback().isEmpty()) {
            evaluation.setCommunicationScore(0);
        }
        // Depth
        if (evaluation.getDepthFeedback() == null || evaluation.getDepthFeedback().isEmpty()) {
            evaluation.setDepthScore(0);
        }
    }
}
