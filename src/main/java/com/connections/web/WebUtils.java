package com.connections.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	public static final String KEY_CURRENT_PUZZLE_NUMBER = "today_puzzle_number";

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
			String findByValue) {
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

	public static void helperCollectionUpdate(WebContext webContext, String collectionName, String findByKey,
			String findByValue, Document updateWith) {
		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(collectionName);
		Document findCriteria = new Document(findByKey, findByValue);
		Document updateCriteria = new Document("$set", updateWith);
		UpdateOptions options = new UpdateOptions();
		options.upsert(true);
		collection.updateOne(findCriteria, updateCriteria, options);
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

		try {
			String gamesJSON = new String(Files.readAllBytes(Paths.get(GAMES_FILE_PATH)));
			Document gamesDocument = Document.parse(gamesJSON);
			if (gamesDocument != null) {
				List<Document> gameDocumentList = gamesDocument.getList("games", Document.class);
				if (gameDocumentList != null && gameDocumentList.size() > 0) {
					for (Document gameDoc : gameDocumentList) {
						helperCollectionPut(webContext, COLLECTION_GAMES, gameDoc);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		helperCollectionPut(webContext, COLLECTION_SERVER_STATUS, KEY_IS_SERVER_INIT, true);
	}

	public static void helperCollectionDrop(WebContext webContext, String collectionName) {
		webContext.getMongoDatabase().getCollection(collectionName).drop();
	}
	
	public static List<GameData> gameGetAll(WebContext webContext) {
		List<GameData> list = new ArrayList<>();
		
		for(Document game : helperCollectionGetAll(webContext, COLLECTION_GAMES)) {
			list.add(new GameData(game));
		}
		
		return list;
	}
	
	public static GameData gameGetByPuzzleNumber(WebContext webContext, int puzzleNumber) {
		Document searchBy = new Document(GameData.KEY_PUZZLE_NUMBER, puzzleNumber);
		Document gameDoc = helperCollectionGet(webContext, COLLECTION_GAMES, searchBy);
		
		if(gameDoc == null) {
			return null;
		}
		
		return new GameData(gameDoc);
	}

	public static String generateGeneralPurposeID() {
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString();
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
