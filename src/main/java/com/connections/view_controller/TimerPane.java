package com.connections.view_controller;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;

/**
* The TimerPane class displays a timer countdown.
* It extends the StackPane class and implements the Modular interface.
*/
public class TimerPane extends StackPane implements Modular {
	public static final int BACKGROUND_PANE_WIDTH = 160;
	public static final int BACKGROUND_PANE_HEIGHT = 60;

	private Pane backgroundPane;
	private Label counterLabel;
	private Timeline timerTimeLine;
	private SVGPath timerSVG;
	private HBox counterLayout;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private int durationSeconds;
	private int prevSecondsLeftBuffer;
	private GameSessionContext gameSessionContext;
	private EventHandler<ActionEvent> onFinishedTimer;
	private EventHandler<ActionEvent> onSecondPassedBy;
	private EventHandler<ActionEvent> onDisappear;
	private boolean timerActive;
	private boolean timerFinished;
	private boolean timerStopped;
	private boolean justInitialized;

   /**
    * Constructs a new TimerPane instance.
    *
    * @param gameSessionContext The GameSessionContext object for accessing shared resources.
    * @param durationSeconds    The duration of the timer in seconds.
    */
	public TimerPane(GameSessionContext gameSessionContext, int durationSeconds) {
		this.gameSessionContext = gameSessionContext;
		this.durationSeconds = durationSeconds;
		initAssets();
	}

   /**
    * Initializes the assets for the TimerPane.
    */
	private void initAssets() {
		timerActive = false;
		timerFinished = false;
		timerStopped = true;
		justInitialized = true;

		setMinSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);
		setMaxSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);

		backgroundPane = new Pane();
		backgroundPane.setMinSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);
		backgroundPane.setMaxSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);

		timerSVG = new SVGPath();
		timerSVG.setContent(
				"M15.24 2H8.76004C5.00004 2 4.71004 5.38 6.74004 7.22L17.26 16.78C19.29 18.62 19 22 15.24 22H8.76004C5.00004 22 4.71004 18.62 6.74004 16.78L17.26 7.22C19.29 5.38 19 2 15.24 2Z");
		timerSVG.setStrokeWidth(1.5);
		timerSVG.setStrokeLineCap(StrokeLineCap.ROUND);
		timerSVG.setStrokeLineJoin(StrokeLineJoin.ROUND);

		KeyFrame timeLineKeyFrame = new KeyFrame(Duration.millis(100), event -> {
			updateTimerLabel();
		});

		timerTimeLine = new Timeline();
		timerTimeLine.setCycleCount(Animation.INDEFINITE);
		timerTimeLine.getKeyFrames().add(timeLineKeyFrame);

		counterLabel = new Label(formatTime(durationSeconds));

		counterLayout = new HBox(16, timerSVG, counterLabel);
		counterLayout.setPadding(new Insets(10));
		counterLayout.setAlignment(Pos.CENTER);

		setAlignment(Pos.CENTER);
		getChildren().addAll(backgroundPane, counterLayout);
		setVisible(false);
		refreshStyle();
	}

   /**
    * Returns the time left on the timer in seconds.
    *
    * @return The time left in seconds.
    */
	public int getTimeLeft() {
		if (timerActive) {
			return getSecondsLeft(ZonedDateTime.now());
		} else if (timerFinished || justInitialized) {
			return 0;
		} else {
			return getSecondsLeft(endTime);
		}
	}

   /**
    * Returns the elapsed time in seconds since the timer started.
    *
    * @return The elapsed time in seconds.
    */
	public int getElapsedTime() {
	    if (timerActive) {
	    	return (int) ChronoUnit.SECONDS.between(startTime, ZonedDateTime.now());
	    } else if (timerFinished || justInitialized) {
	        return durationSeconds;
	    } else {
	        return (int) ChronoUnit.SECONDS.between(startTime, endTime);
	    }
	}

   /**
    * Restarts the timer with the specified start time.
    *
    * @param startTime The start time for the timer.
    */
	public void restartTimer(ZonedDateTime startTime) {
		this.startTime = startTime;
		prevSecondsLeftBuffer = durationSeconds;
		updateTimerLabel();
		timerTimeLine.play();
		timerActive = true;
		timerFinished = false;
		timerStopped = false;
		justInitialized = false;
	}

   /**
    * Starts the timer and makes it appear with a fade-in animation.
    */
	public void appearAndStart() {
	    appearAndStart(ZonedDateTime.now());
	}

   /**
    * Starts the timer and makes it appear with a fade-in animation at the specified start time.
    *
    * @param startTime The start time for the timer.
    */
	public void appearAndStart(ZonedDateTime startTime) {
		PauseTransition delay = new PauseTransition(Duration.millis(100));
		delay.setOnFinished(event -> {
			setVisible(true);
		});

		FadeTransition fadeIn = new FadeTransition(Duration.millis(250), this);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);

		fadeIn.setOnFinished(event -> {
			restartTimer(startTime);
		});

		SequentialTransition sequence = new SequentialTransition(delay, fadeIn);
		sequence.play();
	}

   /**
    * Stops the timer and makes it disappear with a fade-out animation.
    */
	public void disappear() {
		stopTimer();
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.play();

		fadeOut.setOnFinished(event -> {
			if (onDisappear != null) {
				onDisappear.handle(new ActionEvent(this, null));
			}
			setVisible(false);
		});
	}

   /**
    * Stops the timer.
    */
	public void stopTimer() {
		if (timerActive) {
			timerActive = false;
			timerStopped = true;
			timerTimeLine.stop();
			endTime = ZonedDateTime.now();
		}
	}

   /**
    * Returns whether the timer is currently active.
    *
    * @return true if the timer is active, false otherwise.
    */
	public boolean isTimerActive() {
		return timerActive;
	}

   /**
    * Returns whether the timer has finished.
    *
    * @return true if the timer has finished, false otherwise.
    */
	public boolean isTimerFinished() {
		return timerFinished;
	}

   /**
    * Returns whether the timer has been stopped.
    *
    * @return true if the timer has been stopped, false otherwise.
    */
	public boolean isTimerStopped() {
		return timerStopped;
	}

   /**
    * Sets the event handler to be called when the timer finishes.
    *
    * @param onFinishedTimer The event handler to be called when the timer finishes.
    */
	public void setOnFinishedTimer(EventHandler<ActionEvent> onFinishedTimer) {
		this.onFinishedTimer = onFinishedTimer;
	}

	/*
	 * The main purpose of being able to call code when every second passes is so
	 * that the current time trial state can be saved in case the user closes their
	 * browser suddenly
	 */
	// PLEASE PLEASE do not remove this method when refactoring until you know that
	// we do not have that use case anymore.
	
   /**
    * Sets the event handler to be called when a second passes by on the timer.
    *
    * @param onSecondPassedBy The event handler to be called when a second passes by.
    */
	public void setOnSecondPassedBy(EventHandler<ActionEvent> onSecondPassedBy) {
		this.onSecondPassedBy = onSecondPassedBy;
	}

   /**
    * Sets the event handler to be called when the timer disappears.
    *
    * @param onDisappear The event handler to be called when the timer disappears.
    */
	public void setOnDisappear(EventHandler<ActionEvent> onDisappear) {
		this.onDisappear = onDisappear;
	}

   /**
    * Updates the timer label with the remaining time.
    */
	private void updateTimerLabel() {
		int secondsLeft = getSecondsLeft(ZonedDateTime.now());

		if (secondsLeft != prevSecondsLeftBuffer) {
			prevSecondsLeftBuffer = secondsLeft;
			if (onSecondPassedBy != null) {
				onSecondPassedBy.handle(new ActionEvent(this, null));
			}
		}

		if (secondsLeft >= 0) {
			counterLabel.setText(formatTime(secondsLeft));
		}

		if (secondsLeft <= 0) {
			finishTimer();
		}
	}

   /**
    * Returns the number of seconds left on the timer based on the current time.
    *
    * @param current The current time.
    * @return The number of seconds left.
    */
	private int getSecondsLeft(ZonedDateTime current) {
		if (current == null) {
			return 0;
		}
		long secondsElapsed = ChronoUnit.SECONDS.between(startTime, current);
		return durationSeconds - (int) secondsElapsed;
	}

   /**
    * Finishes the timer and triggers any associated events or animations.
    */
	private void finishTimer() {
		timerFinished = true;
		stopTimer();

		if (onFinishedTimer != null) {
			onFinishedTimer.handle(new ActionEvent(this, null));
		}

		FadeTransition pulseTransition = new FadeTransition(Duration.millis(250), counterLabel);
		pulseTransition.setFromValue(1.0);
		pulseTransition.setToValue(0.25);
		pulseTransition.setAutoReverse(true);
		pulseTransition.setCycleCount(8);
		pulseTransition.play();
	}

   /**
    * Formats the given time in seconds to a string in the format "mm:ss".
    *
    * @param seconds The time in seconds to be formatted.
    * @return The formatted time string.
    */
	private String formatTime(int seconds) {
		int minutes = seconds / 60;
		int remainingSeconds = seconds % 60;
		return String.format("%02d:%02d", minutes, remainingSeconds);
	}

   /**
    * Refreshes the style of the TimerPane and its components based on the current style settings.
    */
	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();

		backgroundPane.setOpacity(0.75);
		backgroundPane.setBackground(new Background(new BackgroundFill(styleManager.timerBackground(), new CornerRadii(30), null)));

		timerSVG.setFill(styleManager.colorSVGFill());

		counterLabel.setFont(styleManager.getFont("franklin-normal", 700, 32));
		counterLabel.setTextFill(styleManager.colorSVGFill());
	}

   /**
    * Returns the GameSessionContext object associated with this TimerPane.
    *
    * @return The GameSessionContext object.
    */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
