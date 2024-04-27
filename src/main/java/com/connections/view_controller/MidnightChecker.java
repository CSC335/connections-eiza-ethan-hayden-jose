package com.connections.view_controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class MidnightChecker {
	// This is to prevent the timeline from counting indefinitely (might cause
	// issues with JPro).
	// Will go at most for 24 hours plus 10 seconds.
	private static final int MAX_CYCLE_COUNT = (24 * 60 * 60) + 10;

	// Set this to some positive number if the trigger should occur some seconds
	// before midnight.
	public static final int TRIGGER_SEC_THRESHOLD = 1;

	private Timeline timeline;
	private KeyFrame keyFrame;
	private boolean isRunning;
	private EventHandler<ActionEvent> onMidnight;

	public MidnightChecker() {
		keyFrame = new KeyFrame(Duration.seconds(1), event -> {
			ZonedDateTime currentDate = ZonedDateTime.now();
			ZonedDateTime midnightDate = currentDate.toLocalDate().atStartOfDay(currentDate.getZone()).plusDays(1);

			java.time.Duration durationUntilMidnight = java.time.Duration.between(currentDate, midnightDate);
			
			if (durationUntilMidnight.toSeconds() <= TRIGGER_SEC_THRESHOLD) {
				if (onMidnight != null) {
					onMidnight.handle(new ActionEvent(this, null));
				}
				timeline.stop();
			}
		});

		timeline = new Timeline(keyFrame);
		timeline.setCycleCount(MAX_CYCLE_COUNT);
	}

	public void start() {
		if (timeline != null && !isRunning) {
			timeline.play();
			isRunning = true;
		}
	}

	public void stop() {
		if (timeline != null && isRunning) {
			timeline.stop();
			isRunning = false;
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setOnMidnight(EventHandler<ActionEvent> onMidnight) {
		this.onMidnight = onMidnight;
	}
}
