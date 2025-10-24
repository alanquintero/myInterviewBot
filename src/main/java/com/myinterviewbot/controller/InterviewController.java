/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.FeedbackResponse;
import com.myinterviewbot.model.QuestionResponse;
import com.myinterviewbot.service.FfmpegService;
import com.myinterviewbot.service.OllamaService;
import com.myinterviewbot.service.WhisperService;
import com.myinterviewbot.utils.Utils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * REST controller for handling interview-related requests.
 *
 * <p>This controller manages the workflow of uploading video answers,
 * extracting audio, transcribing them, and generating AI feedback using
 * WhisperService and OllamaService.</p>
 *
 * @author Alan Quintero
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InterviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterviewController.class);

    private final OllamaService ollamaService;
    private final WhisperService whisperService;
    private final FfmpegService ffmpegService;

    /**
     * Generates an interview question for a specific profession.
     *
     * <p>The question is generated dynamically using the {@link OllamaService}.</p>
     *
     * @param profession the profession for which to generate a question
     * @return a {@link QuestionResponse} containing the generated question
     */
    @GetMapping("/question")
    public QuestionResponse getQuestion(@RequestParam("profession") final String profession, final HttpSession session) {
        LOGGER.info("/question profession: {}", profession);
        final String question = ollamaService.generateQuestion(profession, session);
        return new QuestionResponse(question);
    }

    /**
     * Receives a candidate's video answer, processes it, and returns feedback.
     *
     * <p>The process includes storing the video, extracting audio, transcribing
     * it to text, and sending the transcript to Ollama for evaluation.</p>
     *
     * @param file       the uploaded video file from the candidate
     * @param profession the candidate's profession
     * @param question   the question asked to the candidate
     * @return AI-generated feedback on the candidate's answer
     */
    @PostMapping(value = "/feedback", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FeedbackResponse getFeedback(@RequestParam("file") final MultipartFile file, @RequestParam("profession") final String profession, @RequestParam("question") final String question) throws Exception {
        LOGGER.info("/feedback file: {}; profession: {}; question: {}", file, profession, question);

        // Save file locally
        final File videoFile = Utils.saveVideo(file);
        if (videoFile == null) {
            return new FeedbackResponse("", "");
        }

        // Extract audio
        final File audioFile = ffmpegService.extractAudio(videoFile);
        if (audioFile == null) {
            return new FeedbackResponse("", "");
        }

        // Transcribe audio
        final String transcript = whisperService.transcribe(audioFile);
        if (transcript == null || transcript.isEmpty()) {
            return new FeedbackResponse("", "");
        }

        // Get AI feedback
        final String feedback = ollamaService.generateFeedback(transcript, profession, question);

        return new FeedbackResponse(feedback, transcript);
    }
}