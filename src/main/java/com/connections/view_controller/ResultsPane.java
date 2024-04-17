package com.connections.view_controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import com.connections.model.DifficultyColor;
import com.connections.model.Word;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ResultsPane extends StackPane implements Modular {
	private GameSessionContext gameSessionContext;
	private boolean wonGame;
	private int puzzleNumber;
	private List<Set<Word>> guesses;

	private BorderPane entireLayout;
	private VBox resultsLayout;
	private Label titleLabel;
	private Label puzzleNumberLabel;
	private GridPane attemptsGridPane;
	private VBox timerLayout;
	private Label nextPuzzleInLabel;
	private Label timerLabel;
	private Timeline timerTimeline;
	private CircularButton shareButton;
	private NotificationPane copiedToClipboardNotification;

	public ResultsPane(GameSessionContext gameSessionContext,  boolean wonGame, int puzzleNumber, int guessCount,
			List<Set<Word>> guesses) {
		this.gameSessionContext = gameSessionContext;
		this.wonGame = wonGame;
		this.puzzleNumber = puzzleNumber;
		this.guesses = guesses;
		initAssets();
	}

	private void initAssets() {
//		int bestWidth = 667;
//		int bestHeight = 402 + (guessCount * 40) + ((guessCount - 1) * TileGridWord.GAP);
//		int bestHeight = 282 + (guessCount * 40) + ((guessCount - 1) * TileGridWord.GAP);
//		int bestHeight = 492 + (guessCount * 40) + ((guessCount - 1) * TileGridWord.GAP);
//		setMinSize(bestWidth, bestHeight);
//		setMaxSize(bestWidth, bestHeight);

		entireLayout = new BorderPane();

		resultsLayout = new VBox(0);
		resultsLayout.setAlignment(Pos.TOP_CENTER);

		initHeader();
		initAttemptsGrid();
		initNextPuzzleTimer();
		initShareButton();

		resultsLayout.getChildren().addAll(titleLabel, puzzleNumberLabel, attemptsGridPane, timerLayout, shareButton);
//		entireLayout.setCenter(resultsLayout);
//		entireLayout.setBottom(shareButton);

		BorderPane.setAlignment(shareButton, Pos.CENTER);
//		entireLayout.setPadding(new Insets(18));

		getChildren().add(resultsLayout);
		refreshStyle();
	}

	private void initHeader() {
		titleLabel = wonGame ? new Label("Perfect!") : new Label("Next Time!");
		titleLabel.setFont(gameSessionContext.getStyleManager().getFont("karnakpro-condensedblack", 36));
		VBox.setMargin(titleLabel, new Insets(80, 0, 0, 0));

		puzzleNumberLabel = new Label("Connections #" + puzzleNumber);
		puzzleNumberLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 20));
		VBox.setMargin(titleLabel, new Insets(18, 0, 0, 0));
	}

	private void initAttemptsGrid() {
		attemptsGridPane = new GridPane();
		attemptsGridPane.setVgap(TileGridWord.GAP);
		attemptsGridPane.setAlignment(Pos.CENTER);
		VBox.setMargin(attemptsGridPane, new Insets(20, 0, 0, 0));

		int i = 0;
		for (Set<Word> previousGuess : guesses) {
			int j = 0;
			for (Word guess : previousGuess) {
				DifficultyColor colorCategory = guess.getColor();
				Color rectangleColor = gameSessionContext.getStyleManager().colorDifficulty(colorCategory);
				Rectangle square = new Rectangle(40, 40, rectangleColor);
				square.setArcWidth(10);
				square.setArcHeight(10);
				attemptsGridPane.add(square, j, i);
				j++;
			}
			i++;
		}
	}

	private void initNextPuzzleTimer() {
		nextPuzzleInLabel = new Label("NEXT PUZZLE IN");
		nextPuzzleInLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 20));
		nextPuzzleInLabel.setAlignment(Pos.CENTER);
		VBox.setMargin(nextPuzzleInLabel, new Insets(20, 0, 0, 0));

		timerLabel = new Label();
		timerLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 40));
		timerLabel.setAlignment(Pos.CENTER);
		VBox.setMargin(timerLabel, new Insets(-10, 0, 0, 0));

		timerLayout = new VBox(5, nextPuzzleInLabel, timerLabel);
		timerLayout.setAlignment(Pos.CENTER);
		timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime midnight = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);

			long nowMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			long midnightMillis = midnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

			Duration duration = Duration.millis(midnightMillis - nowMillis);

			long hours = (long) duration.toHours();
			long minutes = (long) (duration.toMinutes() % 60);
			long seconds = (long) (duration.toSeconds() % 60);

			String timerText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
			timerLabel.setText(timerText);
		}));

		timerTimeline.setCycleCount(Animation.INDEFINITE);
		timerTimeline.play();
	}

	private void initShareButton() {
		shareButton = new CircularButton("Share Your Results", 162, gameSessionContext, true);
		shareButton.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
		VBox.setMargin(shareButton, new Insets(21, 0, 20, 0));

		shareButton.setOnAction(event -> {
			copiedToClipboardNotification = new NotificationPane("Copied Results to Clipboard", 204.54,
					gameSessionContext);
			getChildren().add(copiedToClipboardNotification);
			copiedToClipboardNotification.popup(this, 1000);
			copyResultsToClipboard();
		});
	}

	private void copyResultsToClipboard() {
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
		String copiedString = "Connections\nPuzzle #" + puzzleNumber + "\n";
		for (Set<Word> previousGuess : guesses) {
			for (Word guess : previousGuess) {
				switch (guess.getColor()) {
				case YELLOW:
					copiedString += "\ud83d\udfe8";
					break;
				case GREEN:
					copiedString += "\ud83d\udfe9";
					break;
				case BLUE:
					copiedString += "\ud83d\udfe6";
					break;
				case PURPLE:
					copiedString += "\ud83d\udfea";
					break;
				}
			}
			copiedString += "\n";
		}
		content.putString(copiedString);
		clipboard.setContent(content);
	}

	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();

//		setStyle(styleManager.resultsPaneStyle() + " -fx-border-color: red;");
//		setStyle(styleManager.resultsPaneStyle());
		shareButton.refreshStyle();
		titleLabel.setTextFill(styleManager.colorText());
		puzzleNumberLabel.setTextFill(styleManager.colorText());
		nextPuzzleInLabel.setTextFill(styleManager.colorText());
		timerLabel.setTextFill(styleManager.colorText());
		if(copiedToClipboardNotification != null) {
			copiedToClipboardNotification.refreshStyle();
		}
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
