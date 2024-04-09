package test;

import model.DifficultyColor;
import model.GameAnswerColor;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGameAnswerColor {
    @Test
    public void testYellow() {
        DifficultyColor color = DifficultyColor.YELLOW;
        String description = "Yellow description";
        String[] hints = {"Yellow Hint1", "Yellow Hint2"};
        String[] words = {"wy1", "wy2", "wy3", "wy4"};

        GameAnswerColor testGameAnswer = new GameAnswerColor(color, description, hints, words);

        assertEquals(color, testGameAnswer.getColor());
        assertEquals(description, testGameAnswer.getDescription());
        assertArrayEquals(hints, testGameAnswer.getHints());
        assertArrayEquals(words, testGameAnswer.getWords());
    }

    @Test
    public void testPurple() {
        DifficultyColor color = DifficultyColor.PURPLE;
        String description = "Purple description";
        String[] hints = {"Purple Hint1", "Purple Hint2"};
        String[] words = {"wp1", "wp2", "wp3", "wp4"};

        GameAnswerColor testGameAnswer = new GameAnswerColor(color, description, hints, words);

        assertEquals(color, testGameAnswer.getColor());
        assertEquals(description, testGameAnswer.getDescription());
        assertArrayEquals(hints, testGameAnswer.getHints());
        assertArrayEquals(words, testGameAnswer.getWords());
    }

    @Test
    public void testGreen() {
        DifficultyColor color = DifficultyColor.GREEN;
        String description = "Green description";
        String[] hints = {"Green Hint1", "Green Hint2"};
        String[] words = {"wg1", "wg2", "wg3", "wg4"};

        GameAnswerColor testGameAnswer = new GameAnswerColor(color, description, hints, words);

        assertEquals(color, testGameAnswer.getColor());
        assertEquals(description, testGameAnswer.getDescription());
        assertArrayEquals(hints, testGameAnswer.getHints());
        assertArrayEquals(words, testGameAnswer.getWords());
    }

    @Test
    public void testBlue() {
        DifficultyColor color = DifficultyColor.BLUE;
        String description = "Blue description";
        String[] hints = {"Blue Hint1", "Blue Hint2"};
        String[] words = {"wb1", "wb2", "wb3", "wb4"};

        GameAnswerColor testGameAnswer = new GameAnswerColor(color, description, hints, words);

        assertEquals(color, testGameAnswer.getColor());
        assertEquals(description, testGameAnswer.getDescription());
        assertArrayEquals(hints, testGameAnswer.getHints());
        assertArrayEquals(words, testGameAnswer.getWords());
    }
}
