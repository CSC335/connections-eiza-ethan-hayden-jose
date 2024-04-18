package com.connections.web;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class WebBridgeUserAccount extends WebBridgeUser implements ModularWeb, DatabaseFormattable {
	public static final String KEY_USER_NAME = "username";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASS_WORD = "password";
	public static final String KEY_BIO = "bio";

	protected String userName;
	protected String email;
	protected String passWord;
	protected String bio;
	
	// it will NOT automatically write to the database
	public WebBridgeUserAccount(WebContext webContext, String userName, String email, String passWord, String bio) {
		super(webContext);
		this.userName = userName;
		this.email = email;
		this.passWord = passWord;
		this.bio = bio;
		setUserID(WebBridge.generateUnusedUserID(webContext));
	}
	
	public WebBridgeUserAccount(WebContext webContext, Document doc) {
		super(webContext, doc);
	}

	public WebBridgeUserAccount(WebContext webContext, String userID) {
		super(webContext, userID);
	}

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
	
	public WebBridgeSession.UserType getType() {
		return WebBridgeSession.UserType.ACCOUNT;
	}

	public static List<WebBridgeUserAccount> getAllAccounts(WebContext webContext) {
		MongoCollection<Document> collection = webContext.getMongoDatabase()
				.getCollection(WebBridge.COLLECTION_ACCOUNT);
		FindIterable<Document> results = collection.find();

		List<WebBridgeUserAccount> list = new ArrayList<>();
		for (Document doc : results) {
			list.add(new WebBridgeUserAccount(webContext, doc));
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
	public void writeToDatabase() {
		WebBridge.updateUniqueEntry(webContext, WebBridge.COLLECTION_ACCOUNT, KEY_USER_ID, userID, getAsDatabaseFormat());
	}

	@Override
	public void readFromDatabase() {
		Document doc = WebBridge.getUniqueEntry(webContext, WebBridge.COLLECTION_ACCOUNT, KEY_USER_ID, userID);
		if(doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}
}
