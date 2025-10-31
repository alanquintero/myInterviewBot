package com.myinterviewbot.service;

import com.myinterviewbot.model.Evaluation;
import com.myinterviewbot.service.ai.model.AIService;
import com.myinterviewbot.service.prompt.HighPerformancePromptService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class HighPerformancePromptServiceTest {

    @Mock
    private AIService aiService;

    @InjectMocks
    private HighPerformancePromptService highPerformancePromptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateQuestion_firstQuestion() {
        // Given
        final String profession = "software engineer";
        final String expectedQuestion = "Tell me about a time you faced a challenge.";
        when(aiService.executePrompt(anyString())).thenReturn(expectedQuestion);
        final HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("currentProfession")).thenReturn(null);
        when(session.getAttribute("firstQuestion")).thenReturn(true);

        // When
        final String question = highPerformancePromptService.generateQuestion(profession, session);

        // Then
        assertEquals(expectedQuestion, question);
        verify(session, times(1)).setAttribute("firstQuestion", false);
        verify(aiService, times(1)).executePrompt(anyString());
    }

    @Test
    void generateQuestion_notTheFirstQuestion() {
        // Given
        final String profession = "software engineer";
        final String expectedQuestion = "Tell me about a time you faced a challenge.";
        when(aiService.executePrompt(anyString())).thenReturn(expectedQuestion);
        final HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("currentProfession")).thenReturn(profession);
        when(session.getAttribute("firstQuestion")).thenReturn(false);

        // When
        final String question = highPerformancePromptService.generateQuestion(profession, session);

        // Then
        assertEquals(expectedQuestion, question);
        verify(session, never()).setAttribute(anyString(), anyBoolean());
        verify(aiService, times(1)).executePrompt(anyString());
    }

    @Test
    void generateQuestion_notTheFirstQuestionButChangedProfession() {
        // Given
        final String profession = "vet";
        final String expectedQuestion = "Tell me about a time you faced a challenge.";
        when(aiService.executePrompt(anyString())).thenReturn(expectedQuestion);
        final HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("currentProfession")).thenReturn("software engineer");
        when(session.getAttribute("firstQuestion")).thenReturn(false);

        // When
        final String question = highPerformancePromptService.generateQuestion(profession, session);

        // Then
        assertEquals(expectedQuestion, question);
        verify(session, times(1)).setAttribute("firstQuestion", false);
        verify(aiService, times(1)).executePrompt(anyString());
    }

    @Test
    void generateQuestion_questionHasMoreWordsThanWanted() {
        // Given
        final String profession = "software engineer";
        final String expectedQuestion = "Tell me about a time you faced a challenge Tell me about a time you faced a challenge Tell me about a time you faced a challenge Tell me about a time you faced a challenge.";
        when(aiService.executePrompt(anyString())).thenReturn(expectedQuestion);
        final HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("currentProfession")).thenReturn(null);
        when(session.getAttribute("firstQuestion")).thenReturn(true);

        // When
        final String question = highPerformancePromptService.generateQuestion(profession, session);

        // Then
        assertEquals(expectedQuestion, question);
        verify(aiService, atLeast(2)).executePrompt(anyString());
    }

    @Test
    void generateFeedback() {
        // Given
        final String profession = "software engineer";
        final String transcript = "";
        final String question = "Tell me about a time you faced a challenge.";
        final String expectedFeedback = "Good example of problem-solving and teamwork. Could be improved by including a measurable result or what was learned from the experience.";
        when(aiService.executePrompt(anyString())).thenReturn(expectedFeedback);

        // When
        final String feedback = highPerformancePromptService.generateFeedback(profession, transcript, question);

        // Then
        assertEquals(expectedFeedback, feedback);
        verify(aiService, times(1)).executePrompt(anyString());
    }

    @Test
    void generateFeedback_feedbackHasMoreWordsThanWanted() {
        // Given
        final String profession = "software engineer";
        final String transcript = "A challenge I faced was when my team’s project was falling behind schedule due to unclear requirements. I took the initiative to organize a quick sync with stakeholders to clarify expectations and re-prioritize tasks. After aligning everyone, we adjusted the sprint goals and completed the project on time. It taught me how important proactive communication is when things start to go off track.";
        final String question = "Tell me about a time you faced a challenge.";
        final String expectedFeedback = "Good example of problem-solving and teamwork. Could be improved by including a measurable result or what was learned from the experience. Good example of problem-solving and teamwork. Could be improved by including a measurable result or what was learned from the experience. Good example of problem-solving and teamwork. Could be improved by including a measurable result or what was learned from the experience. Good example of problem-solving and teamwork. Could be improved by including a measurable result or what was learned from the experience.";
        when(aiService.executePrompt(anyString())).thenReturn(expectedFeedback);

        // When
        final String feedback = highPerformancePromptService.generateFeedback(profession, transcript, question);

        // Then
        assertEquals(expectedFeedback, feedback);
        verify(aiService, times(1)).executePrompt(anyString());
    }

    @Test
    void generateEvaluation() {
        // Given
        final String evaluationResponse = "{ \"clarityScore\": 1,\"clarityFeedback\": \"good\",\"structureScore\": 5,\"structureFeedback\": \"nice\",\"relevanceScore\": 2,\"relevanceFeedback\": \"ok then\",\"communicationScore\": 3,\"communicationFeedback\": \"excellent\",\"depthScore\": 4,\"depthFeedback\": \"ok\"}";
        ;
        when(aiService.executePrompt(anyString())).thenReturn(evaluationResponse);

        // When
        final Evaluation evaluation = highPerformancePromptService.generateEvaluation("transcript", "profession", "question");

        // Then
        assertNotNull(evaluation);
        assertEquals(1, evaluation.getClarityScore());
        assertEquals("good", evaluation.getClarityFeedback());
        assertEquals(5, evaluation.getStructureScore());
        assertEquals("nice", evaluation.getStructureFeedback());
        assertEquals(2, evaluation.getRelevanceScore());
        assertEquals("ok then", evaluation.getRelevanceFeedback());
        assertEquals(3, evaluation.getCommunicationScore());
        assertEquals("excellent", evaluation.getCommunicationFeedback());
        assertEquals(4, evaluation.getDepthScore());
        assertEquals("ok", evaluation.getDepthFeedback());
    }

    @Test
    void generateEvaluation_invalidEvaluationResponse() {
        // Given
        final String evaluationResponse = "{\"error\": \"oh no\"}";
        when(aiService.executePrompt(anyString())).thenReturn(evaluationResponse);

        // When
        final Evaluation evaluation = highPerformancePromptService.generateEvaluation("transcript", "profession", "question");

        // Then
        assertNull(evaluation);
    }
}
