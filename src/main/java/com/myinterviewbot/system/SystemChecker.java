/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.system;

import com.myinterviewbot.model.SystemRequirements;
import com.myinterviewbot.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Cross-platform system requirement checker for Ollama and Whisper using OSHI library.
 *
 * <p>Checks:
 * <ul>
 *   <li>CPU cores and speed</li>
 *   <li>RAM size</li>
 *   <li>GPU availability (required on Windows/Linux)</li>
 *   <li>Ollama and Whisper installation</li>
 * </ul>
 *
 * @author Alan Quintero
 */
@Service
public class SystemChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemChecker.class);

    private static final double MIN_CPU_SPEED_GHZ = 2.0;
    private static final int MIN_CPU_CORES = 4;
    private static final long MIN_RAM_MB = 8000;
    private static final boolean REQUIRE_GPU_WINDOWS_LINUX = true;

    @Value("${ai.provider}")
    private String aiProvider;

    @Value("${whisper.provider}")
    private String whisperProvider;

    private String aiModel;

    private String cpuMessage = "";

    private String ramMessage = "";

    private String gpuMessage = "";

    private String aiProviderMessage = "";

    private String aiModelMessage = "";

    private String ffmpegMessage = "";

    private String whisperProviderMessage = "";

    private final SystemInfo systemInfo;

    private final SystemRequirements systemRequirements;

    public SystemChecker() {
        systemInfo = new SystemInfo();
        systemRequirements = new SystemRequirements();
    }

    public SystemRequirements checkSystemRequirements() {
        aiModel = SettingsService.getInstance().getSettings().getSystemSettings().getSelectedAiModel();

        final boolean cpuOk = checkCPU();
        systemRequirements.setCpuHasMinimumRequirements(cpuOk);
        final boolean ramOk = checkRAM();
        systemRequirements.setRamHasMinimumRequirements(ramOk);
        final boolean gpuOk = checkGPU();
        systemRequirements.setGpuHasMinimumRequirements(gpuOk);
        final boolean aiProviderOk = checkAiProviderInstalled();
        systemRequirements.setAiProviderAvailable(aiProviderOk);
        final boolean aiModelOk = checkAiModelInstalled();
        systemRequirements.setAiModelAvailable(aiModelOk);
        final boolean ffmpegOk = checkFfmpegInstalled();
        systemRequirements.setFfmpegAvailable(ffmpegOk);
        final boolean whisperOk = checkWhisperInstalled();
        systemRequirements.setWhisperServiceAvailable(whisperOk);

        final boolean allOk = cpuOk && ramOk && gpuOk && aiProviderOk && aiModelOk && whisperOk;
        systemRequirements.setAreAllSystemRequirementsMet(allOk);
        LOGGER.info("AreAllSystemRequirementsMet: {}", allOk);

        final String systemRequirementsMessage = "<pre>"
                + "CPU: " + (cpuOk ? "✅ OK" : cpuMessage) + "\n"
                + "RAM: " + (ramOk ? "✅ OK" : ramMessage) + "\n"
                + "GPU: " + (gpuOk ? "✅ OK" : gpuMessage) + "\n"
                + "AI Provider(" + aiProvider + "): " + (aiProviderOk ? "✅ OK" : aiProviderMessage) + "\n"
                + "AI Model(" + aiModel + "): " + (aiModelOk ? "✅ OK" : aiModelMessage) + "\n"
                + "FFmpeg: " + (ffmpegOk ? "✅ OK" : ffmpegMessage) + "\n"
                + "Whisper(" + whisperProvider + "): " + (whisperOk ? "✅ OK" : whisperProviderMessage) + "\n"
                + "</pre>";
        systemRequirements.setSystemRequirementsMessage(systemRequirementsMessage);
        LOGGER.info(systemRequirementsMessage);

        LOGGER.info("System Requirements completed");
        return systemRequirements;
    }

    // ---------------- CPU ----------------
    private boolean checkCPU() {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        int cores = processor.getLogicalProcessorCount();
        double speedGhz = processor.getMaxFreq() / 1_000_000_000.0;

        LOGGER.info("Detected CPU cores: {}", cores);
        System.out.printf(Locale.US, "Detected CPU speed: %.2f GHz%n", speedGhz);

        if (cores < MIN_CPU_CORES) {
            cpuMessage = "❌ Minimum " + MIN_CPU_CORES + " CPU cores required.";
            LOGGER.warn(cpuMessage);
            return false;
        }
        if (speedGhz < MIN_CPU_SPEED_GHZ) {
            final String cpuSpeedMessage = "❌ CPU speed below minimum threshold (" + MIN_CPU_SPEED_GHZ + " GHz).";
            cpuMessage += "\n" + cpuSpeedMessage;
            LOGGER.warn(cpuSpeedMessage);
            return false;
        }
        return true;
    }

    // ---------------- RAM ----------------
    private boolean checkRAM() {
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        long ramMb = memory.getTotal() / (1024 * 1024);
        LOGGER.info("Detected RAM: {} MB", ramMb);

        if (ramMb < MIN_RAM_MB) {
            ramMessage = "❌ Minimum " + MIN_RAM_MB + " MB RAM required.";
            LOGGER.warn(ramMessage);
            return false;
        }
        return true;
    }

    // ---------------- GPU ----------------
    private boolean checkGPU() {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isMac = os.contains("mac");

        List<GraphicsCard> gpus = systemInfo.getHardware().getGraphicsCards();

        if (gpus.isEmpty()) {
            if (!isMac && REQUIRE_GPU_WINDOWS_LINUX) {
                gpuMessage = "❌ Dedicated GPU not detected — performance will be very slow.";
                LOGGER.warn(gpuMessage);
                return false;
            } else {
                LOGGER.warn("⚠️ No GPU detected (may be OK on Mac).");
                return true; // Mac can be OK without discrete GPU
            }
        }

        boolean gpuOk = false;
        LOGGER.info("Detected GPU(s):");
        for (GraphicsCard gpu : gpus) {
            long vramMB = gpu.getVRam() / (1024 * 1024);
            LOGGER.info(" - {} ({} MB VRAM)", gpu.getName(), vramMB);

            if (vramMB >= 2048 &&
                    (gpu.getName().toLowerCase().contains("nvidia") ||
                            gpu.getName().toLowerCase().contains("amd") ||
                            gpu.getName().toLowerCase().contains("apple"))) {
                gpuOk = true;
            }
        }

        if (!gpuOk && !isMac && REQUIRE_GPU_WINDOWS_LINUX) {
            gpuMessage = "❌ No GPU meets minimum requirements (≥2GB VRAM, NVIDIA/AMD/Apple).";
            LOGGER.warn(gpuMessage);
            return false;
        }

        return true;
    }

    // ---------------- AI Provider ----------------
    private boolean checkAiProviderInstalled() {
        if ("ollama".equalsIgnoreCase(aiProvider)) {
            try {
                String output = runCommand("ollama --version");
                boolean installed = output.toLowerCase().contains("ollama");
                if (installed) {
                    LOGGER.info("✅ Ollama detected: {}", output.trim());
                    return true;
                } else {
                    aiProviderMessage = "❌ Ollama not found.";
                    LOGGER.warn(aiProviderMessage);
                    return false;
                }
            } catch (Exception e) {
                aiProviderMessage = "❌ Ollama not installed or not in PATH.";
                LOGGER.warn(aiProviderMessage);
                return false;
            }
        } else {
            aiProviderMessage = "❌ " + aiProvider + " not supported yet.";
            LOGGER.warn(aiProviderMessage);
            return false;
        }
    }

    // ---------------- AI model ----------------
    public boolean checkAiModelInstalled() {
        if ("ollama".equalsIgnoreCase(aiProvider)) {
            try {
                String output = runCommand("ollama list");
                String[] lines = output.split("\\r?\\n");

                // Extract model list for settings
                final List<String> models = new ArrayList<>();
                for (int i = 1; i < lines.length; i++) {
                    String line = lines[i].trim();
                    if (line.isEmpty()) continue;

                    // First column is model name
                    String[] columns = line.split("\\s+");
                    if (columns.length > 0) {
                        models.add(columns[0]);
                    }
                }
                SettingsService.getInstance().saveAiModels(models);

                // Skip header line if present
                for (int i = 1; i < lines.length; i++) {
                    String line = lines[i].trim();
                    if (line.isEmpty()) continue;

                    // The model name is the first column
                    String[] columns = line.split("\\s+");
                    if (columns.length > 0 && columns[0].equalsIgnoreCase(aiModel)) {
                        LOGGER.info("✅ Ollama model detected: {}", aiModel);
                        return true;
                    }
                }

                aiModelMessage = "❌ Ollama model not found: " + aiModel;
                LOGGER.warn(aiModelMessage);
                return false;

            } catch (Exception e) {
                aiModelMessage = "❌ Failed to check Ollama models";
                LOGGER.warn(aiModelMessage + ": {}", e.getMessage());
                return false;
            }
        } else {
            aiModelMessage = "❌ cannot verify " + aiModel + " model because " + aiProvider + " not supported yet.";
            LOGGER.warn(aiModelMessage);
            return false;
        }
    }

    // ---------------- FFmpeg ----------------
    private boolean checkFfmpegInstalled() {
        try {
            String output = runCommand("ffmpeg -version");
            if (output.toLowerCase().contains("ffmpeg") || output.toLowerCase().contains("version")) {
                LOGGER.info("✅ ffmpeg detected.");
                return true;
            } else {
                ffmpegMessage = "❌ FFmpeg not found.";
                LOGGER.warn(ffmpegMessage);
                return false;
            }
        } catch (Exception e) {
            ffmpegMessage = "❌ FFmpeg not installed or not in PATH.";
            LOGGER.warn(ffmpegMessage);
            return false;
        }
    }

    // ---------------- Whisper ----------------
    private boolean checkWhisperInstalled() {
        if ("openai-whisper".equalsIgnoreCase(whisperProvider)) {
            try {
                String output = runCommand("whisper --help");
                if (output.toLowerCase().contains("usage") || output.toLowerCase().contains("options")) {
                    LOGGER.info("✅ Whisper detected.");
                    return true;
                } else {
                    whisperProviderMessage = "❌ Whisper not found.";
                    LOGGER.warn(whisperProviderMessage);
                    return false;
                }
            } catch (Exception e) {
                whisperProviderMessage = "❌ Whisper not installed or not in PATH.";
                LOGGER.warn(whisperProviderMessage);
                return false;
            }
        } else {
            whisperProviderMessage = "❌ " + whisperProvider + " not supported yet.";
            LOGGER.warn(whisperProviderMessage);
            return false;
        }
    }

    // ---------------- Helper Methods ----------------
    private String runCommand(String command) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder builder;

        if (os.contains("win")) {
            builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.environment().put("PYTHONIOENCODING", "UTF-8");
        } else {
            builder = new ProcessBuilder("bash", "-c", command);
        }

        builder.redirectErrorStream(true);
        Process process = builder.start();

        StringBuilder output = new StringBuilder();
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        process.waitFor();
        return output.toString();
    }
}
