package com.connections.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.connections.model.DifficultyColor;
import com.connections.model.Word;

class TestWord {

	@Test
	void testConstructorWithDocument() {
		Document doc = new Document(Word.KEY_TEXT, "apple").append(Word.KEY_COLOR, "yellow");
		Word word = new Word(doc);

		assertEquals("apple", word.getText());
		assertEquals(DifficultyColor.YELLOW, word.getColor());
	}

	@Test
	void testConstructorWithParameters() {
		Word word = new Word("banana", DifficultyColor.GREEN);

		assertEquals("banana", word.getText());
		assertEquals(DifficultyColor.GREEN, word.getColor());
	}

	@Test
	void testGetAsDatabaseFormat() {
		Word word = new Word("cherry", DifficultyColor.PURPLE);
		Document expected = new Document(Word.KEY_TEXT, "cherry").append(Word.KEY_COLOR, "purple");

		Document actual = word.getAsDatabaseFormat();

		assertEquals(expected, actual);
	}

	@Test
	void testLoadFromDatabaseFormat() {
		Document doc = new Document(Word.KEY_TEXT, "date").append(Word.KEY_COLOR, "BLUE");
		Word word = new Word(null, null);
		word.loadFromDatabaseFormat(doc);

		assertEquals("date", word.getText());
		assertEquals(DifficultyColor.BLUE, word.getColor());
	}

	@Test
	void testEquals() {
		Word word1 = new Word("apple", DifficultyColor.YELLOW);
		Word word2 = new Word("apple", DifficultyColor.YELLOW);
		Word word3 = new Word("apple", DifficultyColor.GREEN);
		Word word4 = new Word("banana", DifficultyColor.YELLOW);
		Word word5 = new Word("banana", DifficultyColor.GREEN);

		assertTrue(word1.equals(word2));
		assertFalse(word1.equals(word3));
		assertFalse(word1.equals(word4));
		assertFalse(word1.equals(word5));

		assertFalse(word1.equals(null));
		assertFalse(word1.equals(new Object()));
	}
}