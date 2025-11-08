/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.SystemRequirements;
import com.myinterviewbot.model.Transcript;
import com.myinterviewbot.service.FfmpegService;
import com.myinterviewbot.service.WhisperService;
import com.myinterviewbot.system.SystemChecker;
import com.myinterviewbot.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * REST controller for handling general requests.
 *
 * @author Alan Quintero
 */
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    private static SystemRequirements systemRequirements = null;

    private final SystemChecker systemChecker;

    private final WhisperService whisperService;

    private final FfmpegService ffmpegService;

    public ApiController(final SystemChecker systemChecker, final WhisperService whisperService, final FfmpegService ffmpegService) {
        this.systemChecker = systemChecker;
        this.whisperService = whisperService;
        this.ffmpegService = ffmpegService;
    }

    @GetMapping("/requirements")
    public SystemRequirements checkSystemRequirements(final HttpSession session) {
        LOGGER.info("Checking SystemRequirements");
        if (systemRequirements == null) {
            systemRequirements = systemChecker.checkSystemRequirements();
        }
        return systemRequirements;
    }

    /**
     * Receives a candidate's video answer, processes it, and returns the transcript.
     *
     * <p>The process includes storing the video, extracting audio, transcribing
     * it to text, and sending back the transcript.</p>
     *
     * @param file the uploaded video file from the candidate
     * @return the transcript
     */
    @PostMapping(value = "/transcript", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Transcript getTranscript(@RequestParam("file") final MultipartFile file) {
        LOGGER.info("/transcript file: {}", file);
        // Save file locally
        final File videoFile = Utils.saveVideo(file);
        if (videoFile == null) {
            return new Transcript();
        }

        // Extract audio
        final File audioFile = ffmpegService.extractAudio(videoFile);
        if (audioFile == null) {
            return new Transcript();
        }

        // Transcribe audio
        final String transcript = whisperService.transcribe(audioFile);
        if (transcript == null) {
            return new Transcript();
        }

        return new Transcript(transcript, videoFile.getName());
    }
}
