package com.connections.model;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.connections.web.DatabaseFormattable;

/**
 * Represents a collection of game data, including a list of GameData objects.
 */
public class GameDataCollection implements DatabaseFormattable {
    public final static String KEY_GAMES_LIST = "games";
    public final static int WORDS_PER_COLOR = 4;
    public final static int COLOR_COUNT = 4;
    private List<GameData> gameList;

    /**
     * Constructs a GameDataCollection with the specified file path.
     *
     * @param filePath the file path of the game data
     */
    public GameDataCollection(String filePath) {
        gameList = new ArrayList<>();
    }

    /**
     * Returns the list of GameData objects.
     *
     * @return the list of GameData objects
     */
    public List<GameData> getGameList() {
        return gameList;
    }

    /**
     * Converts the GameDataCollection to a MongoDB Document format.
     *
     * @return the MongoDB Document representation of the GameDataCollection
     */
    @Override
    public Document getAsDatabaseFormat() {
        Document doc = new Document();
        List<Document> gameDataDocList = new ArrayList<>();
        for (GameData gameData : gameList) {
            gameDataDocList.add(gameData.getAsDatabaseFormat());
        }
        doc.append(KEY_GAMES_LIST, gameDataDocList);
        return doc;
    }

    /**
     * Loads the GameDataCollection from a MongoDB Document.
     *
     * @param doc the MongoDB Document containing the GameDataCollection
     */
    @Override
    public void loadFromDatabaseFormat(Document doc) {
        gameList = new ArrayList<>();
        List<Document> gameDataDocList = doc.getList(KEY_GAMES_LIST, Document.class);
        for (Document gameDataDoc : gameDataDocList) {
            gameList.add(new GameData(gameDataDoc));
        }
    }
}