package com.connections.web;

import org.bson.Document;

/**
 * An interface representing an object that can be formatted as a JSON-like
 * Document representation, which is compatible with the MongoDB database. This
 * interface provides methods for converting the object to and from a Document
 * format, facilitating easy storage and retrieval of the object's data in the
 * MongoDB database.
 */
public interface DatabaseFormattable {
	/**
	 * Returns a JSON-like Document representation of the object that is compatible
	 * with the MongoDB database. This is used for writing to the database.
	 *
	 * @return Document representation of the object
	 */
	Document getAsDatabaseFormat();

	/**
	 * Takes in a JSON-like Document representation of the object and parses it,
	 * overwriting the state of the object to match the data in the Document. This
	 * is used when reading from the database.
	 *
	 * @param doc Document representation of the object
	 */
	void loadFromDatabaseFormat(Document doc);
}
