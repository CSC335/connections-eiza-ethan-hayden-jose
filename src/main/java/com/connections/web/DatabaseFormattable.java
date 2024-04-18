package com.connections.web;

import org.bson.Document;

public interface DatabaseFormattable {
	Document getAsDatabaseFormat();
	void loadFromDatabaseFormat(Document doc);
}
