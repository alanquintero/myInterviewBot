/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.model.settings;

/**
 * DTO class to return current All Settings.
 *
 * @author Alan Quintero
 */
public class Settings {

    private AppSettings appSettings;

    private SystemSettings systemSettings;

    public Settings() {
        appSettings = new AppSettings();
        systemSettings = new SystemSettings();
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(final AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    public SystemSettings getSystemSettings() {
        return systemSettings;
    }

    public void setSystemSettings(final SystemSettings systemSettings) {
        this.systemSettings = systemSettings;
    }
}
