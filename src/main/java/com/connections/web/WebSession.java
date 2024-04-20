package com.connections.web;

import org.bson.Document;

public class WebSession implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
	public static final String KEY_SESSION_ID = "session_id";

	private String sessionID;
	private WebUser user;
	private boolean sessionActive;
	private WebContext webContext;

	// option A: load session data from database, log in (uncommon, but possible)
	public WebSession(WebContext webContext, Document doc) {
		loadFromDatabaseFormat(doc);
	}

	// option B:
	// (1) attempt to load from cookie
	// (2) if the cookie fails, then when you login later with the login() method,
	// it will create a guest user
	public WebSession(WebContext webContext) {
		setWebContext(webContext);

		if (!loadFromCookie()) {
			this.sessionID = null;
			this.user = null;
		}
	}

	private boolean loadFromCookie() {
		// there is no cookie with the session ID
		if (!WebUtils.cookieContains(webContext, KEY_SESSION_ID)) {
			return false;
		}

		String readSessionID = WebUtils.cookieGet(webContext, KEY_SESSION_ID);

		// session ID does not actually exist (not valid)
		if (readSessionID == null || !checkSessionIDExists(webContext, readSessionID)) {
			return false;
		}

		Document sessionInfoDoc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_SESSION_ID_NAME,
				KEY_SESSION_ID, readSessionID);
		String readUserID = sessionInfoDoc.getString(WebUser.KEY_USER_ID);

		// user does not exist
		if (WebUser.checkUserTypeByUserID(webContext, readUserID) == WebUser.UserType.NONE) {
			return false;
		}

		user = WebUser.getUserByID(webContext, readUserID);
		sessionID = readSessionID;
		sessionActive = true;

		return true;
	}

	public boolean login() {
		if (sessionActive || existsInDatabase()) {
			return false;
		}

		if (user == null || user.getType() == WebUser.UserType.NONE) {
			user = new WebUserGuest(webContext);
		}

		if (user.getType() == WebUser.UserType.GUEST) {
			user.writeToDatabase();
		}

		sessionID = generateUnusedSessionID(webContext);
		WebUtils.cookieSet(webContext, KEY_SESSION_ID, sessionID);
		writeToDatabase();
		sessionActive = true;
		return true;
	}

	public boolean logout(boolean deleteGuestUser) {
		if (!sessionActive || !existsInDatabase()) {
			return false;
		}

		if (user.getType() == WebUser.UserType.GUEST && user.existsInDatabase()) {
			user.removeFromDatabase();
		}

		WebUtils.cookieRemove(webContext, sessionID);
		removeFromDatabase();
		sessionID = null;
		sessionActive = false;
		return true;
	}

	public boolean setUser(WebUser user) {
		if (sessionActive) {
			return false;
		}
		this.user = user;
		return true;
	}

	public String getSessionID() {
		return sessionID;
	}

	public WebUser getUser() {
		return user;
	}

	public boolean isSignedIn() {
		return sessionActive;
	}

	public boolean isEmpty() {
		return user == null;
	}

	public boolean isSignedIntoAccount() {
		if (!isSignedIn()) {
			return false;
		}

		if (user == null || user.getType() != WebUser.UserType.ACCOUNT) {
			return false;
		}

		return true;
	}

	public static String generateUnusedSessionID(WebContext webContext) {
		boolean unique = false;

		while (!unique) {
			String newID = WebUtils.generateGeneralPurposeID();
			if (!checkSessionIDExists(webContext, newID)) {
				unique = true;
				return newID;
			}
		}

		return null;
	}

	public static boolean checkSessionIDExists(WebContext webContext, String sessionID) {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_SESSION_ID_NAME, KEY_SESSION_ID,
				sessionID);
	}

//	public static WebBridgeUser.UserType checkCookieSessionUserType(WebContext webContext) {
//		if(!WebBridge.cookieContains(webContext, KEY_SESSION_ID)) {
//			return WebBridgeUser.UserType.NONE;
//		}
//	}

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
		Document doc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_SESSION_ID_NAME, KEY_SESSION_ID,
				sessionID);
		if (doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}

	@Override
	public void writeToDatabase() {
		WebUtils.helperCollectionUpdate(webContext, WebUtils.COLLECTION_SESSION_ID_NAME, KEY_SESSION_ID, sessionID,
				getAsDatabaseFormat());
	}

	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_SESSION_ID, sessionID);

		String userID = (user == null) ? null : user.getUserID();

		doc.append(WebUser.KEY_USER_ID, userID);
		return doc;
	}

	@Override
	public void loadFromDatabaseFormat(Document doc) {
		String userID = doc.getString(WebUser.KEY_USER_ID);
		sessionID = doc.getString(KEY_SESSION_ID);
		user = WebUser.getUserByID(webContext, userID);
		sessionActive = false;
	}

	@Override
	public boolean existsInDatabase() {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_SESSION_ID_NAME,
				WebSession.KEY_SESSION_ID, sessionID);
	}

	@Override
	public void removeFromDatabase() {
		WebUtils.helperCollectionDelete(webContext, WebUtils.COLLECTION_SESSION_ID_NAME, WebSession.KEY_SESSION_ID,
				sessionID);
	}
}
