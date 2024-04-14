package com.connections.view_controller;

import com.connections.model.*;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameTileAnswer extends StackPane {
	private static final int POP_UP_MS = 125;
	private static final int FADE_IN_MS = 500;
	private GameBoard gameBoard;
	private GameAnswerColor answer;
	private StyleManager styleManager;
	private Text categoryNameText;
	private Text wordListText;
	private VBox textVBox;
	private Rectangle rectBackground;

	public GameTileAnswer(GameAnswerColor answer, GameBoard gameBoard) {
		this.answer = answer;
		this.styleManager = gameBoard.getStyleManager();
		this.gameBoard = gameBoard;
		this.answer = answer;

		categoryNameText = new Text(answer.getDescription().toUpperCase());
		categoryNameText.setFont(styleManager.getFont("franklin-normal", 700, 20));

		wordListText = new Text(answer.getWordListString());
		wordListText.setFont(styleManager.getFont("franklin-normal", 500, 20));

		textVBox = new VBox(categoryNameText, wordListText);
		textVBox.setAlignment(Pos.CENTER);
		rectBackground = new Rectangle(GameBoard.RECTANGLE_WIDTH * 4 + GameBoard.GAP * 3, GameBoard.RECTANGLE_HEIGHT);
		rectBackground.setArcWidth(GameBoard.CORNER_RADIUS);
		rectBackground.setArcHeight(GameBoard.CORNER_RADIUS);

		refreshStyle();

		this.getChildren().addAll(rectBackground, textVBox);
	}

	public GameAnswerColor getGameAnswerColor() {
		return answer;
	}

	public void refreshStyle() {
		wordListText.setFill(styleManager.colorTextNeutral());
		categoryNameText.setFill(styleManager.colorTextNeutral());
		rectBackground.setFill(styleManager.colorDifficulty(answer.getColor()));
	}

	public ParallelTransition getAppearAnimation() {
		ScaleTransition tileScaleTransition = new ScaleTransition(Duration.millis(POP_UP_MS), this);
		tileScaleTransition.setFromX(1);
		tileScaleTransition.setFromY(1);
		tileScaleTransition.setToX(1.4);
		tileScaleTransition.setToY(1.4);
		tileScaleTransition.setAutoReverse(true);
		tileScaleTransition.setCycleCount(2);

		FadeTransition textFadeTransition = new FadeTransition(Duration.millis(FADE_IN_MS), textVBox);
		textFadeTransition.setFromValue(0.0);
		textFadeTransition.setToValue(1.0);

		ParallelTransition parallelTransition = new ParallelTransition(textFadeTransition, tileScaleTransition);

		return parallelTransition;
	}
}
