package com.connections.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.connections.model.GameSaveState;
import com.connections.model.Word;
import com.connections.view_controller.GameSession;

public class TestGameSaveState {
	@Test
	void testGetAsDatabaseFormat() {
		GameSaveState gameSaveState = new GameSaveState(false, GameSession.GameType.CLASSIC, 2, 1, 3, new ArrayList<>(),
				new ArrayList<>(), ZonedDateTime.now(), ZonedDateTime.now());

		Document doc = gameSaveState.getAsDatabaseFormat();

		assertFalse(doc.getBoolean(GameSaveState.KEY_GAME_FINISHED));
		assertEquals("classic", doc.getString(GameSaveState.KEY_GAME_TYPE));
		assertEquals(2, doc.getInteger(GameSaveState.KEY_HINTS_LEFT_COUNT));
		assertEquals(1, doc.getInteger(GameSaveState.KEY_MISTAKES_LEFT_COUNT));
		assertEquals(3, doc.getInteger(GameSaveState.KEY_PUZZLE_NUMBER));
		assertNotNull(doc.get(GameSaveState.KEY_GAME_START_TIME));
		assertNotNull(doc.get(GameSaveState.KEY_SAVE_STATE_CREATION_TIME));
		assertTrue(doc.get(GameSaveState.KEY_GRID_WORDS) instanceof List);
		assertTrue(doc.get(GameSaveState.KEY_GUESSES) instanceof List);
	}

	@Test
	void testLoadFromDatabaseFormat() {
		Document testData = new Document();
		testData.append(GameSaveState.KEY_GAME_FINISHED, true);
		testData.append(GameSaveState.KEY_GAME_TYPE, "time_trial");
		testData.append(GameSaveState.KEY_HINTS_LEFT_COUNT, 1);
		testData.append(GameSaveState.KEY_MISTAKES_LEFT_COUNT, 0);
		testData.append(GameSaveState.KEY_PUZZLE_NUMBER, 5);
		testData.append(GameSaveState.KEY_GAME_START_TIME, ZonedDateTime.now().toString());
		testData.append(GameSaveState.KEY_SAVE_STATE_CREATION_TIME, ZonedDateTime.now().toString());
		testData.append(GameSaveState.KEY_GRID_WORDS, new ArrayList<>());
		testData.append(GameSaveState.KEY_GUESSES, new ArrayList<>());

		GameSaveState gameSaveState = new GameSaveState(testData);

		assertTrue(gameSaveState.isGameFinished());
		assertEquals(GameSession.GameType.TIME_TRIAL, gameSaveState.getGameType());
		assertEquals(1, gameSaveState.getHintsLeft());
		assertEquals(0, gameSaveState.getMistakesLeft());
		assertEquals(5, gameSaveState.getPuzzleNumber());
		assertNotNull(gameSaveState.getGameStartTime());
		assertNotNull(gameSaveState.getSaveStateCreationTime());
		assertTrue(gameSaveState.getGrid().isEmpty());
		assertTrue(gameSaveState.getGuesses().isEmpty());
	}

	@Test
	void testLoadFromDatabaseFormatNullGameType() {
		Document testData = new Document();
		testData.append(GameSaveState.KEY_GAME_FINISHED, true);
		testData.append(GameSaveState.KEY_GAME_TYPE, null);
		testData.append(GameSaveState.KEY_HINTS_LEFT_COUNT, 1);
		testData.append(GameSaveState.KEY_MISTAKES_LEFT_COUNT, 0);
		testData.append(GameSaveState.KEY_PUZZLE_NUMBER, 5);
		testData.append(GameSaveState.KEY_GAME_START_TIME, ZonedDateTime.now().toString());
		testData.append(GameSaveState.KEY_SAVE_STATE_CREATION_TIME, ZonedDateTime.now().toString());
		testData.append(GameSaveState.KEY_GRID_WORDS, null);
		testData.append(GameSaveState.KEY_GUESSES, null);

		GameSaveState gameSaveState = new GameSaveState(testData);

		assertTrue(gameSaveState.isGameFinished());
		assertEquals(GameSession.GameType.NONE, gameSaveState.getGameType());
		assertEquals(1, gameSaveState.getHintsLeft());
		assertEquals(0, gameSaveState.getMistakesLeft());
		assertEquals(5, gameSaveState.getPuzzleNumber());
		assertNotNull(gameSaveState.getGameStartTime());
		assertNotNull(gameSaveState.getSaveStateCreationTime());
		assertTrue(gameSaveState.getGrid().isEmpty());
		assertTrue(gameSaveState.getGuesses().isEmpty());
	}

	@Test
	void testloadGridFromDatabaseFormat() {
		assertTrue(GameSaveState.getGridAsDatabaseFormat(null).isEmpty());
		assertTrue(GameSaveState.loadGridFromDatabaseFormat(null).isEmpty());
		List<List<Document>> listDocNull = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			listDocNull.add(null);
		}
		List<List<Word>> listWordNull = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			listWordNull.add(null);
		}
		List<List<Document>> listDocEmpty = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			listDocEmpty.add(new ArrayList<>());
		}
		List<List<Word>> listWordEmpty = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			listWordEmpty.add(new ArrayList<>());
		}
		for (List<Document> docList : GameSaveState.getGridAsDatabaseFormat(listWordNull)) {
			assertTrue(docList.isEmpty());
		}
		for (List<Document> docList : GameSaveState.getGridAsDatabaseFormat(listWordEmpty)) {
			assertTrue(docList.isEmpty());
		}
		for (List<Word> wordList : GameSaveState.loadGridFromDatabaseFormat(listDocNull)) {
			assertTrue(wordList.isEmpty());
		}
		for (List<Word> wordList : GameSaveState.loadGridFromDatabaseFormat(listDocEmpty)) {
			assertTrue(wordList.isEmpty());
		}
	}
}
