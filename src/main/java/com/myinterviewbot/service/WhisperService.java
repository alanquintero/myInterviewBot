/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.myinterviewbot.service.whisper.Whisper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Service responsible for transcribing audio files to text.
 *
 * <p>This service handles communication with the Whisper transcription engine
 * and returns the text output of an audio file.</p>
 *
 * @author Alan Quintero
 */
@Service
public class WhisperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhisperService.class);

    @Autowired
    private Whisper whisper;

    /**
     * Transcribes a given audio file into text.
     *
     * @param audioFile the audio file to transcribe
     * @return the transcribed text from the audio file
     */
    public String transcribe(final File audioFile) {
        LOGGER.info("Transcribing audio file...");
        return whisper.transcribe(audioFile);
    }
}
