package com.connections.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bson.Document;

import com.connections.model.DifficultyColor;
import com.connections.model.GameAnswerColor;
import com.connections.model.GameData;
import com.connections.model.PlayedGameInfo;
import com.connections.model.PlayedGameInfoClassic;
import com.connections.model.Word;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import javafx.collections.ObservableMap;

/**
 * A collection of methods that help perform common operations for the MongoDB
 * database and cookies within the web browser.
 */
public class WebUtils {
	public static final String GAMES_FILE_PATH = "nyt-connections-games.json";
	public static final String DATABASE_NAME = "connections_db";

	public static final String COLLECTION_SERVER_STATUS = "server_status";
	public static final String COLLECTION_GAMES = "games";
	public static final String COLLECTION_SESSION_ID_NAME = "session_id";
	public static final String COLLECTION_ACCOUNT = "account";
	public static final String COLLECTION_GUEST = "guest";

	public static final String NULL_AS_STRING = "NULL";
	public static final String KEY_IS_SERVER_INIT = "is_server_init";
	public static final String KEY_LAST_PUZZLE_DATE = "last_puzzle_date";
	public static final String KEY_CURRENT_PUZZLE_NUMBER = "today_puzzle_number";
	public static final String KEY_MIN_PUZZLE_NUMBER = "min_puzzle_number";
	public static final String KEY_MAX_PUZZLE_NUMBER = "max_puzzle_number";
	public static final String KEY_DEBUG_MODE = "debug_mode";

	public static final String[] COLLECTIONS = { COLLECTION_SERVER_STATUS, COLLECTION_GAMES, COLLECTION_SESSION_ID_NAME,
			COLLECTION_ACCOUNT, COLLECTION_GUEST };

	/**
	 * Checks if a MongoDB collection contains a document with the specified query
	 * key and value.
	 * 
	 * @param collection The MongoDB collection to search in
	 * @param queryKey   The key to query for
	 * @param queryValue The value to match against the query key
	 * @return true if the collection contains a document matching the query, false
	 *         otherwise
	 */
	public static boolean helperCollectionContains(MongoCollection<Document> collection, String queryKey,
			Object queryValue) {
		return helperResultsNotEmpty(collection.find(new Document(queryKey, queryValue)));
	}

	/**
	 * Checks if a MongoDB collection contains a document matching the specified
	 * query.
	 * 
	 * @param collection The MongoDB collection to search in
	 * @param query      The query document specifying the search criteria
	 * @return true if the collection contains a document matching the query, false
	 *         otherwise
	 */
	public static boolean helperCollectionContains(MongoCollection<Document> collection, Document query) {
		return helperResultsNotEmpty(collection.find(query));
	}

	/**
	 * Checks if a MongoDB collection in the specified database contains a document
	 * with the specified query key and value.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to search in
	 * @param queryKey       The key to query for
	 * @param queryValue     The value to match against the query key
	 * @return true if the collection contains a document matching the query, false
	 *         otherwise
	 */
	public static boolean helperCollectionContains(WebContext webContext, String collectionName, String queryKey,
			Object queryValue) {
		return helperCollectionContains(webContext.getMongoDatabase().getCollection(collectionName), queryKey,
				queryValue);
	}

	/**
	 * Checks if a MongoDB collection in the specified database contains a document
	 * matching the specified query.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to search in
	 * @param query          The query document specifying the search criteria
	 * @return true if the collection contains a document matching the query, false
	 *         otherwise
	 */
	public static boolean helperCollectionContains(WebContext webContext, String collectionName, Document query) {
		return helperCollectionContains(webContext.getMongoDatabase().getCollection(collectionName), query);
	}

	/**
	 * Inserts a document into the specified MongoDB collection.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to insert into
	 * @param doc            The document to be inserted
	 */
	public static void helperCollectionPut(WebContext webContext, String collectionName, Document doc) {
		webContext.getMongoDatabase().getCollection(collectionName).insertOne(doc);
	}

	/**
	 * Inserts a key-value pair as a document into the specified MongoDB collection.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to insert into
	 * @param key            The key of the document to be inserted
	 * @param value          The value associated with the key
	 */
	public static void helperCollectionPut(WebContext webContext, String collectionName, String key, Object value) {
		helperCollectionPut(webContext, collectionName, new Document(key, value));
	}

	/**
	 * Deletes a document from the specified MongoDB collection.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to delete from
	 * @param doc            The document to be deleted
	 */
	public static void helperCollectionDelete(WebContext webContext, String collectionName, Document doc) {
		webContext.getMongoDatabase().getCollection(collectionName).deleteOne(doc);
	}

	/**
	 * Deletes a document with the specified key-value pair from the specified
	 * MongoDB collection.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to delete from
	 * @param key            The key of the document to be deleted
	 * @param value          The value associated with the key
	 */
	public static void helperCollectionDelete(WebContext webContext, String collectionName, String key, Object value) {
		webContext.getMongoDatabase().getCollection(collectionName).deleteOne(new Document(key, value));
	}

	/**
	 * Retrieves a document from the specified MongoDB collection based on the
	 * specified key-value pair.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to retrieve from
	 * @param findByKey      The key to search for
	 * @param findByValue    The value to match against the search key
	 * @return The found document, or null if no document matches the search
	 *         criteria
	 */
	public static Document helperCollectionGet(WebContext webContext, String collectionName, String findByKey,
			Object findByValue) {
		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(collectionName);
		Document findCriteria = new Document(findByKey, findByValue);
		return collection.find(findCriteria).first();
	}

	/**
	 * Retrieves a document from the specified MongoDB collection based on the
	 * specified search criteria.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to retrieve from
	 * @param findBy         The search criteria as a document
	 * @return The found document, or null if no document matches the search
	 *         criteria
	 */
	public static Document helperCollectionGet(WebContext webContext, String collectionName, Document findBy) {
		return webContext.getMongoDatabase().getCollection(collectionName).find(findBy).first();
	}

	/**
	 * Retrieves all documents from the specified MongoDB collection.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to retrieve from
	 * @return An iterable containing all the documents in the collection
	 */
	public static FindIterable<Document> helperCollectionGetAll(WebContext webContext, String collectionName) {
		return webContext.getMongoDatabase().getCollection(collectionName).find();
	}

	/**
	 * Retrieves a document from the specified MongoDB collection where the
	 * specified key exists.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to retrieve from
	 * @param findByKey      The key to search for
	 * @return The found document, or null if no document has the specified key
	 */
	public static Document helperCollectionGetByKey(WebContext webContext, String collectionName, String findByKey) {
		Document findBy = new Document(findByKey, new Document("$exists", true));
		return helperCollectionGet(webContext, collectionName, findBy);
	}

	/**
	 * Updates a document in the specified MongoDB collection based on the specified
	 * key-value pair.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to update
	 * @param findByKey      The key to search for
	 * @param findByValue    The value to match against the search key
	 * @param updateWith     The document containing the updated fields
	 */
	public static void helperCollectionUpdate(WebContext webContext, String collectionName, String findByKey,
			Object findByValue, Document updateWith) {
		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(collectionName);
		Document findCriteria = new Document(findByKey, findByValue);
		Document updateCriteria = new Document("$set", updateWith);
		UpdateOptions options = new UpdateOptions();
		options.upsert(true);
		collection.updateOne(findCriteria, updateCriteria, options);
	}

	/**
	 * Updates a document in the specified MongoDB collection based on the specified
	 * key.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the MongoDB collection to update
	 * @param findByKey      The key to search for
	 * @param updateWith     The updated value for the specified key
	 */
	public static void helperCollectionUpdateByKey(WebContext webContext, String collectionName, String findByKey,
			Object updateWith) {
		Document found = helperCollectionGetByKey(webContext, collectionName, findByKey);
		if (found != null) {
			MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(collectionName);
			Document findCriteria = new Document(findByKey, found.get(findByKey));
			Document modified = new Document(found);
			modified.put(findByKey, updateWith);
			Document updateCriteria = new Document("$set", modified);
			UpdateOptions options = new UpdateOptions();
			options.upsert(true);
			collection.updateOne(findCriteria, updateCriteria, options);
		}
	}

	/**
	 * Converts a ZonedDateTime object to a string representation in ISO format.
	 * 
	 * @param date The ZonedDateTime object to convert
	 * @return The string representation of the date in ISO format, or null if the
	 *         input is null
	 */
	public static String helperDateToString(ZonedDateTime date) {
		if (date == null) {
			return null;
		}
		return date.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

	/**
	 * Converts a string representation of a date in ISO format to a ZonedDateTime
	 * object.
	 * 
	 * @param dateString The string representation of the date in ISO format
	 * @return The ZonedDateTime object parsed from the string, or null if the input
	 *         is null
	 */
	public static ZonedDateTime helperStringToDate(String dateString) {
		if (dateString == null) {
			return null;
		}
		return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

	/**
	 * Checks if the specified iterable of MongoDB documents is not empty.
	 * 
	 * @param iter The iterable of documents to check
	 * @return true if the iterable contains at least one document, false otherwise
	 */
	public static boolean helperResultsNotEmpty(FindIterable<Document> iter) {
		for (Document document : iter) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the database has been initialized.
	 * 
	 * @param webContext The web context providing access to the MongoDB database
	 * @return true if the database has been initialized, false otherwise
	 */
	public static boolean checkDatabaseInit(WebContext webContext) {
		return helperCollectionContains(webContext, COLLECTION_SERVER_STATUS, KEY_IS_SERVER_INIT, true);
	}

	/**
	 * Clears the database by dropping all collections.
	 * 
	 * @param webContext The web context providing access to the MongoDB database
	 */
	public static void clearDatabase(WebContext webContext) {
		webContext.getMongoDatabase().drop();
	}

	/**
	 * Initializes the database by dropping all collections and inserting initial
	 * data.
	 * 
	 * @param webContext The web context providing access to the MongoDB database
	 */
	public static void initDatabase(WebContext webContext) {
		webContext.getMongoDatabase().drop();

		int minPuzzleNumber = Integer.MAX_VALUE;
		int maxPuzzleNumber = Integer.MIN_VALUE;

		try {
			String gamesJSON = new String(Files.readAllBytes(Paths.get(GAMES_FILE_PATH)));
			Document gamesDocument = Document.parse(gamesJSON);
			if (gamesDocument != null) {
				List<Document> gameDocumentList = gamesDocument.getList("games", Document.class);
				if (gameDocumentList != null && gameDocumentList.size() > 0) {
					for (Document gameDoc : gameDocumentList) {
						helperCollectionPut(webContext, COLLECTION_GAMES, gameDoc);

						int puzzleNumber = gameDoc.getInteger(GameData.KEY_PUZZLE_NUMBER, -1);

						if (puzzleNumber < minPuzzleNumber) {
							minPuzzleNumber = puzzleNumber;
						}
						if (puzzleNumber > maxPuzzleNumber) {
							maxPuzzleNumber = puzzleNumber;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		ZonedDateTime currentDateTime = ZonedDateTime.now();

		helperCollectionPut(webContext, COLLECTION_SERVER_STATUS, KEY_LAST_PUZZLE_DATE,
				helperDateToString(currentDateTime));
		helperCollectionPut(webContext, COLLECTION_SERVER_STATUS, KEY_CURRENT_PUZZLE_NUMBER, minPuzzleNumber);
		helperCollectionPut(webContext, COLLECTION_SERVER_STATUS, KEY_MIN_PUZZLE_NUMBER, minPuzzleNumber);
		helperCollectionPut(webContext, COLLECTION_SERVER_STATUS, KEY_MAX_PUZZLE_NUMBER, maxPuzzleNumber);
		helperCollectionPut(webContext, COLLECTION_SERVER_STATUS, KEY_IS_SERVER_INIT, true);
		helperCollectionPut(webContext, COLLECTION_SERVER_STATUS, KEY_DEBUG_MODE, false);
	}

	/**
	 * Drops the specified collection from the database.
	 * 
	 * @param webContext     The web context providing access to the MongoDB
	 *                       database
	 * @param collectionName The name of the collection to drop
	 */
	public static void helperCollectionDrop(WebContext webContext, String collectionName) {
		webContext.getMongoDatabase().getCollection(collectionName).drop();
	}

	public static List<GameData> gameGetAll(WebContext webContext) {
		List<GameData> list = new ArrayList<>();

		for (Document game : helperCollectionGetAll(webContext, COLLECTION_GAMES)) {
			list.add(new GameData(game));
		}

		return list;
	}

	/**
	 * Retrieves the game data for the specified puzzle number from the database.
	 * 
	 * @param webContext   The web context providing access to the MongoDB database
	 * @param puzzleNumber The puzzle number to search for
	 * @return The GameData object representing the game with the specified puzzle
	 *         number, or null if not found
	 */
	public static GameData gameGetByPuzzleNumber(WebContext webContext, int puzzleNumber) {
		Document searchBy = new Document(GameData.KEY_PUZZLE_NUMBER, puzzleNumber);
		Document gameDoc = helperCollectionGet(webContext, COLLECTION_GAMES, searchBy);

		if (gameDoc == null) {
			return null;
		}

		return new GameData(gameDoc);
	}

	/**
	 * Generates a random UUID as a general-purpose ID.
	 * 
	 * @return The generated UUID as a string
	 */
	public static String generateGeneralPurposeID() {
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString();
	}

	/**
	 * Gets the minimum puzzle number for the daily puzzle.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @return The minimum puzzle number for the daily puzzle, or -1 if not found.
	 */
	public static int dailyPuzzleNumberGetMin(WebContext webContext) {
		Document result = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS, KEY_MIN_PUZZLE_NUMBER);
		if (result != null && result.containsKey(KEY_MIN_PUZZLE_NUMBER)) {
			return result.getInteger(KEY_MIN_PUZZLE_NUMBER, -1);
		}
		return -1;
	}

	/**
	 * Gets the maximum puzzle number for the daily puzzle.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @return The maximum puzzle number for the daily puzzle, or -1 if not found.
	 */
	public static int dailyPuzzleNumberGetMax(WebContext webContext) {
		Document result = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS, KEY_MAX_PUZZLE_NUMBER);
		if (result != null && result.containsKey(KEY_MAX_PUZZLE_NUMBER)) {
			return result.getInteger(KEY_MAX_PUZZLE_NUMBER, -1);
		}
		return -1;
	}

	/**
	 * Gets the current puzzle number for the daily puzzle.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @return The current puzzle number for the daily puzzle, or -1 if not found.
	 */
	public static int dailyPuzzleNumberGet(WebContext webContext) {
		Document result = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS, KEY_CURRENT_PUZZLE_NUMBER);
		if (result != null && result.containsKey(KEY_CURRENT_PUZZLE_NUMBER)) {
			return result.getInteger(KEY_CURRENT_PUZZLE_NUMBER, -1);
		}
		return -1;
	}

	/**
	 * Rewinds the clock for the daily puzzle by the specified number of hours.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @param hours      The number of hours to rewind the clock.
	 */
	public static void dailyPuzzleNumberRewindClockHours(WebContext webContext, int hours) {
		Document prevDateDoc = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS, KEY_LAST_PUZZLE_DATE);
		if (prevDateDoc != null) {
			ZonedDateTime prevDate = helperStringToDate(prevDateDoc.getString(KEY_LAST_PUZZLE_DATE));
			ZonedDateTime newPrevDate = prevDate.minusHours(hours);
			helperCollectionUpdateByKey(webContext, COLLECTION_SERVER_STATUS, KEY_LAST_PUZZLE_DATE,
					helperDateToString(newPrevDate));
		}
	}

	/**
	 * Increments the daily puzzle number if needed based on the current date and
	 * time.
	 *
	 * @param webContext The WebContext associated with the request.
	 */
	public static void dailyPuzzleNumberIncrementIfNeeded(WebContext webContext) {
		Document prevDateDoc = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS, KEY_LAST_PUZZLE_DATE);
		if (prevDateDoc != null) {
			ZonedDateTime prevDate = helperStringToDate(prevDateDoc.getString(KEY_LAST_PUZZLE_DATE));
			ZonedDateTime prevDateRoundedToDay = prevDate.toLocalDate().atStartOfDay(prevDate.getZone());

			ZonedDateTime currentDate = ZonedDateTime.now();
			ZonedDateTime currentDateRoundedToDay = currentDate.toLocalDate().atStartOfDay(currentDate.getZone());

			long daysBetween = ChronoUnit.DAYS.between(prevDateRoundedToDay, currentDateRoundedToDay);

			while (daysBetween > 0) {
				dailyPuzzleNumberIncrement(webContext);
				daysBetween--;
			}
			helperCollectionUpdateByKey(webContext, COLLECTION_SERVER_STATUS, KEY_LAST_PUZZLE_DATE,
					helperDateToString(currentDate));
		}
	}

	/**
	 * Increments the daily puzzle number.
	 *
	 * @param webContext The WebContext associated with the request.
	 */
	public static void dailyPuzzleNumberIncrement(WebContext webContext) {
		Document minNumDoc = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS, KEY_MIN_PUZZLE_NUMBER);
		int minPuzzleNumber = (minNumDoc == null) ? -1 : minNumDoc.getInteger(KEY_MIN_PUZZLE_NUMBER, -1);

		Document currentNumDoc = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS,
				KEY_CURRENT_PUZZLE_NUMBER);
		int currentPuzzleNumber = (currentNumDoc == null) ? -1
				: currentNumDoc.getInteger(KEY_CURRENT_PUZZLE_NUMBER, -1);

		if (currentPuzzleNumber == -1) {
			if (minPuzzleNumber != -1) {
				helperCollectionPut(webContext, COLLECTION_SERVER_STATUS, KEY_CURRENT_PUZZLE_NUMBER, minPuzzleNumber);
			}
		} else {
			int nextPuzzleNumber = currentPuzzleNumber + 1;
			Document nextNumDoc = helperCollectionGet(webContext, COLLECTION_GAMES, GameData.KEY_PUZZLE_NUMBER,
					nextPuzzleNumber);
			int getNextPuzzleNumber = (nextNumDoc == null) ? -1 : nextNumDoc.getInteger(GameData.KEY_PUZZLE_NUMBER, -1);

			Document updateDoc = currentNumDoc;
			if (nextNumDoc != null && getNextPuzzleNumber == nextPuzzleNumber) {
				updateDoc = new Document(KEY_CURRENT_PUZZLE_NUMBER, nextPuzzleNumber);
			} else if (minPuzzleNumber != -1) {
				updateDoc = new Document(KEY_CURRENT_PUZZLE_NUMBER, minPuzzleNumber);
			}
			helperCollectionUpdate(webContext, COLLECTION_SERVER_STATUS, KEY_CURRENT_PUZZLE_NUMBER, currentPuzzleNumber,
					updateDoc);
		}
	}

	/**
	 * Checks if debug mode is enabled.
	 * 
	 * @param webContext The WebContext associated with the request.
	 * @return True if debug mode is enabled, false otherwise.
	 */
	public static boolean debugIsEnabled(WebContext webContext) {
		Document debugDoc = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS, KEY_DEBUG_MODE);
		if(debugDoc != null) {
			return debugDoc.getBoolean(KEY_DEBUG_MODE, false);
		}
		return false;
	}

	/**
	 * Checks if the user's cookie is empty.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @return True if the user's cookie is empty, false otherwise.
	 */
	public static boolean cookieIsEmpty(WebContext webContext) {
		return webContext.getWebAPI().getCookies().size() == 0;
	}

	/**
	 * Clears the user's cookie.
	 *
	 * @param webContext The WebContext associated with the request.
	 */
	public static void cookieClear(WebContext webContext) {
		ObservableMap<String, String> map = webContext.getWebAPI().getCookies();
		for (String key : map.keySet()) {
			webContext.getWebAPI().deleteCookie(key);
		}
	}

	/**
	 * Checks if the user's cookie contains the specified key.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @param key        The key to check for in the cookie.
	 * @return True if the user's cookie contains the specified key, false
	 *         otherwise.
	 */
	public static boolean cookieContains(WebContext webContext, String key) {
		return webContext.getWebAPI().getCookies().containsKey(key);
	}

	/**
	 * Gets the value associated with the specified key in the user's cookie.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @param key        The key to retrieve the value for.
	 * @return The value associated with the specified key in the user's cookie, or
	 *         null if the key is not found.
	 */
	public static String cookieGet(WebContext webContext, String key) {
		return webContext.getWebAPI().getCookies().get(key);
	}

	/**
	 * Sets the value for the specified key in the user's cookie.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @param key        The key to set the value for.
	 * @param value      The value to set for the specified key.
	 */
	public static void cookieSet(WebContext webContext, String key, String value) {
		webContext.getWebAPI().setCookie(key, value);
	}

	/**
	 * Removes the specified key from the user's cookie.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @param key        The key to remove from the cookie.
	 */
	public static void cookieRemove(WebContext webContext, String key) {
		if (cookieContains(webContext, key)) {
			webContext.getWebAPI().deleteCookie(key);
		}
	}

	/**
	 * Gets a map of all the key-value pairs in the user's cookie.
	 *
	 * @param webContext The WebContext associated with the request.
	 * @return A map of all the key-value pairs in the user's cookie.
	 */
	public static ObservableMap<String, String> cookieGetMap(WebContext webContext) {
		return webContext.getWebAPI().getCookies();
	}
}
