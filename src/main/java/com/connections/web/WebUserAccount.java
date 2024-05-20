package com.connections.web;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

/**
 * The WebUserAccount class represents a specific type of WebUser that is
 * attached to a permanent account with more unique identifiers such as a
 * username, email, and password.
 */
public class WebUserAccount extends WebUser implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
	public static final String KEY_USER_NAME = "username";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASS_WORD = "password";
	public static final String KEY_BIO = "bio";

	protected String userName;
	protected String email;
	protected String passWord;
	protected String bio;

	/**
	 * Constructs a new WebUserAccount with the given WebContext, username, email,
	 * password, and bio. It will NOT automatically write to the database.
	 *
	 * @param webContext the WebContext associated with the user account
	 * @param userName   the username of the user account
	 * @param email      the email of the user account
	 * @param passWord   the password of the user account
	 * @param bio        the bio of the user account
	 */
	public WebUserAccount(WebContext webContext, String userName, String email, String passWord, String bio) {
		super(webContext);
		this.userName = userName;
		this.email = email;
		this.passWord = passWord;
		this.bio = bio;
		setUserID(generateUnusedUserID(webContext));
	}

	/**
	 * Constructs a new WebUserAccount with the given WebContext and Document.
	 *
	 * @param webContext the WebContext associated with the user account
	 * @param doc        the Document representing the user account data
	 */
	public WebUserAccount(WebContext webContext, Document doc) {
		super(webContext, doc);
	}

	/**
	 * Constructs a new WebUserAccount with the given WebContext and user ID.
	 *
	 * @param webContext the WebContext associated with the user account
	 * @param userID     the ID of the user account
	 */
	public WebUserAccount(WebContext webContext, String userID) {
		super(webContext, userID);
	}

	/**
	 * Retrieves the username of the user account.
	 *
	 * @return the username of the user account
	 */
	@Override
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the username of the user account.
	 *
	 * @param userName the new username of the user account
	 */
	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Retrieves the email of the user account.
	 *
	 * @return the email of the user account
	 */
	@Override
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email of the user account.
	 *
	 * @param email the new email of the user account
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Retrieves the password of the user account.
	 *
	 * @return the password of the user account
	 */
	@Override
	public String getPassWord() {
		return passWord;
	}

	/**
	 * Sets the password of the user account.
	 *
	 * @param passWord the new password of the user account
	 */
	@Override
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	/**
	 * Retrieves the bio of the user account.
	 *
	 * @return the bio of the user account
	 */
	@Override
	public String getBio() {
		return bio;
	}

	/**
	 * Sets the bio of the user account.
	 *
	 * @param bio the new bio of the user account
	 */
	@Override
	public void setBio(String bio) {
		this.bio = bio;
	}

	/**
	 * Retrieves the type of the user account.
	 *
	 * @return the type of the user account
	 */
	@Override
	public UserType getType() {
		return UserType.ACCOUNT;
	}

	/**
	 * Checks if the given account credentials match an existing user account.
	 *
	 * @param webContext the WebContext associated with the user account
	 * @param email      the email of the user account
	 * @param passWord   the password of the user account
	 * @return true if the account credentials match an existing user account, false
	 *         otherwise
	 */
	public static boolean checkAccountCredentialsMatch(WebContext webContext, String email, String passWord) {
		Document findByDoc = new Document();
		findByDoc.append(KEY_EMAIL, email);
		findByDoc.append(KEY_PASS_WORD, passWord);
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, findByDoc);
	}

	/**
	 * Retrieves the user account with the given account credentials.
	 *
	 * @param webContext the WebContext associated with the user account
	 * @param email      the email of the user account
	 * @param passWord   the password of the user account
	 * @return the user account with the given account credentials, or null if not
	 *         found
	 */
	public static WebUserAccount getUserAccountByCredentials(WebContext webContext, String email, String passWord) {
		Document findByDoc = new Document();
		findByDoc.append(KEY_EMAIL, email);
		findByDoc.append(KEY_PASS_WORD, passWord);
		Document userInfoDoc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_ACCOUNT, findByDoc);
		if (userInfoDoc == null) {
			return null;
		}
		String userID = userInfoDoc.getString(KEY_USER_ID);
		if (userID == null) {
			return null;
		}
		return new WebUserAccount(webContext, userID);
	}

	/**
	 * Checks if a user account with the given email exists.
	 *
	 * @param webContext the WebContext associated with the user account
	 * @param email      the email of the user account
	 * @return true if a user account with the given email exists, false otherwise
	 */
	public static boolean checkAccountExistsByEmail(WebContext webContext, String email) {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_EMAIL, email);
	}

	/**
	 * Checks if a user account with the given username exists.
	 *
	 * @param webContext the WebContext associated with the user account
	 * @param userName   the username of the user account
	 * @return true if a user account with the given username exists, false
	 *         otherwise
	 */
	public static boolean checkAccountExistsByUserName(WebContext webContext, String userName) {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_NAME, userName);
	}

	/**
	 * Retrieves all user accounts.
	 *
	 * @param webContext the WebContext associated with the user accounts
	 * @return a list of all user accounts
	 */
	public static List<WebUserAccount> getAllAccounts(WebContext webContext) {
		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(WebUtils.COLLECTION_ACCOUNT);
		FindIterable<Document> results = collection.find();
		List<WebUserAccount> list = new ArrayList<>();
		for (Document doc : results) {
			list.add(new WebUserAccount(webContext, doc));
		}
		return list;
	}

	/**
	 * Retrieves the user account data as a Document for database storage.
	 *
	 * @return the user account data as a Document for database storage
	 */
	@Override
	public Document getAsDatabaseFormat() {
		Document doc = super.getAsDatabaseFormat();
		doc.append(KEY_USER_NAME, userName);
		doc.append(KEY_EMAIL, email);
		doc.append(KEY_PASS_WORD, passWord);
		doc.append(KEY_BIO, bio);
		return doc;
	}

	/**
	 * Loads the user account data from a Document retrieved from the database.
	 *
	 * @param doc the Document containing the user account data
	 */
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		super.loadFromDatabaseFormat(doc);
		userName = doc.getString(KEY_USER_NAME);
		email = doc.getString(KEY_EMAIL);
		passWord = doc.getString(KEY_PASS_WORD);
		bio = doc.getString(KEY_BIO);
	}

	/**
	 * Reads the user account data from the database.
	 */
	@Override
	public void readFromDatabase() {
		Document doc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_ID, userID);
		if (doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}

	/**
	 * Writes the user account data to the database.
	 */
	@Override
	public void writeToDatabase() {
		WebUtils.helperCollectionUpdate(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_ID, userID,
				getAsDatabaseFormat());
	}

	/**
	 * Checks if the user account exists in the database.
	 *
	 * @return true if the user account exists in the database, false otherwise
	 */
	@Override
	public boolean existsInDatabase() {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_ID, getUserID());
	}

	/**
	 * Removes the user account from the database.
	 */
	@Override
	public void removeFromDatabase() {
		WebUtils.helperCollectionDelete(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_ID, getUserID());
	}
}
