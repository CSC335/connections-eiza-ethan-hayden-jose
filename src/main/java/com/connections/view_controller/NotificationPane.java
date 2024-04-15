package com.connections.view_controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class NotificationPane extends StackPane implements Modular {
	protected static final int HEIGHT = 42;
	
	private static final int FADE_DURATION_MS = 100;
	private Rectangle rectangle;
	private Text text;
	private GameSessionContext gameSessionContext;
	
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
	
	@Override
	public void refreshStyle() {
		rectangle.setFill(gameSessionContext.getStyleManager().colorPopupBackground());
		text.setFill(gameSessionContext.getStyleManager().colorPopupText());
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
