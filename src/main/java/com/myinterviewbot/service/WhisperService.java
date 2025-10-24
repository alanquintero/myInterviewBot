/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Service responsible for transcribing audio files to text using Whisper AI.
 *
 * <p>This service handles communication with the Whisper transcription engine
 * and returns the text output of an audio file.</p>
 *
 * @author Alan Quintero
 */
@Service
public class WhisperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhisperService.class);

    /**
     * Transcribes a given audio file into text.
     *
     * @param audioFile the audio file to transcribe
     * @return the transcribed text from the audio file
     * @throws Exception            if there is an error accessing the audio file
     * @throws InterruptedException if the transcription process is interrupted
     * @throws RuntimeException     if the transcription fails or produces no output
     */
    public String transcribe(final File audioFile) throws Exception {
        LOGGER.info("Transcribing audio file...");

        final ProcessBuilder pb = new ProcessBuilder(
                "whisper",
                audioFile.getAbsolutePath(),
                "--model", "base",
                "--language", "en",
                "--output_format", "txt",
                "--output_dir", audioFile.getParent()
        );

        pb.redirectErrorStream(true);
        final Process process = pb.start();
        process.waitFor();

        // Whisper saves a file like filename.txt
        final String baseName = audioFile.getName().replaceFirst("\\.mp3$", "");
        final File transcriptFile = new File(audioFile.getParentFile(), baseName + ".txt");
        if (!transcriptFile.exists()) {
            throw new RuntimeException("Whisper transcription failed — no output file found.");
        }

        final String transcript = new String(java.nio.file.Files.readAllBytes(transcriptFile.toPath()));
        LOGGER.info("Text generated from audio file: {}", transcript);
        return transcript;
    }
}
