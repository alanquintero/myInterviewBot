/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model.difficulty;

/**
 * Enum class to store the question difficulty level.
 *
 * @author Alan Quintero
 */
public enum QuestionDifficultyLevel {
    EASY("Easy", "Basic reflection, general work situations.", "Tell me about your strengths."),
    MEDIUM("Medium", "Requires structured examples using STAR method.", "Tell me about a time you had to deal with a difficult coworker."),
    HARD("Hard", "Deep reflection, ambiguous or high-stakes situations.", "Describe a time you made a difficult decision with limited data.");

    private final String displayName;
    private final String description;
    private final String example;

    QuestionDifficultyLevel(final String displayName, final String description, final String example) {
        this.displayName = displayName;
        this.description = description;
        this.example = example;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getExample() {
        return example;
    }
}
