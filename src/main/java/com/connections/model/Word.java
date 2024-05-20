package com.connections.model;

import java.util.Objects;

import org.bson.Document;

import com.connections.web.DatabaseFormattable;

/**
 * Represents a word with its text and color. Implements the DatabaseFormattable
 * interface for database serialization and deserialization.
 */
public class Word implements DatabaseFormattable {
	public static final String KEY_TEXT = "text";
	public static final String KEY_COLOR = "color";

	private String text;
	private DifficultyColor color;

	/**
	 * Constructs a Word object from a database document.
	 *
	 * @param doc The database document containing the word information.
	 */
	public Word(Document doc) {
		loadFromDatabaseFormat(doc);
	}

	/**
	 * Constructs a Word object with the given text and color.
	 *
	 * @param text  The text of the word.
	 * @param color The color of the word
	 */
	public Word(String text, DifficultyColor color) {
		this.text = text;
		this.color = color;
	}

	/**
	 * Returns the text of the word.
	 *
	 * @return The text of the word
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the color of the word.
	 *
	 * @return The color of the word
	 */
	public DifficultyColor getColor() {
		return color;
	}

	/**
	 * Converts this Word object to a database document format.
	 *
	 * @return The database document representing this Word object.
	 */
	@Override
	public Document getAsDatabaseFormat() {
		Document doc = new Document();
		doc.append(KEY_TEXT, text);
		doc.append(KEY_COLOR, color.toString().toLowerCase());
		return doc;
	}

	/**
	 * Loads the Word object from a database document.
	 *
	 * @param doc The Word document containing the played game information.
	 */
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		text = doc.getString(KEY_TEXT);
		color = DifficultyColor.valueOf(doc.getString(KEY_COLOR).toUpperCase());
	}

	/**
	 * Returns the hash code of the Word object given the text and color.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(text, color);
	}

	/**
	 * Checks if this Word object is equal to another object.
	 *
	 * @param other The other object to compare.
	 * @return true if the objects are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if ((other == null) || !(other instanceof Word)) {
			return false;
		}

		Word otherWord = (Word) other;

		return Objects.equals(text, otherWord.text) && Objects.equals(color, otherWord.color);
	}
}
