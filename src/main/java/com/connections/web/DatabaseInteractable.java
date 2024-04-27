package com.connections.web;

/**
 * An interface representing an object that can interact with a MongoDB
 * database. This interface provides methods for reading, writing, checking
 * existence, and removing the object's data from the database.
 */
public interface DatabaseInteractable {
	/**
	 * Updates the state and instance variables of the object based on its
	 * corresponding data in the MongoDB database. It may need to use some ID or
	 * unique identifier to be able to search for its entry in the MongoDB database
	 * and read from it.
	 */
	void readFromDatabase();

	/**
	 * Writes the state and instance variables of the object to its corresponding
	 * entry in the MongoDB database. It may need to use some ID or unique
	 * identifier to be able to search for its entry in the MongoDB database and
	 * write to it.
	 */
	void writeToDatabase();

	/**
	 * Returns true if the object has a corresponding entry in the MongoDB database.
	 * It may need to use some ID or unique identifier to be able to search for its
	 * entry in the MongoDB database to verify its existence.
	 * 
	 * @return true if it exists in the MongoDB database and false if otherwise
	 */
	boolean existsInDatabase();

	/**
	 * Removes the corresponding database entry of the object from the MongoDB
	 * database. It may need to use some ID or unique identifier to be able to
	 * search for its entry in the MongoDB database to remove it.
	 */
	void removeFromDatabase();
}
