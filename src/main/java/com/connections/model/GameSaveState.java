package com.connections.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.CircleRowPane;
import com.connections.view_controller.GameSession;
import com.connections.view_controller.GameSessionContext;
import com.connections.view_controller.TileGridWord;
import com.connections.web.DatabaseFormattable;
import com.connections.web.WebUtils;

/**
 * Represents the save state of a game, including game progress, grid words, guesses, and other game-related information.
 */
public class GameSaveState implements DatabaseFormattable {
    public static final String KEY_GAME_FINISHED = "is_game_finished";
    public static final String KEY_GRID_WORDS = "grid_words";
    public static final String KEY_GUESSES = PlayedGameInfo.KEY_GUESSES;
    public static final String KEY_HINTS_LEFT_COUNT = "hints_left_count";
    public static final String KEY_MISTAKES_LEFT_COUNT = "mistakes_left_count";
    public static final String KEY_GAME_TYPE = PlayedGameInfo.KEY_GAME_TYPE;
    public static final String KEY_PUZZLE_NUMBER = PlayedGameInfo.KEY_PUZZLE_NUMBER;
    public static final String KEY_GAME_START_TIME = PlayedGameInfo.KEY_GAME_START_TIME;
    public static final String KEY_SAVE_STATE_CREATION_TIME = "save_state_creation_time";

    protected boolean gameFinished;
    protected GameSession.GameType gameType;
    protected int hintsLeft;
    protected int mistakesLeft;
    protected int puzzleNumber;
    protected List<List<Word>> grid;
    protected List<Set<Word>> guesses;
    protected ZonedDateTime gameStartTime;
    protected ZonedDateTime saveStateCreationTime;

    /**
     * Constructs a GameSaveState from the specified game components and information.
     *
     * @param tileGridWord         the TileGridWord object representing the game grid
     * @param hintsPane            the CircleRowPane object representing the hints
     * @param mistakesPane         the CircleRowPane object representing the mistakes
     * @param gameSessionContext   the GameSessionContext object
     * @param gameFinished         indicates whether the game is finished
     * @param timeCompleted        the time taken to complete the game
     * @param gameType             the type of the game
     * @param gameStartTime        the start time of the game
     */
    public GameSaveState(TileGridWord tileGridWord, CircleRowPane hintsPane, CircleRowPane mistakesPane,
                         GameSessionContext gameSessionContext, boolean gameFinished, int timeCompleted,
                         GameSession.GameType gameType, ZonedDateTime gameStartTime) {
        this.gameFinished = gameFinished;
        this.gameType = gameType;
        this.grid = tileGridWord.getGridAsWords();
        this.guesses = tileGridWord.getGuesses();
        this.puzzleNumber = gameSessionContext.getGameData().getPuzzleNumber();
        this.hintsLeft = hintsPane.getNumCircles();
        this.mistakesLeft = mistakesPane.getNumCircles();
        this.gameStartTime = gameStartTime;
        this.saveStateCreationTime = ZonedDateTime.now();
    }

    /**
     * Constructs a GameSaveState from a MongoDB Document.
     *
     * @param doc the MongoDB Document containing the game save state data
     */
    public GameSaveState(Document doc) {
        loadFromDatabaseFormat(doc);
    }

    /**
     * Converts the game grid to a MongoDB Document format.
     *
     * @param grid the game grid as a list of lists of Word objects
     * @return the MongoDB Document representation of the game grid
     */
    public static List<List<Document>> getGridAsDatabaseFormat(List<List<Word>> grid) {
        if (grid == null) {
            grid = new ArrayList<>();
        }

        List<List<Document>> gridDocList = new ArrayList<>();

        for (List<Word> row : grid) {
            List<Document> rowDocList = new ArrayList<>();

            if (row != null && row.size() > 0) {
                for (Word word : row) {
                    rowDocList.add(word.getAsDatabaseFormat());
                }
            }
            gridDocList.add(rowDocList);
        }

        return gridDocList;
    }

    /**
     * Loads the game grid from a MongoDB Document format.
     *
     * @param gridDocList the MongoDB Document representation of the game grid
     * @return the game grid as a list of lists of Word objects
     */
    public static List<List<Word>> loadGridFromDatabaseFormat(List<List<Document>> gridDocList) {
        List<List<Word>> grid = new ArrayList<>();

        if (gridDocList == null) {
            return grid;
        }

        for (List<Document> row : gridDocList) {
            List<Word> rowWordList = new ArrayList<>();

            for (Document wordDoc : row) {
                rowWordList.add(new Word(wordDoc));
            }

            grid.add(rowWordList);
        }

        return grid;
    }

    /**
     * Returns whether the game is finished.
     *
     * @return true if the game is finished, false otherwise
     */
    public boolean isGameFinished() {
        return gameFinished;
    }

    /**
     * Returns the type of the game.
     *
     * @return the game type
     */
    public GameSession.GameType getGameType() {
        return gameType;
    }

    /**
     * Returns the number of hints left.
     *
     * @return the number of hints left
     */
    public int getHintsLeft() {
        return hintsLeft;
    }

    /**
     * Returns the number of mistakes left.
     *
     * @return the number of mistakes left
     */
	public int getMistakesLeft() {
		return mistakesLeft;
	}

	/**
     * Returns the puzzle number.
     *
     * @return the puzzle number
     */
    public int getPuzzleNumber() {
        return puzzleNumber;
    }

    /**
     * Returns the game grid as a list of lists of Word objects.
     *
     * @return the game grid
     */
    public List<List<Word>> getGrid() {
        return grid;
    }

    /**
     * Returns the list of guesses as sets of Word objects.
     *
     * @return the list of guesses
     */
    public List<Set<Word>> getGuesses() {
        return guesses;
    }

    /**
     * Returns the start time of the game.
     *
     * @return the game start time
     */
    public ZonedDateTime getGameStartTime() {
        return gameStartTime;
    }

    /**
     * Returns the creation time of the save state.
     *
     * @return the save state creation time
     */
    public ZonedDateTime getSaveStateCreationTime() {
        return saveStateCreationTime;
    }

    /**
     * Converts the GameSaveState to a MongoDB Document format.
     *
     * @return the MongoDB Document representation of the GameSaveState
     */
    @Override
    public Document getAsDatabaseFormat() {
        Document doc = new Document();
        doc.append(KEY_GAME_FINISHED, gameFinished);
        doc.append(KEY_GAME_TYPE, gameType.toString().toLowerCase());
        doc.append(KEY_GRID_WORDS, getGridAsDatabaseFormat(grid));
        doc.append(KEY_GUESSES, PlayedGameInfo.getGuessesAsDatabaseFormat(guesses));
        doc.append(KEY_HINTS_LEFT_COUNT, hintsLeft);
        doc.append(KEY_MISTAKES_LEFT_COUNT, mistakesLeft);
        doc.append(KEY_PUZZLE_NUMBER, puzzleNumber);
        doc.append(KEY_GAME_START_TIME, WebUtils.helperDateToString(gameStartTime));
        doc.append(KEY_SAVE_STATE_CREATION_TIME, WebUtils.helperDateToString(saveStateCreationTime));

        return doc;
    }

    /**
     * Loads the GameSaveState from a MongoDB Document.
     *
     * @param doc the MongoDB Document containing the GameSaveState
     */
    @SuppressWarnings("unchecked")
    @Override
    public void loadFromDatabaseFormat(Document doc) {
        hintsLeft = doc.getInteger(KEY_HINTS_LEFT_COUNT, -1);
        mistakesLeft = doc.getInteger(KEY_MISTAKES_LEFT_COUNT, -1);
        gameFinished = doc.getBoolean(KEY_GAME_FINISHED, false);
        gameStartTime = WebUtils.helperStringToDate(doc.getString(KEY_GAME_START_TIME));
        puzzleNumber = doc.getInteger(KEY_PUZZLE_NUMBER, -1);
        saveStateCreationTime = WebUtils.helperStringToDate(doc.getString(KEY_SAVE_STATE_CREATION_TIME));

        String gameTypeString = doc.getString(KEY_GAME_TYPE);
        if (gameTypeString == null) {
            gameType = GameSession.GameType.NONE;
        } else {
            gameType = GameSession.GameType.valueOf(gameTypeString.toUpperCase());
        }

        guesses = new ArrayList<>();
        Object guessesRetrieved = doc.get(KEY_GUESSES);
        if (guessesRetrieved != null) {
            guesses = PlayedGameInfo.loadGuessesFromDatabaseFormat((List<List<Document>>) guessesRetrieved);
        }

        grid = new ArrayList<>();
        Object gridRetrieved = doc.get(KEY_GRID_WORDS);
        if (gridRetrieved != null) {
            grid = loadGridFromDatabaseFormat((List<List<Document>>) gridRetrieved);
        }
    }
}