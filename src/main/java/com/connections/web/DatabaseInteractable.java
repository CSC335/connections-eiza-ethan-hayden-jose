package com.connections.web;

public interface DatabaseInteractable {
	void readFromDatabase();
	void writeToDatabase();
	boolean existsInDatabase();
	void removeFromDatabase();
}
