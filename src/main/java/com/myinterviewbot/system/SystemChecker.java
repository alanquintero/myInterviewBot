package com.myinterviewbot.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Utility class to verify that the user's system meets the minimum hardware and software
 * requirements to run Ollama models and Whisper efficiently.
 *
 * <p>Rules:
 * <ul>
 *     <li>macOS: GPU not required (Apple Silicon unified GPU is sufficient)</li>
 *     <li>Windows/Linux: GPU is required for good performance</li>
 *     <li>All OSes: Require minimum CPU speed, core count, and RAM</li>
 * </ul>
 *
 * <p>This class runs local system commands to check hardware info and whether
 * Ollama and Whisper are installed.</p>
 */
public class SystemChecker {

    private static final double MIN_CPU_SPEED_GHZ = 2.0;
    private static final int MIN_CPU_CORES = 4;
    private static final long MIN_RAM_MB = 8000; // 8 GB minimum
    private static final boolean REQUIRE_GPU_WINDOWS_LINUX = true;

    /**
     * Runs all system checks and prints results to the console.
     *
     * @return true if the system meets all minimum requirements
     */
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

    private boolean checkCPU() {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Detected CPU cores: " + cores);
        if (cores < MIN_CPU_CORES) {
            System.out.println("❌ Minimum " + MIN_CPU_CORES + " CPU cores required.");
            return false;
        }

        double speedGhz = getCpuSpeedGHz();
        System.out.printf(Locale.US, "Detected CPU speed: %.2f GHz%n", speedGhz);
        if (speedGhz < MIN_CPU_SPEED_GHZ) {
            System.out.println("❌ CPU speed below minimum threshold (" + MIN_CPU_SPEED_GHZ + " GHz).");
            return false;
        }

        return true;
    }

    private boolean checkRAM() {
        long ramMb = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        System.out.println("Detected RAM: " + ramMb + " MB");
        if (ramMb < MIN_RAM_MB) {
            System.out.println("❌ Minimum " + MIN_RAM_MB + " MB RAM required.");
            return false;
        }
        return true;
    }

    private boolean checkGPU() {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isMac = os.contains("mac");
        boolean gpuDetected = false;

        try {
            if (isMac) {
                // On macOS, assume Apple Silicon unified GPU is sufficient
                String chip = runCommand("sysctl -n machdep.cpu.brand_string");
                if (chip.toLowerCase().contains("apple")) {
                    System.out.println("✅ Apple Silicon unified GPU detected.");
                    gpuDetected = true;
                } else {
                    System.out.println("⚠️ Intel mac detected, GPU not required but performance may be lower.");
                    gpuDetected = true;
                }
            } else if (os.contains("win")) {
                gpuDetected = runCommand("wmic path win32_VideoController get name").toLowerCase().contains("nvidia");
            } else if (os.contains("linux")) {
                gpuDetected = runCommand("lspci | grep -i nvidia").toLowerCase().contains("nvidia");
            }

            if (!gpuDetected && REQUIRE_GPU_WINDOWS_LINUX && !isMac) {
                System.out.println("❌ Dedicated GPU not detected — performance will be very slow.");
                return false;
            }

        } catch (Exception e) {
            System.out.println("⚠️ Unable to verify GPU presence: " + e.getMessage());
        }

        return true;
    }

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

    private boolean checkWhisperInstalled() {
        try {
            String output = runCommand("whisper --help");
            boolean installed = output.toLowerCase().contains("usage");
            if (installed) {
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

    // --- Helpers ---

    private double getCpuSpeedGHz() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String output;

            if (os.contains("mac")) {
                output = runCommand("sysctl -n hw.cpufrequency");
                return Long.parseLong(output.trim()) / 1_000_000_000.0;
            } else if (os.contains("win")) {
                output = runCommand("wmic cpu get MaxClockSpeed");
                for (String line : output.split("\\r?\\n")) {
                    if (line.trim().matches("\\d+")) {
                        return Double.parseDouble(line.trim()) / 1000.0;
                    }
                }
            } else { // Linux
                output = runCommand("lscpu | grep 'MHz'");
                if (output.contains(":")) {
                    String mhz = output.split(":")[1].trim();
                    return Double.parseDouble(mhz) / 1000.0;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Unable to detect CPU speed: " + e.getMessage());
        }
        return 0.0;
    }

    private String runCommand(String command) throws Exception {
        Process process = new ProcessBuilder("bash", "-c", command).redirectErrorStream(true).start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        process.waitFor();
        return output.toString();
    }
}


