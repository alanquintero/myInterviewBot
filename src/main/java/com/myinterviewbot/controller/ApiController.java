/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.SystemRequirements;
import com.myinterviewbot.system.SystemChecker;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling general requests.
 *
 * @author Alan Quintero
 */
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    private static SystemRequirements systemRequirements = null;

    private final SystemChecker systemChecker;

    public ApiController(final SystemChecker systemChecker) {
        this.systemChecker = systemChecker;
    }

    @GetMapping("/requirements")
    public SystemRequirements checkSystemRequirements(final HttpSession session) {
        LOGGER.info("Checking SystemRequirements");
        if (systemRequirements == null) {
            systemRequirements = systemChecker.checkSystemRequirements();
        }
        return systemRequirements;
    }
}
