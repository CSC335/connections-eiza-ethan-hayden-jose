package com.connections.view_controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Set;

import com.connections.model.DifficultyColor;
import com.connections.model.PlayedGameInfo;
import com.connections.model.PlayedGameInfoClassic;
import com.connections.model.PlayedGameInfoTimed;
import com.connections.model.Word;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ResultsPane extends StackPane implements Modular {
	private GameSessionContext gameSessionContext;
	private PlayedGameInfo playedGameInfo;

	private VBox resultsLayout;
	private Label titleLabel;
	private Label puzzleNumberLabel;
	private GridPane attemptsGridPane;
	private VBox timerLayout;
	private Label nextPuzzleInLabel;
	private Label timerLabel;
	private VBox timeTrialCompletionLayout;
	private Label gameTypeLabel;
	private Label timeTrialCompletionLabel;
	private Label timeTrialTimeLabel;
	private Timeline timerTimeline;
	private CircularButton shareButton;
	private NotificationPane copiedToClipboardNotification;

	public ResultsPane(GameSessionContext gameSessionContext, PlayedGameInfo playedGameInfo) {
		this.gameSessionContext = gameSessionContext;

		if (playedGameInfo == null) {
			playedGameInfo = new PlayedGameInfoClassic(123, 0, 0, 0, new ArrayList<>(), false, ZonedDateTime.now(), ZonedDateTime.now());
		}

		this.playedGameInfo = playedGameInfo;
		initAssets();
	}

	private void initAssets() {
		resultsLayout = new VBox(0);
		resultsLayout.setAlignment(Pos.TOP_CENTER);

		initHeader();
		initAttemptsGrid();
		initNextPuzzleTimer();
		initShareButton();
		initGameTypeAndTimeTrialContent();

		resultsLayout.getChildren().addAll(titleLabel, puzzleNumberLabel, attemptsGridPane, timerLayout,
				timeTrialCompletionLayout, shareButton);

		getChildren().add(resultsLayout);
		refreshStyle();
	}

	private void initHeader() {
		titleLabel = new Label();
		if (playedGameInfo.wasWon() && playedGameInfo.getGuesses().size() > 4) {
			titleLabel.setText("Solid!");
		} else if (playedGameInfo.wasWon() && playedGameInfo.getGuesses().size() == 4) {
			titleLabel.setText("Perfect!");
		} else {
			titleLabel.setText("Next Time!");
		}
		titleLabel.setFont(gameSessionContext.getStyleManager().getFont("karnakpro-condensedblack", 36));
		VBox.setMargin(titleLabel, new Insets(80, 0, 0, 0));

		puzzleNumberLabel = new Label("Connections #" + playedGameInfo.getPuzzleNumber());
		puzzleNumberLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 20));
		VBox.setMargin(titleLabel, new Insets(18, 0, 0, 0));
	}

	private void initAttemptsGrid() {
		attemptsGridPane = new GridPane();
		attemptsGridPane.setVgap(TileGridWord.GAP);
		attemptsGridPane.setAlignment(Pos.CENTER);
		VBox.setMargin(attemptsGridPane, new Insets(20, 0, 0, 0));

		int i = 0;
		for (Set<Word> previousGuess : playedGameInfo.getGuesses()) {
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

	private void initGameTypeAndTimeTrialContent() {
		gameTypeLabel = new Label("PLAYED IN CLASSIC MODE");
		gameTypeLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 20));
		gameTypeLabel.setAlignment(Pos.CENTER);
		VBox.setMargin(gameTypeLabel, new Insets(20, 0, 0, 0));

		switch (playedGameInfo.getGameType()) {
		case CLASSIC:
			gameTypeLabel.setText("PLAYED IN CLASSIC MODE");
			break;
		case TIME_TRIAL:
			gameTypeLabel.setText("PLAYED IN TIME TRIAL MODE");
			break;
		default:
		}

		timeTrialCompletionLabel = new Label("...");
		timeTrialCompletionLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 20));
		timeTrialCompletionLabel.setAlignment(Pos.CENTER);
		VBox.setMargin(timeTrialCompletionLabel, new Insets(20, 0, 0, 0));

		timeTrialTimeLabel = new Label();
		timeTrialTimeLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 40));
		timeTrialTimeLabel.setAlignment(Pos.CENTER);
		VBox.setMargin(timeTrialTimeLabel, new Insets(-10, 0, 0, 0));

		if (playedGameInfo.getGameType() == GameSession.GameType.TIME_TRIAL
				&& playedGameInfo instanceof PlayedGameInfoTimed) {
			timeTrialCompletionLabel.setVisible(true);
			timeTrialTimeLabel.setVisible(true);

			PlayedGameInfoTimed timedGameInfo = (PlayedGameInfoTimed) playedGameInfo;

			if (timedGameInfo.isCompletedBeforeTimeLimit()) {
				timeTrialCompletionLabel.setText("COMPLETED IN");
				timeTrialTimeLabel.setText(formatTimeMinSec(timedGameInfo.getTimeCompleted()));
			} else {
				timeTrialCompletionLabel.setText("COULD NOT COMPLETE IN");
				timeTrialTimeLabel.setText(formatTimeMinSec(timedGameInfo.getTimeLimit()));
			}
		} else {
			timeTrialCompletionLabel.setVisible(false);
			timeTrialTimeLabel.setVisible(false);
		}

		timeTrialCompletionLayout = new VBox(5, gameTypeLabel, timeTrialCompletionLabel, timeTrialTimeLabel);
		timeTrialCompletionLayout.setAlignment(Pos.CENTER);
	}

	private String formatTimeMinSec(int seconds) {
		int minutes = seconds / 60;
		int remainingSeconds = seconds % 60;
		return String.format("%02d:%02d", minutes, remainingSeconds);
	}

	private void copyResultsToClipboard() {
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
		String copiedString = "Connections\nPuzzle #" + playedGameInfo.getPuzzleNumber() + "\n";
		for (Set<Word> previousGuess : playedGameInfo.getGuesses()) {
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
		if (copiedToClipboardNotification != null) {
			copiedToClipboardNotification.refreshStyle();
		}
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
