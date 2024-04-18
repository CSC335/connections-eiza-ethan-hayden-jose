package com.connections.model;

import java.util.List;
import java.util.Set;
import org.bson.Document;
import com.connections.web.DatabaseFormattable;
import com.connections.web.DatabaseUnique;

public class PlayedGameInfoTimed extends PlayedGameInfo implements DatabaseFormattable {
	public static final String KEY_TIME_LIMIT = "time_limit";
	public static final String KEY_COMPLETED_BEFORE_LIMIT = "completed";
	protected int timeLimitMin;
	protected boolean completedBeforeTimeLimit;

	public PlayedGameInfoTimed(Document doc) {
		super(doc);
	}
	
	public PlayedGameInfoTimed(int puzzleNumber, int guessCount, int mistakeCount, int connectionCount, int timeCompleted,
            List<Set<Word>> guesses, int timeLimitMin, boolean completedBeforeTimeLimit) {
		super(puzzleNumber, guessCount, mistakeCount, connectionCount, timeCompleted, guesses);
		this.timeLimitMin = timeLimitMin;
		this.completedBeforeTimeLimit = completedBeforeTimeLimit;
	}

    public int getTimeLimitMin() {
        return timeLimitMin;
    }

    public boolean isCompletedBeforeTimeLimit() {
        return completedBeforeTimeLimit;
    }
	
	@Override
	public Document getAsDatabaseFormat() {
		Document doc = super.getAsDatabaseFormat();
		doc.append(KEY_TIME_LIMIT, timeLimitMin);
		doc.append(KEY_COMPLETED_BEFORE_LIMIT, completedBeforeTimeLimit);
		return doc;
	}
	
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		super.loadFromDatabaseFormat(doc);
		guessCount = doc.getInteger(KEY_TIME_LIMIT, -1);
		completedBeforeTimeLimit = doc.getBoolean(KEY_COMPLETED_BEFORE_LIMIT, false);
	}
}
