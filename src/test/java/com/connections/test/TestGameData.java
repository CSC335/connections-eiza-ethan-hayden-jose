package com.connections.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.connections.model.DifficultyColor;
import com.connections.model.GameAnswerColor;
import com.connections.model.GameData;

public class TestGameData {

    @Test
    void testConstructorWithDocument() {
        Document colorDoc1 = new Document(GameAnswerColor.KEY_COLOR, "yellow")
                .append(GameAnswerColor.KEY_DESCRIPTION, "Sunny")
                .append(GameAnswerColor.KEY_WORDS, Arrays.asList("sun", "lemon", "banana", "gold"));
        Document colorDoc2 = new Document(GameAnswerColor.KEY_COLOR, "green")
                .append(GameAnswerColor.KEY_DESCRIPTION, "Fruity")
                .append(GameAnswerColor.KEY_WORDS, Arrays.asList("apple", "pear", "orange", "peach"));
        Document colorDoc3 = new Document(GameAnswerColor.KEY_COLOR, "blue")
                .append(GameAnswerColor.KEY_DESCRIPTION, "Vegetables")
                .append(GameAnswerColor.KEY_WORDS, Arrays.asList("cucumber", "carrot", "potato", "eggplant"));
        Document colorDoc4 = new Document(GameAnswerColor.KEY_COLOR, "purple")
                .append(GameAnswerColor.KEY_DESCRIPTION, "Drinks")
                .append(GameAnswerColor.KEY_WORDS, Arrays.asList("milk", "juice", "soda", "punch"));
        Document doc = new Document(GameData.KEY_PUZZLE_NUMBER, 123)
                .append(GameData.KEY_COLOR_LIST, Arrays.asList(colorDoc1, colorDoc2, colorDoc3, colorDoc4));
        GameData gameData = new GameData(doc);

        assertEquals(123, gameData.getPuzzleNumber());
        Map<DifficultyColor, GameAnswerColor> answerMap = gameData.getAnswerMap();
        assertEquals(4, answerMap.size());
        assertTrue(answerMap.containsKey(DifficultyColor.YELLOW));
        assertTrue(answerMap.containsKey(DifficultyColor.GREEN));
    }

    @Test
    void testGetAnswerForColor() {
    	Document colorDoc1 = new Document(GameAnswerColor.KEY_COLOR, "yellow")
                .append(GameAnswerColor.KEY_DESCRIPTION, "Sunny")
                .append(GameAnswerColor.KEY_WORDS, Arrays.asList("sun", "lemon", "banana", "gold"));
        Document colorDoc2 = new Document(GameAnswerColor.KEY_COLOR, "green")
                .append(GameAnswerColor.KEY_DESCRIPTION, "Fruity")
                .append(GameAnswerColor.KEY_WORDS, Arrays.asList("apple", "pear", "orange", "peach"));
        Document colorDoc3 = new Document(GameAnswerColor.KEY_COLOR, "blue")
                .append(GameAnswerColor.KEY_DESCRIPTION, "Vegetables")
                .append(GameAnswerColor.KEY_WORDS, Arrays.asList("cucumber", "carrot", "potato", "eggplant"));
        Document colorDoc4 = new Document(GameAnswerColor.KEY_COLOR, "purple")
                .append(GameAnswerColor.KEY_DESCRIPTION, "Drinks")
                .append(GameAnswerColor.KEY_WORDS, Arrays.asList("milk", "juice", "soda", "punch"));
        Document doc = new Document(GameData.KEY_PUZZLE_NUMBER, 123)
                .append(GameData.KEY_COLOR_LIST, Arrays.asList(colorDoc1, colorDoc2, colorDoc3, colorDoc4));
        GameData gameData = new GameData(doc);

        GameAnswerColor yellowAnswer = gameData.getAnswerForColor(DifficultyColor.YELLOW);
        GameAnswerColor greenAnswer = gameData.getAnswerForColor(DifficultyColor.GREEN);

        assertEquals(DifficultyColor.YELLOW, yellowAnswer.getColor());
        assertEquals("Sunny", yellowAnswer.getDescription());
        assertArrayEquals(new String[] {"sun", "lemon", "banana", "gold"}, yellowAnswer.getWords());

        assertEquals(DifficultyColor.GREEN, greenAnswer.getColor());
        assertEquals("Fruity", greenAnswer.getDescription());
        assertArrayEquals(new String[] {"apple", "pear", "orange", "peach"}, greenAnswer.getWords());
    }

    @Test
    void testGetAsDatabaseFormat() {
        String[] yellowWords = {"sun", "lemon", "banana", "gold"};
        String[] greenWords = {"apple", "pear", "orange", "peach"};
        String[] blueWords = {"cucumber", "carrot", "potato", "eggplant"};
        String[] purpleWords = {"milk", "juice", "soda", "punch"};
        GameAnswerColor yellowAnswer = new GameAnswerColor(DifficultyColor.YELLOW, "Sunny", yellowWords);
        GameAnswerColor greenAnswer = new GameAnswerColor(DifficultyColor.GREEN, "Fruity", greenWords);
        GameAnswerColor blueAnswer = new GameAnswerColor(DifficultyColor.BLUE, "Vegetables", blueWords);
        GameAnswerColor purpleAnswer = new GameAnswerColor(DifficultyColor.PURPLE, "Drinks", purpleWords);
        Map<DifficultyColor, GameAnswerColor> answerMap = Map.of(
                DifficultyColor.YELLOW, yellowAnswer,
                DifficultyColor.GREEN, greenAnswer,
                DifficultyColor.BLUE, blueAnswer,
                DifficultyColor.PURPLE, purpleAnswer
        );
        GameData gameData = new GameData(answerMap, 123);

        Document expected = new Document(GameData.KEY_PUZZLE_NUMBER, 123)
                .append(GameData.KEY_COLOR_LIST, Arrays.asList(
                        yellowAnswer.getAsDatabaseFormat(),
                        greenAnswer.getAsDatabaseFormat(),
                        blueAnswer.getAsDatabaseFormat(),
                        purpleAnswer.getAsDatabaseFormat()
                ));

        Document actual = gameData.getAsDatabaseFormat();
        
        assertEquals(expected, actual);
    }
}
