package com.connections.web;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.connections.model.PlayedGameInfo;

public class WebBridgeUser implements ModularWeb, DatabaseFormattable, DatabaseUnique {
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_PLAYED_GAMES = "played_games";
	
	protected List<PlayedGameInfo> playedGameList;
	protected String userID;
	protected WebContext webContext;
	
	public WebBridgeUser(WebContext webContext, Document doc) {
		setWebContext(webContext);
		loadFromDatabaseFormat(doc);
	}
	
	public WebBridgeUser(WebContext webContext, String userID) {
		setWebContext(webContext);
		this.userID = userID;
		
		playedGameList = new ArrayList<>();
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
	
	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_USER_ID, userID);
		List<Document> playedGameListDoc = new ArrayList<>();
		for(PlayedGameInfo game : playedGameList) {
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
		for(Document gameDoc : playedGameListDoc) {
			playedGameList.add(new PlayedGameInfo(gameDoc));
		}
	}

	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}
	
	@Override
	public WebContext getWebContext() {
		return webContext;
	}

	@Override
	public void writeToDatabase() {
		WebBridge.storeUniqueEntry(webContext, WebBridge.COLLECTION_ACCOUNT, KEY_USER_ID, userID, getAsDatabaseFormat());
	}

	@Override
	public void readFromDatabase() {
		Document doc = WebBridge.loadUniqueEntry(webContext, WebBridge.COLLECTION_ACCOUNT, KEY_USER_ID, userID);
		if(doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}

	@Override
	public void setUniqueID(String id) {
		this.userID = id;
	}

	@Override
	public String getUniqueID() {
		return userID;
	}
}
