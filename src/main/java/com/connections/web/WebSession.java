package com.connections.web;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.bson.Document;

import com.connections.view_controller.GameSession.GameType;
import com.mongodb.client.FindIterable;

/**
 * The WebSession class represents an active connection between a user and the
 * website. A session has a unique ID that is stored as a cookie in the web
 * browser and is associated with a user (by the user ID) in the database.
 */
public class WebSession implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
	public static final String KEY_SESSION_ID = "session_id";
	public static final String KEY_CREATION_DATE = "creation_date";
	public static final int SESSION_LIFESPAN_DAYS = 7;

	private String sessionID;
	private WebUser user;
	private boolean sessionActive;
	private WebContext webContext;
	private ZonedDateTime sessionCreationDate;

	/**
	 * Constructs a new WebSession with the given WebContext and Document.
	 *
	 * @param webContext the WebContext associated with the session
	 * @param doc        the Document representing the session data
	 */
	public WebSession(WebContext webContext, Document doc) {
		setWebContext(webContext);
		loadFromDatabaseFormat(doc);
	}

	/**
	 * Constructs a new WebSession with the given WebContext. It will first attempt
	 * to load from the cookie, and if that fails, it will create a new guest user
	 * with the login() method and save it to the database.
	 *
	 * @param webContext the WebContext associated with the session
	 */
	public WebSession(WebContext webContext) {
		setWebContext(webContext);

		if (!loadFromCookie()) {
			this.sessionID = null;
			this.user = null;
			this.sessionCreationDate = null;
		}
	}

	/**
	 * Loads the session data from a cookie.
	 *
	 * @return true if the session data is successfully loaded from the cookie,
	 *         false otherwise
	 */
	private boolean loadFromCookie() {
		// When there is no cookie with the session ID.
		if (!WebUtils.cookieContains(webContext, KEY_SESSION_ID)) {
			return false;
		}

		String readSessionID = WebUtils.cookieGet(webContext, KEY_SESSION_ID);

		// When the session ID does not actually exist (not valid).
		if (readSessionID == null) {
			return false;
		}

		// Remove the cookie if its session ID does not exist anymore.
		if (!checkSessionIDExists(webContext, readSessionID)) {
			WebUtils.cookieRemove(webContext, KEY_SESSION_ID);
			return false;
		}

		Document sessionInfoDoc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_SESSION_ID_NAME,
				KEY_SESSION_ID, readSessionID);
		WebSession readSession = new WebSession(webContext, sessionInfoDoc);

		// When user does not exist.
		if (readSession.getUser() == null || !readSession.getUser().existsInDatabase()) {
			return false;
		}

		if (readSession.isExpired()) {
			WebUtils.cookieRemove(webContext, KEY_SESSION_ID);
			readSession.removeFromDatabase();
		}

		user = readSession.getUser();
		sessionID = readSessionID;
		sessionActive = true;

		return true;
	}

	/**
	 * Logs in the session. If the session contains no user, it will create a new
	 * guest user. It will store the session in the database and in the cookie.
	 *
	 * @return true if the login is successful, false otherwise
	 */
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

		sessionCreationDate = ZonedDateTime.now();
		sessionID = generateUnusedSessionID(webContext);
		WebUtils.cookieSet(webContext, KEY_SESSION_ID, sessionID);
		writeToDatabase();
		sessionActive = true;
		return true;
	}

	/**
	 * Logs out the session. If the user in the session is a guest user, it will be
	 * removed from the database. The session is removed from the database and the
	 * cookies.
	 *
	 * @return true if the logout is successful, false otherwise
	 */
	public boolean logout() {
		if (!sessionActive || !existsInDatabase()) {
			return false;
		}

		WebUtils.cookieRemove(webContext, sessionID);
		removeFromDatabase();
		sessionID = null;
		sessionActive = false;
		return true;
	}

	/**
	 * Sets the user associated with the session.
	 *
	 * @param user the user to associate with the session
	 * @return true if the user is successfully set, false otherwise
	 */
	public boolean setUser(WebUser user) {
		if (sessionActive) {
			return false;
		}
		this.user = user;
		return true;
	}

	/**
	 * Retrieves the session ID.
	 *
	 * @return the session ID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * Retrieves the user associated with the session.
	 *
	 * @return the user associated with the session
	 */
	public WebUser getUser() {
		return user;
	}

	public ZonedDateTime getCreationDate() {
		return sessionCreationDate;
	}

	/**
	 * Checks if the session is signed in.
	 *
	 * @return true if the session is signed in, false otherwise
	 */
	public boolean isSignedIn() {
		return sessionActive;
	}

	/**
	 * Checks if the session is empty (no user associated).
	 *
	 * @return true if the session is empty, false otherwise
	 */
	public boolean isEmpty() {
		return user == null;
	}

	/**
	 * Checks if the session is signed into an account.
	 *
	 * @return true if the session is signed into an account, false otherwise
	 */
	public boolean isSignedIntoAccount() {
		if (!isSignedIn() || user == null || user.getType() != WebUser.UserType.ACCOUNT) {
			return false;
		}

		return true;
	}

	private boolean isExpired() {
		if (sessionCreationDate == null) {
			return false;
		}

		ZonedDateTime currentDate = ZonedDateTime.now();

		if (ChronoUnit.DAYS.between(sessionCreationDate, currentDate) > SESSION_LIFESPAN_DAYS) {
			return true;
		}

		return false;
	}

	/**
	 * Generates an unused session ID.
	 *
	 * @param webContext the WebContext associated with the session
	 * @return an unused session ID, or null if unable to generate a unique ID
	 */
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

	/**
	 * Checks if the given session ID exists.
	 *
	 * @param webContext the WebContext associated with the session
	 * @param sessionID  the session ID to check
	 * @return true if the session ID exists, false otherwise
	 */
	public static boolean checkSessionIDExists(WebContext webContext, String sessionID) {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_SESSION_ID_NAME, KEY_SESSION_ID,
				sessionID);
	}

	public static void clearExpiredSessions(WebContext webContext) {
		FindIterable<Document> sessionDocIterable = WebUtils.helperCollectionGetAll(webContext,
				WebUtils.COLLECTION_SESSION_ID_NAME);

		for (Document currentSessionDoc : sessionDocIterable) {
			WebSession currentSession = new WebSession(webContext, currentSessionDoc);

			if (currentSession.isExpired()) {
				currentSession.removeFromDatabase();
			}
		}
	}

	/**
	 * Retrieves the WebContext associated with the session.
	 *
	 * @return the WebContext associated with the session
	 */
	@Override
	public WebContext getWebContext() {
		return webContext;
	}

	/**
	 * Sets the WebContext associated with the session.
	 *
	 * @param webContext the WebContext to associate with the session
	 */
	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}

	/**
	 * Reads the session data from the database.
	 */
	@Override
	public void readFromDatabase() {
		Document doc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_SESSION_ID_NAME, KEY_SESSION_ID,
				sessionID);
		if (doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}

	/**
	 * Writes the session data to the database.
	 */
	@Override
	public void writeToDatabase() {
		WebUtils.helperCollectionUpdate(webContext, WebUtils.COLLECTION_SESSION_ID_NAME, KEY_SESSION_ID, sessionID,
				getAsDatabaseFormat());
	}

	/**
	 * Retrieves the session data as a Document for database storage.
	 *
	 * @return the session data as a Document for database storage
	 */
	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_SESSION_ID, sessionID);

		String userID = (user == null) ? null : user.getUserID();

		doc.append(WebUser.KEY_USER_ID, userID);
		doc.append(KEY_CREATION_DATE, WebUtils.helperDateToString(sessionCreationDate));
		return doc;
	}

	/**
	 * Loads the session data from a Document retrieved from the database.
	 *
	 * @param doc the Document containing the session data
	 */
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		String userID = doc.getString(WebUser.KEY_USER_ID);
		sessionID = doc.getString(KEY_SESSION_ID);
		user = WebUser.getUserByID(webContext, userID);
		sessionCreationDate = WebUtils.helperStringToDate(doc.getString(KEY_CREATION_DATE));
		sessionActive = false;
	}

	/**
	 * Checks if the session exists in the database.
	 *
	 * @return true if the session exists in the database, false otherwise
	 */
	@Override
	public boolean existsInDatabase() {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_SESSION_ID_NAME,
				WebSession.KEY_SESSION_ID, sessionID);
	}

	/**
	 * Removes the session from the database.
	 */
	@Override
	public void removeFromDatabase() {
		if (user != null && user.getType() == WebUser.UserType.GUEST && user.existsInDatabase()) {
			user.removeFromDatabase();
		}
		WebUtils.helperCollectionDelete(webContext, WebUtils.COLLECTION_SESSION_ID_NAME, WebSession.KEY_SESSION_ID,
				sessionID);
	}

	/**
	 * Updates the user achievement data based on the game session.
	 *
	 * @param gameType      the type of the game played
	 * @param noMistakes    true if the game was completed with no mistakes, false
	 *                      otherwise
	 * @param timeTrialTime the time taken to complete the time trial game
	 * @param wonGame       true if the game was won, false otherwise
	 */
	public void updateUserAchievementData(GameType gameType, boolean noMistakes, int timeTrialTime, boolean wonGame) {
		WebUser user = getUser();
		if (user != null) {
			// Read from database just to be safe that we are not overwriting with old info.
			user.readFromDatabase();

			if (gameType == GameType.CLASSIC) {
				user.incrementRegularGamesCompleted();
			} else if (gameType == GameType.TIME_TRIAL) {
				user.incrementTimeTrialsCompleted();
				if (timeTrialTime < 30 && timeTrialTime > 0 && wonGame) {
					user.incrementTimeTrialsUnderTimeCompleted();
				}
			}

			if (noMistakes) {
				user.incrementNoMistakesCompleted();
			}

			user.writeToDatabase();
		}
	}
}
