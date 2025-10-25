/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    void getTimestamp() {
        // Given
        final String filename = "1761356955742-answer.webm";

        // When
        final long timestamp = Utils.getTimestamp(filename);

        // Then
        assertEquals(1761356955742L, timestamp);
    }

    @Test
    void getTimestamp_fileNameDoesNotHaveTimestamp() {
        // Given
        final String filename = "answer.webm";

        // When
        final long timestamp = Utils.getTimestamp(filename);

        // Then
        assertEquals(0L, timestamp);
    }

    @Test
    void getVideoUrl() {
        // Given
        final String filename = "1761356955742-answer.webm";

        // When
        final String videoUrl = Utils.getVideoUrl(filename);

        // Then
        final String expectedUrl = Utils.INTERVIEWS_DIR + "1761356955742-answer/" + filename;
        assertEquals(expectedUrl, videoUrl);
    }

    @Test
    void getVideoUrl_fileNameDoesNotHaveExtension() {
        // Given
        final String filename = "1761356955742-answer";

        // When
        final String videoUrl = Utils.getVideoUrl(filename);

        // Then
        final String expectedUrl = Utils.INTERVIEWS_DIR + "1761356955742-answer/" + filename;
        assertEquals(expectedUrl, videoUrl);
    }
}
