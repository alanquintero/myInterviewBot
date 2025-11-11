/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model.behavior;

/**
 * Enum class to store the behavior categories.
 *
 * @author Alan Quintero
 */
public enum BehaviorCategory {

    TEAMWORK(
            "Teamwork",
            "How you collaborate, resolve conflict, or support others.",
            "Tell me about a time you worked on a team project that failed."
    ),
    LEADERSHIP(
            "Leadership",
            "How you influence, motivate, or guide others.",
            "Describe a time when you had to lead a team through a difficult project."
    ),
    PROBLEM_SOLVING(
            "Problem Solving",
            "How you analyze issues and find creative solutions.",
            "Tell me about a time you solved a complex problem with limited information."
    ),
    COMMUNICATION(
            "Communication",
            "How clearly and effectively you express ideas.",
            "Describe a situation where you had to explain a technical concept to a non-technical person."
    ),
    CONFLICT_RESOLUTION(
            "Conflict Resolution",
            "How you handle disagreements or tough situations.",
            "Tell me about a time you had a conflict with a coworker and how you resolved it."
    ),
    ADAPTABILITY(
            "Adaptability",
            "How you handle change, uncertainty, or setbacks.",
            "Describe a time you had to learn something new quickly to complete a task."
    ),
    OWNERSHIP(
            "Ownership / Accountability",
            "How you take responsibility for outcomes.",
            "Tell me about a time when you made a mistake at work — how did you handle it?"
    ),
    TIME_MANAGEMENT(
            "Time Management / Prioritization",
            "How you manage competing tasks and deadlines.",
            "Describe a time you had multiple deadlines — how did you prioritize?"
    ),
    ETHICS(
            "Ethics / Integrity",
            "How you act under pressure or when facing ethical dilemmas.",
            "Tell me about a time you faced an ethical decision at work."
    ),
    ACHIEVEMENT(
            "Achievement / Results Orientation",
            "How you measure success and deliver results.",
            "Tell me about your biggest professional accomplishment."
    ),
    CUSTOMER_FOCUS(
            "Customer Focus",
            "How you consider end users or clients in your work.",
            "Give me an example of a time you improved a product or service for a user."
    ),
    INITIATIVE(
            "Initiative / Innovation",
            "How you take action without being told.",
            "Describe a time you identified a problem and solved it without being asked."
    ),
    FAILURE_LEARNING(
            "Failure / Learning",
            "How you respond to mistakes or failures.",
            "Tell me about a time you failed — what did you learn?"
    );

    private final String displayName;
    private final String description;
    private final String example;

    BehaviorCategory(final String displayName, final String description, final String example) {
        this.displayName = displayName;
        this.description = description;
        this.example = example;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getExample() {
        return example;
    }

    public static String getDisplayNameFromName(final String name) {
        if (name != null) {
            try {
                final BehaviorCategory category = BehaviorCategory.valueOf(name);
                return category.getDisplayName();
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
