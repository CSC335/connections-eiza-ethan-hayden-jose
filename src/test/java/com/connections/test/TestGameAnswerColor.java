package com.connections.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.connections.model.DifficultyColor;
import com.connections.model.GameAnswerColor;

public class TestGameAnswerColor {

	@Test
	void testConstructorWithDocument() {
		Document doc = new Document(GameAnswerColor.KEY_COLOR, "yellow")
				.append(GameAnswerColor.KEY_DESCRIPTION, "Sunny")
				.append(GameAnswerColor.KEY_WORDS, Arrays.asList("sun", "lemon", "banana", "gold"));
		GameAnswerColor answerColor = new GameAnswerColor(doc);

		assertEquals(DifficultyColor.YELLOW, answerColor.getColor());
		assertEquals("Sunny", answerColor.getDescription());
		assertArrayEquals(new String[] { "sun", "lemon", "banana", "gold" }, answerColor.getWords());
	}

	@Test
	void testConstructorWithParameters() {
		String[] words = { "apple", "pear", "orange", "peach" };
		GameAnswerColor answerColor = new GameAnswerColor(DifficultyColor.GREEN, "Fruity", words);

		assertEquals(DifficultyColor.GREEN, answerColor.getColor());
		assertEquals("Fruity", answerColor.getDescription());
		assertArrayEquals(words, answerColor.getWords());
	}

	@Test
	void testGetAsDatabaseFormat() {
		String[] words = { "car", "bike", "train", "plane" };
		GameAnswerColor answerColor = new GameAnswerColor(DifficultyColor.BLUE, "Transportation", words);
		Document expected = new Document(GameAnswerColor.KEY_COLOR, "blue")
				.append(GameAnswerColor.KEY_DESCRIPTION, "Transportation")
				.append(GameAnswerColor.KEY_WORDS, Arrays.asList(words));

		Document actual = answerColor.getAsDatabaseFormat();

		assertEquals(expected, actual);
	}

	@Test
	void testWordMatchesSet() {
		String[] words = { "chair", "table", "couch", "bed" };
		GameAnswerColor answerColor = new GameAnswerColor(DifficultyColor.PURPLE, "Furniture", words);
		Set<String> matchingSet = new HashSet<>(Arrays.asList(words));
		Set<String> nonMatchingSet = new HashSet<>(Arrays.asList("apple", "banana", "orange"));

		assertTrue(answerColor.wordMatchesSet(matchingSet));
		assertFalse(answerColor.wordMatchesSet(nonMatchingSet));
	}

	@Test
	void testGetWordListString() {
		String[] words = { "cat", "dog", "bird", "fish" };
		GameAnswerColor answerColor = new GameAnswerColor(DifficultyColor.YELLOW, "Pets", words);

		assertEquals("CAT, DOG, BIRD, FISH", answerColor.getWordListString());
	}
}