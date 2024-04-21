package com.connections.model;

import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.GameSession;
import com.connections.web.DatabaseFormattable;

public class PlayedGameInfoTimed extends PlayedGameInfo implements DatabaseFormattable {
	public static final String KEY_TIME_LIMIT = "time_limit";
	public static final String KEY_COMPLETED_BEFORE_LIMIT = "completed";
	protected int timeLimit;
	protected boolean completedBeforeTimeLimit;

	public PlayedGameInfoTimed(Document doc) {
		super(doc);
	}
	
	public PlayedGameInfoTimed(int puzzleNumber, int mistakesMadeCount, int hintsUsedCount, int connectionCount, int timeCompleted,
			List<Set<Word>> guesses, boolean won, int timeLimit, boolean completedBeforeTimeLimit) {
		super(puzzleNumber, mistakesMadeCount, hintsUsedCount, connectionCount, timeCompleted, guesses, won);
		this.timeLimit = timeLimit;
		this.completedBeforeTimeLimit = completedBeforeTimeLimit;
	}

	/*
	 * The time limit is in seconds
	 */
    public int getTimeLimit() {
        return timeLimit;
    }

    public boolean isCompletedBeforeTimeLimit() {
        return completedBeforeTimeLimit;
    }
    
    @Override
    public GameSession.GameType getGameType() {
    	return GameSession.GameType.TIME_TRIAL;
    }
	
	@Override
	public Document getAsDatabaseFormat() {
		Document doc = super.getAsDatabaseFormat();
		doc.append(KEY_TIME_LIMIT, timeLimit);
		doc.append(KEY_COMPLETED_BEFORE_LIMIT, completedBeforeTimeLimit);
		return doc;
	}
	
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		super.loadFromDatabaseFormat(doc);
		timeLimit = doc.getInteger(KEY_TIME_LIMIT, -1);
		completedBeforeTimeLimit = doc.getBoolean(KEY_COMPLETED_BEFORE_LIMIT, false);
	}
}
