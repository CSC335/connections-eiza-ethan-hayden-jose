package com.connections.web;

import java.util.UUID;

import org.bson.Document;

import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;

import javafx.collections.ObservableMap;

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

//	public static boolean checkSessionIDExists(WebContext webContext, String id) {
//		return helperCollectionContains(webContext, COLLECTION_SESSION_ID_NAME, WebBridgeSession.KEY_SESSION_ID, id);
//	}

	public static WebBridgeUser.UserType checkUserTypeByUserID(WebContext webContext, String userID) {
		if (helperCollectionContains(webContext, COLLECTION_ACCOUNT, WebBridgeUserAccount.KEY_USER_ID, userID)) {
			return WebBridgeUser.UserType.ACCOUNT;
		}

		if (helperCollectionContains(webContext, COLLECTION_GUEST, WebBridgeUserAccount.KEY_USER_ID, userID)) {
			return WebBridgeUser.UserType.GUEST;
		}

		return WebBridgeUser.UserType.NONE;
	} 

	public static String generateGeneralPurposeID() {
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString();
	}

	public static String generateUnusedSessionID(WebContext webContext) {
		boolean unique = false;

		while (!unique) {
			String newID = generateGeneralPurposeID();
			if (!checkSessionIDExists(webContext, newID)) {
				unique = true;
				return newID;
			}
		}

		return null;
	}

	public static String generateUnusedUserID(WebContext webContext) {
		boolean unique = false;

		while (!unique) {
			String newID = generateGeneralPurposeID();
			if (checkUserTypeByUserID(webContext, newID) == WebBridgeUser.UserType.NONE) {
				unique = true;
				return newID;
			}
		}

		return null;
	}

	public static String sessionBegin(WebContext webContext, String userID) {
		if(checkUserTypeByUserID(webContext, userID) == WebBridgeUser.UserType.NONE) {
			return null;
		}
		
		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
		String newSessionID = generateUnusedSessionID(webContext);

		Document newSession = new Document();
		newSession.append(WebBridgeSession.KEY_SESSION_ID, newSessionID);
		newSession.append(WebBridgeUser.KEY_USER_ID, userID);
		collection.insertOne(newSession);

		cookieSetSessionID(webContext, newSessionID);

		return newSessionID;
	}

	public static boolean sessionSignOut(WebContext webContext, boolean deleteGuestUser) {
		String sessionID = cookieGetSessionID(webContext);

		MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
		Document search = new Document(WebBridgeSession.KEY_SESSION_ID, sessionID);
		FindIterable<Document> results = collection.find(search);

		if (helperResultsNotEmpty(results)) {
			Document session = results.first();

			if (deleteGuestUser) {
				MongoCollection<Document> collectionGuest = webContext.getMongoDatabase().getCollection(COLLECTION_GUEST);
				String userID = session.getString(WebBridgeUser.KEY_USER_ID);
				collectionGuest.deleteOne(new Document(WebBridgeUser.KEY_USER_ID, userID));
			}
			collection.deleteOne(session);
			cookieRemoveSessionID(webContext);
			return true;
		}
		return false;
	}

	public static boolean checkAccountExistsByEmail(WebContext webContext, String email) {
		return helperCollectionContains(webContext, COLLECTION_ACCOUNT, WebBridgeUserAccount.KEY_EMAIL, email);
	}

	public static boolean checkAccountExistsByUserName(WebContext webContext, String userName) {
		return helperCollectionContains(webContext, COLLECTION_ACCOUNT, WebBridgeUserAccount.KEY_USER_NAME, userName);
	}

	public static boolean checkAccountCredentialsMatch(WebContext webContext, String userName, String passWord) {
		Document userDoc = new Document();
		userDoc.append(WebBridgeUserAccount.KEY_USER_NAME, userName);
		userDoc.append(WebBridgeUserAccount.KEY_PASS_WORD, passWord);
		return helperCollectionContains(webContext, COLLECTION_ACCOUNT, userDoc);
	}
	
//	public static Document storeGuest(WebContext, webContext, String userID) {
//		
//	}

//	public static Document storeGuest(WebContext webContext, String userID) {
//		Document guest = new Document();
//		guest.append(WebBridgeUser.KEY_USER_ID, userID);
//		webContext.getMongoDatabase().getCollection(COLLECTION_GUEST).insertOne(guest);
//		return guest;
//	}
//
//	public static Document storeAccount(WebContext webContext, String userName, String email, String passWord) {
//		Document user = new Document();
//		user.append(WebBridgeUserAccount.KEY_USER_NAME, userName);
//		user.append(WebBridgeUserAccount.KEY_EMAIL, email);
//		user.append(WebBridgeUserAccount.KEY_PASS_WORD, passWord);
//		webContext.getMongoDatabase().getCollection(COLLECTION_ACCOUNT).insertOne(user);
//		return user;
//	}

	public static WebBridgeUser.UserType checkuserTypeBySessionID(WebContext webContext, String sessionID) {
		MongoCollection<Document> collectionSession = webContext.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
		Document sessionSearch = new Document(WebBridgeSession.KEY_SESSION_ID, sessionID);
		FindIterable<Document> sessionResults = collectionSession.find(sessionSearch);
		if (helperResultsNotEmpty(sessionResults)) {
			Document matchedSession = sessionResults.first();
			String userID = matchedSession.getString(WebBridgeUser.KEY_USER_ID);
			
			if(userID != null) {
				return checkUserTypeByUserID(webContext, userID);
			}
		}
		return WebBridgeUser.UserType.NONE;
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
