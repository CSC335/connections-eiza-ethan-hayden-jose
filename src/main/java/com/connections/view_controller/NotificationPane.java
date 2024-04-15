package com.connections.view_controller;

import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class NotificationPane extends StackPane implements Modular {
	private Rectangle rectangle;
	private Text text;
	private GameSessionContext gameSessionContext;
	
	public NotificationPane(String message, double width, GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		
		rectangle = new Rectangle(width, 42);
		rectangle.setArcWidth(10);
		rectangle.setArcHeight(10);
		
		text = new Text(message);
		text.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
		
		getChildren().addAll(rectangle, text);
		getStyleClass().add("popup-pane");
		
		refreshStyle();
	}
	
	public void popup(Pane parentPane, int duration) {
		PauseTransition pause = new PauseTransition(Duration.millis(duration));
		pause.setOnFinished(event -> {
			if(parentPane.getChildren().contains(this)) {
				parentPane.getChildren().remove(this);
			}
		});
		pause.play();
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
