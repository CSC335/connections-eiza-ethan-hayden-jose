package com.connections.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.GameSession;
import com.connections.web.DatabaseFormattable;

/**
 * Represents played game information for the timed game type. Extends the
 * PlayedGameInfo class and implements the DatabaseFormattable interface. Adds
 * additional properties specific to timed games, such as time limit and
 * completion status.
 */
public class PlayedGameInfoTimed extends PlayedGameInfo implements DatabaseFormattable {
	public static final String KEY_TIME_LIMIT = "time_limit";
	public static final String KEY_COMPLETED_BEFORE_LIMIT = "completed";
	protected int timeLimit;
	protected boolean completedBeforeTimeLimit;

	/**
	 * Constructs a PlayedGameInfoTimed object from a database document.
	 *
	 * @param doc The database document containing the played game information.
	 */
	public PlayedGameInfoTimed(Document doc) {
		super(doc);
	}

	/**
	 * Constructs a PlayedGameInfoTimed object with the given parameters.
	 *
	 * @param puzzleNumber             The puzzle number of the played game.
	 * @param mistakesMadeCount        The number of mistakes made in the game.
	 * @param hintsUsedCount           The number of hints used in the game.
	 * @param connectionCount          The number of connections made in the game.
	 * @param guesses                  The list of guesses made in the game.
	 * @param won                      Indicates whether the game was won or not.
	 * @param timeLimit                The time limit for the game in seconds.
	 * @param completedBeforeTimeLimit Indicates whether the game was completed
	 *                                 before the time limit.
	 * @param gameStartTime            The start time of the game.
	 * @param gameEndTime              The end time of the game.
	 */
	public PlayedGameInfoTimed(int puzzleNumber, int mistakesMadeCount, int hintsUsedCount, int connectionCount,
			List<Set<Word>> guesses, boolean won, int timeLimit, boolean completedBeforeTimeLimit,
			ZonedDateTime gameStartTime, ZonedDateTime gameEndTime) {
		super(puzzleNumber, mistakesMadeCount, hintsUsedCount, connectionCount, guesses, won, gameStartTime,
				gameEndTime);
		this.timeLimit = timeLimit;
		this.completedBeforeTimeLimit = completedBeforeTimeLimit;
	}

	/**
	 * Returns the time limit for the game in seconds.
	 *
	 * @return The time limit for the game in seconds
	 */
	public int getTimeLimit() {
		return timeLimit;
	}

	/**
	 * Indicates whether the game was completed before the time limit.
	 *
	 * @return true if the game was completed before the time limit, false otherwise
	 */
	public boolean isCompletedBeforeTimeLimit() {
		return completedBeforeTimeLimit;
	}

	/**
	 * Returns the game type of the played game.
	 *
	 * @return The game type of the played game, which is always
	 *         GameSession.GameType.TIME_TRIAL for this class.
	 */
	@Override
	public GameSession.GameType getGameType() {
		return GameSession.GameType.TIME_TRIAL;
	}

	/**
	 * Converts this PlayedGameInfoTimed object to a database document format.
	 *
	 * @return The database document representing this PlayedGameInfoTimed object.
	 */
	@Override
	public Document getAsDatabaseFormat() {
		Document doc = super.getAsDatabaseFormat();
		doc.append(KEY_TIME_LIMIT, timeLimit);
		doc.append(KEY_COMPLETED_BEFORE_LIMIT, completedBeforeTimeLimit);
		return doc;
	}

	/**
	 * Loads the PlayedGameInfoTimed object from a database document.
	 *
	 * @param doc The database document containing the played game information.
	 */
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		super.loadFromDatabaseFormat(doc);
		timeLimit = doc.getInteger(KEY_TIME_LIMIT, -1);
		completedBeforeTimeLimit = doc.getBoolean(KEY_COMPLETED_BEFORE_LIMIT, false);
	}
}
