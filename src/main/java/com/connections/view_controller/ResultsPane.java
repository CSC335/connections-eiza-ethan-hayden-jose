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

/**
* The ResultsPane class displays the results of a completed game of Connections.
* It shows information such as the number of attempts, the difficulty colors of the words guessed, the game mode,
* and the time taken for a timed game. It also provides a share button to copy the results to the clipboard.
*/
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

   /**
    * Constructs a new ResultsPane object.
    *
    * @param gameSessionContext The GameSessionContext object that provides access to shared resources.
    * @param playedGameInfo     The PlayedGameInfo object containing information about the completed game.
    */
	public ResultsPane(GameSessionContext gameSessionContext, PlayedGameInfo playedGameInfo) {
		this.gameSessionContext = gameSessionContext;

		if (playedGameInfo == null) {
			playedGameInfo = new PlayedGameInfoClassic(123, 0, 0, 0, new ArrayList<>(), false, ZonedDateTime.now(), ZonedDateTime.now());
		}

		this.playedGameInfo = playedGameInfo;
		initAssets();
	}

   /**
    * Initializes and sets up the UI components for the ResultsPane.
    */
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

   /**
    * Initializes the header section of the ResultsPane, including the title label and puzzle number label.
    */
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

   /**
    * Initializes the grid pane that displays the attempts made during the game.
    */
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

   /**
    * Initializes the timer layout that displays the time remaining until the next puzzle.
    */
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
			ZonedDateTime now = ZonedDateTime.now();
			ZonedDateTime midnight = now.toLocalDate().atStartOfDay(now.getZone()).plusDays(1);

			java.time.Duration duration = java.time.Duration.between(now, midnight);
//			
//			LocalDateTime now = LocalDateTime.now();
//			LocalDateTime midnight = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);
//
//			long nowMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//			long midnightMillis = midnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//
//			Duration duration = Duration.millis(midnightMillis - nowMillis);

			long hours = (long) duration.toHours();
			long minutes = (long) (duration.toMinutes() % 60);
			long seconds = (long) (duration.toSeconds() % 60);

			String timerText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
			timerLabel.setText(timerText);
		}));

		timerTimeline.setCycleCount(Animation.INDEFINITE);
		timerTimeline.play();
	}

   /**
    * Initializes the share button that copies the results to the clipboard when clicked.
    */
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

   /**
    * Initializes the game type label and time trial completion layout, which displays information
    * specific to the game mode played (Classic or Time Trial).
    */
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

   /**
    * Formats the given time in seconds to a string in the format "mm:ss".
    *
    * @param seconds The time in seconds to be formatted.
    * @return The formatted time string.
    */
	private String formatTimeMinSec(int seconds) {
		int minutes = seconds / 60;
		int remainingSeconds = seconds % 60;
		return String.format("%02d:%02d", minutes, remainingSeconds);
	}

   /**
    * Copies the game results to the system clipboard in a formatted string.
    */
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

   /**
    * Refreshes the style of the ResultsPane and its components based on the current style settings.
    */
	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();
		
		shareButton.refreshStyle();
		titleLabel.setTextFill(styleManager.colorText());
		puzzleNumberLabel.setTextFill(styleManager.colorText());
		nextPuzzleInLabel.setTextFill(styleManager.colorText());
		timerLabel.setTextFill(styleManager.colorText());
		gameTypeLabel.setTextFill(styleManager.colorText());
		if (copiedToClipboardNotification != null) {
			copiedToClipboardNotification.refreshStyle();
		}
	}

   /**
    * Returns the GameSessionContext object associated with this ResultsPane.
    *
    * @return The GameSessionContext object.
    */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
