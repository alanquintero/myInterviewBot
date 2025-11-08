/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

/**
 * DTO class to store the transcript.
 *
 * @author Alan Quintero
 */
public class Transcript {

    private String transcript;

    private String fileName;

    public Transcript() {
        this.transcript = "";
        this.fileName = "";
    }

    public Transcript(final String transcript, final String fileName) {
        this.transcript = transcript;
        this.fileName = fileName;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(final String transcript) {
        this.transcript = transcript;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Transcript{" +
                "transcript='" + transcript + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
