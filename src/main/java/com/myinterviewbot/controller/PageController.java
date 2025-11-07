/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling page navigation within the application.
 * <p>
 * This class maps high-level routes to their corresponding Thymeleaf views,
 * setting the appropriate model attributes such as {@code pageTitle} and
 * {@code currentPage}. It serves as the central routing hub for rendering
 * layout-based pages (e.g., behavioral interview, resume interview, settings, etc.).
 * </p>
 *
 * @author Alan Quintero
 */
@Controller
public class PageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

    @GetMapping("/")
    public String showMainPage(Model model) {
        LOGGER.info("at root /");
        model.addAttribute("pageTitle", "Behavioral Interview");
        model.addAttribute("currentPage", "behavioralPage");
        return "layout";
    }

    @GetMapping("/interview/behavioral")
    public String behavioral(Model model) {
        LOGGER.info("/interview/behavioral");
        return showMainPage(model);
    }

    @GetMapping("/interview/resume")
    public String resume(Model model) {
        LOGGER.info("/interview/resume");
        model.addAttribute("pageTitle", "Resume Interview");
        model.addAttribute("currentPage", "resumePage");
        return "layout";
    }

    @GetMapping("/my-interviews")
    public String myInterviews(Model model) {
        LOGGER.info("/my-interviews");
        model.addAttribute("currentPage", "myInterviewsPage");
        return "layout";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        LOGGER.info("/settings");
        model.addAttribute("currentPage", "settingsPage");
        return "layout";
    }
}
