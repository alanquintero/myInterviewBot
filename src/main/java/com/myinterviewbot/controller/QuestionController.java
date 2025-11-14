/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.Question;
import com.myinterviewbot.model.behavior.BehaviorCategory;
import com.myinterviewbot.model.behavior.BehaviorCategoryDto;
import com.myinterviewbot.model.difficulty.QuestionDifficultyLevel;
import com.myinterviewbot.model.difficulty.QuestionDifficultyLevelDto;
import com.myinterviewbot.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * REST controller for handling question-related requests.
 *
 * @author Alan Quintero
 */
@RestController
@RequestMapping("/question/v1")
public class QuestionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionService questionService;

    public QuestionController() {
        this.questionService = QuestionService.getInstance();
    }

    @GetMapping("/behavior/info")
    public ResponseEntity<Map<String, Object>> getBehaviorInfo() {
        LOGGER.info("Get behavior info");
        List<BehaviorCategoryDto> categories =
                Stream.of(BehaviorCategory.values())
                        .map(c -> new BehaviorCategoryDto(
                                c.name(),
                                c.getDisplayName(),
                                c.getDescription(),
                                c.getExample()))
                        .toList();

        List<QuestionDifficultyLevelDto> difficultyLevels =
                Stream.of(QuestionDifficultyLevel.values())
                        .map(d -> new QuestionDifficultyLevelDto(
                                d.name(),
                                d.getDisplayName(),
                                d.getDescription(),
                                d.getExample()))
                        .toList();

        Map<String, Object> map = new HashMap<>();
        map.put("categories", categories);
        map.put("difficultyLevels", difficultyLevels);

        return ResponseEntity.ok(map);
    }

    /**
     * Save the given question.
     *
     * @param question the question to be saved
     * @return the HTTP response
     */
    @PostMapping(value = "/save")
    public ResponseEntity<String> saveQuestion(@RequestBody final Question question) {
        LOGGER.info("Save question {}", question);
        final boolean questionSaved = questionService.saveQuestion(question);
        if (questionSaved) {
            return ResponseEntity.ok("Question saved");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Question not saved.");
        }
    }
}
