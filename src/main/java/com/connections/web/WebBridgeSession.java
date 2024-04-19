package com.connections.web;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class WebBridgeSession implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
	public static final String KEY_SESSION_ID = "session_id";

	private String sessionID;
	private WebBridgeUser user;
	private boolean sessionActive;
	private WebContext webContext;
	
	public WebBridgeSession(WebContext webContext, Document doc) {
		loadFromDatabaseFormat(doc);
	}
	
	public WebBridgeSession(WebContext webContext, WebBridgeUser user) {
		setWebContext(webContext);
		this.sessionID = generateUnusedSessionID(webContext);
		this.user = user;
	}
	
	public boolean login() {
		if (sessionActive || existsInDatabase()) {
			return false;
		}
		
		sessionID = generateUnusedSessionID(webContext);
		WebBridge.cookieSet(webContext, KEY_SESSION_ID, sessionID);
		writeToDatabase();
		sessionActive = true;
		return true;
	}
	
	public static String generateUnusedSessionID(WebContext webContext) {
		boolean unique = false;

		while (!unique) {
			String newID = WebBridge.generateGeneralPurposeID();
			if (!checkSessionIDExists(webContext, newID)) {
				unique = true;
				return newID;
			}
		}

		return null;
	}
	
	public static boolean checkSessionIDExists(WebContext webContext, String sessionID) {
		return WebBridge.helperCollectionContains(webContext, WebBridge.COLLECTION_SESSION_ID_NAME, KEY_SESSION_ID, sessionID);
	}
	
//	public static WebBridgeUser.UserType checkuserTypeBySessionID(WebContext webContext, String sessionID) {
//		MongoCollection<Document> collectionSession = webContext.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
//		Document sessionSearch = new Document(WebBridgeSession.KEY_SESSION_ID, sessionID);
//		FindIterable<Document> sessionResults = collectionSession.find(sessionSearch);
//		if (helperResultsNotEmpty(sessionResults)) {
//			Document matchedSession = sessionResults.first();
//			String userID = matchedSession.getString(WebBridgeUser.KEY_USER_ID);
//			
//			if(userID != null) {
//				return checkUserTypeByUserID(webContext, userID);
//			}
//		}
//		return WebBridgeUser.UserType.NONE;
//	}

	public boolean logout(boolean deleteGuestUser) {
		if (!sessionActive || !existsInDatabase()) {
			return false;
		}
		
		if(user.getType() == WebBridgeUser.UserType.GUEST && user.existsInDatabase()) {
			user.removeFromDatabase();
		}
		
		WebBridge.cookieRemove(webContext, sessionID);
		sessionActive = false;
		return true;
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
		sessionActive = false;
	}

	@Override
	public boolean existsInDatabase() {
		return WebBridge.helperCollectionContains(webContext, WebBridge.COLLECTION_SESSION_ID_NAME, WebBridgeSession.KEY_SESSION_ID, sessionID);
	}
	
	@Override
	public void removeFromDatabase() {
		WebBridge.helperCollectionDelete(webContext, WebBridge.COLLECTION_SESSION_ID_NAME, WebBridgeSession.KEY_SESSION_ID, sessionID);
	}
}
