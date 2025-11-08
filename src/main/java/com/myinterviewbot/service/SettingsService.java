/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myinterviewbot.model.Settings;
import com.myinterviewbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Singleton service class that manages settings data during the application's runtime.
 * <p>
 * This class holds all settings in memory and provides methods to access, add, update settings.
 * It can also persist the data to a JSON file and load it when the application starts.
 * </p>
 *
 * @author Alan Quintero
 */
public class SettingsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsService.class);

    private static SettingsService instance;
    private Settings settings;
    private final ObjectMapper objectMapper;
    private File storageFile;
    private String aiModelFromConfig;

    private SettingsService() {
        this.settings = new Settings();
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized SettingsService getInstance() {
        if (instance == null) {
            instance = new SettingsService();
        }
        return instance;
    }

    public void initData(final String aiProvider, final String aiModel, final String whisperProvider) throws IOException {
        LOGGER.info("initData");
        aiModelFromConfig = aiModel;
        // Ensure base upload directory exists
        final File uploadsDir = new File("uploads/settings");
        if (!uploadsDir.exists() && !uploadsDir.mkdirs()) {
            throw new IOException("Failed to create uploads/settings directory");
        }

        storageFile = new File(uploadsDir, "settings.json");

        // Create empty JSON file if it doesn't exist
        if (!storageFile.exists()) {
            try {
                settings.setAiProvider(aiProvider);
                settings.setSelectedAiModel(aiModel);
                settings.setWhisperProvider(whisperProvider);
                settings.setOperatingSystem(Utils.getOperatingSystemName());
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, settings);
            } catch (IOException e) {
                throw new IOException("Failed to create interviews.json");
            }
        }

        loadFromFile();
    }

    public Settings getSettings() {
        return settings;
    }

    public void saveAiModels(final List<String> aiModels) {
        settings.setAiModels(aiModels);

        // check if selected AI Model is in the list
        if (!aiModels.contains(settings.getSelectedAiModel())) {
            LOGGER.warn("Selected AI Model {} does not exist. Selected AI Model {} from config.", settings.getSelectedAiModel(), aiModelFromConfig);
            settings.setSelectedAiModel(aiModelFromConfig);
        }

        saveSettings(settings);
    }

    public void saveSettings(final Settings settings) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, settings);
        } catch (Exception e) {
            LOGGER.error("Failed to save settings.json", e);
        }
    }

    private void loadFromFile() {
        LOGGER.info("loadFromFile");
        if (storageFile.exists()) {
            try {
                settings = objectMapper.readValue(storageFile, Settings.class);
                LOGGER.info("Loaded settings from file: {}", settings);
            } catch (IOException e) {
                LOGGER.error("Failed to load settings: {}", e.getMessage());
            }
        }
    }
}
