package test;

import model.DifficultyColor;
import model.GameAnswerColor;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestGameAnswerColor {
	private void testColor(DifficultyColor color, String description, String[] words, String[] hints) {
		Set<String> wordsSet = new HashSet<>(Arrays.asList(words));
		String[] wordsDiff = { "x", "x", "x", "x" };
		Set<String> wordsSetDiff = new HashSet<>(Arrays.asList(wordsDiff));

		GameAnswerColor testGameAnswer = new GameAnswerColor(color, description, hints, words);

		assertEquals(color, testGameAnswer.getColor());
		assertEquals(description, testGameAnswer.getDescription());
		assertArrayEquals(hints, testGameAnswer.getHints());
		assertArrayEquals(words, testGameAnswer.getWords());

		assertTrue(testGameAnswer.wordMatchesSet(wordsSet));
		assertFalse(testGameAnswer.wordMatchesSet(wordsSetDiff));

		assertEquals(String.join(", ", words).toUpperCase(), testGameAnswer.getWordListString());
	}

	@Test
	public void testYellow() {
		DifficultyColor color = DifficultyColor.YELLOW;
		String description = "Yellow description";
		String[] hints = { "Yellow Hint1", "Yellow Hint2" };
		String[] words = { "wy1", "wy2", "wy3", "wy4" };
		testColor(color, description, words, hints);
	}

	@Test
	public void testPurple() {
		DifficultyColor color = DifficultyColor.PURPLE;
		String description = "Purple description";
		String[] hints = { "Purple Hint1", "Purple Hint2" };
		String[] words = { "wp1", "wp2", "wp3", "wp4" };
		testColor(color, description, words, hints);
	}

	@Test
	public void testGreen() {
		DifficultyColor color = DifficultyColor.GREEN;
		String description = "Green description";
		String[] hints = { "Green Hint1", "Green Hint2" };
		String[] words = { "wg1", "wg2", "wg3", "wg4" };
		testColor(color, description, words, hints);
	}

	@Test
	public void testBlue() {
		DifficultyColor color = DifficultyColor.BLUE;
		String description = "Blue description";
		String[] hints = { "Blue Hint1", "Blue Hint2" };
		String[] words = { "wb1", "wb2", "wb3", "wb4" };
		testColor(color, description, words, hints);
	}
}
