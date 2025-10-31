/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service.prompt;

import com.myinterviewbot.model.Evaluation;
import com.myinterviewbot.service.ai.model.AIService;
import com.myinterviewbot.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class LowPerformancePromptService implements Prompt{

    private static final Logger LOGGER = LoggerFactory.getLogger(LowPerformancePromptService.class);

    private static final Random random = new Random();

    @Autowired
    private AIService aiService;

    @Override
    public String generateQuestion(String profession, HttpSession session) {
        int index = random.nextInt(behavioralQuestions.size());
        return behavioralQuestions.get(index);
    }

    @Override
    public String generateFeedback(String transcript, String profession, String question) {
        String feedbackPrompt = String.format(
                "You are an interview coach. Read the following answer and give short, clear feedback using the STAR method. " +
                        "Identify which parts of Situation, Task, Action, and Result are strong and which could be improved. " +
                        "Be concise, 2-3 sentences max." +
                        "\nQuestion: " + question +
                        " \nAnswer: " + transcript
        );
        final String feedback = aiService.executePrompt(feedbackPrompt);
        LOGGER.info("Feedback: {}", feedback);

        return Utils.removeQuotesAndFormatList(feedback);
    }

    @Override
    public Evaluation generateEvaluation(String transcript, String profession, String question) {
        final String jsonParameters = "{clarityScore: int, clarityFeedback: string, structureScore: int, structureFeedback: string, relevanceScore: int, relevanceFeedback: string, communicationScore: int, communicationFeedback: string, depthScore: int, depthFeedback: string}";

        final String promptLowPerf = "You are an interview coach. Evaluate the following candidate's answer to a behavioral question: " + question
                + "\nCandidate Response: " + transcript
                + "\nInstructions:"
                + "\n- Evaluate 5 parameters: Clarity, Structure, Relevance, Communication, Depth."
                + "\n- Provide scores 1-10 for each parameter and a very short comment (1 sentence) per parameter."
                + "\n- Be strict: minimal, vague, or off-topic answers should get low scores."
                + "\n- Output only JSON using these fields: " + jsonParameters
                + "\n- Keep JSON concise and include all fields, even if not applicable."
                + "\n- Keep the output short (1-2 sentences per comment).";

        final String evaluationTxt = aiService.executePrompt(promptLowPerf);

        return Utils.generateEvaluation(evaluationTxt);
    }

    // List of 100 common behavioral questions
    private List<String> behavioralQuestions = Arrays.asList(
            "Tell me about a time you faced a challenging situation at work.",
            "Describe a situation where you had to work under pressure.",
            "Tell me about a time you had to meet a tight deadline.",
            "Describe a time you had a conflict with a coworker and how you resolved it.",
            "Tell me about a time you went above and beyond your job responsibilities.",
            "Describe a situation where you had to adapt to a major change.",
            "Tell me about a time you had to learn something new quickly.",
            "Describe a time when you had to make a difficult decision.",
            "Tell me about a situation where you took initiative to solve a problem.",
            "Describe a time you had to handle multiple priorities at once.",
            "Tell me about a time you received constructive feedback and how you responded.",
            "Describe a situation where you collaborated with a team to achieve a goal.",
            "Tell me about a time you failed or made a mistake — what did you learn?",
            "Describe a time when you had to persuade someone to see your point of view.",
            "Tell me about a time you had to handle an unexpected problem.",
            "Describe a situation where you had to deliver results with limited resources.",
            "Tell me about a time you improved a process or found a better way to do something.",
            "Describe a time when you had to take on a leadership role.",
            "Tell me about a time you had to make a decision with incomplete information.",
            "Describe a time when you helped a colleague or team member succeed.",
            "Tell me about a time you disagreed with your manager — how did you handle it?",
            "Describe a time when you had to motivate others.",
            "Tell me about a time when you had to prioritize your tasks.",
            "Describe a situation where you demonstrated attention to detail.",
            "Tell me about a time when you had to work with someone difficult.",
            "Describe a project or task that required strong problem-solving skills.",
            "Tell me about a time when you had to deliver bad news.",
            "Describe a situation where you identified a potential problem and took action to prevent it.",
            "Tell me about a time when you managed competing deadlines successfully.",
            "Describe a time you had to communicate complex information clearly.",
            "Tell me about a time you had to handle a dissatisfied customer or stakeholder.",
            "Describe a time you led a team through a challenging situation.",
            "Tell me about a time you had to take responsibility for an error.",
            "Describe a situation where you had to make a quick decision.",
            "Tell me about a time you had to deal with ambiguity.",
            "Describe a project where you took ownership from start to finish.",
            "Tell me about a time you set and achieved a difficult goal.",
            "Describe a situation where you successfully managed your time.",
            "Tell me about a time when you influenced a decision without having authority.",
            "Describe a time when you had to deliver results despite obstacles.",
            "Tell me about a time you had to adjust your approach to get better results.",
            "Describe a time when you helped improve team morale.",
            "Tell me about a time when you handled confidential information.",
            "Describe a time when you had to explain something technical to a non-technical person.",
            "Tell me about a time when you built a strong working relationship.",
            "Describe a time when you had to ask for help.",
            "Tell me about a time when you worked on a project outside your comfort zone.",
            "Describe a time when you had to make a compromise to reach an agreement.",
            "Tell me about a time when you received recognition for your work.",
            "Describe a time when you made a process more efficient.",
            "Tell me about a time when you took initiative without being asked.",
            "Describe a situation where you helped solve a conflict between others.",
            "Tell me about a time when you showed creativity in solving a problem.",
            "Describe a time when you had to balance quality and speed.",
            "Tell me about a time when you had to manage your emotions at work.",
            "Describe a time when you worked effectively under limited supervision.",
            "Tell me about a time when you contributed to a positive team environment.",
            "Describe a time when you handled feedback you didn’t agree with.",
            "Tell me about a time you successfully led a cross-functional effort.",
            "Describe a time when you had to explain your decisions to others.",
            "Tell me about a time when you handled a project with incomplete requirements.",
            "Describe a time when you used data to make a decision.",
            "Tell me about a time when you coached or mentored someone.",
            "Describe a time when you had to delegate effectively.",
            "Tell me about a time you dealt with a major setback.",
            "Describe a time when you identified and fixed a recurring issue.",
            "Tell me about a time when you helped reduce costs or save time.",
            "Describe a time when you managed expectations effectively.",
            "Tell me about a time when you had to present in front of others.",
            "Describe a time when you had to earn someone's trust.",
            "Tell me about a time when you had to deliver work with limited information.",
            "Describe a time when you collaborated with people from different backgrounds.",
            "Tell me about a time when you improved your team's workflow.",
            "Describe a time when you took feedback and implemented it successfully.",
            "Tell me about a time when you worked on a long-term project.",
            "Describe a situation where you had to multitask effectively.",
            "Tell me about a time when you faced an ethical dilemma.",
            "Describe a time when you learned from failure.",
            "Tell me about a time when you made a decision that wasn’t popular.",
            "Describe a time when you helped implement a change at work.",
            "Tell me about a time you challenged the status quo.",
            "Describe a time when you had to manage limited resources.",
            "Tell me about a time when you had to adapt to a new team or manager.",
            "Describe a time when you improved communication in your team.",
            "Tell me about a time when you had to stay organized in a chaotic situation.",
            "Describe a time when you turned a negative situation into a positive one.",
            "Tell me about a time when you had to convince others to try a new idea.",
            "Describe a time when you worked with incomplete or unclear goals.",
            "Tell me about a time when you made a mistake that taught you something valuable.",
            "Describe a time when you contributed to improving team performance.",
            "Tell me about a time when you managed to exceed expectations.",
            "Describe a time when you balanced multiple stakeholders’ needs.",
            "Tell me about a time you had to work on a project with a tight budget.",
            "Describe a time you had to resolve a misunderstanding quickly.",
            "Tell me about a time you had to adjust your communication style.",
            "Describe a time when you had to stay motivated during a long project.",
            "Tell me about a time you worked on something you weren’t initially interested in.",
            "Describe a time when you handled competing opinions in a group.",
            "Tell me about a time when you took the lead without being assigned.",
            "Describe a time when you managed a project from idea to completion.",
            "Tell me about a time when you had to make a judgment call with limited guidance.",
            "Describe a time when you successfully managed stress in the workplace."
    );


}
