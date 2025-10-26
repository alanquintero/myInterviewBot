package com.myinterviewbot.service;

import com.myinterviewbot.service.ai.model.AIService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PromptServiceTest {

    @Mock
    private AIService aiService;

    @InjectMocks
    private PromptService promptService;

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
        final String question = promptService.generateQuestion(profession, session);

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
        final String question = promptService.generateQuestion(profession, session);

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
        final String question = promptService.generateQuestion(profession, session);

        // Then
        assertEquals(expectedQuestion, question);
        verify(session, times(1)).setAttribute("firstQuestion", false);
        verify(aiService, times(1)).executePrompt(anyString());
    }

    @Test
    void generateQuestion_questionHasMoreWordsThanWanted() {
        // Given
        final String profession = "software engineer";
        final String expectedQuestion = "Tell me about a time you faced a challenge. Tell me about a time you faced a challenge. Tell me about a time you faced a challenge.";
        when(aiService.executePrompt(anyString())).thenReturn(expectedQuestion);
        final HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("currentProfession")).thenReturn(null);
        when(session.getAttribute("firstQuestion")).thenReturn(true);

        // When
        final String question = promptService.generateQuestion(profession, session);

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
        final String feedback = promptService.generateFeedback(profession, transcript, question);

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
        final String feedback = promptService.generateFeedback(profession, transcript, question);

        // Then
        assertEquals(expectedFeedback, feedback);
        verify(aiService, times(1)).executePrompt(anyString());
    }
}
