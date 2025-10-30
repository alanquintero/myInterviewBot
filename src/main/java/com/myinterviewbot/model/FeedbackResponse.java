/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to return the feedback and transcript.
 *
 * @author Alan Quintero
 */
public class FeedbackResponse {

    private String feedback;
    private String transcript;
    private Evaluation evaluation;

    public FeedbackResponse(final String feedback, final String transcript, final Evaluation evaluation) {
        this.feedback = feedback;
        this.transcript = transcript;
        this.evaluation = evaluation;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(final String feedback) {
        this.feedback = feedback;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(final String transcript) {
        this.transcript = transcript;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(final Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}
