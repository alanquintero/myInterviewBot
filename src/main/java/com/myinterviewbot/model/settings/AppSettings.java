/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myinterviewbot.utils.Constants;

/**
 * DTO class to return current App Settings.
 *
 * @author Alan Quintero
 */
public class AppSettings {

    private String defaultProfession = Constants.DEFAULT_PROFESSION;

    @JsonProperty(defaultValue = "true")
    private Boolean showQuestionCategory = true;

    @JsonProperty(defaultValue = "true")
    private Boolean showQuestionDifficulty = true;

    @JsonProperty(defaultValue = "true")
    private Boolean showSavedQuestions = true;

    public String getDefaultProfession() {
        return defaultProfession != null ? defaultProfession : Constants.DEFAULT_PROFESSION;
    }

    public void setDefaultProfession(final String defaultProfession) {
        this.defaultProfession = defaultProfession;
    }

    public boolean isShowQuestionCategory() {
        return showQuestionCategory != null ? showQuestionCategory : true;
    }

    public void setShowQuestionCategory(final boolean showQuestionCategory) {
        this.showQuestionCategory = showQuestionCategory;
    }

    public boolean isShowQuestionDifficulty() {
        return showQuestionDifficulty != null ? showQuestionDifficulty : true;
    }

    public void setShowQuestionDifficulty(final boolean showQuestionDifficulty) {
        this.showQuestionDifficulty = showQuestionDifficulty;
    }

    public boolean isShowSavedQuestions() {
        return showSavedQuestions != null ? showSavedQuestions : true;
    }

    public void setShowSavedQuestions(final boolean showSavedQuestions) {
        this.showSavedQuestions = showSavedQuestions;
    }

    @Override
    public String toString() {
        return "AppSettings{" +
                "defaultProfession='" + defaultProfession + '\'' +
                ", showQuestionCategory=" + showQuestionCategory +
                ", showQuestionDifficulty=" + showQuestionDifficulty +
                ", showSavedQuestions=" + showSavedQuestions +
                '}';
    }
}
