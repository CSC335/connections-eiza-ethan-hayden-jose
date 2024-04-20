package com.connections.view_controller;

import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

public abstract class SVGButton extends Pane implements Modular {
	protected GameSessionContext gameSessionContext;
	protected SVGPath svgPath;

	public SVGButton(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		this.svgPath = null;
		setPrefWidth(30);
		prefHeightProperty().bind(widthProperty());
		setOnMouseEntered(event -> {
			setCursor(Cursor.HAND);
		});
		setOnMouseExited(event -> {
			setCursor(Cursor.DEFAULT);
		});
	}

	protected void setSVG(SVGPath svgPath) {
		this.svgPath = svgPath;
		getChildren().add(svgPath);
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
