package com.connections.web;

import org.bson.Document;

public class WebBridgeUserGuest extends WebBridgeUser implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {
	// it will NOT automatically write to the database
	public WebBridgeUserGuest(WebContext webContext) {
		super(webContext);
		setUserID(generateUnusedUserID(webContext));
	}

	public WebBridgeUserGuest(WebContext webContext, Document doc) {
		super(webContext, doc);
	}

	public WebBridgeUserGuest(WebContext webContext, String userID) {
		super(webContext, userID);
	}

	public UserType getType() {
		return UserType.GUEST;
	}

	@Override
	public void writeToDatabase() {
		WebBridge.updateUniqueEntry(webContext, WebBridge.COLLECTION_GUEST, KEY_USER_ID, userID, getAsDatabaseFormat());
	}

	@Override
	public void readFromDatabase() {
		Document doc = WebBridge.getUniqueEntry(webContext, WebBridge.COLLECTION_GUEST, KEY_USER_ID, userID);
		if (doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}

	@Override
	public boolean existsInDatabase() {
		return WebBridge.helperCollectionContains(webContext, WebBridge.COLLECTION_GUEST, KEY_USER_ID, getUserID());
	}
	
	@Override
	public void removeFromDatabase() {
		WebBridge.helperCollectionDelete(webContext, WebBridge.COLLECTION_GUEST, KEY_USER_ID, getUserID()); 
	}
}