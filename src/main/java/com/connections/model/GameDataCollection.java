package com.connections.model;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.connections.web.DatabaseFormattable;

public class GameDataCollection implements DatabaseFormattable {
	public final static String KEY_GAMES_LIST = "games";
	public final static int WORDS_PER_COLOR = 4;
	public final static int COLOR_COUNT = 4;
	private List<GameData> gameList;
	
	public GameDataCollection(String filePath) {
		gameList = new ArrayList<>();
	}
	
	public List<GameData> getGameList() {
		return gameList;
	}

	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		List<Document> gameDataDocList = new ArrayList<>();
		for(GameData gameData : gameList) {
			gameDataDocList.add(gameData.getAsDatabaseFormat());
		}
		doc.append(KEY_GAMES_LIST, gameDataDocList);
		return doc;
	}

	@Override
	public void loadFromDatabaseFormat(Document doc) {
		gameList = new ArrayList<>();
		List<Document> gameDataDocList = doc.getList(KEY_GAMES_LIST, Document.class);
		for(Document gameDataDoc : gameDataDocList) {
			gameList.add(new GameData(gameDataDoc));
		}
	}
}
