package com.connections.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.connections.model.DifficultyColor;
import com.connections.model.PlayedGameInfo;

public abstract class WebUser implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
    public enum UserType {
        NONE, ACCOUNT, GUEST,
    }

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_PLAYED_GAMES = "played_games";

    protected List<PlayedGameInfo> playedGameList;
    protected String userID;
    protected WebContext webContext;

    protected int regularGamesCompleted;
    protected int timeTrialsCompleted;
    protected int noMistakesCompleted;
    protected int timeTrialsUnderTimeCompleted;

    public WebUser(WebContext webContext) {
        setWebContext(webContext);
        this.userID = null;
        this.playedGameList = new ArrayList<>();
        regularGamesCompleted = 0;
        timeTrialsCompleted = 0;
        noMistakesCompleted = 0;
        timeTrialsUnderTimeCompleted = 0;
    }

    public WebUser(WebContext webContext, Document doc) {
        setWebContext(webContext);
        loadFromDatabaseFormat(doc);
    }

    public WebUser(WebContext webContext, String userID) {
        setWebContext(webContext);
        this.userID = userID;
        this.playedGameList = new ArrayList<>();
        regularGamesCompleted = 0;
        timeTrialsCompleted = 0;
        noMistakesCompleted = 0;
        timeTrialsUnderTimeCompleted = 0;
        readFromDatabase();
    }

	public List<PlayedGameInfo> getPlayedGameList() {
		return playedGameList;
	}

	public void addPlayedGame(PlayedGameInfo playedGameInfo) {
		playedGameList.add(playedGameInfo);
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public abstract UserType getType();
	
	public boolean hasPlayedGameByPuzzleNum(int puzzleNumber) {
		for(PlayedGameInfo playedGame : playedGameList) {
			if(playedGame.getPuzzleNumber() == puzzleNumber) {
				return true;
			}
		}
		return false;
	}

	public static String generateUnusedUserID(WebContext webContext) {
		boolean unique = false;

		while (!unique) {
			String newID = WebUtils.generateGeneralPurposeID();
			if (checkUserTypeByUserID(webContext, newID) == WebUser.UserType.NONE) {
				unique = true;
				return newID;
			}
		}

		return null;
	}

	public static WebUser.UserType checkUserTypeByUserID(WebContext webContext, String userID) {
		if (WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, WebUserAccount.KEY_USER_ID,
				userID)) {
			return WebUser.UserType.ACCOUNT;
		}

		if (WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_GUEST, WebUserAccount.KEY_USER_ID,
				userID)) {
			return WebUser.UserType.GUEST;
		}

		return WebUser.UserType.NONE;
	}

	public static WebUser getUserByID(WebContext webContext, String userID) {
		UserType userType = checkUserTypeByUserID(webContext, userID);

		switch (userType) {
		case ACCOUNT:
			return new WebUserAccount(webContext, userID);
		case GUEST:
			return new WebUserGuest(webContext, userID);
		default:
			return null;
		}
	}

	public static String getUserIDByCookie(WebContext webContext) {
		String sessionID = WebUtils.cookieGet(webContext, WebSession.KEY_SESSION_ID);
		if(sessionID == null) {
			return null;
		}
		return getUserIDBySessionID(webContext, sessionID);
	}

	public static String getUserIDBySessionID(WebContext webContext, String sessionID) {
		Document sessionDoc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_SESSION_ID_NAME,
				WebSession.KEY_SESSION_ID, sessionID);
		if(sessionDoc == null) {
			return null;
		}
		return sessionDoc.getString(KEY_USER_ID);
	}

	@Override
    public Document getAsDatabaseFormat() {
        Document doc = new Document();
        doc.append(KEY_USER_ID, userID);
        List<Document> playedGameListDoc = new ArrayList<>();
        for (PlayedGameInfo game : playedGameList) {
            playedGameListDoc.add(game.getAsDatabaseFormat());
        }
        doc.append(KEY_PLAYED_GAMES, playedGameListDoc);
        doc.append("regular_games_completed", regularGamesCompleted);
        doc.append("time_trials_completed", timeTrialsCompleted);
        doc.append("no_mistakes_completed", noMistakesCompleted);
        doc.append("time_trials_under_time_completed", timeTrialsUnderTimeCompleted);
        return doc;
    }

    @Override
    public void loadFromDatabaseFormat(Document doc) {
        playedGameList = new ArrayList<>();
        userID = doc.getString(KEY_USER_ID);
        List<Document> playedGameListDoc = doc.getList(KEY_PLAYED_GAMES, Document.class);
        for (Document gameDoc : playedGameListDoc) {
            playedGameList.add(new PlayedGameInfo(gameDoc));
        }
        regularGamesCompleted = doc.getInteger("regular_games_completed", 0);
        timeTrialsCompleted = doc.getInteger("time_trials_completed", 0);
        noMistakesCompleted = doc.getInteger("no_mistakes_completed", 0);
        timeTrialsUnderTimeCompleted = doc.getInteger("time_trials_under_time_completed", 0);
    }

	@Override
	public WebContext getWebContext() {
		return webContext;
	}

	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}
	
	public boolean hasCompletedRegularGameAchievement(DifficultyColor difficultyColor) {
        int numGamesCompleted = getNumRegularGamesCompleted();
        return numGamesCompleted >= getAchievementThreshold(difficultyColor);
    }

    public boolean hasCompletedTimeTrialAchievement(DifficultyColor difficultyColor) {
        int numTimeTrialsCompleted = getNumTimeTrialsCompleted();
        return numTimeTrialsCompleted >= getAchievementThreshold(difficultyColor);
    }

    public boolean hasCompletedNoMistakesAchievement(DifficultyColor difficultyColor) {
        int numNoMistakesCompleted = getNumNoMistakesCompleted();
        return numNoMistakesCompleted >= getAchievementThreshold(difficultyColor);
    }

    public boolean hasCompletedTimeTrialUnderTimeAchievement(DifficultyColor difficultyColor) {
        int numTimeTrialsUnderTimeCompleted = getNumTimeTrialsUnderTimeCompleted();
        return numTimeTrialsUnderTimeCompleted >= getAchievementThreshold(difficultyColor);
    }

    private int getAchievementThreshold(DifficultyColor difficultyColor) {
        switch (difficultyColor) {
            case YELLOW:
                return 1;
            case GREEN:
                return 10;
            case BLUE:
                return 50;
            case PURPLE:
                return 100;
            default:
                return 0;
        }
    }

    private int getNumRegularGamesCompleted() {
        return regularGamesCompleted;
    }

    private int getNumTimeTrialsCompleted() {
        return timeTrialsCompleted;
    }

    private int getNumNoMistakesCompleted() {
        return noMistakesCompleted;
    }

    private int getNumTimeTrialsUnderTimeCompleted() {
        return timeTrialsUnderTimeCompleted;
    }
    
    public void incrementRegularGamesCompleted() {
        regularGamesCompleted++;
    }

    public void incrementTimeTrialsCompleted() {
        timeTrialsCompleted++;
    }

    public void incrementNoMistakesCompleted() {
        noMistakesCompleted++;
    }

    public void incrementTimeTrialsUnderTimeCompleted() {
        timeTrialsUnderTimeCompleted++;
    }

}
