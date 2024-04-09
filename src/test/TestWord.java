package test;

import org.junit.jupiter.api.Test;

import model.DifficultyColor;
import model.Word;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWord {
	@Test
	public void testNullWord() { 
		Word word = new Word(null, null);
		assertNull(word.getText());
		assertNull(word.getColor());
	}
	
	@Test
	public void testWordYellow() {
		String text = "yellow";
		DifficultyColor color = DifficultyColor.YELLOW;
		Word word = new Word(text, color);
		assertEquals(word.getText(), text);
		assertEquals(word.getColor(), color);
	}
	
	@Test
	public void testWordGreen() {
		String text = "green";
		DifficultyColor color = DifficultyColor.GREEN;
		Word word = new Word(text, color);
		assertEquals(word.getText(), text);
		assertEquals(word.getColor(), color);
	}

	@Test
	public void testWordBlue() {
		String text = "blue";
		DifficultyColor color = DifficultyColor.BLUE;
		Word word = new Word(text, color);
		assertEquals(word.getText(), text);
		assertEquals(word.getColor(), color);
	}

	@Test
	public void testWordPurple() {
		String text = "purple";
		DifficultyColor color = DifficultyColor.PURPLE;
		Word word = new Word(text, color);
		assertEquals(word.getText(), text);
		assertEquals(word.getColor(), color);
	}
}
