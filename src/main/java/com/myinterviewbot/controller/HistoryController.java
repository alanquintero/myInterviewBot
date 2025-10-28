/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.MyInterviews;
import com.myinterviewbot.service.InterviewDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling history requests.
 *
 * @author Alan Quintero
 */
@RestController
@RequestMapping("/history/v1")
public class HistoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryController.class);

    private final InterviewDataService interviewDataService;

    public HistoryController() {
        this.interviewDataService = InterviewDataService.getInstance();
    }

    /**
     * Get all interviews sorted by timestamp descending
     */
    @GetMapping("/all")
    public MyInterviews getAllInterviews() {
        LOGGER.info("Get All Interviews");
        return interviewDataService.getMyInterviews();
    }

    /**
     * Delete a specific interview by ID
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInterview(@PathVariable("id") long id) {
        LOGGER.info("Delete Interview {}", id);
        boolean removed = interviewDataService.removeInterview(id);
        if (removed) {
            return ResponseEntity.ok("Interview deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interview not found");
        }
    }
}
