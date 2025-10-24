/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Service responsible for handling video and audio operations using FFmpeg.
 *
 * <p>This service can extract audio from video files and potentially handle
 * other video/audio processing tasks.</p>
 *
 * @author Alan Quintero
 */
@Service
public class FfmpegService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FfmpegService.class);

    /**
     * Extracts the audio track from a given video file and saves it as an MP3 file.
     *
     * @param videoFile the video file from which to extract audio
     * @return the extracted audio file as an MP3
     * @throws IOException          if there is an error reading or writing files
     * @throws InterruptedException if the FFmpeg process is interrupted
     */
    public File extractAudio(final File videoFile) {
        LOGGER.info("Extracting audio file from video...");

        try {
            // Change extension to .mp3 regardless of input extension
            String baseName = videoFile.getName();
            if (baseName.contains(".")) {
                baseName = baseName.substring(0, baseName.lastIndexOf('.'));
            }
            final File audioFile = new File(videoFile.getParent(), baseName + ".mp3");

            final ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", videoFile.getAbsolutePath(),
                    "-vn",  // no video
                    "-acodec", "libmp3lame",
                    audioFile.getAbsolutePath()
            );

            pb.inheritIO();
            final Process process = pb.start();
            final int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg failed to extract audio from " + videoFile.getName());
            }
            LOGGER.info("Audio file extracted...");
            return audioFile;
        } catch (Exception e) {
            LOGGER.error("Failed while extracting audio.", e);
        }
        return null;
    }

}
