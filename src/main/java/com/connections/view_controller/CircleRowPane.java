package com.connections.view_controller;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Represents a pane containing a row of circles.
 *
 * @param label              The label for the circle row.
 * @param gameSessionContext The context of the game session.
 */
public class CircleRowPane extends HBox implements Modular {
	private GameSessionContext gameSessionContext;
	private Pane circlePane;
	private Text text;
	private int numCircles;
	private final static int START_SIZE = 4;

	/**
	 * Constructs a PlayedGameInfoTimed object with the given parameters.
	 *
	 * @param label              The label for the circle row.
	 * @param gameSessionContext The game session's context.
	 */
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

	/**
	 * Removes a circle from the circle row.
	 *
	 * @return true if a circle was removed successfully, false otherwise.
	 */
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

	/**
	 * Returns the number of circles in the circle row.
	 *
	 * @return The number of circles.
	 */
	public int getNumCircles() {
		return numCircles;
	}

	/**
	 * Sets the number of circles in the circle row.
	 *
	 * @param numCircles The new number of circles.
	 */
	public void setNumCircles(int numCircles) {
		this.numCircles = numCircles;
		circlePane.getChildren().clear();
		for (int i = 0; i < numCircles; i++) {
			circlePane.getChildren().add(makeCircle(i));
		}
	}

	/**
	 * Resets the circle row to its initial state.
	 */
	public void resetCircles() {
		setNumCircles(START_SIZE);
	}

	/**
	 * Returns makes a stylized Circle object.
	 *
	 * @return The stylized Circle object.
	 */
	private Circle makeCircle(int indexPosition) {
		Circle circle = new Circle(8);
		circle.setFill(Color.rgb(90, 89, 78));
		circle.setLayoutX(indexPosition * 28 + 10);
		circle.setLayoutY(circlePane.getPrefHeight() / 2 + 12);
		return circle;
	}

	/**
	 * Returns the maximum number of circles allowed in the circle row.
	 *
	 * @return The maximum number of circles.
	 */
	public int getMaxNumCircles() {
		return START_SIZE;
	}

	/**
	 * Refreshes the style for the CircleRowPane
	 */
	@Override
	public void refreshStyle() {
		text.setFill(gameSessionContext.getStyleManager().colorText());
	}

	/**
	 * Returns the GameSessionContext associated with the CircleRowPane.
	 *
	 * @return the GameSessionContext associated with the CircleRowPane
	 */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}