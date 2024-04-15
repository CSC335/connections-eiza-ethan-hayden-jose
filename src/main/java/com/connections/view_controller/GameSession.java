package com.connections.view_controller;

import com.connections.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.SVGPath;

public class GameSession extends Pane implements Modular {
	private static final int POPUP_DEFAULT_DURATION_MS = 1000;

	private GameSessionContext gameSessionContext;

	private Text mainHeaderText;
	private BorderPane organizationPane;
	private VBox gameContentPane;
	private StackPane tileGridStackPane;
	private CircleRowPane hintsPane;
	private CircleRowPane mistakesPane;
	private HBox gameButtonRowPane;
	private HBox menuButtonRowPane;
	
	private Button viewResultsButton;

	private boolean wonGame;
	private boolean gameActive;

	private TileGridWord tileGridWord;
	private TileGridWordAnimationOverlay tileGridWordAnimationPane;

	private DarkModeToggle darkModeToggleMenuButton;
	private HintMenuButton hintMenuButton;
	private AchievementsMenuButton achievementsMenuButton;
	private LeaderboardMenuButton leaderboardMenuButton;

	private CircularButton gameSubmitButton;
	private CircularButton gameDeselectButton;
	private CircularButton gameShuffleButton;
	
	private boolean submissionAnimationActive;

	public GameSession(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		initAssets();
	}

	private void initAssets() {
//		setPrefSize(GameBoard.STAGE_WIDTH, GameBoard.STAGE_HEIGHT);
//		setStyle("-fx-border-color: red;");
		
		darkModeToggleMenuButton = new DarkModeToggle(gameSessionContext);

		tileGridWord = new TileGridWord(gameSessionContext);
		tileGridWordAnimationPane = new TileGridWordAnimationOverlay(tileGridWord);
		tileGridStackPane = new StackPane(tileGridWord, tileGridWordAnimationPane);

		hintMenuButton = new HintMenuButton(gameSessionContext);
		achievementsMenuButton = new AchievementsMenuButton(gameSessionContext);
		leaderboardMenuButton = new LeaderboardMenuButton(gameSessionContext);

		mainHeaderText = new Text("Create four groups of four!");
		mainHeaderText.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 18));

		hintsPane = new CircleRowPane("Hints remaining:", gameSessionContext);
		mistakesPane = new CircleRowPane("Mistakes remaining:", gameSessionContext);

		gameSubmitButton = new CircularButton("Shuffle", 88, gameSessionContext);
		gameDeselectButton = new CircularButton("Deselect all", 120, gameSessionContext);
		gameShuffleButton = new CircularButton("Submit", 88, gameSessionContext);

		gameButtonRowPane = new HBox(8);
		gameButtonRowPane.setAlignment(Pos.CENTER);
		gameButtonRowPane.getChildren().addAll(gameShuffleButton, gameDeselectButton, gameSubmitButton);

		menuButtonRowPane = new HBox(10, hintMenuButton, leaderboardMenuButton, achievementsMenuButton,
				darkModeToggleMenuButton);
		menuButtonRowPane.setStyle("-fx-alignment: center-right;");

		gameContentPane = new VBox(24, mainHeaderText, tileGridStackPane, hintsPane, mistakesPane, gameButtonRowPane);
		gameContentPane.setAlignment(Pos.CENTER);

		organizationPane = new BorderPane();
		organizationPane.setPadding(new Insets(10));
		organizationPane.setTop(menuButtonRowPane);
		organizationPane.setCenter(gameContentPane);

		getChildren().add(organizationPane);
	}

	private void initListeners() {

	}

	public void sessionBeginNewGame() {
		gameActive = true;
		wonGame = false;
	}

	public void sessionSkipToEnd() {

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

			// correct
			if (isCorrect) {
				SequentialTransition animation = helperCreateAnimationSubmissionCorrect();
				animation.play();
				// incorrect
			} else {
				boolean lostGame = (mistakesPane.getNumCircles() == 1);
				
				SequentialTransition animation = helperCreateAnimationSubmissionIncorrect(lostGame, isOneAway);
				animation.play();
			}
		}
	}

	private void helperDisplayPopupNotifcation(String message, double width, int duration) {
		NotificationPane popupNotification = new NotificationPane(message, width, gameSessionContext);
		tileGridStackPane.getChildren().add(popupNotification);
		popupNotification.popup(tileGridStackPane, duration);
//		PauseTransition pause = new PauseTransition(Duration.millis(duration));
//		tileGridWord.setTileWordDisable(true);
		
//		pause.setOnFinished(event -> {
//			if(tileGridStackPane.getChildren().contains(popupNotification)) {
//				tileGridStackPane.getChildren().remove(popupNotification);
//			}
//		});
//		pause.play();
	}

	public void sessionHintUsed() {

	}

	public void sessionReachedEndGame() {
		gameActive = false;
		tileGridWord.setTileWordDisable(true);
		gameContentPane.getChildren().removeAll(gameButtonRowPane, mistakesPane);
		
		if(hintsPane != null && gameContentPane.getChildren().contains(hintsPane)) {
			gameContentPane.getChildren().remove(hintsPane);
		}
		
		viewResultsButton = new CircularButton("View Results", 160, gameSessionContext);
		
//		disableGameBoard();
//		gameDeselect();
//		showResultsPane((Stage) wholeGameStackPane.getScene().getWindow());
	}

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
//				showResultsPane((Stage) wholeGameStackPane.getScene().getWindow());
				tileGridWord.setTileWordStyleChangeable(true);
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
		
		ParallelTransition shakeTransition = tileGridWord.getTransitionTileWordShake();
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
			helperSetGameButtonsDisabled(false);
			tileGridWord.setTileWordDisable(false);
			
			if (lostGame) {
				helperDisplayPopupNotifcation("Next Time", 88.13, POPUP_DEFAULT_DURATION_MS);
				sessionReachedEndGame();
				
				PauseTransition autoSolveDelay = new PauseTransition(Duration.millis(500));
				autoSolveDelay.setOnFinished(event -> {
					helperAutoSolverBegin();
				});
			} else {
				if(isOneAway) {
					helperDisplayPopupNotifcation("One Away...", 96.09, POPUP_DEFAULT_DURATION_MS);
				}
				helperSetGameButtonsDisabled(false);
				tileGridWord.setTileWordDisable(false);
			}
		});
		
		sequentialIncorrectTrans.getChildren().addAll(placeholderPause, jumpTransition, pauseAfterJump, shakeTransition, deselectDelay, removeCircleDelay);
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
		pauseTransition.setOnFinished(event -> {
			submissionAnimationActive = false;
			if (tileGridWord.checkAllCategoriesGuessed()) {
				wonGame = true;
				sessionReachedEndGame();
			} else {
				helperSetGameButtonsDisabled(false);
				tileGridWord.setTileWordDisable(false);
			}
		});
		
		sequentialCorrectTrans.getChildren().addAll(placeholderPause, jumpTransition, pauseTransition, swapAndAnswerTileSequence, pauseTransition);
		return sequentialCorrectTrans;
	}

	private void helperSetGameButtonsDisabled(boolean disabled) {
		if(disabled) {
			gameShuffleButton.setDisable(true);
			gameDeselectButton.setDisable(true);
			gameSubmitButton.setDisable(true);
			gameSubmitButton.setStyle(gameSessionContext.getStyleManager().buttonStyle());
		} else {
			gameShuffleButton.setDisable(false);
			if(tileGridWord.checkNumWordsMatchSelected() > 0) {
				gameSubmitButton.setDisable(false);
			}
		}
	}
	
	private void helperUpdateGameButtonStatus() {
		gameDeselectButton.setDisable(tileGridWord.getSelectedTileWordCount() == 0);
		gameSubmitButton.setDisable(tileGridWord.getSelectedTileWordCount() != TileGridWord.MAX_SELECTED);

		// remove?
		gameDeselectButton.setStyle(gameSessionContext.getStyleManager().buttonStyle());

		if (tileGridWord.getSelectedTileWordCount() == TileGridWord.MAX_SELECTED) {
			gameSubmitButton.setStyle(gameSessionContext.getStyleManager().submitButtonFillStyle());
		} else {
			gameSubmitButton.setStyle(gameSessionContext.getStyleManager().buttonStyle());
		}
	}

	@Override
	public void refreshStyle() {

	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
