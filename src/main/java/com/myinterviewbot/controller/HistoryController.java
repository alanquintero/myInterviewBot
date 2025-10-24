/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.InterviewEntry;
import com.myinterviewbot.service.InterviewDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * REST controller for handling history requests.
 *
 * @author Alan Quintero
 */
@RestController
@RequestMapping("/history/v1")
public class HistoryController {

    private final InterviewDataService interviewDataService;

    public HistoryController() {
        this.interviewDataService = InterviewDataService.getInstance();
    }

    /**
     * Get all interviews sorted by timestamp descending
     */
    @GetMapping("/all")
    public List<InterviewEntry> getAllInterviews() {
        return interviewDataService.getAllInterviews()
                .stream()
                .sorted(Comparator.comparingLong(InterviewEntry::getTimestamp).reversed())
                .toList();
    }

    /**
     * Delete a specific interview by ID
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInterview(@PathVariable("id") long id) {
        boolean removed = interviewDataService.removeInterview(id);
        if (removed) {
            return ResponseEntity.ok("Interview deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interview not found");
        }
    }
}
