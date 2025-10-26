/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myinterviewbot.model.Evaluation;
import com.myinterviewbot.service.ai.model.AIService;
import com.myinterviewbot.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending prompts to the selected AI model and retrieving responses.
 *
 * <p>This service formats candidate answers, sends them to AI model, and
 * returns the AI-generated evaluation or feedback.</p>
 *
 * @author Alan Quintero
 */
@Service
public class PromptService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptService.class);

    private static final int QUESTION_MAX_NUMBER_OF_WORDS = 35;
    private static final int FEEDBACK_MAX_NUMBER_OF_WORDS = 250;
    private static final int MAX_NUMBER_OF_ATTEMPTS = 3;

    @Autowired
    private AIService aiService;

    public String generateQuestion(final String profession, final HttpSession session) {
        LOGGER.info("Generating question for profession: {}", profession);

        String lastProfession = (String) session.getAttribute("currentProfession");
        Boolean firstQuestion = (Boolean) session.getAttribute("firstQuestion");

        // If profession changed, reset session info
        if (lastProfession == null || !lastProfession.equals(profession)) {
            session.setAttribute("currentProfession", profession);
            session.setAttribute("firstQuestion", true);
            firstQuestion = true;
        }

        String prompt;
        if (firstQuestion == null || firstQuestion) {
            prompt = "You are a concise behavioral interview coach. Generate a single, realistic behavioral interview question for a " + profession + ". The question must be less than 15 words. Only output the question.";
            session.setAttribute("firstQuestion", false);
        } else {
            prompt = "Give me another behavioral interview question for a " + profession + ". Remember that the question must be less than 15 words. Only output the question.";
        }

        /*
            Sometimes the model response with a very long question, the next code will try to avoid returning a long question by asking the model to generate another question.
            This process will be repeated a maximum of three time, hope the model can generate a good question.
        */
        String question = aiService.executePrompt(prompt);
        int words = Utils.countWords(question);
        if (words > QUESTION_MAX_NUMBER_OF_WORDS) {
            LOGGER.warn("⚠︎⚠︎⚠︎ Question has more than " + QUESTION_MAX_NUMBER_OF_WORDS + " words, asking model to generate another question...");
            int requestNewAnswer = 0;
            while (requestNewAnswer < MAX_NUMBER_OF_ATTEMPTS) {
                requestNewAnswer++;
                prompt = switch (requestNewAnswer) {
                    case 1 ->
                            "Please provide the next behavioral interview question in " + QUESTION_MAX_NUMBER_OF_WORDS + " words or less: " + question;
                    case 2 ->
                            "Give me a totally different behavioral interview question for a " + profession + ". Remember that the question must be less than 15 words. Only output the question.";
                    case 3 ->
                            "Give me a the most common behavioral interview question for a " + profession + ". Remember that the question must be less than 15 words. Only output the question.";
                    default ->
                            "Give me a generic behavioral interview question for a " + profession + ". Remember that the question must be less than 15 words. Only output the question.";
                };

                // Extracting the question because AI sometimes gives an explanation of what it did to shorten the question.
                question = Utils.extractQuestion(aiService.executePrompt(prompt));
                words = Utils.countWords(question);
                if (words <= QUESTION_MAX_NUMBER_OF_WORDS) {
                    break;
                } else {
                    LOGGER.warn("⚠︎⚠︎⚠︎ New generated question has more than " + QUESTION_MAX_NUMBER_OF_WORDS + " words, asking model to generate another question...");
                }
            }
        }
        words = Utils.countWords(question);
        if (words > QUESTION_MAX_NUMBER_OF_WORDS) {
            LOGGER.warn("⚠︎⚠︎⚠︎ The question has more than " + QUESTION_MAX_NUMBER_OF_WORDS + " words!");
        } else {
            LOGGER.info("Number of words in the question: {}", words);
        }

        return Utils.removeQuotes(question);
    }

    public String generateFeedback(final String transcript, final String profession, final String question) {
        String prompt = "You are a technical hiring manager. Evaluate the following interview answer, focusing on clarity, structure, relevance, and communication style. "
                + "Provide actionable feedback in 3–4 concise sentences, output only the feedback, no extra commentary. "
                + "Candidate profession: " + profession + ". "
                + "Question: " + question + ". "
                + "Candidate answer: " + transcript;

        String feedback = aiService.executePrompt(prompt);

        /*
            Sometimes the model response with a very long feedback, the next code will try to avoid returning a long feedback by asking the model to generate another feedback.
            This process will be repeated a maximum of three time, hope the model can generate a good and short feedback.
        */
        int words = Utils.countWords(feedback);
        if (words > FEEDBACK_MAX_NUMBER_OF_WORDS) {
            LOGGER.warn("⚠︎⚠︎⚠︎ Feedback has more than " + FEEDBACK_MAX_NUMBER_OF_WORDS + " words, asking model to generate another feedback...");
            int requestNewFeedback = 0;
            while (requestNewFeedback < MAX_NUMBER_OF_ATTEMPTS) {
                requestNewFeedback++;
                prompt = "Please provide the next feedback in " + FEEDBACK_MAX_NUMBER_OF_WORDS + " words or less: " + feedback;

                feedback = aiService.executePrompt(prompt);
                words = Utils.countWords(feedback);
                if (words <= FEEDBACK_MAX_NUMBER_OF_WORDS) {
                    break;
                } else {
                    LOGGER.warn("⚠︎⚠︎⚠︎ New generated feedback has more than " + FEEDBACK_MAX_NUMBER_OF_WORDS + "  words, asking model to generate another feedback...");
                }
            }
        }
        words = Utils.countWords(feedback);
        if (words > FEEDBACK_MAX_NUMBER_OF_WORDS) {
            LOGGER.warn("⚠︎⚠︎⚠︎ The feedback has more than " + FEEDBACK_MAX_NUMBER_OF_WORDS + " words!");
        } else {
            LOGGER.info("Number of words in the feedback: {}", words);
        }
        return Utils.removeQuotesAndFormatList(feedback);
    }

    public Evaluation generateEvaluation(final String transcript, final String profession, final String question) {
        final String evaluationJsonFormat = "{ \"clarityScore\": 0,\"clarityFeedback\": \"\",\"structureScore\": 0,\"structureFeedback\": \"\",\"relevanceScore\": 0,\"relevanceFeedback\": \"\",\"communicationScore\": 0,\"communicationFeedback\": \"\",\"depthScore\": 0,\"depthFeedback\": \"\"}";

        final String prompt = "You are a technical hiring manager. Evaluate the following " + profession + " candidate's response to a behavioral interview question: " + question
                + " Parameters to evaluate (score each from 1 to 10, 10 = excellent): "
                + " 1. Clarity: How understandable the answer is, considering content and depth. Minimal answers get low scores. "
                + " 2. Structure: Logical flow of the answer; use of STAR or other coherent structure. Single sentences or unorganized responses score low. "
                + " 3. Relevance: How well the answer addresses the question. Off-topic answers score 1–2. "
                + " 4. Communication: How effectively the candidate conveys ideas, including grammar, vocabulary, and conciseness. Minimal answers with no examples are scored low even if grammar is correct. "
                + " 5. Depth: Specificity, examples, measurable outcomes, and demonstration of skills. Minimal or vague answers score low. "
                + "Instructions: "
                + "- Provide numeric scores for each parameter. "
                + "- Add a one-sentence comment per parameter if needed. "
                + "- If the candidate provides a minimal answer, irrelevant answer, or does not directly address the question, give low scores (1–2). "
                + "- Be strict: if the answer is minimal, off-topic, or does not contain examples or meaningful detail, give low scores even if grammar is correct. "
                + "- Output only in JSON format: " + evaluationJsonFormat
                + " Candidate Response: " + transcript;

        final String evaluation = aiService.executePrompt(prompt);
        final String evaluationJson = Utils.extractJson(evaluation);

        LOGGER.info("Evaluation JSON: {}", evaluationJson);
        if (evaluationJson == null) {
            LOGGER.warn("Evaluation JSON not found in evaluation output: {}", evaluation);
            return null;
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(evaluationJson, Evaluation.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.warn("Evaluation failed. Please try again later.");
        return null;
    }
}
