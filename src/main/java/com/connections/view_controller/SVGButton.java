package com.connections.view_controller;

import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

public abstract class SVGButton extends Pane implements Stylable {
	protected GameBoard gameBoard;
	protected StyleManager styleManager;
	protected SVGPath svgPath;
	
	public SVGButton(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
		this.styleManager = gameBoard.getStyleManager();
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
}
