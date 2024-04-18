package com.connections.web;

import org.bson.Document;

public class WebBridgeUserGuest extends WebBridgeUser implements ModularWeb, DatabaseFormattable {
	// it will NOT automatically write to the database
	public WebBridgeUserGuest(WebContext webContext) {
		super(webContext);
		setUserID(WebBridge.generateUnusedUserID(webContext));
	}

	public WebBridgeUserGuest(WebContext webContext, Document doc) {
		super(webContext, doc);
	}

	public WebBridgeUserGuest(WebContext webContext, String userID) {
		super(webContext, userID);
	}

	public WebBridgeSession.UserType getType() {
		return WebBridgeSession.UserType.GUEST;
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
}