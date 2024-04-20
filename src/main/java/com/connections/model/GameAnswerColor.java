package com.connections.model;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.web.DatabaseFormattable;

public class GameAnswerColor implements DatabaseFormattable {
	public static final String KEY_COLOR = "color";
	public static final String KEY_DESCRIPTION = "label";
	public static final String KEY_WORDS = "words";
	
	private DifficultyColor color;
	private String description;
	private String[] words;
	
	public GameAnswerColor(Document doc) {
		loadFromDatabaseFormat(doc);
	}
	
	public GameAnswerColor(DifficultyColor color, String description, String[] words) {
		this.color = color;
		this.description = description;
		this.words = words;
	}

	public DifficultyColor getColor() {
		return color;
	}

	public String getDescription() {
		return description;
	}
	
	public String[] getWords() {
		return words;
	}

	public boolean wordMatchesSet(Set<String> otherWordsSet) {
		Set<String> wordsSet = new HashSet<>(Arrays.asList(words));
		return otherWordsSet.equals(wordsSet);
	}

	public String getWordListString() {
		return String.join(", ", words).toUpperCase();
	}

	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_COLOR, color.toString().toLowerCase());
		doc.append(KEY_DESCRIPTION, description);
		doc.append(KEY_WORDS, Arrays.asList(words));
		return doc;
	}

	@Override
	public void loadFromDatabaseFormat(Document doc) {
		color = DifficultyColor.valueOf(doc.getString(KEY_COLOR).toUpperCase());
		description = doc.getString(KEY_DESCRIPTION);
		List<String> wordList= doc.getList(KEY_WORDS, String.class);
		words= wordList.toArray(new String[0]);
	}

}
