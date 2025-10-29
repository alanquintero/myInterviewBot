/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service.whisper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Service responsible for using Open-AI Whisper to transcribe audio to text.
 *
 * @author Alan Quintero
 */
@Service
public class OpenAiWhisper implements Whisper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAiWhisper.class);

    @Override
    public String transcribe(final File audioFile) {
        LOGGER.info("Transcribing audio file...");
        try {
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
        } catch (Exception e) {
            LOGGER.error("Whisper transcription failed.", e);
        }
        return null;
    }
}
