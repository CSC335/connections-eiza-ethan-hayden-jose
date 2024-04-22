package com.connections.web;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.connections.model.GameSaveState;
import com.connections.model.PlayedGameInfo;

public abstract class WebUser implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
	public enum UserType {
		NONE, ACCOUNT, GUEST,
	}

	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_PLAYED_GAMES = "played_games";
	public static final String KEY_LATEST_SAVE_STATE = "latest_game_save_state";
	public static final String KEY_HAS_LATEST_SAVE_STATE = "has_latest_game_save_state";
	public static final String KEY_CURRENTLY_IN_GAME = "currently_in_game";

	protected List<PlayedGameInfo> playedGameList;
	protected String userID;
	protected WebContext webContext;
	protected GameSaveState latestSaveState;
	protected boolean hasLatestSaveState;
	protected boolean currentlyInGame;

	public WebUser(WebContext webContext) {
		setWebContext(webContext);
		this.userID = null;
		this.playedGameList = new ArrayList<>();
		this.latestSaveState = null;
		this.hasLatestSaveState = false;
		this.currentlyInGame = false;
	}

	public WebUser(WebContext webContext, Document doc) {
		setWebContext(webContext);
		loadFromDatabaseFormat(doc);
	}

	public WebUser(WebContext webContext, String userID) {
		setWebContext(webContext);
		this.userID = userID;
		this.playedGameList = new ArrayList<>();
		this.latestSaveState = null;
		this.hasLatestSaveState = false;
		this.currentlyInGame = false;
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
	
	/*
	 * If the user does not have any latest game save state, then this will be null.
	 */
	public GameSaveState getLatestGameSaveState() {
		return latestSaveState;
	}
	
	public void setLatestGameSaveState(GameSaveState latestSaveState) {
		if(latestSaveState == null) {
			hasLatestSaveState = false;
		} else {
			hasLatestSaveState = true;
		}
		this.latestSaveState = latestSaveState;
	}
	
	public void clearLatestGameSaveState() {
		this.latestSaveState = null;
		this.hasLatestSaveState = false;
	}
	
	public boolean hasLatestSaveState() {
		return hasLatestSaveState;
	}

	public void setCurrentlyInGameStatus(boolean currentlyInGame) {
		this.currentlyInGame = currentlyInGame; 
	}
	
	public boolean isCurrentlyInGame() {
		return currentlyInGame;
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
	
	public PlayedGameInfo getPlayedGameByPuzzleNum(int puzzleNumber) {
		for(PlayedGameInfo playedGame : playedGameList) {
			if(playedGame.getPuzzleNumber() == puzzleNumber) {
				return playedGame;
			}
		}
		return null;
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
		if(latestSaveState != null) {
			doc.append(KEY_LATEST_SAVE_STATE, latestSaveState.getAsDatabaseFormat());
		}
		doc.append(KEY_HAS_LATEST_SAVE_STATE, hasLatestSaveState);
		doc.append(KEY_CURRENTLY_IN_GAME, currentlyInGame);
		
		return doc;
	}

	@Override
	public void loadFromDatabaseFormat(Document doc) {
		playedGameList = new ArrayList<>();
		userID = doc.getString(KEY_USER_ID);
		List<Document> playedGameListDoc = doc.getList(KEY_PLAYED_GAMES, Document.class);
		for (Document gameDoc : playedGameListDoc) {
			playedGameList.add(PlayedGameInfo.getGameInfoFromDatabaseFormat(gameDoc));
		}
		Document saveStateDoc = doc.get(KEY_LATEST_SAVE_STATE, Document.class);
		if(saveStateDoc != null) {
			latestSaveState = new GameSaveState(saveStateDoc);
		}
		hasLatestSaveState = doc.getBoolean(KEY_HAS_LATEST_SAVE_STATE, false);
		currentlyInGame = doc.getBoolean(KEY_CURRENTLY_IN_GAME, false);
	}

	@Override
	public WebContext getWebContext() {
		return webContext;
	}

	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}
}
