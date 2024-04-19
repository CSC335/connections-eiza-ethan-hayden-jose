package com.connections.web;

public interface ModularWeb {
	void setWebContext(WebContext webContext);
	WebContext getWebContext();
	void readFromDatabase();
	void writeToDatabase();
	boolean existsInDatabase();
	void removeFromDatabase();
}
