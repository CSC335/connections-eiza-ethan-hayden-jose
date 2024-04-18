package com.connections.model;

import org.bson.Document;

import com.connections.web.DatabaseFormattable;

public class Word implements DatabaseFormattable {
	public static final String KEY_TEXT = "text";
	public static final String KEY_COLOR = "color";
	
    private String text;
    private DifficultyColor color;

    public Word(Document doc) {
    	loadFromDatabaseFormat(doc);
    }
    
    public Word(String text, DifficultyColor color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public DifficultyColor getColor() {
        return color;
    }
    
    @Override
    public Document getAsDatabaseFormat() {
    	Document doc = new Document();
    	doc.append(KEY_TEXT, text);
    	doc.append(KEY_COLOR, color.toString().toLowerCase());
    	return doc;
    }

	@Override
	public void loadFromDatabaseFormat(Document doc) {
		text = doc.getString(KEY_TEXT);
		color = DifficultyColor.valueOf(doc.getString(KEY_COLOR).toUpperCase());
	}
}
