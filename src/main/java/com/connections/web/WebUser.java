package com.connections.web;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

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

	public WebUser(WebContext webContext) {
		setWebContext(webContext);
		this.userID = null;
		this.playedGameList = new ArrayList<>();
	}

	public WebUser(WebContext webContext, Document doc) {
		setWebContext(webContext);
		loadFromDatabaseFormat(doc);
	}

	public WebUser(WebContext webContext, String userID) {
		setWebContext(webContext);
		this.userID = userID;
		this.playedGameList = new ArrayList<>();
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

	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_USER_ID, userID);
		List<Document> playedGameListDoc = new ArrayList<>();
		for (PlayedGameInfo game : playedGameList) {
			playedGameListDoc.add(game.getAsDatabaseFormat());
		}
		doc.append(KEY_PLAYED_GAMES, playedGameListDoc);
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
