package com.connections.web;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class WebUserAccount extends WebUser implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
	public static final String KEY_USER_NAME = "username";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASS_WORD = "password";
	public static final String KEY_BIO = "bio";

	protected String userName;
	protected String email;
	protected String passWord;
	protected String bio;

	// it will NOT automatically write to the database
	public WebUserAccount(WebContext webContext, String userName, String email, String passWord, String bio) {
		super(webContext);
		this.userName = userName;
		this.email = email;
		this.passWord = passWord;
		this.bio = bio;
		setUserID(generateUnusedUserID(webContext));
	}

	public WebUserAccount(WebContext webContext, Document doc) {
		super(webContext, doc);
	}

	public WebUserAccount(WebContext webContext, String userID) {
		super(webContext, userID);
	}

	@Override
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	@Override
	public UserType getType() {
		return UserType.ACCOUNT;
	}

	public static boolean checkAccountCredentialsMatch(WebContext webContext, String email, String passWord) {
		Document findByDoc = new Document();
		findByDoc.append(KEY_EMAIL, email);
		findByDoc.append(KEY_PASS_WORD, passWord);
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, findByDoc);
	}

	public static WebUserAccount getUserAccountByCredentials(WebContext webContext, String email, String passWord) {
		Document findByDoc = new Document();
		findByDoc.append(KEY_EMAIL, email);
		findByDoc.append(KEY_PASS_WORD, passWord);

		Document userInfoDoc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_ACCOUNT, findByDoc);
		if(userInfoDoc == null) {
			return null;
		}

		String userID = userInfoDoc.getString(KEY_USER_ID);
		if(userID == null) {
			return null;
		}

		return new WebUserAccount(webContext, userID);
	}

	public static boolean checkAccountExistsByEmail(WebContext webContext, String email) {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_EMAIL, email);
	}

	public static boolean checkAccountExistsByUserName(WebContext webContext, String userName) {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_NAME, userName);
	}

	public static List<WebUserAccount> getAllAccounts(WebContext webContext) {
		MongoCollection<Document> collection = webContext.getMongoDatabase()
				.getCollection(WebUtils.COLLECTION_ACCOUNT);
		FindIterable<Document> results = collection.find();

		List<WebUserAccount> list = new ArrayList<>();
		for (Document doc : results) {
			list.add(new WebUserAccount(webContext, doc));
		}
		return list;
	}

	@Override
	public Document getAsDatabaseFormat() {
		Document doc = super.getAsDatabaseFormat();
		doc.append(KEY_USER_NAME, userName);
		doc.append(KEY_EMAIL, email);
		doc.append(KEY_PASS_WORD, passWord);
		doc.append(KEY_BIO, bio);
		return doc;
	}

	@Override
	public void loadFromDatabaseFormat(Document doc) {
		super.loadFromDatabaseFormat(doc);
		userName = doc.getString(KEY_USER_NAME);
		email = doc.getString(KEY_EMAIL);
		passWord = doc.getString(KEY_PASS_WORD);
		bio = doc.getString(KEY_BIO);
	}

	@Override
	public void readFromDatabase() {
		Document doc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_ID, userID);
		if(doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}

	@Override
	public void writeToDatabase() {
		WebUtils.helperCollectionUpdate(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_ID, userID, getAsDatabaseFormat());
	}

	@Override
	public boolean existsInDatabase() {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_ID, getUserID());
	}

	@Override
	public void removeFromDatabase() {
		WebUtils.helperCollectionDelete(webContext, WebUtils.COLLECTION_ACCOUNT, KEY_USER_ID, getUserID());
	}
}
