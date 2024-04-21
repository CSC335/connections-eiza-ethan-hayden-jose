package com.connections.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.GameSession;
import com.connections.web.DatabaseFormattable;

public abstract class PlayedGameInfo implements DatabaseFormattable {
	public static final String KEY_PUZZLE_NUMBER = "puzzle_number";
	public static final String KEY_MISTAKES_MADE_COUNT = "mistakes_made_count";
	public static final String KEY_HINTS_USED_COUNT = "hints_used_count";
	public static final String KEY_CONNECTION_COUNT = "connection_count";
	public static final String KEY_TIME_COMPLETED = "time_completed";
	public static final String KEY_GUESSES = "guesses";
	public static final String KEY_WON = "won";
	public static final String KEY_GAME_TYPE = "game_type";

	// NOTE:
	// puzzle number is used as unique ID
	// and, time completed is in milliseconds (ms), so 1 sec = 1000 ms
	protected int puzzleNumber;
	protected int mistakesMadeCount;
	protected int hintsUsedCount;
	protected int connectionCount;
	protected int timeCompleted;
	protected List<Set<Word>> guesses;
	protected boolean won;
	protected GameSession.GameType gameType;

	/*
	 * The time completed is in seconds
	 */
	
	public PlayedGameInfo(int puzzleNumber, int mistakesMadeCount, int hintsUsedCount, int connectionCount, int timeCompleted,
			List<Set<Word>> guesses, boolean won) {
		this.puzzleNumber = puzzleNumber;
		this.mistakesMadeCount = mistakesMadeCount;
		this.hintsUsedCount = hintsUsedCount;
		this.connectionCount = connectionCount;
		this.timeCompleted = timeCompleted;
		this.guesses = guesses;
		this.won = won;
	}
	
	public PlayedGameInfo(Document doc) {
		loadFromDatabaseFormat(doc);
	}

	public int getPuzzleNumber() {
		return puzzleNumber;
	}

	public int getMistakesMadeCount() {
		return mistakesMadeCount;
	}

	public int getHintsUsedCount() {
		return mistakesMadeCount;
	}

	public int getConnectionCount() {
		return connectionCount;
	}

	public int getTimeCompleted() {
		return timeCompleted;
	}

	public List<Set<Word>> getGuesses() {
		return guesses;
	}

	public boolean wasWon() {
		return won;
	}
	
	public abstract GameSession.GameType getGameType();
	
	public static PlayedGameInfo getGameInfoFromDatabaseFormat(Document doc) {
		String gameTypeString = doc.getString(KEY_GAME_TYPE);
		
		if(gameTypeString != null) {
			GameSession.GameType gameType = GameSession.GameType.valueOf(gameTypeString.toUpperCase());
			
			switch(gameType) {
			case CLASSIC:
				return new PlayedGameInfoClassic(doc);
			case TIME_TRIAL:
				return new PlayedGameInfoTimed(doc);
			default:
				return null;
			}
		}
		return null;
	}
	
	public static List<List<Document>> getGuessesAsDatabaseFormat(List<Set<Word>> guesses) {
		if(guesses == null) {
			guesses = new ArrayList<>();
		}
		
		List<List<Document>> wordSetList = new ArrayList<>();
		for (Set<Word> set : guesses) {
			List<Document> wordList = new ArrayList<>();
			for (Word word : set) {
				wordList.add(word.getAsDatabaseFormat());
			}
			wordSetList.add(wordList);
		}
		return wordSetList;
	}

	public static List<Set<Word>> loadGuessesFromDatabaseFormat(List<List<Document>> wordSetList) {
		List<Set<Word>> guessesList = new ArrayList<>();
		for (List<Document> wordList : wordSetList) {
			Set<Word> set = new HashSet<>();
			for (Document wordDoc : wordList) {
				set.add(new Word(wordDoc));
			}
			guessesList.add(set);
		}
		return guessesList;
	}

	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_PUZZLE_NUMBER, puzzleNumber);
		doc.append(KEY_GUESSES, getGuessesAsDatabaseFormat(guesses));
		doc.append(KEY_MISTAKES_MADE_COUNT, mistakesMadeCount);
		doc.append(KEY_HINTS_USED_COUNT, hintsUsedCount);
		doc.append(KEY_CONNECTION_COUNT, connectionCount);
		doc.append(KEY_TIME_COMPLETED, timeCompleted);
		doc.append(KEY_WON, won);
		doc.append(KEY_GAME_TYPE, getGameType().toString().toLowerCase());
		return doc;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		puzzleNumber = doc.getInteger(KEY_PUZZLE_NUMBER, -1);
		mistakesMadeCount = doc.getInteger(KEY_MISTAKES_MADE_COUNT, -1);
		hintsUsedCount = doc.getInteger(KEY_HINTS_USED_COUNT, -1);
		connectionCount = doc.getInteger(KEY_CONNECTION_COUNT, -1);
		timeCompleted = doc.getInteger(KEY_TIME_COMPLETED, -1);
		won = doc.getBoolean(KEY_WON, false);

		// NOTE: gameType does not need to be loaded from the database because it
		// already is hard-coded into the class.

		guesses = new ArrayList<>();
		Object guessesRetrieved = doc.get(KEY_GUESSES);
		if (guessesRetrieved != null) {
			guesses = loadGuessesFromDatabaseFormat((List<List<Document>>) guessesRetrieved);
		}
	}
}
