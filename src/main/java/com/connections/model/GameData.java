package com.connections.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.connections.web.DatabaseFormattable;

/**
 * Represents the game data, including the puzzle number and a map of difficulty
 * colors to game answer colors.
 */
public class GameData implements DatabaseFormattable {
	public static final String KEY_COLOR_LIST = "colors";
	public static final String KEY_PUZZLE_NUMBER = "number";

	private Map<DifficultyColor, GameAnswerColor> answerMap;
	private int puzzleNumber;

	/**
	 * Constructs a GameData from a MongoDB Document.
	 *
	 * @param doc the MongoDB Document containing the game data
	 */
	public GameData(Document doc) {
		loadFromDatabaseFormat(doc);
	}

	public GameData(Map<DifficultyColor, GameAnswerColor> answerMap, int puzzleNumber) {
		this.answerMap = answerMap;
		this.puzzleNumber = puzzleNumber;
	}

	/**
	 * Returns the game answer color for the specified difficulty color.
	 *
	 * @param color the difficulty color
	 * @return the corresponding game answer color
	 */
	public GameAnswerColor getAnswerForColor(DifficultyColor color) {
		return answerMap.get(color);
	}

	/**
	 * Returns the map of difficulty colors to game answer colors.
	 *
	 * @return the map of difficulty colors to game answer colors
	 */
	public Map<DifficultyColor, GameAnswerColor> getAnswerMap() {
		return answerMap;
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
	 * Converts the GameData to a MongoDB Document format.
	 *
	 * @return the MongoDB Document representation of the GameData
	 */
	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_PUZZLE_NUMBER, puzzleNumber);

		// Make the list of colors in order of the color difficulty (yellow, green,
		// blue, purple).
		List<Document> colorList = new ArrayList<>();
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			if (answerMap.keySet().contains(color)) {
				colorList.add(answerMap.get(color).getAsDatabaseFormat());
			}
		}
		doc.append(KEY_COLOR_LIST, colorList);

		return doc;
	}

	/**
	 * Loads the GameData from a MongoDB Document.
	 *
	 * @param doc the MongoDB Document containing the GameData
	 */
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		puzzleNumber = doc.getInteger(KEY_PUZZLE_NUMBER, -1);

		answerMap = new HashMap<>();
		List<Document> colorList = doc.getList(KEY_COLOR_LIST, Document.class);
		for (Document colorAnswerDoc : colorList) {
			GameAnswerColor answerColor = new GameAnswerColor(colorAnswerDoc);
			DifficultyColor color = answerColor.getColor();
			answerMap.put(color, answerColor);
		}
	}
}