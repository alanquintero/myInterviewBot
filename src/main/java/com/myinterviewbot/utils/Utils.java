/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.utils;

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
        File baseDir = new File(INTERVIEWS_DIR);

        if (!baseDir.exists()) {
            LOGGER.warn("Base directory does not exist: {}", INTERVIEWS_DIR);
            return "No interviews found to delete.";
        }

        boolean success = deleteDirectoryContents(baseDir);
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
    private static boolean deleteDirectoryContents(File directory) {
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
}
