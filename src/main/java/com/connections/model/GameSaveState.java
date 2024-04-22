package com.connections.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.CircleRowPane;
import com.connections.view_controller.GameSession;
import com.connections.view_controller.GameSessionContext;
import com.connections.view_controller.TileGridWord;
import com.connections.web.DatabaseFormattable;
import com.connections.web.WebUtils;

public class GameSaveState implements DatabaseFormattable {
	public static final String KEY_GAME_FINISHED = "is_game_finished";
	public static final String KEY_GRID_WORDS = "grid_words";
	public static final String KEY_GUESSES = PlayedGameInfo.KEY_GUESSES;
	public static final String KEY_HINTS_LEFT_COUNT = "hints_left_count";
	public static final String KEY_MISTAKES_LEFT_COUNT = "mistakes_left_count";
	public static final String KEY_GAME_TYPE = PlayedGameInfo.KEY_GAME_TYPE;
	public static final String KEY_PUZZLE_NUMBER = PlayedGameInfo.KEY_PUZZLE_NUMBER;
	public static final String KEY_GAME_START_TIME = PlayedGameInfo.KEY_GAME_START_TIME;
	public static final String KEY_SAVE_STATE_CREATION_TIME = "save_state_creation_time";

	protected boolean gameFinished;
	protected GameSession.GameType gameType;
	protected int hintsLeft;
	protected int mistakesLeft;
	protected int puzzleNumber;
	protected List<List<Word>> grid;
	protected List<Set<Word>> guesses;
	protected ZonedDateTime gameStartTime;
	protected ZonedDateTime saveStateCreationTime;

	/*
	 * NOTE: we will NOT allow the user to resume a game that belongs to some
	 * previous day (show an error message)
	 */

	/*
	 * NOTE: do NOT include dark mode setting, that will probably be saved into some
	 * user profile in the future
	 */

	/*
	 * NOTES FOR PARAMETERS: listColorsSolved - non-answered colors are null, and
	 * the order of solved colors
	 */
	public GameSaveState(TileGridWord tileGridWord, CircleRowPane hintsPane, CircleRowPane mistakesPane,
			GameSessionContext gameSessionContext, boolean gameFinished, int timeCompleted,
			GameSession.GameType gameType, ZonedDateTime gameStartTime) {
		this.gameFinished = gameFinished;
		this.gameType = gameType;
		this.grid = tileGridWord.getGridAsWords();
		this.guesses = tileGridWord.getGuesses();
		this.puzzleNumber = gameSessionContext.getGameData().getPuzzleNumber();
		this.hintsLeft = hintsPane.getNumCircles();
		this.mistakesLeft = mistakesPane.getNumCircles();
		this.gameStartTime = gameStartTime;
		this.saveStateCreationTime = ZonedDateTime.now();
	}

	public GameSaveState(Document doc) {
		loadFromDatabaseFormat(doc);
	}
	
	public static List<List<Document>> getGridAsDatabaseFormat(List<List<Word>> grid) {
		if(grid == null) {
			grid = new ArrayList<>();
		}
		
		List<List<Document>> gridDocList = new ArrayList<>();
		
		for(List<Word> row : grid) {
			List<Document> rowDocList = new ArrayList<>();
			
			if(row != null && row.size() > 0) {
				for(Word word : row) {
					rowDocList.add(word.getAsDatabaseFormat());
				}
			}
			gridDocList.add(rowDocList);
		}
		
		return gridDocList;
	}
	
	public static List<List<Word>> loadGridFromDatabaseFormat(List<List<Document>> gridDocList) {
		List<List<Word>> grid = new ArrayList<>();
		
		if(gridDocList == null) {
			return grid;
		}
		
		for(List<Document> row : gridDocList) {
			List<Word> rowWordList = new ArrayList<>();
			
			for(Document wordDoc : row) {
				rowWordList.add(new Word(wordDoc));
			}
			
			grid.add(rowWordList);
		}
		
		return grid;
	}
	
	public boolean isGameFinished() {
		return gameFinished;
	}

	public GameSession.GameType getGameType() {
		return gameType;
	}

	public int getHintsLeft() {
		return hintsLeft;
	}

	public int getMistakesLeft() {
		return mistakesLeft;
	}

	public List<List<Word>> getGrid() {
		return grid;
	}

	public List<Set<Word>> getGuesses() {
		return guesses;
	}

	public int getPuzzleNumber() {
		return puzzleNumber;
	}
	
	public ZonedDateTime getGameStartTime() {
		return gameStartTime;
	}
	
	public ZonedDateTime getSaveStateCreationTime() {
		return saveStateCreationTime;
	}

	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_GAME_FINISHED, gameFinished);
		doc.append(KEY_GAME_TYPE, gameType.toString().toLowerCase());
		doc.append(KEY_GRID_WORDS, getGridAsDatabaseFormat(grid));
		doc.append(KEY_GUESSES, PlayedGameInfo.getGuessesAsDatabaseFormat(guesses));
		doc.append(KEY_HINTS_LEFT_COUNT, hintsLeft);
		doc.append(KEY_MISTAKES_LEFT_COUNT, mistakesLeft);
		doc.append(KEY_PUZZLE_NUMBER, puzzleNumber);
		doc.append(KEY_GAME_START_TIME, WebUtils.helperDateToString(gameStartTime));
		doc.append(KEY_SAVE_STATE_CREATION_TIME, WebUtils.helperDateToString(saveStateCreationTime));

		return doc;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		hintsLeft = doc.getInteger(KEY_HINTS_LEFT_COUNT, -1);
		mistakesLeft = doc.getInteger(KEY_MISTAKES_LEFT_COUNT, -1);
		gameFinished = doc.getBoolean(KEY_GAME_FINISHED, false);
		gameStartTime = WebUtils.helperStringToDate(doc.getString(KEY_GAME_START_TIME));
		puzzleNumber = doc.getInteger(KEY_PUZZLE_NUMBER, -1);
		saveStateCreationTime = WebUtils.helperStringToDate(doc.getString(KEY_SAVE_STATE_CREATION_TIME));

		String gameTypeString = doc.getString(KEY_GAME_TYPE);
		if (gameTypeString == null) {
			gameType = GameSession.GameType.NONE;
		} else {
			// For some reason Eclipse does not like using valueOf here, even though this is
			// a string and should be valid? Investigate later...
			gameType = GameSession.GameType.valueOf(gameTypeString.toUpperCase());
		}

		guesses = new ArrayList<>();
		Object guessesRetrieved = doc.get(KEY_GUESSES);
		if (guessesRetrieved != null) {
			guesses = PlayedGameInfo.loadGuessesFromDatabaseFormat((List<List<Document>>) guessesRetrieved);
		}

		grid = new ArrayList<>();
		Object gridRetrieved = doc.get(KEY_GRID_WORDS);
		if (gridRetrieved != null) {
			grid = loadGridFromDatabaseFormat((List<List<Document>>) gridRetrieved);
		}
	}
}
