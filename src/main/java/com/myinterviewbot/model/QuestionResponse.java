/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO class to return the question.
 *
 * @author Alan Quintero
 */
@Data
@AllArgsConstructor
public class QuestionResponse {
    private String question;
}
