/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.behavior.BehaviorCategory;
import com.myinterviewbot.model.behavior.BehaviorCategoryDto;
import com.myinterviewbot.model.difficulty.QuestionDifficultyLevel;
import com.myinterviewbot.model.difficulty.QuestionDifficultyLevelDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
