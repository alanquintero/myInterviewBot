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
import org.springframework.http.MediaType;
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
