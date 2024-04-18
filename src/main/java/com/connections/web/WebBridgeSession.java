package com.connections.web;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class WebBridgeSession implements ModularWeb, DatabaseFormattable {
	public static final String KEY_SESSION_ID = "session_id";

	private String sessionID;
	private WebBridgeUser user;
	private boolean sessionActive;
	private WebContext webContext;

	public enum UserType {
		NONE, ACCOUNT, GUEST,
	}

	public WebBridgeSession(WebContext webContext) {
		setWebContext(webContext);
		this.sessionID = null;
		this.user = null;
		this.sessionActive = false;
	}

	public boolean loginUser(String userID) {
		// you MUST log out first to properly end the session
		if (sessionActive) {
			return false;
		}

		String newSessionID = WebBridge.sessionBegin(webContext, userID);

		// some error occurred
		if (newSessionID == null) {
			// failed to login
			return false;
		}

		WebBridgeUser newUser = WebBridgeUser.getUserByID(webContext, userID);

		if (newUser == null) {
			return false;
		}

		sessionID = newSessionID;
		user = newUser;
		sessionActive = true;
		return true;
	}

	// NOTE: this affects the CURRENTLY CONNECTED USER
	// this cannot "log out" any other user
	public boolean logout(boolean deleteGuestUser) {
		if (!sessionActive) {
			return false;
		}

		sessionID = null;
		user = null;
		sessionActive = false;
		return WebBridge.sessionSignOut(webContext, deleteGuestUser);
	}

	@Override
	public WebContext getWebContext() {
		return webContext;
	}

	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}

	@Override
	public void readFromDatabase() {
		Document doc = WebBridge.getUniqueEntry(webContext, WebBridge.COLLECTION_SESSION_ID_NAME, KEY_SESSION_ID, sessionID);
		if(doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}

	@Override
	public void writeToDatabase() {
		WebBridge.updateUniqueEntry(webContext, WebBridge.COLLECTION_SESSION_ID_NAME, KEY_SESSION_ID, sessionID, getAsDatabaseFormat());
	}

	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_SESSION_ID, sessionID);
		doc.append(WebBridgeUser.KEY_USER_ID, user.getUserID());
		return doc;
	}

	@Override
	public void loadFromDatabaseFormat(Document doc) {
		String userID = doc.getString(WebBridgeUser.KEY_USER_ID);
		sessionID = doc.getString(KEY_SESSION_ID);
		user = WebBridgeUser.getUserByID(webContext, userID);

		// assuming that the session is active
		sessionActive = true;
	}
}
