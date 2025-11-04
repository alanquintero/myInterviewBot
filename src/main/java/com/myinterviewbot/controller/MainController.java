/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.PromptResponse;
import com.myinterviewbot.service.SystemRequirementsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static PromptResponse systemRequirements = null;

    private final SystemRequirementsService systemRequirementsService;

    public MainController(final SystemRequirementsService systemRequirementsService) {
        this.systemRequirementsService = systemRequirementsService;
    }

    @GetMapping("/requirements")
    public PromptResponse checkSystemRequirements(final HttpSession session) {
        LOGGER.info("Checking SystemRequirements");
        Boolean systemRequirementsChecked = (Boolean) session.getAttribute("systemRequirementsChecked");
        if((systemRequirementsChecked != null && systemRequirementsChecked) && systemRequirements != null) {
            LOGGER.info("SystemRequirements already checked");
            return systemRequirements;
        }
        systemRequirements = systemRequirementsService.executeInitialPrompt();
        session.setAttribute("systemRequirementsChecked", true);
        LOGGER.info("SystemRequirements check complete: {}",  systemRequirements);

        return systemRequirements;
    }
}
