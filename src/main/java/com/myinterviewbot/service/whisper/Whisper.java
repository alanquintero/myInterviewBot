/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.service.whisper;

import java.io.File;

/**
 * Interface for Whisper Services
 *
 * @author Alan Quintero
 */
public interface Whisper {

    String transcribe(final File audioFile);
}
