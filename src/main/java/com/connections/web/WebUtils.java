package com.connections.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.connections.model.GameData;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import javafx.collections.ObservableMap;

public class WebUtils {
	public static final String GAMES_FILE_PATH = "nyt-connections-games.json";
	public static final String DATABASE_NAME = "connections_db";

	public static final String COLLECTION_SERVER_STATUS = "server_status";
	public static final String COLLECTION_GAMES = "games";
	public static final String COLLECTION_SESSION_ID_NAME = "session_id";
	public static final String COLLECTION_ACCOUNT = "account";
	public static final String COLLECTION_GUEST = "guest";

	public static final String KEY_IS_SERVER_INIT = "is_server_init";
	public static final String KEY_LAST_PUZZLE_DATE = "last_puzzle_date";
	public static final String KEY_CURRENT_PUZZLE_NUMBER = "today_puzzle_number";
	public static final String KEY_MIN_PUZZLE_NUMBER = "min_puzzle_number";
	public static final String KEY_MAX_PUZZLE_NUMBER = "max_puzzle_number";

	public static final String[] COLLECTIONS = { COLLECTION_SERVER_STATUS, COLLECTION_GAMES, COLLECTION_SESSION_ID_NAME,
			COLLECTION_ACCOUNT, COLLECTION_GUEST };

	public static boolean helperCollectionContains(MongoCollection<Document> collection, String queryKey,
			Object queryValue) {
		return helperResultsNotEmpty(collection.find(new Document(queryKey, queryValue)));
	}

	public static boolean helperCollectionContains(MongoCollection<Document> collection, Document query) {
		return helperResultsNotEmpty(collection.find(query));
	}

	public static boolean helperCollectionContains(WebContext webContext, String collectionName, String queryKey,
			Object queryValue) {
		return helperCollectionContains(webContext.getMongoDatabase().getCollection(collectionName), queryKey,
				queryValue);
	}

	public static boolean helperCollectionContains(WebContext webContext, String collectionName, Document query) {
		return helperCollectionContains(webContext.getMongoDatabase().getCollection(collectionName), query);
	}

	public static void helperCollectionPut(WebContext webContext, String collectionName, Document doc) {
		webContext.getMongoDatabase().getCollection(collectionName).insertOne(doc);
	}

	public static void helperCollectionPut(WebContext webContext, String collectionName, String key, Object value) {
		helperCollectionPut(webContext, collectionName, new Document(key, value));
	}

	public static void helperCollectionDelete(WebContext webContext, String collectionName, Document doc) {
		webContext.getMongoDatabase().getCollection(collectionName).deleteOne(doc);
	}

	public static void helperCollectionDelete(WebContext webContext, String collectionName, String key, Object value) {
		webContext.getMongoDatabase().getCollection(collectionName).deleteOne(new Document(key, value));
	}

	public static Document helperCollectionGet(WebContext webContext, String collectionName, String findByKey,
			Object findByValue) {
		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(collectionName);
		Document findCriteria = new Document(findByKey, findByValue);
		return collection.find(findCriteria).first();
	}

	public static Document helperCollectionGet(WebContext webContext, String collectionName, Document findBy) {
		return webContext.getMongoDatabase().getCollection(collectionName).find(findBy).first();
	}

	public static FindIterable<Document> helperCollectionGetAll(WebContext webContext, String collectionName) {
		return webContext.getMongoDatabase().getCollection(collectionName).find();
	}

	public static Document helperCollectionGetByKey(WebContext webContext, String collectionName, String findByKey) {
		Document findBy = new Document(findByKey, new Document("$exists", true));
		return helperCollectionGet(webContext, collectionName, findBy);
	}

	public static void helperCollectionUpdate(WebContext webContext, String collectionName, String findByKey,
			Object findByValue, Document updateWith) {
		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(collectionName);
		Document findCriteria = new Document(findByKey, findByValue);
		Document updateCriteria = new Document("$set", updateWith);
		UpdateOptions options = new UpdateOptions();
		options.upsert(true);
		collection.updateOne(findCriteria, updateCriteria, options);
	}

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

	public static String helperDateToString(ZonedDateTime date) {
		return date.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

	public static ZonedDateTime helperStringToDate(String dateString) {
		return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

	public static boolean helperResultsNotEmpty(FindIterable<Document> iter) {
		for (Document document : iter) {
			return true;
		}
		return false;
	}

	public static boolean checkDatabaseInit(WebContext webContext) {
		return helperCollectionContains(webContext, COLLECTION_SERVER_STATUS, KEY_IS_SERVER_INIT, true);
	}

	public static void clearDatabase(WebContext webContext) {
		webContext.getMongoDatabase().drop();
	}

	// RECOMMENDED to use this method over calling drop directly because it will
	// re-initialize constant values like the archived game data and its words,
	// answers, etc.
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
	}

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

	public static GameData gameGetByPuzzleNumber(WebContext webContext, int puzzleNumber) {
		Document searchBy = new Document(GameData.KEY_PUZZLE_NUMBER, puzzleNumber);
		Document gameDoc = helperCollectionGet(webContext, COLLECTION_GAMES, searchBy);

		if (gameDoc == null) {
			return null;
		}

		return new GameData(gameDoc);
	}

	public static String generateGeneralPurposeID() {
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString();
	}

	public static int dailyPuzzleNumberGet(WebContext webContext) {
		Document result = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS, KEY_CURRENT_PUZZLE_NUMBER);
		if (result != null && result.containsKey(KEY_CURRENT_PUZZLE_NUMBER)) {
			return result.getInteger(KEY_CURRENT_PUZZLE_NUMBER, -1);
		}
		return -1;
	}

	public static void dailyPuzzleNumberRewindClockHours(WebContext webContext, int hours) {
		Document prevDateDoc = helperCollectionGetByKey(webContext, COLLECTION_SERVER_STATUS, KEY_LAST_PUZZLE_DATE);
		if (prevDateDoc != null) {
			ZonedDateTime prevDate = helperStringToDate(prevDateDoc.getString(KEY_LAST_PUZZLE_DATE));
			ZonedDateTime newPrevDate = prevDate.minusHours(hours);
			helperCollectionUpdateByKey(webContext, COLLECTION_SERVER_STATUS, KEY_LAST_PUZZLE_DATE,
					helperDateToString(newPrevDate));
		}
	}
	
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
			helperCollectionUpdateByKey(webContext, COLLECTION_SERVER_STATUS, KEY_LAST_PUZZLE_DATE, helperDateToString(currentDate));
		}
	}

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

	public static boolean cookieIsEmpty(WebContext webContext) {
		return webContext.getWebAPI().getCookies().size() == 0;
	}

	public static void cookieClear(WebContext webContext) {
		ObservableMap<String, String> map = webContext.getWebAPI().getCookies();
		for (String key : map.keySet()) {
			webContext.getWebAPI().deleteCookie(key);
		}
	}

	public static boolean cookieContains(WebContext webContext, String key) {
		return webContext.getWebAPI().getCookies().containsKey(key);
	}

	public static String cookieGet(WebContext webContext, String key) {
		return webContext.getWebAPI().getCookies().get(key);
	}

	public static void cookieSet(WebContext webContext, String key, String value) {
		webContext.getWebAPI().setCookie(key, value);
	}

	public static void cookieRemove(WebContext webContext, String key) {
		if (cookieContains(webContext, key)) {
			webContext.getWebAPI().deleteCookie(key);
		}
	}

	public static ObservableMap<String, String> cookieGetMap(WebContext webContext) {
		return webContext.getWebAPI().getCookies();
	}
}
