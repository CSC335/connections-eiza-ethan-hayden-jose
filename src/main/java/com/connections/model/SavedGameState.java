package com.connections.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.view_controller.CircleRowPane;
import com.connections.view_controller.GameSession;
import com.connections.view_controller.GameSessionContext;
import com.connections.view_controller.TileGridWord;
import com.connections.view_controller.GameSession.GameType;
import com.connections.web.DatabaseFormattable;
import com.connections.web.WebSession;
import com.connections.web.WebUtils;

public class SavedGameState implements DatabaseFormattable {
	public static final String KEY_GAME_FINISHED = "is_game_finished";
	public static final String KEY_GAME_TYPE = "game_type";
	public static final String KEY_GRID_WORDS = "grid_words";
	public static final String KEY_GUESSES = PlayedGameInfo.KEY_GUESSES;
	public static final String KEY_HINTS_LEFT_COUNT = "hints_left_count";
	public static final String KEY_LIST_COLORS_SOLVED = "list_colors_solved";
	public static final String KEY_MISTAKES_LEFT_COUNT = "mistakes_left_count";
	public static final String KEY_PUZZLE_NUMBER = PlayedGameInfo.KEY_PUZZLE_NUMBER;
	public static final String KEY_TIME_COMPLETED = PlayedGameInfo.KEY_TIME_COMPLETED;

	protected boolean gameFinished;
	protected GameSession.GameType gameType;
	protected int hintsLeft;
	protected List<DifficultyColor> listColorsSolved;
	protected int mistakesLeft;
	protected int puzzleNumber;
	protected List<List<Word>> grid;
	protected List<Set<Word>> guesses;
	protected int timeCompleted;

	public static List<List<Document>> getGridAsDatabaseFormat(List<List<Word>> grid) {
		List<List<Document>> gridDocList = new ArrayList<>();
		for (List<Word> row : grid) {
			List<Document> wordList = new ArrayList<>();
			for (Word word : row) {
				wordList.add(word.getAsDatabaseFormat());
			}
			gridDocList.add(wordList);
		}
		return gridDocList;
	}

	public static List<List<Word>> loadGridFromDatabaseFormat(List<List<Document>> gridDocList) {
		List<List<Word>> grid = new ArrayList<>();
		for (List<Document> row : gridDocList) {
			List<Word> wordList = new ArrayList<>();
			for (Document wordDoc : row) {
				wordList.add(new Word(wordDoc));
			}
			grid.add(wordList);
		}
		return grid;
	}

	public static Document getColorsAsDatabaseFormat(List<DifficultyColor> listColorsSolved) {
		Document colorListDoc = new Document();
		List<String> colorListAsString = new ArrayList<>();

		for (DifficultyColor color : listColorsSolved) {
			if (color == null) {
				colorListAsString.add(WebUtils.NULL_AS_STRING);
			} else {
				colorListAsString.add(color.toString().toLowerCase());
			}
		}

		colorListDoc.append(KEY_LIST_COLORS_SOLVED, colorListAsString);
		return colorListDoc;
	}

	public static List<DifficultyColor> loadColorsFromDatabaseFormat(Document wordSetList) {
		List<DifficultyColor> colorList = new ArrayList<>();
		List<String> colorListAsString = wordSetList.getList(colorList, String.class);

		for (String colorString : colorListAsString) {
			colorList.add(DifficultyColor.valueOf(colorString.toUpperCase()));
		}

		return colorList;
	}

	/*
	 * NOTE: we will NOT allow the user to resume a game that belongs to some
	 * previous day (show an error message)
	 */

	/*
	 * NOTE: do NOT include dark mode setting, that will probably be saved into some
	 * user profile in the future
	 */

	/*
	 * NOTES FOR PARAMETERS: listColorsSolved - non-answered colors are null, and
	 * the order of solved colors
	 */
	public SavedGameState(TileGridWord tileGridWord, CircleRowPane hintsPane, CircleRowPane mistakesPane,
			GameSessionContext gameSessionContext, boolean gameFinished, int timeCompleted,
			GameSession.GameType gameType) {
		this.gameFinished = gameFinished;
		this.gameType = gameType;
		this.grid = tileGridWord.getGridAsWords();
		this.hintsLeft = hintsPane.getNumCircles();
		this.listColorsSolved = tileGridWord.getColorsSolvedOrdered();
		this.mistakesLeft = mistakesPane.getNumCircles();
		this.timeCompleted = timeCompleted;
	}
	
	public boolean isGameFinished() {
        return gameFinished;
    }

    public GameSession.GameType getGameType() {
        return gameType;
    }

    public int getHintsLeft() {
        return hintsLeft;
    }

    public int getMistakesLeft() {
        return mistakesLeft;
    }

    public List<DifficultyColor> getListColorsSolved() {
        return listColorsSolved;
    }

    public List<List<Word>> getGrid() {
        return grid;
    }

    public List<Set<Word>> getGuesses() {
        return guesses;
    }

    public int getPuzzleNumber() {
        return puzzleNumber;
    }

    public int getTimeCompleted() {
        return timeCompleted;
    }

	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_GAME_FINISHED, gameFinished);
		doc.append(KEY_GAME_TYPE, gameType.toString().toLowerCase());
		doc.append(KEY_GRID_WORDS, getGridAsDatabaseFormat(grid));
		doc.append(KEY_GUESSES, PlayedGameInfo.getGuessesAsDatabaseFormat(guesses));
		doc.append(KEY_HINTS_LEFT_COUNT, hintsLeft);
		doc.append(KEY_LIST_COLORS_SOLVED, getColorsAsDatabaseFormat(listColorsSolved));
		doc.append(KEY_MISTAKES_LEFT_COUNT, mistakesLeft);
		doc.append(KEY_PUZZLE_NUMBER, puzzleNumber);
		doc.append(KEY_TIME_COMPLETED, timeCompleted);

		return doc;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		hintsLeft = doc.getInteger(KEY_HINTS_LEFT_COUNT, -1);
		mistakesLeft = doc.getInteger(KEY_MISTAKES_LEFT_COUNT, -1);
		gameFinished = doc.getBoolean(KEY_GAME_FINISHED, false);
		timeCompleted = doc.getInteger(KEY_TIME_COMPLETED, -1);
		puzzleNumber = doc.getInteger(KEY_PUZZLE_NUMBER, -1);

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

		listColorsSolved = new ArrayList<>();
		Document colorsRetrieved = doc.get(KEY_LIST_COLORS_SOLVED, Document.class);
		if (colorsRetrieved != null) {
			listColorsSolved = loadColorsFromDatabaseFormat(colorsRetrieved);
		}
	}
}
