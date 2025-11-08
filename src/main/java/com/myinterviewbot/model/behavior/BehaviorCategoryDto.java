/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model.behavior;

/**
 * Dto class to store the behavior categories.
 *
 * @author Alan Quintero
 */
public class BehaviorCategoryDto {

    private String name;
    private String displayName;
    private String description;
    private String example;

    public BehaviorCategoryDto() {
    }

    public BehaviorCategoryDto(final String name, final String displayName, final String description, final String example) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.example = example;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
