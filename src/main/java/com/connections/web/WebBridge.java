package com.connections.web;

import java.util.UUID;

import org.bson.Document;

import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;

import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class WebBridge {
	public static final String DATABASE_NAME = "connections_db";

	public static final String COLLECTION_SERVER_STATUS = "server_status";
	public static final String COLLECTION_SESSION_ID_NAME = "session_id";
	public static final String COLLECTION_ACCOUNT = "account";
	public static final String COLLECTION_GUEST = "guest";
	
	public static final String KEY_IS_SERVER_INIT = "is_server_init";
	public static final String KEY_CURRENT_PUZZLE_NUMBER = "today_puzzle_number";

	public static final String[] COLLECTIONS = { COLLECTION_SESSION_ID_NAME, COLLECTION_ACCOUNT, COLLECTION_GUEST };

	
	// NOTE FOR COLLECTION_SESSION_ID_NAME:
	// > For accounts, the session ID is paired with the user name of the account
	// > For guests, the session ID is paired with the guest ID of the guest

	public static boolean helperCollectionContains(MongoCollection<Document> collection, String queryKey, Object queryValue) {
		return helperResultsNotEmpty(collection.find(new Document(queryKey, queryValue)));
	}

	public static boolean helperCollectionContains(MongoCollection<Document> collection, Document query) {
		return helperResultsNotEmpty(collection.find(query));
	}
	
	public static boolean helperCollectionContains(WebContext webContext, String collectionName, String queryKey, Object queryValue) {
		return helperCollectionContains(webContext.getMongoDatabase().getCollection(collectionName), queryKey, queryValue);
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

	public static boolean helperResultsNotEmpty(FindIterable<Document> iter) {
		for (Document document : iter) {
			return true;
		}
		return false;
	}

	public static boolean checkDatabaseInit(WebContext webContext) {
		return helperCollectionContains(webContext, COLLECTION_SERVER_STATUS, KEY_IS_SERVER_INIT, true);
	}
	
	// RECOMMENDED to use this method over calling drop directly because it will
	// re-initialize constant values like the archived game data and its words,
	// answers, etc.
	public static void initDatabase(WebContext webContext) {
		webContext.getMongoDatabase().drop();
		
		helperCollectionPut(webContext, COLLECTION_SERVER_STATUS, KEY_IS_SERVER_INIT, true);
	}

	public static void helperCollectionDrop(WebContext webContext, String collectionName) {
		webContext.getMongoDatabase().getCollection(collectionName).drop();
	}

	public static void updateUniqueEntry(WebContext webContext, String collectionName, String key, String value,
			Document newDoc) {
		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(collectionName);
		Document findCriteria = new Document(key, value);
		
			System.out.printf("updateUniqueEntry matched criteria\n");
			Document updateCriteria = new Document("$set", newDoc);

			UpdateOptions options = new UpdateOptions();
			options.upsert(true);

			collection.updateOne(findCriteria, updateCriteria, options);
	}

	public static Document getUniqueEntry(WebContext webContext, String collectionName, String key, String value) {
		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(collectionName);
		Document findCriteria = new Document(key, value);
		return collection.find(findCriteria).first();
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
		if(cookieContains(webContext, key)) {
			webContext.getWebAPI().deleteCookie(key);
		}
	}

	public static ObservableMap<String, String> cookieGetMap(WebContext webContext) {
		return webContext.getWebAPI().getCookies();
	}
}
