/**
 * Copyright 2025 Alan Quintero
 * Source: https://github.com/alanquintero/myInterviewBot
 */
package com.myinterviewbot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    void getTimestamp() {
        // Given
        final String filename = "1761356955742-answer.webm";

        // When
        final long timestamp = Utils.getTimestamp(filename);

        // Then
        assertEquals(1761356955742L, timestamp);
    }

    @Test
    void getTimestamp_fileNameDoesNotHaveTimestamp() {
        // Given
        final String filename = "answer.webm";

        // When
        final long timestamp = Utils.getTimestamp(filename);

        // Then
        assertEquals(0L, timestamp);
    }

    @Test
    void getVideoUrl() {
        // Given
        final String filename = "1761356955742-answer.webm";

        // When
        final String videoUrl = Utils.getVideoUrl(filename);

        // Then
        final String expectedUrl = Utils.INTERVIEWS_DIR + "1761356955742-answer/" + filename;
        assertEquals(expectedUrl, videoUrl);
    }

    @Test
    void getVideoUrl_fileNameDoesNotHaveExtension() {
        // Given
        final String filename = "1761356955742-answer";

        // When
        final String videoUrl = Utils.getVideoUrl(filename);

        // Then
        final String expectedUrl = Utils.INTERVIEWS_DIR + "1761356955742-answer/" + filename;
        assertEquals(expectedUrl, videoUrl);
    }

    @Test
    void countWords() {
        // Given
        final String text = "hello this is a test";

        // When
        final int words = Utils.countWords(text);

        // Then
        assertEquals(5, words);
    }

    @Test
    void countWords_inputIsEmpty() {
        // When
        final int words = Utils.countWords("");

        // Then
        assertEquals(0, words);
    }

    @Test
    void removeQuotes() {
        // Given
        final String inputText = "\"hello this is a test\"";

        // When
        final String text = Utils.removeQuotes(inputText);

        // Then
        assertEquals("hello this is a test", text);
    }

    @Test
    void removeQuotes_textHasNoQuotes() {
        // Given
        final String inputText = "hello this is a test";

        // When
        final String text = Utils.removeQuotes(inputText);

        // Then
        assertEquals(inputText, text);
    }

    @Test
    void removeQuotesAndFormatList() {
        // Given
        final String inputText = "hello this is a test. 1. One. 2. Two three. 3. Tamarindo.";

        // When
        final String text = Utils.removeQuotesAndFormatList(inputText);

        // Then
        assertEquals("hello this is a test. \n1. One. \n2. Two three. \n3. Tamarindo.", text);
    }

    @Test
    void removeQuotesAndFormatList_textHasAsteriskForList() {
        // Given
        final String inputText = "hello this is a test. * One. * Two three. * Tamarindo.";

        // When
        final String text = Utils.removeQuotesAndFormatList(inputText);

        // Then
        assertEquals("hello this is a test.\n* One.\n* Two three.\n* Tamarindo.", text);
    }

    @Test
    void removeQuotesAndFormatList_dotFollowBySpecialCharacters() {
        // Given
        final String inputText = "hello this is a test in a 'stand-up meeting.'-First step";

        // When
        final String text = Utils.removeQuotesAndFormatList(inputText);

        // Then
        assertEquals("hello this is a test in a 'stand-up meeting.'\n-First step", text);
    }

    @Test
    void removeQuotesAndFormatList_dotFollowBySpecialCharacter() {
        // Given
        final String inputText = "hello this is a test in a stand-up meeting.-First step";

        // When
        final String text = Utils.removeQuotesAndFormatList(inputText);

        // Then
        assertEquals("hello this is a test in a stand-up meeting.\n-First step", text);
    }

    @Test
    void removeQuotesAndFormatList_dotFollowBySpecialCharacterAndSpace() {
        // Given
        final String inputText = "hello this is a test in a stand-up meeting.- First step";

        // When
        final String text = Utils.removeQuotesAndFormatList(inputText);

        // Then
        assertEquals("hello this is a test in a stand-up meeting.\n- First step", text);
    }

    @Test
    void removeQuotesAndFormatList_shouldNotBreakLine() {
        // Given
        final String inputText = "hello this is a test in a stand-up meeting.";

        // When
        final String text = Utils.removeQuotesAndFormatList(inputText);

        // Then
        assertEquals(inputText, text);
    }

    @Test
    void removeQuotesAndFormatList_textHasNoLists() {
        // Given
        final String inputText = "hello this is a test";

        // When
        final String text = Utils.removeQuotes(inputText);

        // Then
        assertEquals(inputText, text);
    }

    @Test
    void extractJson() {
        // Given
        final String inputText = "{\"text\": \"hello this is a test\"}";

        // When
        final String json = Utils.extractJson(inputText);

        // Then
        assertEquals(inputText, json);
    }

    @Test
    void extractJson_textContainsMoreThings() {
        // Given
        final String inputText = "here: {\"text\": \"hello this is a test\"}";

        // When
        final String json = Utils.extractJson(inputText);

        // Then
        assertEquals("{\"text\": \"hello this is a test\"}", json);
    }

    @Test
    void extractJson_notValidJson() {
        // Given
        final String inputText = "{\"text\": \"hello this is a test\"";

        // When
        final String json = Utils.extractJson(inputText);

        // Then
        assertNull(json);
    }

    @Test
    void extractJson_notContainsJson() {
        // Given
        final String inputText = "hello this is a test";

        // When
        final String json = Utils.extractJson(inputText);

        // Then
        assertNull(json);
    }

    @Test
    void extractQuestion_questionEndsWithDot() {
        // Given
        final String text = "Describe a time when you had to learn something new quickly. This shortened version of \"Can you describe an experience where your learning speed helped solve a technical challenge?\" fits within the word limit and targets relevant skills for a Software Engineer, such as adaptability and self-motivated learning.";

        // When
        final String question = Utils.extractQuestion(text);

        // Then
        assertEquals("Describe a time when you had to learn something new quickly.", question);
    }

    @Test
    void extractQuestion_questionEndsWithQuestionMark() {
        // Given
        final String text = "Describe a time when you had to learn something new quickly? This shortened version of \"Can you describe an experience where your learning speed helped solve a technical challenge?\" fits within the word limit and targets relevant skills for a Software Engineer, such as adaptability and self-motivated learning.";

        // When
        final String question = Utils.extractQuestion(text);

        // Then
        assertEquals("Describe a time when you had to learn something new quickly?", question);
    }

    @Test
    void extractQuestion_noQuestionFound() {
        // Given
        final String text = "F fits within the word limit and targets relevant skills for a Software Engineer, such as adaptability and self-motivated learning";

        // When
        final String question = Utils.extractQuestion(text);

        // Then
        assertEquals(text, question);
    }
}
