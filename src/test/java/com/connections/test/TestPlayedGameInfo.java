package com.connections.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.connections.model.DifficultyColor;
import com.connections.model.PlayedGameInfo;
import com.connections.model.PlayedGameInfoClassic;
import com.connections.model.PlayedGameInfoTimed;
import com.connections.model.Word;
import com.connections.view_controller.GameSession;

class TestPlayedGameInfo {

	private PlayedGameInfoClassic playedGameInfoClassic;
	private PlayedGameInfoTimed playedGameInfoTimed;
	private int puzzleNumber;
	private int mistakesMadeCount;
	private int hintsUsedCount;
	private int connectionCount;
	private List<Set<Word>> guesses;
	private boolean won;
	private ZonedDateTime gameStartTime;
	private ZonedDateTime gameEndTime;
	private int timeLimit;
	private boolean completedBeforeTimeLimit;

	@BeforeEach
	void setUp() {
		puzzleNumber = 1;
		mistakesMadeCount = 2;
		hintsUsedCount = 3;
		connectionCount = 4;
		guesses = new ArrayList<>();
		Set<Word> guess1 = new HashSet<>();
		guess1.add(new Word("apple", DifficultyColor.GREEN));
		guess1.add(new Word("banana", DifficultyColor.YELLOW));
		guess1.add(new Word("cherry", DifficultyColor.BLUE));
		guess1.add(new Word("date", DifficultyColor.PURPLE));
		guesses.add(guess1);
		won = true;
		gameStartTime = ZonedDateTime.now().minusHours(1);
		gameEndTime = ZonedDateTime.now();
		timeLimit = 600;
		completedBeforeTimeLimit = true;

		playedGameInfoClassic = new PlayedGameInfoClassic(puzzleNumber, mistakesMadeCount, hintsUsedCount,
				connectionCount, guesses, won, gameStartTime, gameEndTime);
		playedGameInfoTimed = new PlayedGameInfoTimed(puzzleNumber, mistakesMadeCount, hintsUsedCount, connectionCount,
				guesses, won, timeLimit, completedBeforeTimeLimit, gameStartTime, gameEndTime);
	}

	void assertGuessesEqual(List<Set<Word>> guessOne, List<Set<Word>> guessTwo) {
		assertEquals(guessOne.size(), guessTwo.size());
		for (int i = 0; i < guessOne.size(); i++) {
			assertEquals(guessOne.get(i), guessTwo.get(i));
		}
	}

	@Test
	void testConstructorClassic() {
		assertEquals(puzzleNumber, playedGameInfoClassic.getPuzzleNumber());
		assertEquals(mistakesMadeCount, playedGameInfoClassic.getMistakesMadeCount());
		assertEquals(hintsUsedCount, playedGameInfoClassic.getHintsUsedCount());
		assertEquals(connectionCount, playedGameInfoClassic.getConnectionCount());
		assertGuessesEqual(guesses, playedGameInfoClassic.getGuesses());
		assertEquals(won, playedGameInfoClassic.wasWon());
		assertEquals(gameStartTime, playedGameInfoClassic.getGameStartTime());
		assertEquals(gameEndTime, playedGameInfoClassic.getGameEndTime());
		assertEquals(GameSession.GameType.CLASSIC, playedGameInfoClassic.getGameType());
	}

	@Test
	void testConstructorTimed() {
		assertEquals(puzzleNumber, playedGameInfoTimed.getPuzzleNumber());
		assertEquals(mistakesMadeCount, playedGameInfoTimed.getMistakesMadeCount());
		assertEquals(hintsUsedCount, playedGameInfoTimed.getHintsUsedCount());
		assertEquals(connectionCount, playedGameInfoTimed.getConnectionCount());
		assertGuessesEqual(guesses, playedGameInfoTimed.getGuesses());
		assertEquals(won, playedGameInfoTimed.wasWon());
		assertEquals(gameStartTime, playedGameInfoTimed.getGameStartTime());
		assertEquals(gameEndTime, playedGameInfoTimed.getGameEndTime());
		assertEquals(GameSession.GameType.TIME_TRIAL, playedGameInfoTimed.getGameType());
		assertEquals(timeLimit, playedGameInfoTimed.getTimeLimit());
		assertEquals(completedBeforeTimeLimit, playedGameInfoTimed.isCompletedBeforeTimeLimit());
	}

	@Test
	void testConstructorWithDocumentClassic() {
		Document doc = playedGameInfoClassic.getAsDatabaseFormat();
		PlayedGameInfoClassic loadedGameInfo = new PlayedGameInfoClassic(doc);
		assertEquals(puzzleNumber, loadedGameInfo.getPuzzleNumber());
		assertEquals(mistakesMadeCount, loadedGameInfo.getMistakesMadeCount());
		assertEquals(hintsUsedCount, loadedGameInfo.getHintsUsedCount());
		assertEquals(connectionCount, loadedGameInfo.getConnectionCount());
		assertGuessesEqual(guesses, loadedGameInfo.getGuesses());
		assertEquals(won, loadedGameInfo.wasWon());
		assertEquals(gameStartTime, loadedGameInfo.getGameStartTime());
		assertEquals(gameEndTime, loadedGameInfo.getGameEndTime());
	}

	@Test
	void testConstructorWithDocumentTimed() {
		Document doc = playedGameInfoTimed.getAsDatabaseFormat();
		PlayedGameInfoTimed loadedGameInfo = new PlayedGameInfoTimed(doc);
		assertEquals(puzzleNumber, loadedGameInfo.getPuzzleNumber());
		assertEquals(mistakesMadeCount, loadedGameInfo.getMistakesMadeCount());
		assertEquals(hintsUsedCount, loadedGameInfo.getHintsUsedCount());
		assertEquals(connectionCount, loadedGameInfo.getConnectionCount());
		assertGuessesEqual(guesses, loadedGameInfo.getGuesses());
		assertEquals(won, loadedGameInfo.wasWon());
		assertEquals(gameStartTime, loadedGameInfo.getGameStartTime());
		assertEquals(gameEndTime, loadedGameInfo.getGameEndTime());
		assertEquals(timeLimit, loadedGameInfo.getTimeLimit());
		assertEquals(completedBeforeTimeLimit, loadedGameInfo.isCompletedBeforeTimeLimit());
	}

	@Test
	void testGetTimeCompletedClassic() {
		int expectedTimeCompleted = (int) ChronoUnit.SECONDS.between(gameStartTime, gameEndTime);
		assertEquals(expectedTimeCompleted, playedGameInfoClassic.getTimeCompleted());
	}

	@Test
	void testGetTimeCompletedTimed() {
		int expectedTimeCompleted = (int) ChronoUnit.SECONDS.between(gameStartTime, gameEndTime);
		assertEquals(expectedTimeCompleted, playedGameInfoTimed.getTimeCompleted());
	}

	@Test
	void testGetGameInfoFromDatabaseFormatTimed() {
		Document doc = playedGameInfoTimed.getAsDatabaseFormat();
		PlayedGameInfo loadedGameInfo = PlayedGameInfo.getGameInfoFromDatabaseFormat(doc);
		assertTrue(loadedGameInfo instanceof PlayedGameInfoTimed);
		PlayedGameInfoTimed loadedGameInfoTimed = (PlayedGameInfoTimed) loadedGameInfo;
		assertEquals(puzzleNumber, loadedGameInfoTimed.getPuzzleNumber());
		assertEquals(mistakesMadeCount, loadedGameInfoTimed.getMistakesMadeCount());
		assertEquals(hintsUsedCount, loadedGameInfoTimed.getHintsUsedCount());
		assertEquals(connectionCount, loadedGameInfoTimed.getConnectionCount());
		assertGuessesEqual(guesses, loadedGameInfoTimed.getGuesses());
		assertEquals(won, loadedGameInfoTimed.wasWon());
		assertEquals(gameStartTime, loadedGameInfoTimed.getGameStartTime());
		assertEquals(gameEndTime, loadedGameInfoTimed.getGameEndTime());
		assertEquals(GameSession.GameType.TIME_TRIAL, loadedGameInfoTimed.getGameType());
		assertEquals(timeLimit, loadedGameInfoTimed.getTimeLimit());
		assertEquals(completedBeforeTimeLimit, loadedGameInfoTimed.isCompletedBeforeTimeLimit());
	}

	@Test
	void testGetGameInfoFromDatabaseFormatClassic() {
		Document doc = playedGameInfoClassic.getAsDatabaseFormat();
		PlayedGameInfo loadedGameInfo = PlayedGameInfo.getGameInfoFromDatabaseFormat(doc);
		assertTrue(loadedGameInfo instanceof PlayedGameInfoClassic);
		PlayedGameInfoClassic loadedGameInfoClassic = (PlayedGameInfoClassic) loadedGameInfo;
		assertEquals(puzzleNumber, loadedGameInfoClassic.getPuzzleNumber());
		assertEquals(mistakesMadeCount, loadedGameInfoClassic.getMistakesMadeCount());
		assertEquals(hintsUsedCount, loadedGameInfoClassic.getHintsUsedCount());
		assertEquals(connectionCount, loadedGameInfoClassic.getConnectionCount());
		assertGuessesEqual(guesses, loadedGameInfoClassic.getGuesses());
		assertEquals(won, loadedGameInfoClassic.wasWon());
		assertEquals(gameStartTime, loadedGameInfoClassic.getGameStartTime());
		assertEquals(gameEndTime, loadedGameInfoClassic.getGameEndTime());
		assertEquals(GameSession.GameType.CLASSIC, loadedGameInfoClassic.getGameType());
	}

	@Test
	void testGetGameInfoFromDatabaseFormatNullType() {
		Document doc = new Document();
		doc.append(PlayedGameInfo.KEY_GAME_TYPE, null);
		PlayedGameInfo loadedGameInfo = PlayedGameInfo.getGameInfoFromDatabaseFormat(doc);
		assertNull(loadedGameInfo);
	}

	@Test
	void testGetAsDatabaseFormatClassic() {
		Document doc = playedGameInfoClassic.getAsDatabaseFormat();
		assertEquals(puzzleNumber, doc.getInteger(PlayedGameInfo.KEY_PUZZLE_NUMBER));
		assertEquals(mistakesMadeCount, doc.getInteger(PlayedGameInfo.KEY_MISTAKES_MADE_COUNT));
		assertEquals(hintsUsedCount, doc.getInteger(PlayedGameInfo.KEY_HINTS_USED_COUNT));
		assertEquals(connectionCount, doc.getInteger(PlayedGameInfo.KEY_CONNECTION_COUNT));
		assertEquals(won, doc.getBoolean(PlayedGameInfo.KEY_WON));
		assertEquals("classic", doc.getString(PlayedGameInfo.KEY_GAME_TYPE));
	}

	@Test
	void testGetAsDatabaseFormatTimed() {
		Document doc = playedGameInfoTimed.getAsDatabaseFormat();
		assertEquals(puzzleNumber, doc.getInteger(PlayedGameInfo.KEY_PUZZLE_NUMBER));
		assertEquals(mistakesMadeCount, doc.getInteger(PlayedGameInfo.KEY_MISTAKES_MADE_COUNT));
		assertEquals(hintsUsedCount, doc.getInteger(PlayedGameInfo.KEY_HINTS_USED_COUNT));
		assertEquals(connectionCount, doc.getInteger(PlayedGameInfo.KEY_CONNECTION_COUNT));
		assertEquals(won, doc.getBoolean(PlayedGameInfo.KEY_WON));
		assertEquals("time_trial", doc.getString(PlayedGameInfo.KEY_GAME_TYPE));
		assertEquals(timeLimit, doc.getInteger(PlayedGameInfoTimed.KEY_TIME_LIMIT));
		assertEquals(completedBeforeTimeLimit, doc.getBoolean(PlayedGameInfoTimed.KEY_COMPLETED_BEFORE_LIMIT));
	}

	@Test
	void testGetGuessesAsDatabaseFormatNull() {
		List<List<Document>> emptyList = new ArrayList<>();
		assertEquals(PlayedGameInfo.getGuessesAsDatabaseFormat(null), emptyList);
	}

	@Test
	void testLoadGuessesFromDatabaseFormatNull() {
		List<List<Word>> emptyList = new ArrayList<>();
		assertEquals(PlayedGameInfo.loadGuessesFromDatabaseFormat(null), emptyList);
	}

	@Test
	void testGetGameInfoFromDatabaseFormatNone() {
		GameSession.GameType noneType = GameSession.GameType.NONE;
		Document doc = new Document(PlayedGameInfo.KEY_GAME_TYPE, noneType.toString().toLowerCase());
		assertNull(PlayedGameInfo.getGameInfoFromDatabaseFormat(doc));
	}
}