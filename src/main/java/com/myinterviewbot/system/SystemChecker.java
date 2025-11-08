package com.myinterviewbot.system;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;

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
 */
public class SystemChecker {

    private static final double MIN_CPU_SPEED_GHZ = 2.0;
    private static final int MIN_CPU_CORES = 4;
    private static final long MIN_RAM_MB = 8000;
    private static final boolean REQUIRE_GPU_WINDOWS_LINUX = true;

    private final SystemInfo systemInfo;

    public SystemChecker() {
        systemInfo = new SystemInfo();
    }

    public boolean checkSystemRequirements() {
        boolean cpuOk = checkCPU();
        boolean ramOk = checkRAM();
        boolean gpuOk = checkGPU();
        boolean ollamaOk = checkOllamaInstalled();
        boolean whisperOk = checkWhisperInstalled();

        boolean allOk = cpuOk && ramOk && gpuOk && ollamaOk && whisperOk;

        System.out.println("\n=== System Check Summary ===");
        System.out.println("CPU: " + (cpuOk ? "✅ OK" : "❌ FAIL"));
        System.out.println("RAM: " + (ramOk ? "✅ OK" : "❌ FAIL"));
        System.out.println("GPU: " + (gpuOk ? "✅ OK" : "❌ FAIL"));
        System.out.println("Ollama: " + (ollamaOk ? "✅ OK" : "❌ FAIL"));
        System.out.println("Whisper: " + (whisperOk ? "✅ OK" : "❌ FAIL"));
        System.out.println("============================");

        return allOk;
    }

    // ---------------- CPU ----------------
    private boolean checkCPU() {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        int cores = processor.getLogicalProcessorCount();
        double speedGhz = processor.getMaxFreq() / 1_000_000_000.0;

        System.out.println("Detected CPU cores: " + cores);
        System.out.printf(Locale.US, "Detected CPU speed: %.2f GHz%n", speedGhz);

        if (cores < MIN_CPU_CORES) {
            System.out.println("❌ Minimum " + MIN_CPU_CORES + " CPU cores required.");
            return false;
        }
        if (speedGhz < MIN_CPU_SPEED_GHZ) {
            System.out.println("❌ CPU speed below minimum threshold (" + MIN_CPU_SPEED_GHZ + " GHz).");
            return false;
        }
        return true;
    }

    // ---------------- RAM ----------------
    private boolean checkRAM() {
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        long ramMb = memory.getTotal() / (1024 * 1024);
        System.out.println("Detected RAM: " + ramMb + " MB");

        if (ramMb < MIN_RAM_MB) {
            System.out.println("❌ Minimum " + MIN_RAM_MB + " MB RAM required.");
            return false;
        }
        return true;
    }

    // ---------------- GPU ----------------
    private boolean checkGPU() {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isMac = os.contains("mac");

        List<GraphicsCard> gpus = systemInfo.getHardware().getGraphicsCards();
        boolean gpuDetected = !gpus.isEmpty();

        if (!gpuDetected && REQUIRE_GPU_WINDOWS_LINUX && !isMac) {
            System.out.println("❌ Dedicated GPU not detected — performance will be very slow.");
            return false;
        }

        if (gpuDetected) {
            System.out.println("Detected GPU(s):");
            gpus.forEach(gpu -> System.out.println(" - " + gpu.getName()));
        } else {
            System.out.println("⚠️ No GPU detected (may be OK on Mac).");
        }

        return true;
    }

    // ---------------- Ollama ----------------
    private boolean checkOllamaInstalled() {
        try {
            String output = runCommand("ollama --version");
            boolean installed = output.toLowerCase().contains("ollama");
            if (installed) {
                System.out.println("✅ Ollama detected: " + output.trim());
                return true;
            } else {
                System.out.println("❌ Ollama not found.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Ollama not installed or not in PATH.");
            return false;
        }
    }

    // ---------------- Whisper ----------------
    private boolean checkWhisperInstalled() {
        try {
            String output = runCommand("whisper --help");
            if (output.toLowerCase().contains("usage") || output.toLowerCase().contains("options")) {
                System.out.println("✅ Whisper detected.");
                return true;
            } else {
                System.out.println("❌ Whisper not found.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Whisper not installed or not in PATH.");
            return false;
        }
    }

    // ---------------- Helper Methods ----------------
    private String runCommand(String command) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder builder;

        if (os.contains("win")) {
            builder = new ProcessBuilder("cmd.exe", "/c", command);
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
