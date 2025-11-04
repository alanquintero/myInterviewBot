/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.*;
import com.myinterviewbot.service.FfmpegService;
import com.myinterviewbot.service.InterviewDataService;
import com.myinterviewbot.service.PromptService;
import com.myinterviewbot.service.WhisperService;
import com.myinterviewbot.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
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
@RequestMapping("/interview/v1")
public class InterviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterviewController.class);

    private final PromptService promptService;
    private final WhisperService whisperService;
    private final FfmpegService ffmpegService;
    private final InterviewDataService interviewDataService;

    public InterviewController(final PromptService promptService, final WhisperService whisperService, final FfmpegService ffmpegService) {
        this.promptService = promptService;
        this.whisperService = whisperService;
        this.ffmpegService = ffmpegService;
        this.interviewDataService = InterviewDataService.getInstance();
    }

    /**
     * Generates an interview question for a specific profession.
     *
     * <p>The question is generated dynamically using the {@link PromptService}.</p>
     *
     * @param profession the profession for which to generate a question
     * @return a {@link QuestionResponse} containing the generated question
     */
    @GetMapping("/question")
    public PromptResponse getQuestion(@RequestParam("profession") final String profession, final HttpSession session) {
        LOGGER.info("/question profession: {}", profession);
        final PromptResponse promptResponse = promptService.generateQuestion(profession, session);
        promptResponse.setPromptResponse(new QuestionResponse(promptResponse.getPromptResponse().toString()));
        return promptResponse;
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


    /**
     * Receives a transcript, asks AI for feedback, and returns feedback.
     *
     * @param profession the candidate's profession
     * @param question   the question asked to the candidate
     * @return AI-generated feedback on the candidate's answer
     */
    @PostMapping(value = "/feedback")
    public PromptResponse getFeedback(@RequestParam("transcript") final String transcript, @RequestParam("profession") final String profession, @RequestParam("question") final String question) {
        LOGGER.info("/feedback transcript: {}, profession: {}; question: {}", transcript, profession, question);
        return promptService.generateFeedback(transcript, profession, question);
    }

    /**
     * Receives a transcript, asks AI for an evaluation, and returns evaluation.
     *
     * <p>The process includes storing the interview entry.</p>
     *
     * @param transcript the candidate's transcript
     * @param feedback   the AI generated feedback
     * @param profession the candidate's profession
     * @param question   the question asked to the candidate
     * @return AI-generated evaluation on the candidate's answer
     */
    @PostMapping(value = "/evaluation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PromptResponse getEvaluation(@RequestPart("transcript") final Transcript transcript, @RequestParam("feedback") final String feedback, @RequestParam("profession") final String profession, @RequestParam("question") final String question) {
        LOGGER.info("/evaluation transcript: {}, profession: {}; question: {}", transcript, profession, question);

        // Generating AI evaluation
        final PromptResponse promptResponse = promptService.generateEvaluation(transcript.getTranscript(), profession, question);
        final Evaluation evaluation = (Evaluation) promptResponse.getPromptResponse();

        // Saves the interview entry
        final long timestamp = Utils.getTimestamp(transcript.getFileName());
        final String videoUrl = Utils.getVideoUrl(transcript.getFileName());
        interviewDataService.addInterview(timestamp, new InterviewEntry(timestamp, InterviewType.BEHAVIORAL, profession, question, transcript.getTranscript(), feedback, videoUrl, evaluation));

        return promptResponse;
    }
}