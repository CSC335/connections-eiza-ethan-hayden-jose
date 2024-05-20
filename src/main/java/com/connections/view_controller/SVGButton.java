package com.connections.view_controller;

import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

/**
 * An abstract class representing a button with an SVG icon. This class extends
 * the JavaFX Pane and implements the Modular interface.
 */
public abstract class SVGButton extends Pane implements Modular {
	public static final int PREF_WIDTH = 30;
	protected GameSessionContext gameSessionContext;
	protected SVGPath svgPath;

	/**
	 * Constructs a new SVGButton object.
	 *
	 * @param gameSessionContext The GameSessionContext object for accessing shared
	 *                           resources.
	 */
	public SVGButton(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		this.svgPath = null;
		setPrefWidth(PREF_WIDTH);
		prefHeightProperty().bind(widthProperty());
		setOnMouseEntered(event -> {
			setCursor(Cursor.HAND);
		});
		setOnMouseExited(event -> {
			setCursor(Cursor.DEFAULT);
		});
	}

	/**
	 * Sets the SVGPath object representing the SVG icon.
	 *
	 * @param svgPath The SVGPath object to be set.
	 */
	protected void setSVG(SVGPath svgPath) {
		this.svgPath = svgPath;
		getChildren().add(svgPath);
	}

	/**
	 * Returns the GameSessionContext object associated with this SVGButton.
	 *
	 * @return The GameSessionContext object.
	 */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
