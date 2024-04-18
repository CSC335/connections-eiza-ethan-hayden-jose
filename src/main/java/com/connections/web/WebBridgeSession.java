package com.connections.web;

public class WebBridgeSession implements ModularWeb {
	private String sessionID;
	private WebContext webContext;
	
	public enum UserType {
		NONE,
		ACCOUNT,
		GUEST,
	}
	
	public WebBridgeSession() {
		
	}
	
	// replaceoption	
	public void loginAccount() {
		
	}
	
	// replaceoption
	public void loginGuest() {
		
	}
	
	public static boolean checkUserExists(WebContext context, String userName, String email) {
		
	}
	
	public static boolean checkGuestExists(WebContext context, String guestID) {
		
	}
	
	public static boolean checkCredentials(WebContext context, String userName, String passWord) {
		
	}
	
	@Override
	public WebContext getWebContext() {
		return webContext;
	}
}
