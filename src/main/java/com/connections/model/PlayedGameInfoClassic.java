package com.connections.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.GameSession;
import com.connections.web.DatabaseFormattable;

public class PlayedGameInfoClassic extends PlayedGameInfo implements DatabaseFormattable {
	public PlayedGameInfoClassic(Document doc) {
		super(doc);
	}

	public PlayedGameInfoClassic(int puzzleNumber, int mistakesMadeCount, int hintsUsedCount, int connectionCount, int timeCompleted,
			List<Set<Word>> guesses, boolean won) {
		super(puzzleNumber, mistakesMadeCount, hintsUsedCount, connectionCount, timeCompleted, guesses, won);
	}

	@Override
	public GameSession.GameType getGameType() {
		return GameSession.GameType.CLASSIC;
	}
}
