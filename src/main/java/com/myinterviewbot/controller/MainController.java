/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.PromptResponse;
import com.myinterviewbot.model.SystemRequirements;
import com.myinterviewbot.service.SystemRequirementsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
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
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static SystemRequirements systemRequirements = null;

    private final SystemRequirementsService systemRequirementsService;

    public MainController(final SystemRequirementsService systemRequirementsService) {
        this.systemRequirementsService = systemRequirementsService;
    }

    @GetMapping("/requirements")
    public SystemRequirements checkSystemRequirements(final HttpSession session) {
        LOGGER.info("Checking SystemRequirements");
        Boolean systemRequirementsChecked = (Boolean) session.getAttribute("systemRequirementsChecked");
        Boolean isSlowPromptResponse = (Boolean) session.getAttribute("isSlowPromptResponse");
        if (systemRequirementsChecked != null && systemRequirementsChecked && isSlowPromptResponse != null) {
            LOGGER.info("SystemRequirements already checked");
            if (systemRequirements == null) {
                systemRequirements = new SystemRequirements(isSlowPromptResponse, 0);
            }
            return systemRequirements;
        }
        final PromptResponse promptResponse = systemRequirementsService.executeInitialPrompt();
        systemRequirements = new SystemRequirements(promptResponse.getPromptStats().isSlowPromptResponse(), promptResponse.getPromptStats().getSecondsTakenToRespondPrompt());
        session.setAttribute("systemRequirementsChecked", true);
        session.setAttribute("isSlowPromptResponse", promptResponse.getPromptStats().isSlowPromptResponse());
        LOGGER.info("SystemRequirements check complete: {}", systemRequirements);
        return systemRequirements;
    }
}
