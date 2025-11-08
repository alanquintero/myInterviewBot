/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model.settings;

/**
 * DTO class to return current App Settings.
 *
 * @author Alan Quintero
 */
public class AppSettings {

    private String defaultProfession = "Software Engineer";

    private boolean showQuestionCategory = true;

    private boolean showQuestionDifficulty = true;

    public String getDefaultProfession() {
        return defaultProfession;
    }

    public void setDefaultProfession(final String defaultProfession) {
        this.defaultProfession = defaultProfession;
    }

    public boolean isShowQuestionCategory() {
        return showQuestionCategory;
    }

    public void setShowQuestionCategory(final boolean showQuestionCategory) {
        this.showQuestionCategory = showQuestionCategory;
    }

    public boolean isShowQuestionDifficulty() {
        return showQuestionDifficulty;
    }

    public void setShowQuestionDifficulty(final boolean showQuestionDifficulty) {
        this.showQuestionDifficulty = showQuestionDifficulty;
    }
}
