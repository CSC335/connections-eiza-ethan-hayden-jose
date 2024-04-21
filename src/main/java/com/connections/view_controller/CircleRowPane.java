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
	private int numCircles;
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
		if (numCircles > 0 && circlePane.getChildren().size() > 0) {
			numCircles--;

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

	/*
	 * Using a dedicated "numCircles" variable now because relying on the pane for
	 * the count is not the most up-to-date becuse, when removing the circle, the
	 * size will only update AFTER the circle has disappeared fully in the
	 * animation.
	 */

	public int getNumCircles() {
//		return circlePane.getChildren().size();
		return numCircles;
	}

	public void setNumCircles(int numCircles) {
		this.numCircles = numCircles;
		circlePane.getChildren().clear();
		for (int i = 0; i < numCircles; i++) {
			circlePane.getChildren().add(makeCircle(i));
		}
	}

	public void resetCircles() {
		setNumCircles(START_SIZE);
	}

	private Circle makeCircle(int indexPosition) {
		Circle circle = new Circle(8);
		circle.setFill(Color.rgb(90, 89, 78));
		circle.setLayoutX(indexPosition * 28 + 10);
		circle.setLayoutY(circlePane.getPrefHeight() / 2 + 12);
		return circle;
	}

	public int getMaxNumCircles() {
		return START_SIZE;
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
