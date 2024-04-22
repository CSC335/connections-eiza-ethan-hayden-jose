package com.connections.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.GameSession;
import com.connections.web.DatabaseFormattable;

public class PlayedGameInfoClassic extends PlayedGameInfo implements DatabaseFormattable {
	public PlayedGameInfoClassic(Document doc) {
		super(doc);
	}

	public PlayedGameInfoClassic(int puzzleNumber, int mistakesMadeCount, int hintsUsedCount, int connectionCount,
			List<Set<Word>> guesses, boolean won, ZonedDateTime gameStartTime, ZonedDateTime gameEndTime) {
		super(puzzleNumber, mistakesMadeCount, hintsUsedCount, connectionCount, guesses, won, gameStartTime, gameEndTime);
	}

	@Override
	public GameSession.GameType getGameType() {
		return GameSession.GameType.CLASSIC;
	}
}
