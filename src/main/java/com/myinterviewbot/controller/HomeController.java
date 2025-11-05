package com.myinterviewbot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String showMainPage(Model model) {
        model.addAttribute("pageTitle", "Behavioral Interview");
        model.addAttribute("currentPage", "behavioralPage");
        return "layout";
    }

    @GetMapping("/interview/behavioral")
    public String behavioral(Model model) {
        LOGGER.info("/interview/behavioral");
        model.addAttribute("pageTitle", "Behavioral Interview");
        model.addAttribute("content", "behavioralPage :: content");
        return "layout";
    }

    @GetMapping("/interview/resume")
    public String resume(Model model) {
        LOGGER.info("/interview/resume");
        model.addAttribute("pageTitle", "Resume Interview");
        model.addAttribute("content", "resumePage :: content");
        return "layout";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        LOGGER.info("/settings");
        model.addAttribute("content", "settingsPage :: content");
        return "layout";
    }
}
