package com.connections.web;

import org.bson.Document;

public class WebBridgeUserGuest extends WebBridgeUser implements ModularWeb, DatabaseFormattable, DatabaseUnique {
	public WebBridgeUserGuest(WebContext webContext, Document doc) {
		super(webContext, doc);
	}
	
	public WebBridgeUserGuest(WebContext webContext, String userID) {
		super(webContext, userID);
	}
}