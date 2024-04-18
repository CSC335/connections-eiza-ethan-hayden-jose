package com.connections.web;

import java.util.UUID;

import org.bson.Document;

import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;

import javafx.collections.ObservableMap;

public class WebBridge {
	public static final String DATABASE_NAME = "connectionsdb";

	public static final String COLLECTION_SESSION_ID_NAME = "sessionid";
	public static final String COLLECTION_ACCOUNT = "account";
	public static final String COLLECTION_GUEST = "guest";

	public static final String[] COLLECTIONS = { COLLECTION_SESSION_ID_NAME, COLLECTION_ACCOUNT, COLLECTION_GUEST };

	public static final String KEY_GUEST_ID = "guestid";
	public static final String KEY_SESSION_ID = "sessionid";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_EMAIL = "email";

	public static final String IS_SESSION_ID_FOR_GUEST = "guest";
	public static final String IS_SESSION_ID_FOR_ACCOUNT = "account";
	public static final String IS_SESSION_ID_FOR_NONE = "none";

	// NOTE FOR COLLECTION_SESSION_ID_NAME:
	// > For accounts, the session ID is paired with the user name of the account
	// > For guests, the session ID is paired with the guest ID of the guest

	public static boolean collectionContains(MongoCollection<Document> collection, String queryKey, String queryValue) {
		return notEmpty(collection.find(new Document(queryKey, queryValue)));
	}

	public static boolean collectionContains(MongoCollection<Document> collection, Document query) {
		return notEmpty(collection.find(query));
	}

	public static boolean notEmpty(FindIterable<Document> iter) {
		for (Document document : iter) {
			return true;
		}
		return false;
	}

	// RECOMMENDED to use this method over calling drop directly because it will
	// re-initialize constant values like the archived game data and its words,
	// answers, etc.
	public static void dropAllAndReInitialize(WebContext context) {
		context.getMongoDatabase().drop();
	}

	public static void dropCollection(WebContext context, String collectionName) {
		context.getMongoDatabase().getCollection(collectionName).drop();
	}
	
	public static void storeUniqueEntry(WebContext context, String collectionName, String key, String value, Document newDoc) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(collectionName);
		Document findCriteria = new Document(key, value);
		Document updateCriteria = new Document("$set", newDoc);
		
		UpdateOptions options = new UpdateOptions();
		options.upsert(true);
		
		collection.updateOne(findCriteria, updateCriteria, options);
	}
	
	public static Document loadUniqueEntry(WebContext context, String collectionName, String key, String value) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(collectionName);
		Document findCriteria = new Document(key, value);
		return collection.find(findCriteria).first();
	}

	public static FindIterable<Document> findGuestByID(WebContext context, String guestID) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_GUEST);
		return collection.find(new Document(KEY_GUEST_ID, guestID));
	}

	public static FindIterable<Document> findUserByName(WebContext context, String userName) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_ACCOUNT);
		return collection.find(new Document(KEY_USERNAME, userName));
	}

	public static FindIterable<Document> findUserByEmail(WebContext context, String email) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_ACCOUNT);
		return collection.find(new Document(KEY_EMAIL, email));
	}

	public static boolean checkSessionIDExists(WebContext context, String id) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
		return collectionContains(collection, KEY_SESSION_ID, id);
	}

	public static boolean checkGuestIDExists(WebContext context, String guestID) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_GUEST);
		return collectionContains(collection, KEY_GUEST_ID, guestID);
	}

	public static String generateGeneralPurposeID() {
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString();
	}

	public static String generateUnusedSessionID(WebContext context) {
		boolean unique = false;

		while (!unique) {
			String newID = generateGeneralPurposeID();
			if (!checkSessionIDExists(context, newID)) {
				unique = true;
				return newID;
			}
		}

		return null;
	}

	public static String generateUnusedGuestID(WebContext context) {
		boolean unique = false;

		while (!unique) {
			String newID = generateGeneralPurposeID();
			if (!checkGuestIDExists(context, newID)) {
				unique = true;
				return newID;
			}
		}

		return null;
	}

	// returns session ID
	public static String sessionAccountBegin(WebContext context, String userName, boolean replaceExistingSession,
			boolean setCookie) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
		Document userSearch = new Document(KEY_USERNAME, userName);
		FindIterable<Document> results = collection.find(userSearch);

		if (notEmpty(results)) {
			if (replaceExistingSession) {
				// delete all existing sessions
				collection.deleteMany(userSearch);
			} else {
//				throw new WebDatabaseException(String.format("User %s already has an existing session ID and told not to replace it", userName));
				return null;
			}
		}

		String newSessionID = generateUnusedSessionID(context);

		Document newSession = new Document();
		newSession.append(KEY_SESSION_ID, newSessionID);
		newSession.append(KEY_USERNAME, userName);

		if (setCookie) {
			cookiesSetSessionID(context, newSessionID);
		}

		collection.insertOne(newSession);
		return newSessionID;
	}

	// returns session ID
	public static String sessionGuestBegin(WebContext context, String guestID, boolean replaceExistingSession,
			boolean setCookie) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
		Document guestSearch = new Document(KEY_GUEST_ID, guestID);
		FindIterable<Document> results = collection.find(guestSearch);

		if (notEmpty(results)) {
			if (replaceExistingSession) {
				// delete all existing sessions
				collection.deleteMany(guestSearch);
			} else {
//				throw new WebDatabaseException(String.format("Guest %s already has an existing session ID and told not to replace it", guestID));
				return null;
			}
		}

		String newSessionID = generateUnusedSessionID(context);

		Document newSession = new Document();
		newSession.append(KEY_SESSION_ID, newSessionID);
		newSession.append(KEY_GUEST_ID, guestID);

		if (setCookie) {
			cookiesSetSessionID(context, newSessionID);
		}

		collection.insertOne(newSession);
		return newSessionID;
	}

	public static boolean sessionSignOut(WebContext context, boolean deleteCookie, boolean deleteGuestUser) {
		String sessionID = cookiesGetSessionID(context);

		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
		Document search = new Document(KEY_SESSION_ID, sessionID);
		FindIterable<Document> results = collection.find(search);

		if (notEmpty(results)) {
			Document session = results.first();

			// remove guest
			if (deleteGuestUser && session.containsKey(KEY_GUEST_ID)) {
				String guestID = session.getString(KEY_GUEST_ID);
				MongoCollection<Document> guestCollection = context.getMongoDatabase().getCollection(COLLECTION_GUEST);
				guestCollection.deleteOne(new Document(KEY_GUEST_ID, guestID));
			}

			collection.deleteOne(session);

			if (deleteCookie) {
				cookieRemoveSessionID(context);
			}

			return true;
		}

		return false;
	}
	
//	public static String currentGetSessionIDType(WebContext context) {
//		return checkSessionIDUserType(context, cookiesGetSessionID(context));
//	}
//
//	public static Document currentGetAccountData(WebContext context) {
//		String sessionID = cookiesGetSessionID(context);
//		String userName = currentGetAccountUserName(context);
//		if(userName == null) {
//			return null;
//		}
//		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_ACCOUNT);
//		FindIterable<Document> results = collection.find(new Document(KEY_USERNAME, userName));
//		return results.first();
//	}
//
//	public static String currentGetAccountUserName(WebContext context) {
//		String sessionID = cookiesGetSessionID(context);
//		if (checkSessionIDUserType(context, sessionID).equals(IS_SESSION_ID_FOR_ACCOUNT)) {
//			MongoCollection<Document> collectionSession = context.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
//			FindIterable<Document> resultsSession = collectionSession.find(new Document(KEY_SESSION_ID, sessionID));
//			return resultsSession.first().getString(KEY_USERNAME);
//		}
//		return null;
//	}
//
//	public static String currentGetAccountEmail(WebContext context) {
//		Document doc = currentGetAccountData(context);
//		if(doc == null) {
//			return null;
//		}
//		return doc.getString(KEY_EMAIL);
//	}
//
//	public static String currentGetAccountPassword(WebContext context) {
//		Document doc = currentGetAccountData(context);
//		if(doc == null) {
//			return null;
//		}
//		return doc.getString(KEY_PASSWORD);
//	}

	public static Document storeGuest(WebContext context, String guestID) {
		Document guest = new Document();
		guest.append(KEY_GUEST_ID, guestID);
		context.getMongoDatabase().getCollection(COLLECTION_GUEST).insertOne(guest);
		return guest;
	}

	public static Document storeAccount(WebContext context, String userName, String email, String passWord) {
		Document user = new Document();
		user.append(KEY_USERNAME, userName);
		user.append(KEY_EMAIL, email);
		user.append(KEY_PASSWORD, passWord);
		context.getMongoDatabase().getCollection(COLLECTION_ACCOUNT).insertOne(user);
		return user;
	}

	public static boolean checkAccountCredentialsMatch(WebContext context, String userName, String passWord) {
		Document userDoc = new Document();
		userDoc.append(KEY_USERNAME, userName);
		userDoc.append(KEY_PASSWORD, passWord);
		return collectionContains(context.getMongoDatabase().getCollection(COLLECTION_ACCOUNT), userDoc);
	}

	public static String checkSessionIDUserType(WebContext context, String sessionID) {
		MongoCollection<Document> collection = context.getMongoDatabase().getCollection(COLLECTION_SESSION_ID_NAME);
		Document sessionSearch = new Document(KEY_SESSION_ID, sessionID);
		FindIterable<Document> results = collection.find(sessionSearch);
		if (notEmpty(results)) {
			Document match = results.first();
			if (match.containsKey(KEY_GUEST_ID)) {
				return IS_SESSION_ID_FOR_GUEST;
			}
			if (match.containsKey(KEY_USERNAME)) {
				return IS_SESSION_ID_FOR_ACCOUNT;
			}
		}
		return IS_SESSION_ID_FOR_NONE;
	}

	public static boolean cookiesEmpty(WebContext context) {
		return context.getWebAPI().getCookies().size() == 0;
	}

	public static void cookiesClear(WebContext context) {
		ObservableMap<String, String> map = context.getWebAPI().getCookies();
		for (String key : map.keySet()) {
			context.getWebAPI().deleteCookie(key);
		}
	}

	public static String cookiesGetSessionID(WebContext context) {
		return context.getWebAPI().getCookies().get(KEY_SESSION_ID);
	}

	public static void cookiesSetSessionID(WebContext context, String sessionID) {
		context.getWebAPI().setCookie(KEY_SESSION_ID, sessionID);
	}

	public static void cookieRemoveSessionID(WebContext context) {
		context.getWebAPI().deleteCookie(KEY_SESSION_ID);
	}

	public static ObservableMap<String, String> cookiesGetMap(WebContext context) {
		return context.getWebAPI().getCookies();
	}
}
