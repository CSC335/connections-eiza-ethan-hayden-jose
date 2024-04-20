package com.connections.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.connections.web.DatabaseFormattable;

public class PlayedGameInfo implements DatabaseFormattable {
	public static final String KEY_PUZZLE_NUMBER = "puzzle_number";
	public static final String KEY_GUESS_COUNT = "guess_count";
    public static final String KEY_MISTAKE_COUNT = "mistake_count";
    public static final String KEY_CONNECTION_COUNT = "connection_count";
    public static final String KEY_TIME_COMPLETED = "time_completed";
    public static final String KEY_GUESSES = "guesses";
    public static final String KEY_WON = "won";
    
    // NOTE:
    // puzzle number is used as unique ID
    // and, time completed is in milliseconds (ms), so 1 sec = 1000 ms
    protected int puzzleNumber;
    protected int guessCount;
    protected int mistakeCount;
    protected int connectionCount;
    protected int timeCompleted;
	protected List<Set<Word>> guesses;
	protected boolean won;

	public PlayedGameInfo(Document doc) {
		loadFromDatabaseFormat(doc);
	}
	
    public PlayedGameInfo(int puzzleNumber, int guessCount, int mistakeCount, int connectionCount, int timeCompleted,
                          List<Set<Word>> guesses, boolean won) {
        this.puzzleNumber = puzzleNumber;
    	this.guessCount = guessCount;
        this.mistakeCount = mistakeCount;
        this.connectionCount = connectionCount;
        this.timeCompleted = timeCompleted;
        this.guesses = guesses;
        this.won = won;
    }

    public int getPuzzleNumber() {
        return puzzleNumber;
    }
    
    public int getGuessCount() {
        return guessCount;
    }

    public int getMistakeCount() {
        return mistakeCount;
    }

    public int getConnectionCount() {
        return connectionCount;
    }

    public int getTimeCompleted() {
        return timeCompleted;
    }

    public List<Set<Word>> getGuesses() {
        return guesses;
    }
    
    public boolean wasWon() {
    	return won;
    }

    public List<List<Document>> getGuessesAsDatabaseFormat() {
        List<List<Document>> wordSetList = new ArrayList<>();
        for (Set<Word> set : guesses) {
        	List<Document> wordList = new ArrayList<>();
            for (Word word : set) {
                wordList.add(word.getAsDatabaseFormat());
            }
            wordSetList.add(wordList);
        }
        return wordSetList;
    }
    
    public void loadGuessesFromDatabaseFormat(List<List<Document>> wordSetList) {
    	guesses = new ArrayList<>();
    	for(List<Document> wordList : wordSetList) {
    		Set<Word> set = new HashSet<>();
    		for(Document wordDoc : wordList) {
    			set.add(new Word(wordDoc));
    		}
    		guesses.add(set);
    	}
    }

    public Document getAsDatabaseFormat() {
        Document doc = new Document();
        doc.append(KEY_PUZZLE_NUMBER, puzzleNumber);
        doc.append(KEY_GUESSES, getGuessesAsDatabaseFormat());
        doc.append(KEY_GUESS_COUNT, guessCount);
        doc.append(KEY_MISTAKE_COUNT, mistakeCount);
        doc.append(KEY_CONNECTION_COUNT, connectionCount);
        doc.append(KEY_TIME_COMPLETED, timeCompleted);
        doc.append(KEY_WON, won);
        return doc;
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public void loadFromDatabaseFormat(Document doc) {
		puzzleNumber = doc.getInteger(KEY_PUZZLE_NUMBER, -1);
        guessCount = doc.getInteger(KEY_GUESS_COUNT, -1);
        mistakeCount = doc.getInteger(KEY_MISTAKE_COUNT, -1);
        connectionCount = doc.getInteger(KEY_CONNECTION_COUNT, -1);
        timeCompleted = doc.getInteger(KEY_TIME_COMPLETED, -1);
        won = doc.getBoolean(KEY_WON, false);
        
        guesses = new ArrayList<>();
        Object guessesRetrieved = doc.get(KEY_GUESSES);
        if(guessesRetrieved != null) {
        	loadGuessesFromDatabaseFormat((List<List<Document>>) guessesRetrieved);
        }
	}
}
