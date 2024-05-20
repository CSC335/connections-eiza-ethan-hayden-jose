package com.connections.view_controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.connections.model.DifficultyColor;
import com.connections.model.GameAnswerColor;
import com.connections.model.GameSaveState;
import com.connections.model.PlayedGameInfo;
import com.connections.model.Word;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 * The TileGridWord class is a JavaFX component that represents a grid of tiles
 * containing words. It extends the BorderPane class and implements the Modular
 * interface.
 */
public class TileGridWord extends BorderPane implements Modular {
	public static final int MAX_SELECTED = 4;
	public static final int ROWS = 4;
	public static final int COLS = 4;
	public static final int GAP = 8;
	public static final int PANE_WIDTH = GameTile.RECTANGLE_WIDTH * 4 + GAP * 3;
	public static final int PANE_HEIGHT = GameTile.RECTANGLE_HEIGHT * 4 + GAP * 3;

	private GridPane gridPane;
	private int currentSolvingRow;
	public int selectedTileWordCount;
	private List<Set<Word>> previousGuesses;
	private GameSessionContext gameSessionContext;
	private EventHandler<ActionEvent> onTileWordSelection;
	private EventHandler<ActionEvent> onHintAnimationStopped;

	private Set<GameTileWord> tileWordHintShowSet;
	private ParallelTransition tileWordHintPulseTransition;
	private ParallelTransition tileWordHintReturnNormalTransition;
	private boolean hintAnimationInitialActive;
	private boolean hintAnimationPlaying;

	/**
	 * Constructs a new TileGridWord instance.
	 *
	 * @param gameSessionContext The GameSessionContext object for accessing shared
	 *                           resources.
	 */
	public TileGridWord(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		initAssets();
	}

	/**
	 * Loads the grid from a saved game state.
	 *
	 * @param gameSaveState The GameSaveState object containing the saved game
	 *                      state.
	 */
	public void loadFromSaveState(GameSaveState gameSaveState) {
		initAssets();
		gridPane.getChildren().clear();

		initGuessedWordsFromExternalVar(gameSaveState.getGuesses());

		for (Set<Word> guessedWordSet : previousGuesses) {
			GameAnswerColor answerColor = checkMatchingAnswerColor(guessedWordSet);
			if (answerColor != null) {
				GameTileAnswer tileAnswer = new GameTileAnswer(answerColor, this);
				currentSolvingRow++;
				gridSetTileAnswer(tileAnswer);
			}
		}

		for (int row = currentSolvingRow; row < ROWS; row++) {
			List<Word> wordsOnRow = gameSaveState.getGrid().get(row);

			for (int col = 0; col < COLS; col++) {
				GameTileWord tileWord = new GameTileWord(this);
				tileWord.setWord(wordsOnRow.get(col));
				gridPane.add(tileWord, col, row);
			}
		}
	}

	/**
	 * Loads the grid from a played game info.
	 *
	 * @param playedGameInfo The PlayedGameInfo object containing the played game
	 *                       information.
	 */
	public void loadFromPlayedGameInfo(PlayedGameInfo playedGameInfo) {
		initAssets();
		initGuessedWordsFromExternalVar(playedGameInfo.getGuesses());

		for (Set<Word> guessedWordSet : previousGuesses) {
			GameAnswerColor answerColor = checkMatchingAnswerColor(guessedWordSet);
			if (answerColor != null) {
				GameTileAnswer tileAnswer = new GameTileAnswer(answerColor, this);
				currentSolvingRow++;
				gridSetTileAnswer(tileAnswer);
			}
		}

		List<DifficultyColor> remainingColors = getSortedUnansweredDifficultyColor();
		for (DifficultyColor color : remainingColors) {
			GameAnswerColor answerColor = gameSessionContext.getGameData().getAnswerForColor(color);
			GameTileAnswer tileAnswer = new GameTileAnswer(answerColor, this);
			currentSolvingRow++;
			gridSetTileAnswer(tileAnswer);
		}
	}

	/**
	 * Initializes the assets for the TileGridWord.
	 */
	private void initAssets() {
		currentSolvingRow = 0;
		selectedTileWordCount = 0;
		previousGuesses = new ArrayList<>();

		gridPane = new GridPane();
		gridPane.setHgap(GAP);
		gridPane.setVgap(GAP);
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setMaxWidth(PANE_WIDTH);

		setMaxWidth(PANE_WIDTH);
		setCenter(gridPane);
		initEmptyTileWords();
	}

	/**
	 * Initializes the previously guessed words from an external variable.
	 *
	 * @param previousGuessesExternal The external list of previously guessed word
	 *                                sets.
	 */
	private void initGuessedWordsFromExternalVar(List<Set<Word>> previousGuessesExternal) {
		previousGuesses = new ArrayList<>();

		for (Set<Word> guessSetFromSave : previousGuessesExternal) {
			Set<Word> guessSetFromTileWord = new HashSet<>();

			for (Word wordFromSave : guessSetFromSave) {
				// by default set it to the save copy
				Word wordFromTileWord = wordFromSave;

				for (int row = currentSolvingRow; row < ROWS; row++) {
					for (int col = 0; col < COLS; col++) {
						Node node = gridGetNode(row, col);
						if (node instanceof GameTileWord) {
							GameTileWord tileWord = (GameTileWord) node;
							if (wordFromSave.equals(tileWord.getWord())) {
								wordFromTileWord = tileWord.getWord();

								row = ROWS;
								col = COLS;
								break;
							}
						}
					}
				}

				guessSetFromTileWord.add(wordFromTileWord);
			}

			previousGuesses.add(guessSetFromTileWord);
		}
	}

	/**
	 * Initializes empty tile words in the grid.
	 */
	private void initEmptyTileWords() {
		gridPane.getChildren().clear();
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				gridPane.add(new GameTileWord(this), col, row);
			}
		}
	}

	/**
	 * Initializes the tile words with words from the game data.
	 */
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

	/**
	 * Deselects all tile words in the grid.
	 */
	public void deselectTileWords() {
		gridPane.getChildren().forEach(node -> {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.setSelectedStatus(false);
			}
		});
		selectedTileWordCount = 0;
	}

	/**
	 * Shuffles the tile words in the grid.
	 */
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

	/**
	 * Retrieves the number of selected tiles.
	 *
	 * @return The number of selected tiles.
	 */
	public int getSelectedTileWordCount() {
		return selectedTileWordCount;
	}

	/**
	 * Checks the number of words that match the selected words.
	 *
	 * @return The number of words that match the selected words.
	 */
	public int checkNumWordsMatchSelected() {
		int maxMatchCount = 0;
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			GameAnswerColor answer = gameSessionContext.getGameData().getAnswerForColor(color);
			List<String> colorWords = Arrays.asList(answer.getWords());
			int matchCount = (int) getSelectedWords().stream().filter(word -> colorWords.contains(word.getText()))
					.count();
			maxMatchCount = Math.max(maxMatchCount, matchCount);
		}
		return maxMatchCount;
	}

	/**
	 * Checks the number of words in the given set that match an answer.
	 *
	 * @param words The set of words to check.
	 * @return The number of words that match an answer.
	 */
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

	/**
	 * Checks if the given set of words matches an answer color.
	 *
	 * @param words The set of words to check.
	 * @return The GameAnswerColor object if a match is found, otherwise null.
	 */
	public GameAnswerColor checkMatchingAnswerColor(Set<Word> words) {
		int maxMatchCount = 0;
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			GameAnswerColor answer = gameSessionContext.getGameData().getAnswerForColor(color);
			List<String> colorWords = Arrays.asList(answer.getWords());
			int matchCount = (int) words.stream().filter(word -> colorWords.contains(word.getText())).count();
			maxMatchCount = Math.max(maxMatchCount, matchCount);
			if (maxMatchCount == 4) {
				return answer;
			}
		}
		return null;
	}

	/**
	 * Checks if all categories (difficulty colors) have been guessed.
	 *
	 * @return true if all categories have been guessed, false otherwise.
	 */
	public boolean checkAllCategoriesGuessed() {
		Set<DifficultyColor> guessedColors = new HashSet<>();
		for (Set<Word> guess : previousGuesses) {
			if (checkNumWordsMatch(guess) == MAX_SELECTED) {
				guessedColors.add(guess.iterator().next().getColor());
			}
		}
		return guessedColors.size() == DifficultyColor.getAllColors().size();
	}

	/**
	 * Gets the set of currently selected tile words.
	 *
	 * @return The set of selected tile words.
	 */
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

	/**
	 * Checks if the currently selected words have already been guessed.
	 *
	 * @return true if the selected words have already been guessed, false
	 *         otherwise.
	 */
	public boolean checkSelectedAlreadyGuessed() {
		Set<Word> selected = getSelectedWords();
		return previousGuesses.contains(selected);
	}

	/**
	 * Saves the currently selected words as a guess.
	 */
	public void saveSelectedAsGuess() {
		Set<Word> selected = getSelectedWords();
		if (!previousGuesses.contains(selected)) {
			previousGuesses.add(selected);
		}
	}

	/**
	 * Gets the set of currently selected words.
	 *
	 * @return The set of selected words.
	 */
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

	/**
	 * Gets the sorted list of unanswered difficulty colors.
	 *
	 * @return The list of unanswered difficulty colors sorted by difficulty.
	 */
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

	/**
	 * Unsets the incorrect status of all tile words.
	 */
	public void unsetIncorrectTileWords() {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				if (tileWord.getIncorrectStatus()) {
					tileWord.setIncorrectStatus(false);
				}
			}
		}
	}

	/**
	 * Selects the tile words that match the given answer.
	 *
	 * @param answer The GameAnswerColor object containing the answer.
	 */
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

	/**
	 * Gets a sequential transition that shakes the selected tile words.
	 *
	 * @return The sequential transition for shaking the selected tile words.
	 */
	public SequentialTransition getTransitionTileWordShake() {
		ParallelTransition shakeTransition = new ParallelTransition();
		Set<GameTileWord> selectedTileWords = getSelectedTileWords();

		for (GameTileWord tileWord : selectedTileWords) {
			TranslateTransition individualShakeTransition = new TranslateTransition(Duration.millis(100), tileWord);
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

		SequentialTransition sequentialTransition = new SequentialTransition(placeholderPause, shakeTransition);
		return sequentialTransition;
	}

	/**
	 * Gets a parallel transition that makes the selected tile words jump.
	 *
	 * @return The parallel transition for making the selected tile words jump.
	 */
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

	/**
	 * Gets the list of previous guesses.
	 *
	 * @return The list of previous guesses.
	 */
	public List<Set<Word>> getGuesses() {
		return previousGuesses;
	}

	/**
	 * Sets the disabled state of all tile words.
	 *
	 * @param status The disabled state to set.
	 */
	public void setTileWordDisable(boolean status) {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				node.setDisable(status);
			}
		}
	}

	/**
	 * Refreshes the style of the TileGridWord and its children based on the current
	 * style settings.
	 */
	@Override
	public void refreshStyle() {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof Modular) {
				Modular stylableNode = (Modular) node;
				stylableNode.refreshStyle();
			}
		}
	}

	/**
	 * Gets the GameSessionContext object associated with this TileGridWord.
	 *
	 * @return The GameSessionContext object.
	 */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}

	/**
	 * Gets the current solving row index.
	 *
	 * @return The current solving row index.
	 */
	public int getCurrentSolvingRow() {
		return currentSolvingRow;
	}

	/**
	 * Increments the current solving row index.
	 */
	public void incrementCurrentSolvingRow() {
		currentSolvingRow++;
	}

	/**
	 * Sets the event handler for tile word selection.
	 *
	 * @param event The event handler to be called when a tile word is selected.
	 */
	public void setOnTileWordSelection(EventHandler<ActionEvent> event) {
		onTileWordSelection = event;
	}

	/**
	 * Increments the count of selected tile words.
	 */
	public void incrementSelectedTileWordCount() {
		selectedTileWordCount++;
		if (onTileWordSelection != null) {
			onTileWordSelection.handle(new ActionEvent(this, null));
		}
	}

	/**
	 * Decrements the count of selected tile words.
	 */
	public void decrementSelectedTileWordCount() {
		selectedTileWordCount--;
		if (onTileWordSelection != null) {
			onTileWordSelection.handle(new ActionEvent(this, null));
		}
	}

	/**
	 * Gets the node at the specified row and column in the grid.
	 *
	 * @param row The row index.
	 * @param col The column index.
	 * @return The node at the specified row and column, or null if not found.
	 */
	public Node gridGetNode(int row, int col) {
		for (Node node : gridPane.getChildren()) {
			if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Removes the specified set of nodes from the grid.
	 *
	 * @param nodeSet The set of nodes to be removed.
	 */
	public void gridRemoveNodeSet(Set<? extends Node> nodeSet) {
		gridPane.getChildren().removeAll(nodeSet);
	}

	/**
	 * Sets the tile answer in the grid at the specified row.
	 *
	 * @param tileAnswer The tile answer to be set.
	 */
	public void gridSetTileAnswer(GameTileAnswer tileAnswer) {
		gridPane.add(tileAnswer, 0, currentSolvingRow - 1);
		GridPane.setColumnSpan(tileAnswer, COLS);
	}

	/**
	 * Swaps the nodes at the specified source and destination row and column
	 * indices.
	 *
	 * @param sourceRow The source row index.
	 * @param sourceCol The source column index.
	 * @param destRow   The destination row index.
	 * @param destCol   The destination column index.
	 */
	public void gridSwapNode(int sourceRow, int sourceCol, int destRow, int destCol) {
		Node node1 = gridGetNode(sourceRow, sourceCol);
		Node node2 = gridGetNode(destRow, destCol);

		gridPane.getChildren().removeAll(node1, node2);

		gridPane.add(node1, destCol, destRow);
		gridPane.add(node2, sourceCol, sourceRow);
	}

	/**
	 * Sets the visibility of non-solving nodes in the grid.
	 *
	 * @param status The visibility status to set.
	 */
	public void gridSetNonSolvingNodeVisible(boolean status) {
		for (Node node : gridPane.getChildren()) {
			if (GridPane.getRowIndex(node) >= currentSolvingRow) {
				node.setVisible(true);
			}
		}
	}

	/**
	 * Gets the grid as a list of lists of words.
	 *
	 * @return The grid as a list of lists of words.
	 */
	public List<List<Word>> getGridAsWords() {
		List<List<Word>> gridWords = new ArrayList<>();

		for (int row = 0; row < currentSolvingRow; row++) {
			gridWords.add(new ArrayList<>());
		}

		for (int row = currentSolvingRow; row < ROWS; row++) {
			List<Word> wordList = new ArrayList<>();

			for (int col = 0; col < COLS; col++) {
				Node node = gridGetNode(row, col);
				if (node instanceof GameTileWord) {
					GameTileWord tile = (GameTileWord) node;
					wordList.add(tile.getWord());
				}
			}

			gridWords.add(wordList);
		}

		return gridWords;
	}

	/**
	 * Sets the event handler to be called when the hint animation stops.
	 *
	 * @param onHintAnimationStopped The event handler to be called when the hint
	 *                               animation stops.
	 */
	public void setOnHintAnimationStopped(EventHandler<ActionEvent> onHintAnimationStopped) {
		this.onHintAnimationStopped = onHintAnimationStopped;
	}

	/**
	 * Checks if the hint animation is currently running.
	 *
	 * @return true if the hint animation is running, false otherwise.
	 */
	public boolean hintAnimationIsRunning() {
		return hintAnimationPlaying;
	}

	/**
	 * Stops the hint animation.
	 */
	public void hintAnimationStop() {
		if (hintAnimationInitialActive && hintAnimationPlaying) {
			hintAnimationInitialActive = false;
			tileWordHintPulseTransition.stop();

			tileWordHintReturnNormalTransition = new ParallelTransition();
			for (GameTileWord tileWord : tileWordHintShowSet) {
				tileWordHintReturnNormalTransition.getChildren().add(tileWord.getHintReturnNormalAnimation());
			}
			tileWordHintReturnNormalTransition.setOnFinished(event -> {
				if (onHintAnimationStopped != null) {
					onHintAnimationStopped.handle(new ActionEvent(this, null));
				}
				for (GameTileWord tileWord : tileWordHintShowSet) {
					tileWord.setStyleChangeable(true);
					tileWord.refreshStyle();
				}
				hintAnimationPlaying = false;
			});
			tileWordHintReturnNormalTransition.play();
		}
		gridPane.setOnMouseClicked(null);
	}

	/**
	 * Shows the hint animation.
	 */
	public void hintAnimationShow() {
		tileWordHintShowSet = new HashSet<>();

		Set<GameTileWord> tileWordSelectedSet = getSelectedTileWords();
		Map<DifficultyColor, Integer> tilesByColorCount = new TreeMap<>();

		// Initialize the HashMap with zero for each color.
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			tilesByColorCount.put(color, 0);
		}

		// Go through all of the selected colors and increment the counters in the
		// HashMap.
		for (GameTileWord tileWord : tileWordSelectedSet) {
			DifficultyColor color = tileWord.getWord().getColor();
			tilesByColorCount.put(color, tilesByColorCount.get(color) + 1);
			tileWordHintShowSet.add(tileWord);
		}

		Set<DifficultyColor> colorsToSearchFor = new TreeSet<>();
		int numExtraTilesToPulse = 3;

		// Find the first and second maximum count.
		for (int i = 0; i < numExtraTilesToPulse; i++) {
			DifficultyColor maxColor = null;
			int maxCount = -1;
			for (DifficultyColor color : tilesByColorCount.keySet()) {
				if (tilesByColorCount.get(color) > maxCount) {
					maxCount = tilesByColorCount.get(color);
					maxColor = color;
				}
			}
			if (maxColor != null) {
				colorsToSearchFor.add(maxColor);
				tilesByColorCount.remove(maxColor);
			}
		}

		// Search the entire grid for non-selected GameTileWord objects that have a
		// matching color, pulse the first object that is found (and pulse no more
		// objects of that color).
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				if (!tileWord.getSelectedStatus()) {
					DifficultyColor color = tileWord.getWord().getColor();
					if (colorsToSearchFor.contains(color)) {
						colorsToSearchFor.remove(color);
						tileWordHintShowSet.add(tileWord);
					}
				}
			}

			if (colorsToSearchFor.isEmpty()) {
				break;
			}
		}

		tileWordHintPulseTransition = new ParallelTransition();

		for (GameTileWord tileWord : tileWordHintShowSet) {
			tileWordHintPulseTransition.getChildren().add(tileWord.getHintPulseAnimation());
			tileWord.setStyleChangeable(false);
		}

		tileWordHintPulseTransition.setOnFinished(event -> {
			hintAnimationStop();
		});

		gridPane.setOnMouseClicked(event -> {
			hintAnimationStop();
		});

		tileWordHintPulseTransition.play();
		hintAnimationInitialActive = true;
		hintAnimationPlaying = true;
	}
}
