package com.connections.web;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

/**
 * The WebUserGuest class represents a specific type of WebUser that only has a
 * unique user ID and no other unique information that can be used to identify
 * or authenticate the user.
 */
public class WebUserGuest extends WebUser implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
	public static final String GUEST_DEFAULT_USER_NAME = "Guest";
	public static final String GUEST_DEFAULT_EMAIL = "Guest";
	public static final String GUEST_DEFAULT_PASSWORD = "Guest";
	public static final String GUEST_DEFAULT_BIO = "Guest";

	/**
	 * Constructs a new WebUserGuest with the given WebContext.
	 *
	 * @param webContext the WebContext associated with the guest user
	 */
	public WebUserGuest(WebContext webContext) {
		super(webContext);
		setUserID(generateUnusedUserID(webContext));
	}

	/**
	 * Constructs a new WebUserGuest with the given WebContext and Document.
	 *
	 * @param webContext the WebContext associated with the guest user
	 * @param doc        the Document representing the guest user data
	 */
	public WebUserGuest(WebContext webContext, Document doc) {
		super(webContext, doc);
	}

	/**
	 * Constructs a new WebUserGuest with the given WebContext and user ID.
	 *
	 * @param webContext the WebContext associated with the guest user
	 * @param userID     the ID of the guest user
	 */
	public WebUserGuest(WebContext webContext, String userID) {
		super(webContext, userID);
	}

	/**
	 * Retrieves the username of the guest user.
	 *
	 * @return the username of the guest user
	 */
	@Override
	public String getUserName() {
		return GUEST_DEFAULT_USER_NAME;
	}

	/**
	 * Retrieves the email of the guest user.
	 *
	 * @return the email of the guest user
	 */
	@Override
	public String getEmail() {
		return GUEST_DEFAULT_EMAIL;
	}

	/**
	 * Retrieves the password of the guest user.
	 *
	 * @return the password of the guest user
	 */
	@Override
	public String getPassWord() {
		return GUEST_DEFAULT_PASSWORD;
	}

	/**
	 * Retrieves the bio of the guest user.
	 *
	 * @return the bio of the guest user
	 */
	@Override
	public String getBio() {
		return GUEST_DEFAULT_BIO;
	}

	/**
	 * Retrieves the type of the guest user.
	 *
	 * @return the type of the guest user
	 */
	@Override
	public UserType getType() {
		return UserType.GUEST;
	}

	/**
	 * Reads the guest user data from the database.
	 */
	@Override
	public void readFromDatabase() {
		Document doc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_GUEST, KEY_USER_ID, userID);
		if (doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}

	/**
	 * Writes the guest user data to the database.
	 */
	@Override
	public void writeToDatabase() {
		WebUtils.helperCollectionUpdate(webContext, WebUtils.COLLECTION_GUEST, KEY_USER_ID, userID,
				getAsDatabaseFormat());
	}

	/**
	 * Checks if the guest user exists in the database.
	 *
	 * @return true if the guest user exists in the database, false otherwise
	 */
	@Override
	public boolean existsInDatabase() {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_GUEST, KEY_USER_ID, getUserID());
	}

	/**
	 * Removes the guest user from the database.
	 */
	@Override
	public void removeFromDatabase() {
		WebUtils.helperCollectionDelete(webContext, WebUtils.COLLECTION_GUEST, KEY_USER_ID, getUserID());
	}

	/**
	 * Sets the username of the guest user.
	 *
	 * @param userName the new username of the guest user
	 */
	@Override
	public void setUserName(String userName) {
	}

	/**
	 * Sets the email of the guest user.
	 *
	 * @param email the new email of the guest user
	 */
	@Override
	public void setEmail(String userName) {
	}

	/**
	 * Sets the password of the guest user.
	 *
	 * @param password the new password of the guest user
	 */
	@Override
	public void setPassWord(String userName) {
	}

	/**
	 * Sets the bio of the guest user.
	 *
	 * @param bio the new bio of the guest user
	 */
	@Override
	public void setBio(String userName) {
	}
}