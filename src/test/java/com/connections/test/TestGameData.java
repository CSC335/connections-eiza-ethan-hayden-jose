package com.connections.test;

import com.connections.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

public class TestGameData {
	private GameAnswerColor testGameAnswerYellowOne;
	private GameAnswerColor testGameAnswerGreenOne;
	private GameAnswerColor testGameAnswerBlueOne;
	private GameAnswerColor testGameAnswerPurpleOne;

	private DifficultyColor testGameAnswerYellowOneColor;
	private String testGameAnswerYellowOneDesc;
	private String[] testGameAnswerYellowOneHints;
	private String[] testGameAnswerYellowOneWords;

	private DifficultyColor testGameAnswerGreenOneColor;
	private String testGameAnswerGreenOneDesc;
	private String[] testGameAnswerGreenOneHints;
	private String[] testGameAnswerGreenOneWords;

	private DifficultyColor testGameAnswerBlueOneColor;
	private String testGameAnswerBlueOneDesc;
	private String[] testGameAnswerBlueOneHints;
	private String[] testGameAnswerBlueOneWords;

	private DifficultyColor testGameAnswerPurpleOneColor;
	private String testGameAnswerPurpleOneDesc;
	private String[] testGameAnswerPurpleOneHints;
	private String[] testGameAnswerPurpleOneWords;

	private GameData gameDataFour;

	@BeforeEach
	public void setUp() {
		testGameAnswerYellowOneColor = DifficultyColor.YELLOW;
		testGameAnswerYellowOneDesc = "Yellow description";
		testGameAnswerYellowOneHints = new String[] { "Yellow Hint1", "Yellow Hint2" };
		testGameAnswerYellowOneWords = new String[] { "wy1", "wy2", "wy3", "wy4" };

		testGameAnswerGreenOneColor = DifficultyColor.GREEN;
		testGameAnswerGreenOneDesc = "Green description";
		testGameAnswerGreenOneHints = new String[] { "Green Hint1", "Green Hint2" };
		testGameAnswerGreenOneWords = new String[] { "wg1", "wg2", "wg3", "wg4" };

		testGameAnswerBlueOneColor = DifficultyColor.BLUE;
		testGameAnswerBlueOneDesc = "Blue description";
		testGameAnswerBlueOneHints = new String[] { "Blue Hint1", "Blue Hint2" };
		testGameAnswerBlueOneWords = new String[] { "wb1", "wb2", "wb3", "wb4" };

		testGameAnswerPurpleOneColor = DifficultyColor.PURPLE;
		testGameAnswerPurpleOneDesc = "Purple description";
		testGameAnswerPurpleOneHints = new String[] { "Purple Hint1", "Purple Hint2" };
		testGameAnswerPurpleOneWords = new String[] { "wp1", "wp2", "wp3", "wp4" };

		testGameAnswerYellowOne = new GameAnswerColor(testGameAnswerYellowOneColor, testGameAnswerYellowOneDesc,
				testGameAnswerYellowOneHints, testGameAnswerYellowOneWords);
		testGameAnswerGreenOne = new GameAnswerColor(testGameAnswerGreenOneColor, testGameAnswerGreenOneDesc,
				testGameAnswerGreenOneHints, testGameAnswerGreenOneWords);
		testGameAnswerBlueOne = new GameAnswerColor(testGameAnswerBlueOneColor, testGameAnswerBlueOneDesc,
				testGameAnswerBlueOneHints, testGameAnswerBlueOneWords);
		testGameAnswerPurpleOne = new GameAnswerColor(testGameAnswerPurpleOneColor, testGameAnswerPurpleOneDesc,
				testGameAnswerPurpleOneHints, testGameAnswerPurpleOneWords);

		Map<DifficultyColor, GameAnswerColor> answerMap = new HashMap<>();
		answerMap.put(DifficultyColor.YELLOW, testGameAnswerYellowOne);
		answerMap.put(DifficultyColor.GREEN, testGameAnswerGreenOne);
		answerMap.put(DifficultyColor.BLUE, testGameAnswerBlueOne);
		answerMap.put(DifficultyColor.PURPLE, testGameAnswerPurpleOne);

		gameDataFour = new GameData(answerMap);
	}

	@Test
	public void testEmptyGameData() {
		Map<DifficultyColor, GameAnswerColor> emptyMap = new HashMap<>();
		GameData emptyGameData = new GameData(emptyMap);

		assertEquals(0, emptyGameData.getAnswerMap().size());

		assertNull(emptyGameData.getAnswerForColor(DifficultyColor.YELLOW));
		assertNull(emptyGameData.getAnswerForColor(DifficultyColor.GREEN));
		assertNull(emptyGameData.getAnswerForColor(DifficultyColor.BLUE));
		assertNull(emptyGameData.getAnswerForColor(DifficultyColor.PURPLE));
	}

	@Test
	public void testMapSize() {
		Map<DifficultyColor, GameAnswerColor> answerMap = gameDataFour.getAnswerMap();
		assertEquals(4, answerMap.size());
	}

	@Test
	public void testAllColors() {
		assertEquals(testGameAnswerYellowOne, gameDataFour.getAnswerForColor(DifficultyColor.YELLOW));
		assertEquals(testGameAnswerGreenOne, gameDataFour.getAnswerForColor(DifficultyColor.GREEN));
		assertEquals(testGameAnswerBlueOne, gameDataFour.getAnswerForColor(DifficultyColor.BLUE));
		assertEquals(testGameAnswerPurpleOne, gameDataFour.getAnswerForColor(DifficultyColor.PURPLE));
	}

	@Test
	public void testOnlyYellow() {
		Map<DifficultyColor, GameAnswerColor> answerMap = new HashMap<>();
		answerMap.put(DifficultyColor.YELLOW, testGameAnswerYellowOne);
		GameData gameData = new GameData(answerMap);

		assertEquals(testGameAnswerYellowOne, gameData.getAnswerForColor(DifficultyColor.YELLOW));
		assertNull(gameData.getAnswerForColor(DifficultyColor.GREEN));
		assertNull(gameData.getAnswerForColor(DifficultyColor.BLUE));
		assertNull(gameData.getAnswerForColor(DifficultyColor.PURPLE));
	}

	@Test
	public void testOnlyGreen() {
		Map<DifficultyColor, GameAnswerColor> answerMap = new HashMap<>();
		answerMap.put(DifficultyColor.GREEN, testGameAnswerGreenOne);
		GameData gameData = new GameData(answerMap);

		assertEquals(testGameAnswerGreenOne, gameData.getAnswerForColor(DifficultyColor.GREEN));
		assertNull(gameData.getAnswerForColor(DifficultyColor.YELLOW));
		assertNull(gameData.getAnswerForColor(DifficultyColor.BLUE));
		assertNull(gameData.getAnswerForColor(DifficultyColor.PURPLE));
	}

	@Test
	public void testOnlyBlue() {
		Map<DifficultyColor, GameAnswerColor> answerMap = new HashMap<>();
		answerMap.put(DifficultyColor.BLUE, testGameAnswerBlueOne);
		GameData gameData = new GameData(answerMap);

		assertEquals(testGameAnswerBlueOne, gameData.getAnswerForColor(DifficultyColor.BLUE));
		assertNull(gameData.getAnswerForColor(DifficultyColor.YELLOW));
		assertNull(gameData.getAnswerForColor(DifficultyColor.GREEN));
		assertNull(gameData.getAnswerForColor(DifficultyColor.PURPLE));
	}

	@Test
	public void testOnlyPurple() {
		Map<DifficultyColor, GameAnswerColor> answerMap = new HashMap<>();
		answerMap.put(DifficultyColor.PURPLE, testGameAnswerPurpleOne);
		GameData gameData = new GameData(answerMap);

		assertEquals(testGameAnswerPurpleOne, gameData.getAnswerForColor(DifficultyColor.PURPLE));
		assertNull(gameData.getAnswerForColor(DifficultyColor.YELLOW));
		assertNull(gameData.getAnswerForColor(DifficultyColor.GREEN));
		assertNull(gameData.getAnswerForColor(DifficultyColor.BLUE));
	}
}
