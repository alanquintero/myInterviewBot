/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to receive input parameters related to Prompt requests from the front-end.
 *
 * @author Alan Quintero
 */
public class PromptRequest {

    private String profession;
    private Question question;
    private Transcript transcript;
    private String feedback;

    public String getProfession() {
        return profession;
    }

    public void setProfession(final String profession) {
        this.profession = profession;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(final Question question) {
        this.question = question;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public void setTranscript(final Transcript transcript) {
        this.transcript = transcript;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(final String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "PromptRequest{" +
                "profession='" + profession + '\'' +
                ", question=" + question +
                ", transcript=" + transcript +
                ", feedback='" + feedback + '\'' +
                '}';
    }
}
