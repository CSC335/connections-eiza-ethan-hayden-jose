package com.connections.view_controller;

import javafx.scene.Cursor;
import javafx.scene.control.Button;

public class CircularButton extends Button implements Modular {
	private GameSessionContext gameSessionContext;
	private boolean fillStyle;
	
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
	
	public void setFillStyle(boolean fillStyle) {
		this.fillStyle = fillStyle;
		refreshStyle();
	}

	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();
		
		if(fillStyle && !isDisabled()) {
			setStyle(gameSessionContext.getStyleManager().styleSubmitButtonFill());
		} else {
			setStyle(gameSessionContext.getStyleManager().styleButton());
		}
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}