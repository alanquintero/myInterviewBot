/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import com.myinterviewbot.model.PromptExecutionResult;
import com.myinterviewbot.model.PromptResponse;
import com.myinterviewbot.model.SystemRequirements;
import com.myinterviewbot.service.SystemRequirementsService;
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

    private final SystemRequirementsService systemRequirementsService;

    public ApiController(final SystemRequirementsService systemRequirementsService) {
        this.systemRequirementsService = systemRequirementsService;
    }

    @GetMapping("/requirements")
    public SystemRequirements checkSystemRequirements(final HttpSession session) {
        LOGGER.info("Checking SystemRequirements");
        SystemChecker systemChecker = new SystemChecker();
        systemChecker.checkSystemRequirements();
        Boolean systemRequirementsChecked = (Boolean) session.getAttribute("systemRequirementsChecked");
        Boolean isSlowPromptResponse = (Boolean) session.getAttribute("isSlowPromptResponse");
        if (systemRequirementsChecked != null && systemRequirementsChecked && isSlowPromptResponse != null) {
            LOGGER.info("SystemRequirements already checked");
            if (systemRequirements == null) {
                systemRequirements = new SystemRequirements(true, isSlowPromptResponse, false);
            }
            return systemRequirements;
        }
        final PromptResponse promptResponse = systemRequirementsService.executeInitialPrompt();
        final boolean exceptionDetected;
        if (promptResponse.getPromptStats().getReasonExecutionFailed() != null && !PromptExecutionResult.EMPTY_RESULT.equals(promptResponse.getPromptStats().getReasonExecutionFailed())) {
            exceptionDetected = true;
        } else {
            exceptionDetected = false;
        }

        systemRequirements = new SystemRequirements(promptResponse.getPromptStats().isSlowPromptResponse(), promptResponse.getPromptStats().isExecutedSuccessfully(), exceptionDetected);
        session.setAttribute("systemRequirementsChecked", true);
        session.setAttribute("isSlowPromptResponse", promptResponse.getPromptStats().isSlowPromptResponse());
        LOGGER.info("SystemRequirements check complete: {}", systemRequirements);
        return systemRequirements;
    }
}
