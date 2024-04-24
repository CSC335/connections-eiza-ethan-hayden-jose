package com.connections.view_controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The NotificationPane class represents a pane that displays a notification message.
 * It appears and disappears with a fade animation.
 */
public class NotificationPane extends StackPane implements Modular {
	protected static final int HEIGHT = 42;

	private static final int FADE_DURATION_MS = 100;
	private Rectangle rectangle;
	private Text text;
	private GameSessionContext gameSessionContext;

    /**
     * Constructs a new NotificationPane with the specified message, width, and GameSessionContext.
     *
     * @param message            the message to be displayed in the notification pane
     * @param width              the width of the notification pane
     * @param gameSessionContext the GameSessionContext used by the notification pane
     */
	public NotificationPane(String message, double width, GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;

		rectangle = new Rectangle(width, HEIGHT);
		rectangle.setArcWidth(10);
		rectangle.setArcHeight(10);

		text = new Text(message);
		text.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
		text.setStyle("-fx-text-alignment: center;");

		getChildren().addAll(rectangle, text);
		getStyleClass().add("popup-pane");

		refreshStyle();
	}

    /**
     * Displays the notification pane with a popup animation on the specified parent pane for the given duration.
     *
     * @param parentPane the parent pane to display the notification pane on
     * @param duration   the duration (in milliseconds) for which the notification pane should be displayed
     */
	public void popup(Pane parentPane, int duration) {
		FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_DURATION_MS), this);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);

		FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_DURATION_MS), this);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);

		PauseTransition pause = new PauseTransition(Duration.millis(duration));

		fadeOut.setOnFinished(event -> {
			if(parentPane.getChildren().contains(this)) {
				parentPane.getChildren().remove(this);
			}
		});

		SequentialTransition sequence = new SequentialTransition(fadeIn, pause, fadeOut);
		sequence.play();
	}

    /**
     * Refreshes the style of the notification pane based on the current style manager.
     */
	@Override
	public void refreshStyle() {
		rectangle.setFill(gameSessionContext.getStyleManager().colorPopupBackground());
		text.setFill(gameSessionContext.getStyleManager().colorPopupText());
	}

    /**
     * Returns the GameSessionContext associated with the notification pane.
     *
     * @return the GameSessionContext associated with the notification pane
     */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
