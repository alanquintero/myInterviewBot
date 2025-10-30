/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to return the question.
 *
 * @author Alan Quintero
 */
public class QuestionResponse {

    private String question;

    public QuestionResponse(final String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }
}
