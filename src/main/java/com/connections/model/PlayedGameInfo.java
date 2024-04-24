package com.connections.model;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.GameSession;
import com.connections.web.DatabaseFormattable;
import com.connections.web.WebUtils;

/**
 * Abstract base class representing played game information.
 * Stores details about a played game such as puzzle number, mistakes made, hints used, etc.
 * Subclasses should implement the getGameType() method to specify the specific game type.
 */
public abstract class PlayedGameInfo implements DatabaseFormattable {
	public static final String KEY_PUZZLE_NUMBER = "puzzle_number";
	public static final String KEY_MISTAKES_MADE_COUNT = "mistakes_made_count";
	public static final String KEY_HINTS_USED_COUNT = "hints_used_count";
	public static final String KEY_CONNECTION_COUNT = "connection_count";
	public static final String KEY_GUESSES = "guesses";
	public static final String KEY_WON = "won";
	public static final String KEY_GAME_TYPE = "game_type";
	public static final String KEY_GAME_START_TIME = "game_start_time";
	public static final String KEY_GAME_END_TIME = "game_end_time";

	protected int puzzleNumber;
	protected int mistakesMadeCount;
	protected int hintsUsedCount;
	protected int connectionCount;
	protected List<Set<Word>> guesses;
	protected boolean won;
	protected GameSession.GameType gameType;
	protected ZonedDateTime gameStartTime;
	protected ZonedDateTime gameEndTime;

	/**
     * Constructs a PlayedGameInfo object with the given parameters.
     *
     * @param puzzleNumber      The puzzle number of the played game.
     * @param mistakesMadeCount The number of mistakes made in the game.
     * @param hintsUsedCount    The number of hints used in the game.
     * @param connectionCount   The number of connections made in the game.
     * @param guesses           The list of guesses made in the game.
     * @param won               Indicates whether the game was won or not.
     * @param gameStartTime     The start time of the game.
     * @param gameEndTime       The end time of the game.
     */
	public PlayedGameInfo(int puzzleNumber, int mistakesMadeCount, int hintsUsedCount, int connectionCount,
			List<Set<Word>> guesses, boolean won, ZonedDateTime gameStartTime, ZonedDateTime gameEndTime) {
		this.puzzleNumber = puzzleNumber;
		this.mistakesMadeCount = mistakesMadeCount;
		this.hintsUsedCount = hintsUsedCount;
		this.connectionCount = connectionCount;
		this.guesses = guesses;
		this.won = won;
		this.gameStartTime = gameStartTime;
		this.gameEndTime = gameEndTime;
	}

    /**
     * Constructs a PlayedGameInfo object from a database document.
     *
     * @param doc The database document containing the played game information.
     */
	public PlayedGameInfo(Document doc) {
		loadFromDatabaseFormat(doc);
	}

    /**
     * Returns the puzzle number.
     *
     * @return the puzzle number
     */
	public int getPuzzleNumber() {
		return puzzleNumber;
	}

    /**
     * Returns the amount of mistakes made.
     *
     * @return the amount of mistakes made
     */
	public int getMistakesMadeCount() {
		return mistakesMadeCount;
	}

    /**
     * Returns the amount of hints used.
     *
     * @return the amount of hints used
     */
	public int getHintsUsedCount() {
		return mistakesMadeCount;
	}
	
    /**
     * Returns the connection count.
     *
     * @return the connection count
     */
	public int getConnectionCount() {
		return connectionCount;
	}

    /**
     * Returns the start time of the game.
     *
     * @return the start time of the game
     */
	public ZonedDateTime getGameStartTime() {
		return gameStartTime;
	}

    /**
     * Returns the end time of the game.
     *
     * @return the end time of the game
     */
	public ZonedDateTime getGameEndTime() {
		return gameEndTime;
	}

    /**
     * Returns the time that the game has completed at.
     *
     * @return the time that the game has completed at
     */
	public int getTimeCompleted() {
		return (int)(ChronoUnit.SECONDS.between(gameStartTime, gameEndTime));
	}

    /**
     * Returns the list of all previous guesses.
     *
     * @return the list of all previous guesses
     */
	public List<Set<Word>> getGuesses() {
		return guesses;
	}

    /**
     * Returns if the game was won or not.
     *
     * @return true if the game was won, false otherwise
     */
	public boolean wasWon() {
		return won;
	}

    /**
     * Returns the game type of the played game.
     *
     * @return The game type of the played game
     */
	public abstract GameSession.GameType getGameType();

    /**
     * Creates a PlayedGameInfo object from a database document based on the game type.
     *
     * @param doc The database document containing the played game information.
     * @return The PlayedGameInfo object created from the database document, or null if the game type is unknown.
     */
	public static PlayedGameInfo getGameInfoFromDatabaseFormat(Document doc) {
		String gameTypeString = doc.getString(KEY_GAME_TYPE);

		if (gameTypeString != null) {
			GameSession.GameType gameType = GameSession.GameType.valueOf(gameTypeString.toUpperCase());

			switch (gameType) {
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

    /**
     * Converts a list of guesses to the database format.
     *
     * @param guesses The list of guesses to convert.
     * @return The list of guesses in the database format.
     */
	public static List<List<Document>> getGuessesAsDatabaseFormat(List<Set<Word>> guesses) {
		if (guesses == null) {
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

    /**
     * Loads guesses from the database format.
     *
     * @param wordSetList The list of guesses in the database format.
     * @return The list of guesses loaded from the database format.
     */
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
	
	/**
	 * Converts this PlayedGameInfo object to a database document format.
	 *
	 * @return The database document representing this PlayedGameInfo object.
	 */
	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_PUZZLE_NUMBER, puzzleNumber);
		doc.append(KEY_GUESSES, getGuessesAsDatabaseFormat(guesses));
		doc.append(KEY_MISTAKES_MADE_COUNT, mistakesMadeCount);
		doc.append(KEY_HINTS_USED_COUNT, hintsUsedCount);
		doc.append(KEY_CONNECTION_COUNT, connectionCount);
		doc.append(KEY_GAME_START_TIME, WebUtils.helperDateToString(gameStartTime));
		doc.append(KEY_GAME_END_TIME, WebUtils.helperDateToString(gameEndTime));
		doc.append(KEY_WON, won);
		doc.append(KEY_GAME_TYPE, getGameType().toString().toLowerCase());
		return doc;
	}

	/**
	 * Loads the PlayedGameInfo object from a database document.
	 *
	 * @param doc The database document containing the played game information.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		puzzleNumber = doc.getInteger(KEY_PUZZLE_NUMBER, -1);
		mistakesMadeCount = doc.getInteger(KEY_MISTAKES_MADE_COUNT, -1);
		hintsUsedCount = doc.getInteger(KEY_HINTS_USED_COUNT, -1);
		connectionCount = doc.getInteger(KEY_CONNECTION_COUNT, -1);
		gameStartTime = WebUtils.helperStringToDate(doc.getString(KEY_GAME_START_TIME));
		gameEndTime = WebUtils.helperStringToDate(doc.getString(KEY_GAME_END_TIME));
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
