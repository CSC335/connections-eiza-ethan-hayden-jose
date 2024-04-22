package com.connections.web;

import org.bson.Document;

public class WebUserGuest extends WebUser implements WebContextAccessible, DatabaseFormattable, DatabaseInteractable {

	public static final String GUEST_DEFAULT_USER_NAME = "Guest";
	
	// it will NOT automatically write to the database
	public WebUserGuest(WebContext webContext) {
		super(webContext);
		setUserID(generateUnusedUserID(webContext));
	}

	public WebUserGuest(WebContext webContext, Document doc) {
		super(webContext, doc);
	}

	public WebUserGuest(WebContext webContext, String userID) {
		super(webContext, userID);
	}
	
	@Override
	public String getUserName() {
		return GUEST_DEFAULT_USER_NAME;
	}

	@Override
	public UserType getType() {
		return UserType.GUEST;
	}

	@Override
	public void readFromDatabase() {
		Document doc = WebUtils.helperCollectionGet(webContext, WebUtils.COLLECTION_GUEST, KEY_USER_ID, userID);
		if (doc != null) {
			loadFromDatabaseFormat(doc);
		}
	}

	@Override
	public void writeToDatabase() {
		WebUtils.helperCollectionUpdate(webContext, WebUtils.COLLECTION_GUEST, KEY_USER_ID, userID,
				getAsDatabaseFormat());
	}

	@Override
	public boolean existsInDatabase() {
		return WebUtils.helperCollectionContains(webContext, WebUtils.COLLECTION_GUEST, KEY_USER_ID, getUserID());
	}

	@Override
	public void removeFromDatabase() {
		WebUtils.helperCollectionDelete(webContext, WebUtils.COLLECTION_GUEST, KEY_USER_ID, getUserID());
	}
}