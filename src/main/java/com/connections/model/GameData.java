package com.connections.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.connections.web.DatabaseFormattable;

public class GameData implements DatabaseFormattable {
	public static final String KEY_COLOR_LIST = "colors";
	public static final String KEY_PUZZLE_NUMBER = "number";
	
	private Map<DifficultyColor, GameAnswerColor> answerMap;
	private int puzzleNumber;
	
	public GameData(Document doc) {
		loadFromDatabaseFormat(doc);
	}

	public GameAnswerColor getAnswerForColor(DifficultyColor color) {
		return answerMap.get(color);
	}

	public Map<DifficultyColor, GameAnswerColor> getAnswerMap() {
		return answerMap;
	}

	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_PUZZLE_NUMBER, puzzleNumber);
		
		List<Document> colorList = new ArrayList<>();
		for(DifficultyColor color : answerMap.keySet()) {
			colorList.add(answerMap.get(color).getAsDatabaseFormat());
		}
		doc.append(KEY_COLOR_LIST, colorList);
		
		return doc;
	}

	@Override
	public void loadFromDatabaseFormat(Document doc) {
		puzzleNumber = doc.getInteger(KEY_PUZZLE_NUMBER, -1);
		
		answerMap = new HashMap<>();
		List<Document> colorList = doc.getList(KEY_COLOR_LIST, Document.class);
		for(Document colorAnswerDoc : colorList) {
			GameAnswerColor answerColor = new GameAnswerColor(colorAnswerDoc);
			DifficultyColor color = answerColor.getColor();
			answerMap.put(color, answerColor);
		}
	}
}
