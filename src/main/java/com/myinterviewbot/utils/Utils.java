/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.utils;

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

    private final static String BASE_DIR = "uploads/interviews/";

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
     * the directory could not be created
     * @throws IOException if an error occurs while writing the file
     */
    public static File saveVideo(final MultipartFile file) throws IOException {
        final String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        final String baseName = filename.replaceFirst("\\.webm$", "");
        final File recordDir = new File(BASE_DIR + baseName);
        if (!recordDir.exists() && !recordDir.mkdirs()) {
            return null;
        }

        final File videoFile = new File(recordDir, filename);
        try (FileOutputStream fos = new FileOutputStream(videoFile)) {
            fos.write(file.getBytes());
        }

        return videoFile;
    }
}
