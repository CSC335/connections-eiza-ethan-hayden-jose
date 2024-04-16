package com.connections.view_controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.connections.model.DifficultyColor;
import com.connections.model.GameAnswerColor;
import com.connections.model.GameData;
import com.connections.model.Word;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class TileGridWord extends BorderPane implements Modular {
	protected static final int MAX_SELECTED = 4;
	protected static final int ROWS = 4;
	protected static final int COLS = 4;
	protected static final int GAP = 8;
	protected static final int PANE_WIDTH = GameTile.RECTANGLE_WIDTH * 4 + GAP * 3;
	protected static final int PANE_HEIGHT = GameTile.RECTANGLE_HEIGHT * 4 + GAP * 3;

	private GridPane gridPane;
	private int currentSolvingRow;
	public int selectedTileWordCount;
	private List<Set<Word>> previousGuesses = new ArrayList<>();
	private GameSessionContext gameSessionContext;
	private EventHandler<ActionEvent> onTileWordSelection;

	public TileGridWord(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		gridPane = new GridPane();
		gridPane.setHgap(GAP);
		gridPane.setVgap(GAP);
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setMaxWidth(PANE_WIDTH);
		setMaxWidth(PANE_WIDTH);
		setCenter(gridPane);
		initEmptyTileWords();
	}

	private void initEmptyTileWords() {
		gridPane.getChildren().clear();
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				gridPane.add(new GameTileWord(gameSessionContext.getStyleManager().getFont("franklin-normal", 700, 18), this), col, row);
			}
		}
	}

	public void initTileWords() {
		List<Word> words = new ArrayList<>();
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			GameAnswerColor answer = gameSessionContext.getGameData().getAnswerForColor(color);
			for (String wordText : answer.getWords()) {
				words.add(new Word(wordText, color));
			}
		}

		Collections.shuffle(words);

		int wordIndex = 0;
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.setWord(words.get(wordIndex));
				wordIndex++;
			}
		}
	}

	public void deselectTileWords() {
		gridPane.getChildren().forEach(node -> {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.setSelectedStatus(false);
			}
		});
		selectedTileWordCount = 0;
	}

	public void shuffleTileWords() {
		ObservableList<Node> children = gridPane.getChildren();
		List<GameTileWord> gameTileWords = children.stream().filter(node -> node instanceof GameTileWord)
				.map(node -> (GameTileWord) node).collect(Collectors.toList());

		Collections.shuffle(gameTileWords);

		int index = 0;
		for (int row = currentSolvingRow; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				GridPane.setRowIndex(gameTileWords.get(index), row);
				GridPane.setColumnIndex(gameTileWords.get(index), col);
				index++;
			}
		}

		ParallelTransition fadeInTransition = new ParallelTransition();

		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.fadeInWordText(fadeInTransition);
			}
		}

		fadeInTransition.play();
	}

	public int getSelectedTileWordCount() {
		return selectedTileWordCount;
	}
	
	public int checkNumWordsMatchSelected() {
		int maxMatchCount = 0;
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			GameAnswerColor answer = gameSessionContext.getGameData().getAnswerForColor(color);
			List<String> colorWords = Arrays.asList(answer.getWords());
			int matchCount = (int) getSelectedWords().stream().filter(word -> colorWords.contains(word.getText())).count();
			maxMatchCount = Math.max(maxMatchCount, matchCount);
		}
		return maxMatchCount;
	}

	public int checkNumWordsMatch(Set<Word> words) {
		int maxMatchCount = 0;
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			GameAnswerColor answer = gameSessionContext.getGameData().getAnswerForColor(color);
			List<String> colorWords = Arrays.asList(answer.getWords());
			int matchCount = (int) words.stream().filter(word -> colorWords.contains(word.getText())).count();
			maxMatchCount = Math.max(maxMatchCount, matchCount);
		}
		return maxMatchCount;
	}

	public boolean checkAllCategoriesGuessed() {
		Set<DifficultyColor> guessedColors = new HashSet<>();
		for (Set<Word> guess : previousGuesses) {
			if (checkNumWordsMatch(guess) == MAX_SELECTED) {
				guessedColors.add(guess.iterator().next().getColor());
			}
		}
		return guessedColors.size() == DifficultyColor.getAllColors().size();
	}

	public Set<GameTileWord> getSelectedTileWords() {
		Set<GameTileWord> selectedPieceSet = new HashSet<>();

		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				if (tileWord.getSelectedStatus()) {
					selectedPieceSet.add(tileWord);
				}
			}
		}
		return selectedPieceSet;
	}
	
	public boolean checkSelectedAlreadyGuessed() {
		Set<Word> selected = getSelectedWords();
		return previousGuesses.contains(selected);
	}
	
	public void saveSelectedAsGuess() {
		Set<Word> selected = getSelectedWords();
		if(!previousGuesses.contains(selected)) {
			previousGuesses.add(selected);
		}
	}

	public Set<Word> getSelectedWords() {
		Set<Word> selectedWords = new HashSet<>();

		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				if (tileWord.getSelectedStatus()) {
					selectedWords.add(tileWord.getWord());
				}
			}
		}
		return selectedWords;
	}
	
	public List<DifficultyColor> getSortedUnansweredDifficultyColor() {
		List<DifficultyColor> unansweredColor = new ArrayList<>(DifficultyColor.getAllColors());

		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileAnswer) {
				GameTileAnswer tileAnswer = (GameTileAnswer) node;
				unansweredColor.remove(tileAnswer.getGameAnswerColor().getColor());
			}
		}
		
		// Sort in order of difficulty (YELLOW, GREEN, BLUE, PURPLE);
		Collections.sort(unansweredColor);
		
		return unansweredColor;
	}
	
	public void unsetIncorrectTileWords() {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				if(tileWord.getIncorrectStatus()) {
					tileWord.setIncorrectStatus(false);
				}
			}
		}
	}
	
	public void selectMatchingAnswerWords(GameAnswerColor answer) {
		Set<String> wordStringSet = new HashSet<>(Arrays.asList(answer.getWords()));
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				String tileWordText = tileWord.getWord().getText().toLowerCase();
				if (wordStringSet.contains(tileWordText)) {
					tileWord.setSelectedStatus(true);
				}
			}
		}
	}

	public SequentialTransition getTransitionTileWordShake() {
		ParallelTransition shakeTransition = new ParallelTransition();
		Set<GameTileWord> selectedTileWords = getSelectedTileWords();
		
		for (GameTileWord tileWord : selectedTileWords) {
			TranslateTransition individualShakeTransition = new TranslateTransition(Duration.millis(100),
					tileWord);
			individualShakeTransition.setByX(8);
			individualShakeTransition.setAutoReverse(true);
			individualShakeTransition.setCycleCount(4);
			shakeTransition.getChildren().add(individualShakeTransition);
		}
		
		PauseTransition placeholderPause = new PauseTransition(Duration.millis(5));
		placeholderPause.setOnFinished(event -> {
			for (GameTileWord tileWord : selectedTileWords) {
				tileWord.setSelectedStatus(false);
				tileWord.setIncorrectStatus(true);
			}
		});

		SequentialTransition sequentialTransition  = new SequentialTransition(placeholderPause, shakeTransition);
		return sequentialTransition;
	}

	public ParallelTransition getTransitionTileWordJump() {
		GameTileWord[][] tileWordGrid = new GameTileWord[ROWS][COLS];
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				tileWordGrid[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)] = (GameTileWord) node;
			}
		}

		ParallelTransition jumpTransition = new ParallelTransition();
		int delay = 0;

		for (GameTileWord[] rowTileWords : tileWordGrid) {
			for (GameTileWord colTileWord : rowTileWords) {
				if (colTileWord != null && colTileWord.getSelectedStatus()) {
					TranslateTransition individualJumpTransition = new TranslateTransition(Duration.millis(200),
							colTileWord);
					individualJumpTransition.setByY(-8);
					individualJumpTransition.setAutoReverse(true);
					individualJumpTransition.setCycleCount(2);
					individualJumpTransition.setDelay(Duration.millis(delay));
					jumpTransition.getChildren().add(individualJumpTransition);
					delay += 50;
				}
			}
		}

		return jumpTransition;
	}
	
	public List<Set<Word>> getGuesses() {
		return previousGuesses;
	}

	public void setTileWordDisable(boolean status) {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				node.setDisable(status);
			}
		}
	}

	// Method likely no longer needed
	// Disabling/enabling style changeable during auto solve caused darkmode issues
	public void setTileWordStyleChangeable(boolean status) {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.setStyleChangeable(status);
			}
		}
	} 
	
	@Override
	public void refreshStyle() {
		for (Node node : gridPane.getChildren()) {
			if(node instanceof Modular) {
				Modular stylableNode = (Modular) node;
				stylableNode.refreshStyle();
			}
		}
	}
	
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}	

	public int getCurrentSolvingRow() {
		return currentSolvingRow;
	}

	public void incrementCurrentSolvingRow() {
		currentSolvingRow++;
	}
	
	public void setOnTileWordSelection(EventHandler<ActionEvent> event) {
		onTileWordSelection = event;
	}

	public void incrementSelectedTileWordCount() {
		selectedTileWordCount++;
		if(onTileWordSelection != null) {
			onTileWordSelection.handle(new ActionEvent(this, null));
		}
	}

	public void decrementSelectedTileWordCount() {
		selectedTileWordCount--;
		if(onTileWordSelection != null) {
			onTileWordSelection.handle(new ActionEvent(this, null));
		}
	}
	
	public Node gridGetNode(int row, int col) {
		for (Node node : gridPane.getChildren()) {
			if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
				return node;
			}
		}
		return null;
	}
	
	public void gridRemoveNodeSet(Set<? extends Node> nodeSet) {
		gridPane.getChildren().removeAll(nodeSet);
	}
	
	public void gridSetTileAnswer(GameTileAnswer tileAnswer) {
		gridPane.add(tileAnswer, 0, currentSolvingRow - 1);
		GridPane.setColumnSpan(tileAnswer, COLS);
	}
	
	public void gridSwapNode(int sourceRow, int sourceCol, int destRow, int destCol) {
		Node node1 = gridGetNode(sourceRow, sourceCol);
		Node node2 = gridGetNode(destRow, destCol);

		gridPane.getChildren().removeAll(node1, node2);

		gridPane.add(node1, destCol, destRow);
		gridPane.add(node2, sourceCol, sourceRow);
	}
	
	public void gridSetNonSolvingNodeVisible(boolean status) {
		for (Node node : gridPane.getChildren()) {
			if (GridPane.getRowIndex(node) >= currentSolvingRow) {
				node.setVisible(true);
			}
		}
	}
}
