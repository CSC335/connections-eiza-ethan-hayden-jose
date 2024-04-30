package com.connections.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.GameSession;
import com.connections.web.DatabaseFormattable;

/**
 * Represents played game information for the classic game type. Extends the
 * PlayedGameInfo class and implements the DatabaseFormattable interface.
 */
public class PlayedGameInfoClassic extends PlayedGameInfo implements DatabaseFormattable {
	/**
	 * Constructs a PlayedGameInfoClassic object from a database document.
	 *
	 * @param doc The database document containing the played game information.
	 */
	public PlayedGameInfoClassic(Document doc) {
		super(doc);
	}

	/**
	 * Constructs a PlayedGameInfoClassic object with the given parameters.
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
	public PlayedGameInfoClassic(int puzzleNumber, int mistakesMadeCount, int hintsUsedCount, int connectionCount,
			List<Set<Word>> guesses, boolean won, ZonedDateTime gameStartTime, ZonedDateTime gameEndTime) {
		super(puzzleNumber, mistakesMadeCount, hintsUsedCount, connectionCount, guesses, won, gameStartTime,
				gameEndTime);
	}

	/**
	 * Returns the game type of the played game.
	 *
	 * @return The game type of the played game, which is always
	 *         GameSession.GameType.CLASSIC for this class.
	 */
	@Override
	public GameSession.GameType getGameType() {
		return GameSession.GameType.CLASSIC;
	}
}
