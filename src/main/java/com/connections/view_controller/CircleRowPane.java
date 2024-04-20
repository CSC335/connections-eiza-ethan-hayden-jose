package com.connections.view_controller;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CircleRowPane extends HBox implements Modular {
	private GameSessionContext gameSessionContext;
	private Pane circlePane;
	private Text text;
	private final static int START_SIZE = 4;

	public CircleRowPane(String label, GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;

		text = new Text(label);
		text.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 16));

		circlePane = new Pane();
		circlePane.setPrefWidth(100);

		resetCircles();

		setSpacing(10);
		setAlignment(Pos.CENTER);
		getChildren().addAll(text, circlePane);
		refreshStyle();
	}

	public boolean removeCircle() {
		if(circlePane.getChildren().size() > 0) {
			Circle circle = (Circle) circlePane.getChildren().get(circlePane.getChildren().size() - 1);
			ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), circle);
			scaleTransition.setFromX(1.0);
			scaleTransition.setFromY(1.0);
			scaleTransition.setToX(0.0);
			scaleTransition.setToY(0.0);
			scaleTransition.setOnFinished(event -> circlePane.getChildren().remove(circle));

			scaleTransition.play();

			return true;
		}
		return false;
	}

	public int getNumCircles() {
		return circlePane.getChildren().size();
	}

	public void resetCircles() {
		circlePane.getChildren().clear();
		for (int i = 0; i < START_SIZE; i++) {
			Circle circle = new Circle(8);
			circle.setFill(Color.rgb(90, 89, 78));
			circle.setLayoutX(i * 28 + 10);
			circle.setLayoutY(circlePane.getPrefHeight() / 2 + 12);
			circlePane.getChildren().add(circle);
		}
	}

	@Override
	public void refreshStyle() {
		text.setFill(gameSessionContext.getStyleManager().colorText());
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
