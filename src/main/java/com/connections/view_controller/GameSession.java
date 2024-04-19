package com.connections.view_controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.connections.entry.ConnectionsAppLocal;
import com.connections.model.DifficultyColor;
import com.connections.model.GameAnswerColor;
import com.connections.model.GameData;
import com.connections.model.Word;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameSession extends StackPane implements Modular {
	private static final int POPUP_DEFAULT_DURATION_MS = 3000;
	private static final int MENU_PANE_HEIGHT = NotificationPane.HEIGHT + 10;

	private GameSessionContext gameSessionContext;

	private Text mainHeaderText;
	private BorderPane organizationPane;
	private StackPane menuPane;
	private VBox gameContentPane;
	private StackPane tileGridStackPane;
	private CircleRowPane hintsPane;
	private CircleRowPane mistakesPane;
	private HBox gameButtonRowPane;
	private HBox menuButtonRowPane;
	
	private TileGridAchievement tileGridAchievement;

	private boolean wonGame;
	private boolean gameActive;

	private TileGridWord tileGridWord;
	private TileGridWordAnimationOverlay tileGridWordAnimationPane;

	private DarkModeToggle darkModeToggleMenuButton;
	private HintMenuButton hintMenuButton;
	private AchievementsMenuButton achievementsMenuButton;
	private LeaderboardMenuButton leaderboardMenuButton;
	private ProfileMenuButton profileMenuButton;

	private CircularButton gameSubmitButton;
	private CircularButton gameDeselectButton;
	private CircularButton gameShuffleButton;
	private CircularButton gameViewResultsButton;

	// Keep reference to results pane to avoid re-loading it each time
	private ResultsPane resultsPane;
	private PopupWrapperPane popupPane;

	private boolean submissionAnimationActive;

	public GameSession(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		initAssets();
		initListeners();
	}

	// === === === === === === === === === === === ===
	// === === === === INIT METHODS
	// === === === === === === === === === === === ===

	private void initAssets() {
		setPrefSize(ConnectionsAppLocal.STAGE_WIDTH, ConnectionsAppLocal.STAGE_HEIGHT);

		darkModeToggleMenuButton = new DarkModeToggle(gameSessionContext);

		tileGridWord = new TileGridWord(gameSessionContext);
		tileGridWord.initTileWords();
		tileGridWordAnimationPane = new TileGridWordAnimationOverlay(tileGridWord);
		tileGridStackPane = new StackPane(tileGridWord, tileGridWordAnimationPane);

		tileGridAchievement = new TileGridAchievement(gameSessionContext);
		
		hintMenuButton = new HintMenuButton(gameSessionContext);
		achievementsMenuButton = new AchievementsMenuButton(gameSessionContext);
		leaderboardMenuButton = new LeaderboardMenuButton(gameSessionContext);
		profileMenuButton = new ProfileMenuButton(gameSessionContext);

		mainHeaderText = new Text("Create four groups of four!");
		mainHeaderText.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 18));

		hintsPane = new CircleRowPane("Hints remaining:", gameSessionContext);
		mistakesPane = new CircleRowPane("Mistakes remaining:", gameSessionContext);

		gameShuffleButton = new CircularButton("Shuffle", 88, gameSessionContext, false);
		gameDeselectButton = new CircularButton("Deselect all", 120, gameSessionContext, false);
		gameSubmitButton = new CircularButton("Submit", 88, gameSessionContext, true);
		gameViewResultsButton = new CircularButton("View Results", 160, gameSessionContext, false);

		gameButtonRowPane = new HBox(8);
		gameButtonRowPane.setAlignment(Pos.CENTER);
		gameButtonRowPane.getChildren().addAll(gameShuffleButton, gameDeselectButton, gameSubmitButton);

		menuButtonRowPane = new HBox(10, hintMenuButton, profileMenuButton, leaderboardMenuButton,
				achievementsMenuButton, darkModeToggleMenuButton);
		menuButtonRowPane.setAlignment(Pos.CENTER);
		menuButtonRowPane.setMaxHeight(DarkModeToggle.HEIGHT);
		menuButtonRowPane.setStyle("-fx-alignment: center-right;");

		// exclude the hints pane for now
		gameContentPane = new VBox(24, mainHeaderText, tileGridStackPane, mistakesPane, gameButtonRowPane);
		gameContentPane.setAlignment(Pos.CENTER);

		menuPane = new StackPane(menuButtonRowPane);
		menuPane.setPrefHeight(MENU_PANE_HEIGHT);

		organizationPane = new BorderPane();
		organizationPane.setTop(menuPane);
		organizationPane.setCenter(gameContentPane);
		organizationPane.setPrefSize(ConnectionsAppLocal.STAGE_WIDTH, ConnectionsAppLocal.STAGE_HEIGHT);
		organizationPane.setPadding(new Insets(10));

		getChildren().add(organizationPane);

		helperSetGameButtonsDisabled(false);
		controlsSetNormal();
		refreshStyle();
	}

	private void initListeners() {
		tileGridWord.setOnTileWordSelection(event -> {
			helperUpdateGameButtonStatus();
		});
		gameShuffleButton.setOnAction(event -> {
			tileGridWord.shuffleTileWords();
		});
		gameSubmitButton.setOnAction(event -> {
			sessionSubmissionAttempt();
		});
		gameSessionContext.getStyleManager().setOnDarkModeChange(event -> {
			refreshStyle();
		});
		gameViewResultsButton.setOnAction(event -> {
			screenDisplayResults();
		});
		hintMenuButton.setOnMouseClicked(event -> {
			debugSimulateResultsPane();
		});
		achievementsMenuButton.setOnMouseClicked(event -> {
			screenDisplayAchievements();
		});
		leaderboardMenuButton.setOnMouseClicked(event -> {
			screenDisplayLeaderboard();
		});
		profileMenuButton.setOnMouseClicked(event -> {
			screenDisplayProfile();
		});
	}

	// === === === === === === === === === === === ===
	// === === === === SET CONTROLS
	// === === === === === === === === === === === ===

	private void controlsSetNormal() {
		gameButtonRowPane.getChildren().clear();
		gameButtonRowPane.getChildren().addAll(gameShuffleButton, gameDeselectButton, gameSubmitButton);
		gameContentPane.getChildren().clear();
		gameContentPane.getChildren().addAll(mainHeaderText, tileGridStackPane, mistakesPane, gameButtonRowPane);
	}

	private void controlsSetViewResultsOnly() {
		gameButtonRowPane.getChildren().clear();
		gameButtonRowPane.getChildren().add(gameViewResultsButton);
		gameContentPane.getChildren().clear();
		gameContentPane.getChildren().addAll(mainHeaderText, tileGridStackPane, gameButtonRowPane);
	}

	// === === === === === === === === === === === ===
	// === === === === SCREEN METHODS
	// === === === === === === === === === === === ===

	private void screenDisplayResults() {
		if (!gameActive) {
			if (resultsPane == null) {
				List<Set<Word>> guesses = tileGridWord.getGuesses();
				resultsPane = new ResultsPane(gameSessionContext, wonGame, 123, guesses.size(), guesses);
			}

			helperPopupScreen(resultsPane, "");
		}
	}

	private void screenDisplayAchievements() {
		tileGridAchievement.animateCompletion();
		helperPopupScreen(tileGridAchievement, "Achievements:");
	}

	private void screenDisplayLeaderboard() {
		BorderPane pane = new BorderPane();
		pane.setCenter(new Text("LEADERS"));
		helperPopupScreen(pane, "Leaderboard:");
	}

	private void screenDisplayProfile() {
		BorderPane pane = new BorderPane();
		pane.setCenter(new Text("PROFILE"));
		helperPopupScreen(pane, "Profile:");
	}

	private void helperPopupScreen(Pane pane, String title) {
		if (popupPane == null) {
			popupPane = new PopupWrapperPane(gameSessionContext, pane, title);
			popupPane.setOnGoBackPressed(event -> {
				getChildren().remove(popupPane);
			});
		} else {
			popupPane.setChild(pane);
			popupPane.setTitle(title);
		}

		if (!getChildren().contains(popupPane)) {
			getChildren().add(popupPane);
		}

		popupPane.popup();
	}

	// === === === === === === === === === === === ===
	// === === === === MAIN "SESSION" METHODS
	// === === === === === === === === === === === ===

	public void sessionBeginNewGame() {
		gameActive = true;
		wonGame = false;
	}

	public void sessionReachedEndGame() {
		gameActive = false;

		helperSetGameButtonsDisabled(true);
		tileGridWord.setTileWordDisable(true);

		screenDisplayResults();
		controlsSetViewResultsOnly();
	}

	public void sessionSubmissionAttempt() {
		boolean alreadyGuessed = tileGridWord.checkSelectedAlreadyGuessed();

		if (alreadyGuessed) {
			helperDisplayPopupNotifcation("Already Guessed!", 132.09, POPUP_DEFAULT_DURATION_MS);
		} else {
			tileGridWord.saveSelectedAsGuess();

			int matchCount = tileGridWord.checkNumWordsMatchSelected();
			boolean isCorrect = (matchCount == TileGridWord.MAX_SELECTED);
			boolean isOneAway = (matchCount == TileGridWord.MAX_SELECTED - 1);

			if (isCorrect) {
				SequentialTransition animation = helperCreateAnimationSubmissionCorrect();
				animation.play();
			} else {
				boolean lostGame = (mistakesPane.getNumCircles() == 1);

				SequentialTransition animation = helperCreateAnimationSubmissionIncorrect(lostGame, isOneAway);
				animation.play();
			}
		}
	}

	public void sessionHintUsed() {

	}

	// === === === === === === === === === === === ===
	// === === === === HELPER METHODS
	// === === === === === === === === === === === ===

	private void helperDisplayPopupNotifcation(String message, double width, int duration) {
		NotificationPane popupNotification = new NotificationPane(message, width, gameSessionContext);
		menuPane.getChildren().add(0, popupNotification);
		popupNotification.popup(menuPane, duration);
	}

	private void helperSetGameButtonsDisabled(boolean disabled) {
		if (disabled) {
			gameShuffleButton.setDisable(true);
			gameDeselectButton.setDisable(true);
			gameSubmitButton.setDisable(true);
		} else {
			gameShuffleButton.setDisable(false);
			gameDeselectButton.setDisable(tileGridWord.checkNumWordsMatchSelected() == 0);
			gameSubmitButton.setDisable(tileGridWord.checkNumWordsMatchSelected() < TileGridWord.MAX_SELECTED);
			gameSubmitButton.refreshStyle();
		}
	}

	private void helperUpdateGameButtonStatus() {
		gameDeselectButton.setDisable(tileGridWord.getSelectedTileWordCount() == 0);
		gameSubmitButton.setDisable(tileGridWord.getSelectedTileWordCount() < TileGridWord.MAX_SELECTED);

		gameDeselectButton.refreshStyle();
		gameSubmitButton.refreshStyle();
	}

	private void helperRefreshStyle(StyleManager styleManager, Region region) {
		for (Node node : region.getChildrenUnmodifiable()) {
			if (node instanceof Modular) {
				((Modular) node).refreshStyle();
			} else if (node instanceof Region) {
				helperRefreshStyle(styleManager, (Region) node);
			} else if (node instanceof Text) {
				((Text) node).setFill(styleManager.colorText());
			}
		}
	}

	// === === === === === === === === === === === === === === === === === === ===
	// === === === === HELPER METHODS FOR ANIMATIONS (INCLUDING AUTO SOLVER)
	// === === === === === === === === === === === === === === === === === === ===

	private void helperAutoSolverNextCategory(List<GameAnswerColor> remainingAnswerCategories) {
		if (tileGridWord.getCurrentSolvingRow() < TileGridWord.ROWS) {
			GameAnswerColor currentColorAnswer = remainingAnswerCategories.remove(0);

			tileGridWord.deselectTileWords();
			tileGridWord.selectMatchingAnswerWords(currentColorAnswer);

			SequentialTransition sequentialTransition = new SequentialTransition();
			PauseTransition pauseBeforeSwapTransition = new PauseTransition(Duration.millis(350));
			SequentialTransition swapAndAnswerTileSequence = tileGridWordAnimationPane.getSequenceCorrectAnswer();
			PauseTransition pauseAfterSwapTransition = new PauseTransition(Duration.millis(350));
			sequentialTransition.getChildren().addAll(pauseBeforeSwapTransition, swapAndAnswerTileSequence,
					pauseAfterSwapTransition);

			pauseAfterSwapTransition.setOnFinished(event -> {
				helperAutoSolverNextCategory(remainingAnswerCategories);
			});

			sequentialTransition.play();
		} else {
			PauseTransition pauseBeforeResultsTransition = new PauseTransition(Duration.millis(1000));
			pauseBeforeResultsTransition.setOnFinished(event -> {
				tileGridWord.setTileWordStyleChangeable(true);
				sessionReachedEndGame();
			});
			pauseBeforeResultsTransition.play();
		}
	}

	private void helperAutoSolverBegin() {
		List<DifficultyColor> unansweredColor = tileGridWord.getSortedUnansweredDifficultyColor();

		if (unansweredColor.size() > 0) {
			List<GameAnswerColor> remainingAnswerCategories = new ArrayList<>();
			for (DifficultyColor color : unansweredColor) {
				GameAnswerColor colorAnswer = gameSessionContext.getGameData().getAnswerForColor(color);
				remainingAnswerCategories.add(colorAnswer);
			}

			tileGridWord.setTileWordStyleChangeable(false);
			helperAutoSolverNextCategory(remainingAnswerCategories);
		}
	}

	private SequentialTransition helperCreateAnimationSubmissionIncorrect(boolean lostGame, boolean isOneAway) {
		SequentialTransition sequentialIncorrectTrans = new SequentialTransition();

		PauseTransition placeholderPause = new PauseTransition(Duration.millis(5));
		placeholderPause.setOnFinished(event -> {
			submissionAnimationActive = true;
			helperSetGameButtonsDisabled(true);
			tileGridWord.setTileWordDisable(true);
		});

		ParallelTransition jumpTransition = tileGridWord.getTransitionTileWordJump();

		PauseTransition pauseAfterJump = new PauseTransition(Duration.millis(500));

		SequentialTransition shakeTransition = tileGridWord.getTransitionTileWordShake();
		shakeTransition.setOnFinished(event -> {
			tileGridWord.unsetIncorrectTileWords();
		});

		PauseTransition deselectDelay = new PauseTransition(Duration.millis(500));
		deselectDelay.setOnFinished(event -> {
			tileGridWord.deselectTileWords();
		});

		PauseTransition removeCircleDelay = new PauseTransition(Duration.millis(500));
		removeCircleDelay.setOnFinished(removeCircleEvent -> {
			submissionAnimationActive = false;
			mistakesPane.removeCircle();

			if (lostGame) {
				helperDisplayPopupNotifcation("Next Time", 88.13, POPUP_DEFAULT_DURATION_MS);

				PauseTransition autoSolveDelay = new PauseTransition(Duration.millis(500));
				autoSolveDelay.setOnFinished(event -> {
					helperAutoSolverBegin();
				});

				autoSolveDelay.play();
				helperSetGameButtonsDisabled(true);
				tileGridWord.setTileWordDisable(true);
			} else {
				if (isOneAway) {
					helperDisplayPopupNotifcation("One Away...", 96.09, POPUP_DEFAULT_DURATION_MS);
				}
				helperSetGameButtonsDisabled(false);
				tileGridWord.setTileWordDisable(false);
			}
		});

		sequentialIncorrectTrans.getChildren().addAll(placeholderPause, jumpTransition, pauseAfterJump, shakeTransition,
				deselectDelay, removeCircleDelay);
		return sequentialIncorrectTrans;
	}

	private SequentialTransition helperCreateAnimationSubmissionCorrect() {
		SequentialTransition sequentialCorrectTrans = new SequentialTransition();
		PauseTransition placeholderPause = new PauseTransition(Duration.millis(5));
		placeholderPause.setOnFinished(event -> {
			submissionAnimationActive = true;
		});

		ParallelTransition jumpTransition = tileGridWord.getTransitionTileWordJump();
		SequentialTransition swapAndAnswerTileSequence = tileGridWordAnimationPane.getSequenceCorrectAnswer();
		PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
		PauseTransition endPauseTransition = new PauseTransition(Duration.millis(500));

		endPauseTransition.setOnFinished(event -> {
			submissionAnimationActive = false;
			if (tileGridWord.checkAllCategoriesGuessed()) {
				wonGame = true;
				sessionReachedEndGame();
			} else {
				helperSetGameButtonsDisabled(false);
				tileGridWord.setTileWordDisable(false);
			}
		});

		sequentialCorrectTrans.getChildren().addAll(placeholderPause, jumpTransition, pauseTransition,
				swapAndAnswerTileSequence, endPauseTransition);
		return sequentialCorrectTrans;
	}

	// === === === === === === === === === === === ===
	// === === === === DEBUG METHODS
	// === === === === === === === === === === === ===

	private void debugSimulateResultsPane() {
		long seed = 123456789L;
		Random randGen = new Random(seed);
		GameData gameData = gameSessionContext.getGameData();
		int numGuesses = 3 + randGen.nextInt(6);
		List<Set<Word>> guesses = new ArrayList<>();
		for (int i = 0; i < numGuesses; i++) {
			Set<Word> set = new HashSet<>();

			DifficultyColor[] possibleColors = { DifficultyColor.YELLOW, DifficultyColor.GREEN, DifficultyColor.BLUE,
					DifficultyColor.PURPLE };
			while (set.size() < 4) {
				DifficultyColor randomColor = possibleColors[randGen.nextInt(4)];
				GameAnswerColor randAnswer = gameData.getAnswerForColor(randomColor);
				String[] words = randAnswer.getWords();
				Word randomWord = new Word(words[randGen.nextInt(words.length)], randomColor);
				if (!set.contains(randomWord)) {
					set.add(randomWord);
				}
			}
			guesses.add(set);
		}
		resultsPane = new ResultsPane(gameSessionContext, false, 123, guesses.size(), guesses);
		screenDisplayResults();
	}

	// === === === === === === === === === === === ===
	// === === === === OTHER METHODS
	// === === === === === === === === === === === ===

	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();

		setBackground(new Background(new BackgroundFill(styleManager.colorWholeGameBackground(), null, null)));
		helperRefreshStyle(styleManager, this);
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
