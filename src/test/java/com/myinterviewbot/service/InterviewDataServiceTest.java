/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.myinterviewbot.model.Evaluation;
import com.myinterviewbot.model.InterviewEntry;
import com.myinterviewbot.model.ScoreSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InterviewDataServiceTest {

    private InterviewDataService interviewDataService;

    @BeforeEach
    public void setup() {
        interviewDataService = InterviewDataService.getInstance();
    }

    @Test
    void createScoreSummary() {
        // Given
        final List<InterviewEntry> entries = new ArrayList<>();
        final InterviewEntry entry1 = new InterviewEntry();
        final Evaluation evaluation1 = new Evaluation();
        evaluation1.setClarityScore(10);
        evaluation1.setStructureScore(9);
        evaluation1.setRelevanceScore(8);
        evaluation1.setCommunicationScore(7);
        evaluation1.setDepthScore(6);
        entry1.setEvaluation(evaluation1);
        entries.add(entry1);
        final InterviewEntry entry2 = new InterviewEntry();
        final Evaluation evaluation2 = new Evaluation();
        evaluation2.setClarityScore(1);
        evaluation2.setStructureScore(2);
        evaluation2.setRelevanceScore(3);
        evaluation2.setCommunicationScore(4);
        evaluation2.setDepthScore(5);
        entry2.setEvaluation(evaluation2);
        entries.add(entry2);

        // When
        final ScoreSummary scoreSummary = interviewDataService.createScoreSummary(entries);

        // Then
        assertNotNull(scoreSummary);
        assertEquals((double) (10 + 1) / 2, scoreSummary.getClarityScoreAverage());
        assertEquals((double) (9 + 2) / 2, scoreSummary.getStructureScoreAverage());
        assertEquals((double) (8 + 3) / 2, scoreSummary.getRelevanceScoreAverage());
        assertEquals((double) (7 + 4) / 2, scoreSummary.getCommunicationScoreAverage());
        assertEquals((double) (6 + 5) / 2, scoreSummary.getDepthScoreAverage());
    }

    @Test
    void createScoreSummary_entriesIsEmpty() {
        // Given && When
        final ScoreSummary scoreSummary = interviewDataService.createScoreSummary(new ArrayList<>());

        // Then
        assertNotNull(scoreSummary);
        assertEquals(0, scoreSummary.getClarityScoreAverage());
        assertEquals(0, scoreSummary.getStructureScoreAverage());
        assertEquals(0, scoreSummary.getRelevanceScoreAverage());
        assertEquals(0, scoreSummary.getCommunicationScoreAverage());
        assertEquals(0, scoreSummary.getDepthScoreAverage());
    }
}
