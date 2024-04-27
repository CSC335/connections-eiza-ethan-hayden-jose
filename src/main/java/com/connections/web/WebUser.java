package com.connections.web;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.connections.model.DifficultyColor;
import com.connections.model.GameSaveState;
import com.connections.model.PlayedGameInfo;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

/**
 * The WebUser class represents a user in the Connections game: a user has a
 * unique String ID, has a save state of the latest game they played, and a list
 * of information relating to the previous games they played.
 */
public abstract class WebUser implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
	/**
	 * Enum representing the types of users.
	 */
	public enum UserType {
		NONE, ACCOUNT, GUEST,
	}

	protected int regularGamesCompleted;
	protected int timeTrialsCompleted;
	protected int noMistakesCompleted;
	protected int timeTrialsUnderTimeCompleted;
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

	/**
	 * Constructs a new WebUser with the given WebContext.
	 *
	 * @param webContext the WebContext associated with the user
	 */
	public WebUser(WebContext webContext) {
		setWebContext(webContext);
		this.userID = null;
		this.playedGameList = new ArrayList<>();
		this.latestSaveState = null;
		this.hasLatestSaveState = false;
		regularGamesCompleted = 0;
		timeTrialsCompleted = 0;
		noMistakesCompleted = 0;
		timeTrialsUnderTimeCompleted = 0;
		this.currentlyInGame = false;
	}

	/**
	 * Constructs a new WebUser with the given WebContext and Document.
	 *
	 * @param webContext the WebContext associated with the user
	 * @param doc        the Document representing the user data
	 */
	public WebUser(WebContext webContext, Document doc) {
		setWebContext(webContext);
		loadFromDatabaseFormat(doc);
	}

	/**
	 * Constructs a new WebUser with the given WebContext and user ID.
	 *
	 * @param webContext the WebContext associated with the user
	 * @param userID     the ID of the user
	 */
	public WebUser(WebContext webContext, String userID) {
		setWebContext(webContext);
		this.userID = userID;
		this.playedGameList = new ArrayList<>();
		this.latestSaveState = null;
		this.hasLatestSaveState = false;
		regularGamesCompleted = 0;
		timeTrialsCompleted = 0;
		noMistakesCompleted = 0;
		timeTrialsUnderTimeCompleted = 0;
		this.currentlyInGame = false;
		readFromDatabase();
	}

	/**
	 * Retrieves the username of the user.
	 *
	 * @return the username of the user
	 */
	public abstract String getUserName();

	/**
	 * Retrieves the email of the user.
	 *
	 * @return the email of the user
	 */
	public abstract String getEmail();

	/**
	 * Retrieves the password of the user.
	 *
	 * @return the password of the user
	 */
	public abstract String getPassWord();

	/**
	 * Retrieves the bio of the user.
	 *
	 * @return the bio of the user
	 */
	public abstract String getBio();

	/**
	 * Sets the username of the user.
	 *
	 * @param userName the new username of the user
	 */
	public abstract void setUserName(String userName);

	/**
	 * Sets the email of the user.
	 *
	 * @param email the new email of the user
	 */
	public abstract void setEmail(String email);

	/**
	 * Sets the password of the user.
	 *
	 * @param password the new password of the user
	 */
	public abstract void setPassWord(String password);

	/**
	 * Sets the bio of the user.
	 *
	 * @param bio the new bio of the user
	 */
	public abstract void setBio(String bio);

	/**
	 * Retrieves the list of played games by the user.
	 *
	 * @return the list of played games by the user
	 */
	public List<PlayedGameInfo> getPlayedGameList() {
		return playedGameList;
	}

	/**
	 * Adds a played game to the user's list of played games.
	 *
	 * @param playedGameInfo the played game information to add
	 */
	public void addPlayedGame(PlayedGameInfo playedGameInfo) {
		playedGameList.add(playedGameInfo);
	}

	/**
	 * Retrieves the user ID.
	 *
	 * @return the user ID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * Sets the user ID.
	 *
	 * @param userID the new user ID
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * Retrieves the latest game save state of the user. If the user does not have
	 * any latest game save state, then this will be null.
	 *
	 * @return the latest game save state of the user
	 */
	public GameSaveState getLatestGameSaveState() {
		return latestSaveState;
	}

	/**
	 * Sets the latest game save state of the user.
	 *
	 * @param latestSaveState the new latest game save state of the user
	 */
	public void setLatestGameSaveState(GameSaveState latestSaveState) {
		if (latestSaveState == null) {
			hasLatestSaveState = false;
		} else {
			hasLatestSaveState = true;
		}
		this.latestSaveState = latestSaveState;
	}

	/**
	 * Clears the latest game save state of the user.
	 */
	public void clearLatestGameSaveState() {
		this.latestSaveState = null;
		this.hasLatestSaveState = false;
	}

	/**
	 * Checks if the user has a latest game save state.
	 *
	 * @return true if the user has a latest game save state, false otherwise
	 */
	public boolean hasLatestSaveState() {
		return hasLatestSaveState;
	}

	/**
	 * Sets the currently in-game status of the user.
	 *
	 * @param currentlyInGame true if the user is currently in a game, false
	 *                        otherwise
	 */
	public void setCurrentlyInGameStatus(boolean currentlyInGame) {
		this.currentlyInGame = currentlyInGame;
	}

	/**
	 * Checks if the user is currently in a game.
	 *
	 * @return true if the user is currently in a game, false otherwise
	 */
	public boolean isCurrentlyInGame() {
		return currentlyInGame;
	}

	/**
	 * Retrieves the type of the user.
	 *
	 * @return the type of the user
	 */
	public abstract UserType getType();

	/**
	 * Checks if the user has played a game with the given puzzle number.
	 *
	 * @param puzzleNumber the puzzle number to check
	 * @return true if the user has played a game with the given puzzle number,
	 *         false otherwise
	 */
	public boolean hasPlayedGameByPuzzleNum(int puzzleNumber) {
		for (PlayedGameInfo playedGame : playedGameList) {
			if (playedGame.getPuzzleNumber() == puzzleNumber) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves the played game information for the given puzzle number.
	 *
	 * @param puzzleNumber the puzzle number to retrieve the played game information
	 *                     for
	 * @return the played game information for the given puzzle number, or null if
	 *         not found
	 */
	public PlayedGameInfo getPlayedGameByPuzzleNum(int puzzleNumber) {
		for (PlayedGameInfo playedGame : playedGameList) {
			if (playedGame.getPuzzleNumber() == puzzleNumber) {
				return playedGame;
			}
		}
		return null;
	}

	/**
	 * Generates an unused user ID.
	 *
	 * @param webContext the WebContext associated with the user
	 * @return an unused user ID, or null if unable to generate a unique ID
	 */
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

	/**
	 * Checks the user type based on the given user ID.
	 *
	 * @param webContext the WebContext associated with the user
	 * @param userID     the user ID to check
	 * @return the user type based on the given user ID
	 */
	public static WebUser.UserType checkUserTypeByUserID(WebContext webContext, String userID) {
		if (WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, WebUser.KEY_USER_ID, userID)) {
			return WebUser.UserType.ACCOUNT;
		}
		if (WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_GUEST, WebUser.KEY_USER_ID, userID)) {
			return WebUser.UserType.GUEST;
		}
		return WebUser.UserType.NONE;
	}

	/**
	 * Retrieves the WebUser based on the given user ID.
	 *
	 * @param webContext the WebContext associated with the user
	 * @param userID     the user ID to retrieve the WebUser for
	 * @return the WebUser based on the given user ID, or null if not found
	 */
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

	/**
	 * Retrieves the user ID from the cookie.
	 *
	 * @param webContext the WebContext associated with the user
	 * @return the user ID from the cookie, or null if not found
	 */
	public static String getUserIDByCookie(WebContext webContext) {
		String sessionID = WebUtils.cookieGet(webContext, WebSession.KEY_SESSION_ID);
		if (sessionID == null) {
			return null;
		}
		return getUserIDBySessionID(webContext, sessionID);
	}

	/**
	 * Retrieves the user ID based on the given session ID.
	 *
	 * @param webContext the WebContext associated with the user
	 * @param sessionID  the session ID to retrieve the user ID for
	 * @return the user ID based on the given session ID, or null if not found
	 */
	public static String getUserIDBySessionID(WebContext webContext, String sessionID) {
		Document sessionDoc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_SESSION_ID_NAME,
				WebSession.KEY_SESSION_ID, sessionID);
		if (sessionDoc == null) {
			return null;
		}
		return sessionDoc.getString(KEY_USER_ID);
	}

	/**
	 * Retrieves the user data as a Document for database storage.
	 *
	 * @return the user data as a Document for database storage
	 */
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
		if (latestSaveState != null) {
			doc.append(KEY_LATEST_SAVE_STATE, latestSaveState.getAsDatabaseFormat());
		}
		doc.append(KEY_HAS_LATEST_SAVE_STATE, hasLatestSaveState);
		doc.append(KEY_CURRENTLY_IN_GAME, currentlyInGame);
		return doc;
	}

	/**
	 * Loads the user data from a Document retrieved from the database.
	 *
	 * @param doc the Document containing the user data
	 */
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		playedGameList = new ArrayList<>();
		userID = doc.getString(KEY_USER_ID);
		List<Document> playedGameListDoc = doc.getList(KEY_PLAYED_GAMES, Document.class);
		for (Document gameDoc : playedGameListDoc) {
			playedGameList.add(PlayedGameInfo.getGameInfoFromDatabaseFormat(gameDoc));
		}
		regularGamesCompleted = doc.getInteger("regular_games_completed", 0);
		timeTrialsCompleted = doc.getInteger("time_trials_completed", 0);
		noMistakesCompleted = doc.getInteger("no_mistakes_completed", 0);
		timeTrialsUnderTimeCompleted = doc.getInteger("time_trials_under_time_completed", 0);
		Document saveStateDoc = doc.get(KEY_LATEST_SAVE_STATE, Document.class);
		if (saveStateDoc != null) {
			latestSaveState = new GameSaveState(saveStateDoc);
		}
		hasLatestSaveState = doc.getBoolean(KEY_HAS_LATEST_SAVE_STATE, false);
		currentlyInGame = doc.getBoolean(KEY_CURRENTLY_IN_GAME, false);
	}

	/**
	 * Retrieves the WebContext associated with the user.
	 *
	 * @return the WebContext associated with the user
	 */
	@Override
	public WebContext getWebContext() {
		return webContext;
	}

	/**
	 * Sets the WebContext associated with the user.
	 *
	 * @param webContext the WebContext to associate with the user
	 */
	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}

	/**
	 * Checks if the user has completed the regular game achievement for the given
	 * difficulty color.
	 *
	 * @param difficultyColor the difficulty color to check the achievement for
	 * @return true if the user has completed the regular game achievement for the
	 *         given difficulty color, false otherwise
	 */
	public boolean hasCompletedRegularGameAchievement(DifficultyColor difficultyColor) {
		int numGamesCompleted = getNumRegularGamesCompleted();
		return numGamesCompleted >= getAchievementThreshold(difficultyColor);
	}

	/**
	 * Checks if the user has completed the time trial achievement for the given
	 * difficulty color.
	 *
	 * @param difficultyColor the difficulty color to check the achievement for
	 * @return true if the user has completed the time trial achievement for the
	 *         given difficulty color, false otherwise
	 */
	public boolean hasCompletedTimeTrialAchievement(DifficultyColor difficultyColor) {
		int numTimeTrialsCompleted = getNumTimeTrialsCompleted();
		return numTimeTrialsCompleted >= getAchievementThreshold(difficultyColor);
	}

	/**
	 * Checks if the user has completed the no mistakes achievement for the given
	 * difficulty color.
	 *
	 * @param difficultyColor the difficulty color to check the achievement for
	 * @return true if the user has completed the no mistakes achievement for the
	 *         given difficulty color, false otherwise
	 */
	public boolean hasCompletedNoMistakesAchievement(DifficultyColor difficultyColor) {
		int numNoMistakesCompleted = getNumNoMistakesCompleted();
		return numNoMistakesCompleted >= getAchievementThreshold(difficultyColor);
	}

	/**
	 * Checks if the user has completed the time trial under time achievement for
	 * the given difficulty color.
	 *
	 * @param difficultyColor the difficulty color to check the achievement for
	 * @return true if the user has completed the time trial under time achievement
	 *         for the given difficulty color, false otherwise
	 */
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

	/**
	 * Retrieves the top users based on the total number of games completed for
	 * achievements.
	 *
	 * @param webContext the WebContext associated with the user
	 * @param limit      the maximum number of users to retrieve
	 * @return the list of top users based on the total number of games completed
	 *         for achievements
	 */
	public static List<WebUser> getTopUsers(WebContext webContext, int limit) {
		List<WebUser> allUsers = new ArrayList<>();
		MongoCollection<Document> accountCollection = webContext.getMongoDatabase()
				.getCollection(WebUtils.COLLECTION_ACCOUNT);
		FindIterable<Document> accountDocs = accountCollection.find();
		for (Document doc : accountDocs) {
			String userId = doc.getString(KEY_USER_ID);
			WebUser user = getUserByID(webContext, userId);
			if (user != null) {
				allUsers.add(user);
			}
		}
		for (WebUser user : allUsers) {
			user.readFromDatabase();
		}
		allUsers.sort((user1, user2) -> {
			int totalCount1 = user1.getNumAllGamesForAchievements();
			int totalCount2 = user2.getNumAllGamesForAchievements();
			return Integer.compare(totalCount2, totalCount1);
		});
		return allUsers.subList(0, Math.min(limit, allUsers.size()));
	}

	/**
	 * Retrieves the total number of games completed by the user for achievements.
	 *
	 * @return the total number of games completed by the user for achievements
	 */
	public int getNumAllGamesForAchievements() {
		return regularGamesCompleted + timeTrialsCompleted + noMistakesCompleted + timeTrialsUnderTimeCompleted;
	}

	/**
	 * Retrieves the number of regular games completed by the user for achievements.
	 *
	 * @return The number of regular games completed by the user.
	 */
	private int getNumRegularGamesCompleted() {
		return regularGamesCompleted;
	}

	/**
	 * Retrieves the number of time trials completed by the user for achievements.
	 *
	 * @return The number of time trials completed by the user.
	 */
	private int getNumTimeTrialsCompleted() {
		return timeTrialsCompleted;
	}

	/**
	 * Retrieves the number of games completed with no mistakes by the user for
	 * achievements.
	 *
	 * @return The number of games completed with no mistakes by the user.
	 */
	private int getNumNoMistakesCompleted() {
		return noMistakesCompleted;
	}

	/**
	 * Retrieves the number of time trials completed under the time limit by the
	 * user for achievements.
	 *
	 * @return The number of time trials completed under the time limit by the user.
	 */
	private int getNumTimeTrialsUnderTimeCompleted() {
		return timeTrialsUnderTimeCompleted;
	}

	/**
	 * Increments the count of regular games completed by the user.
	 */
	public void incrementRegularGamesCompleted() {
		regularGamesCompleted++;
	}

	/**
	 * Increments the count of time trials completed by the user.
	 */
	public void incrementTimeTrialsCompleted() {
		timeTrialsCompleted++;
	}

	/**
	 * Increments the count of games completed with no mistakes by the user.
	 */
	public void incrementNoMistakesCompleted() {
		noMistakesCompleted++;
	}

	/**
	 * Increments the count of time trials completed under the time limit by the
	 * user.
	 */
	public void incrementTimeTrialsUnderTimeCompleted() {
		timeTrialsUnderTimeCompleted++;
	}
}
