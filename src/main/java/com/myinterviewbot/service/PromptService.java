/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myinterviewbot.model.Evaluation;
import com.myinterviewbot.model.PromptResponse;
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

    public PromptResponse generateQuestion(final String profession, final HttpSession session) {
        LOGGER.info("Generating question for profession: {}", profession);

        String lastProfession = (String) session.getAttribute("currentProfession");
        Boolean firstQuestion = (Boolean) session.getAttribute("firstQuestion");

        // If profession changed, reset session info
        if (lastProfession == null || !lastProfession.equals(profession)) {
            session.setAttribute("currentProfession", profession);
            session.setAttribute("firstQuestion", true);
            firstQuestion = true;
        }

        final String restriction = "The question must be less than " + QUESTION_MAX_NUMBER_OF_WORDS + " words. Generate ONLY the behavioral interview question — do not include any explanations or introductions.";
        String prompt;
        if (firstQuestion == null || firstQuestion) {
            prompt = "You are a concise behavioral interview coach. Generate a single, realistic behavioral interview question for a " + profession + ". " + restriction;
            session.setAttribute("firstQuestion", false);
        } else {
            prompt = "Give me another behavioral interview question for a " + profession + ". " + restriction;
        }

        /*
            Sometimes the model response with a very long question, the next code will try to avoid returning a long question by asking the model to generate another question.
            This process will be repeated a maximum of three time, hope the model can generate a good question.
        */
        PromptResponse promptResponse = aiService.executePrompt(prompt);
        String question = promptResponse.getPromptResponse().toString();
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
                            "Give me a the most common behavioral interview question for a " + profession + ". " + restriction;
                    default ->
                            "Give me a generic behavioral interview question for a " + profession + ". " + restriction;
                };

                // Extracting the question because AI sometimes gives an explanation of what it did to shorten the question.
                promptResponse = aiService.executePrompt(prompt);
                question = Utils.extractQuestion(promptResponse.getPromptResponse().toString());
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

        promptResponse.setPromptResponse(Utils.removeQuotes(question));
        return promptResponse;
    }

    public PromptResponse generateFeedback(final String transcript, final String profession, final String question) {
        String prompt = "You are a technical hiring manager. Evaluate the following interview answer, focusing on clarity, structure, relevance, and communication style. "
                + "Provide actionable feedback in 3–4 concise sentences, output only the feedback, no extra commentary. "
                + "Candidate profession: " + profession + ". "
                + "Question: " + question + ". "
                + "Candidate answer: " + transcript;

        PromptResponse promptResponse = aiService.executePrompt(prompt);
        String feedback = promptResponse.getPromptResponse().toString();

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

                promptResponse = aiService.executePrompt(prompt);
                feedback = promptResponse.getPromptResponse().toString();
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

        promptResponse.setPromptResponse(Utils.removeQuotesAndFormatList(feedback));
        return promptResponse;
    }

    public PromptResponse generateEvaluation(final String transcript, final String profession, final String question) {
        final String jsonParameters = "{clarityScore:  int, clarityFeedback: string, structureScore: int, structureFeedback: string, relevanceScore: int, relevanceFeedback: string, communicationScore: int, communicationFeedback: string, depthScore: int, depthFeedback: string}";

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
                + "- Be very strict: If the candidate provides a minimal answer, off-topic, irrelevant answer, does not directly address the question, does not contain examples or meaningful details, lacks detail and examples, or even if grammar and vocabulary are correct, give low scores (1-2). "
                + "- Output only in JSON format, create a JSON using the next parameters: " + jsonParameters
                + " - Include all JSON parameters, even if one of them is missing or not applicable. "
                + " Candidate Response: " + transcript;

        PromptResponse promptResponse = aiService.executePrompt(prompt);

        String evaluationTxt = promptResponse.getPromptResponse().toString();
        if (evaluationTxt == null || evaluationTxt.isEmpty()) {
            LOGGER.warn("Evaluation text is null");
            promptResponse.setPromptResponse(null);
            return promptResponse;
        }

        final String evaluationJson = Utils.extractJson(evaluationTxt);

        LOGGER.info("Evaluation JSON: {}", evaluationJson);
        if (evaluationJson == null) {
            LOGGER.warn("Evaluation JSON not found in evaluation output: {}", evaluationTxt);
            promptResponse.setPromptResponse(null);
            return promptResponse;
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final Evaluation evaluation = mapper.readValue(evaluationJson, Evaluation.class);
            validateFeedback(evaluation);
            promptResponse.setPromptResponse(evaluation);
            return promptResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.warn("Evaluation failed. Please try again later.");
        promptResponse.setPromptResponse(null);
        return promptResponse;
    }

    private void validateFeedback(final Evaluation evaluation) {
        if (evaluation == null) {
            return;
        }
        // Clarity
        if (evaluation.getClarityFeedback() == null || evaluation.getClarityFeedback().isEmpty()) {
            evaluation.setClarityScore(0);
        }
        // Structure
        if (evaluation.getStructureFeedback() == null || evaluation.getStructureFeedback().isEmpty()) {
            evaluation.setStructureScore(0);
        }
        // Relevance
        if (evaluation.getRelevanceFeedback() == null || evaluation.getRelevanceFeedback().isEmpty()) {
            evaluation.setRelevanceScore(0);
        }
        // Communication
        if (evaluation.getCommunicationFeedback() == null || evaluation.getCommunicationFeedback().isEmpty()) {
            evaluation.setCommunicationScore(0);
        }
        // Depth
        if (evaluation.getDepthFeedback() == null || evaluation.getDepthFeedback().isEmpty()) {
            evaluation.setDepthScore(0);
        }
    }
}
