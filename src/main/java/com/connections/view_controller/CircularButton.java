package com.connections.view_controller;

import javafx.scene.Cursor;
import javafx.scene.control.Button;

/**
 * The CircularButton class represents a customized button with circular
 * styling. It extends the JavaFX Button class and implements the Modular
 * interface.
 */
public class CircularButton extends Button implements Modular {

	private GameSessionContext gameSessionContext;
	private boolean fillStyle;

	/**
	 * Constructs a CircularButton object with the specified text, width,
	 * GameSessionContext, and fill style.
	 *
	 * @param text               the text to be displayed on the button
	 * @param width              the minimum width of the button
	 * @param gameSessionContext the GameSessionContext associated with the button
	 * @param fillStyle          a boolean indicating whether the button should have
	 *                           a fill style
	 */
	public CircularButton(String text, double width, GameSessionContext gameSessionContext, boolean fillStyle) {
		this.fillStyle = fillStyle;
		this.gameSessionContext = gameSessionContext;
		setText(text);
		setMinHeight(48);
		setMinWidth(width);
		setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
		setOnMouseEntered(event -> {
			setCursor(Cursor.HAND);
		});
		setOnMouseExited(event -> {
			setCursor(Cursor.DEFAULT);
		});
		refreshStyle();
	}

	/**
	 * Sets the fill style of the button.
	 *
	 * @param fillStyle a boolean indicating whether the button should have a fill
	 *                  style
	 */
	public void setFillStyle(boolean fillStyle) {
		this.fillStyle = fillStyle;
		refreshStyle();
	}

	/**
	 * Refreshes the style of the button based on the fill style and disabled state.
	 * If the button has a fill style and is not disabled, it applies the submit
	 * button fill style. Otherwise, it applies the regular button style.
	 */
	@Override
	public void refreshStyle() {
		gameSessionContext.getStyleManager();
		if (fillStyle && !isDisabled()) {
			setStyle(gameSessionContext.getStyleManager().styleSubmitButtonFill());
		} else {
			setStyle(gameSessionContext.getStyleManager().styleButton());
		}
	}

	/**
	 * Returns the GameSessionContext associated with the button.
	 *
	 * @return the GameSessionContext associated with the button
	 */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}