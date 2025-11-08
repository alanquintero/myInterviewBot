/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.*;
import com.myinterviewbot.service.InterviewDataService;
import com.myinterviewbot.service.PromptService;
import com.myinterviewbot.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling prompt-related requests.
 *
 * <p>This controller manages the workflow of uploading video answers,
 * extracting audio, transcribing them, and generating AI feedback using
 * WhisperService and OllamaService.</p>
 *
 * @author Alan Quintero
 */
@RestController
@RequestMapping("/prompt/v1")
public class PromptController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptController.class);

    private final PromptService promptService;
    private final InterviewDataService interviewDataService;

    public PromptController(final PromptService promptService) {
        this.promptService = promptService;
        this.interviewDataService = InterviewDataService.getInstance();
    }

    /**
     * Generates an interview question for a specific profession.
     *
     * <p>The question is generated dynamically using the {@link PromptService}.</p>
     *
     * @param promptRequest the input needed to generate a question
     * @return a {@link QuestionResponse} containing the generated question
     */
    @PostMapping("/generateQuestion")
    public PromptResponse generateQuestion(@RequestBody final PromptRequest promptRequest, final HttpSession session) {
        LOGGER.info("/question input: {}", promptRequest);
        final PromptResponse promptResponse = promptService.generateQuestion(promptRequest, session);
        promptResponse.setPromptResponse(new QuestionResponse(promptResponse.getPromptResponse().toString()));
        return promptResponse;
    }

    /**
     * Receives a transcript, asks AI for feedback, and returns feedback.
     *
     * @param promptRequest the input needed to generate a question
     * @return AI-generated feedback on the candidate's answer
     */
    @PostMapping(value = "/feedback")
    public PromptResponse getFeedback(@RequestBody final PromptRequest promptRequest) {
        LOGGER.info("/feedback input: {}", promptRequest);
        return promptService.generateFeedback(promptRequest);
    }

    /**
     * Receives a transcript, asks AI for an evaluation, and returns evaluation.
     *
     * <p>The process includes storing the interview entry.</p>
     *
     * @param promptRequest the input needed to generate a question
     * @return AI-generated evaluation on the candidate's answer
     */
    @PostMapping(value = "/evaluation")
    public PromptResponse getEvaluation(@RequestBody final PromptRequest promptRequest) {
        LOGGER.info("/evaluation input: {}", promptRequest);

        // Generating AI evaluation
        final PromptResponse promptResponse = promptService.generateEvaluation(promptRequest);
        final Evaluation evaluation = (Evaluation) promptResponse.getPromptResponse();

        // Saves the interview entry
        final long timestamp = Utils.getTimestamp(promptRequest.getTranscript().getFileName());
        final String videoUrl = Utils.getVideoUrl(promptRequest.getTranscript().getFileName());
        interviewDataService.addInterview(timestamp, new InterviewEntry(timestamp, InterviewType.BEHAVIORAL, promptRequest.getProfession(), promptRequest.getQuestion(), promptRequest.getTranscript().getTranscript(), promptRequest.getFeedback(), videoUrl, evaluation));

        return promptResponse;
    }
}
