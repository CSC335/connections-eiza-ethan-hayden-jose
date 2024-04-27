package com.connections.model;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bson.Document;

import com.connections.web.DatabaseFormattable;

/**
 * Represents the game answer for a specific color, including the color, description, and associated words.
 */
public class GameAnswerColor implements DatabaseFormattable {
    public static final String KEY_COLOR = "color";
    public static final String KEY_DESCRIPTION = "label";
    public static final String KEY_WORDS = "words";

    private DifficultyColor color;
    private String description;
    private String[] words;

    /**
     * Constructs a GameAnswerColor from a MongoDB Document.
     *
     * @param doc the MongoDB Document containing the game answer color data
     */
    public GameAnswerColor(Document doc) {
        loadFromDatabaseFormat(doc);
    }

    /**
     * Constructs a GameAnswerColor with the specified color, description, and words.
     *
     * @param color       the difficulty color associated with the answer
     * @param description the description of the answer color
     * @param words       the array of words associated with the answer color
     */
    public GameAnswerColor(DifficultyColor color, String description, String[] words) {
        this.color = color;
        this.description = description;
        this.words = words;
    }

    /**
     * Returns the difficulty color associated with the answer.
     *
     * @return the difficulty color
     */
    public DifficultyColor getColor() {
        return color;
    }

    /**
     * Returns the description of the answer color.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the array of words associated with the answer color.
     *
     * @return the array of words
     */
    public String[] getWords() {
        return words;
    }

    /**
     * Checks if the set of words matches the words associated with the answer color.
     *
     * @param otherWordsSet the set of words to compare
     * @return true if the word sets match, false otherwise
     */
    public boolean wordMatchesSet(Set<String> otherWordsSet) {
        Set<String> wordsSet = new HashSet<>(Arrays.asList(words));
        return otherWordsSet.equals(wordsSet);
    }

    /**
     * Returns a string representation of the word list, with words in uppercase and separated by commas.
     *
     * @return the string representation of the word list
     */
    public String getWordListString() {
        return String.join(", ", words).toUpperCase();
    }

    /**
     * Converts the GameAnswerColor to a MongoDB Document format.
     *
     * @return the MongoDB Document representation of the GameAnswerColor
     */
    @Override
    public Document getAsDatabaseFormat() {
        Document doc = new Document();
        doc.append(KEY_COLOR, color.toString().toLowerCase());
        doc.append(KEY_DESCRIPTION, description);
        doc.append(KEY_WORDS, Arrays.asList(words));
        return doc;
    }

    /**
     * Loads the GameAnswerColor data from a MongoDB Document.
     *
     * @param doc the MongoDB Document containing the GameAnswerColor data
     */
    @Override
    public void loadFromDatabaseFormat(Document doc) {
        color = DifficultyColor.valueOf(doc.getString(KEY_COLOR).toUpperCase());
        description = doc.getString(KEY_DESCRIPTION);
        List<String> wordList = doc.getList(KEY_WORDS, String.class);
        words = wordList.toArray(new String[0]);
    }
}