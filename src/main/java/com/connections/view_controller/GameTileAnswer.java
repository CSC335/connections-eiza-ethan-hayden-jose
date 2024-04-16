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

public class GameTileAnswer extends StackPane implements Modular {
	private static final int POP_UP_MS = 125;
	private static final int FADE_IN_MS = 500;
	private GameAnswerColor answer;
	private Text categoryNameText;
	private Text wordListText;
	private VBox textVBox;
	private Rectangle rectBackground;
	private TileGridWord tileGridWord;
	
	public GameTileAnswer(GameAnswerColor answer, TileGridWord tileGridWord) {
		this.tileGridWord = tileGridWord;
		this.answer = answer;

		categoryNameText = new Text(answer.getDescription().toUpperCase());
		categoryNameText.setFont(tileGridWord.getGameSessionContext().getStyleManager().getFont("franklin-normal",700, 20));

		wordListText = new Text(answer.getWordListString());
		wordListText.setFont(tileGridWord.getGameSessionContext().getStyleManager().getFont("franklin-normal",500, 20));
		
		textVBox = new VBox(categoryNameText, wordListText);
		textVBox.setAlignment(Pos.CENTER);
		rectBackground = new Rectangle(TileGridWord.PANE_WIDTH, GameTile.RECTANGLE_HEIGHT);
		rectBackground.setArcWidth(GameTile.CORNER_RADIUS);
		rectBackground.setArcHeight(GameTile.CORNER_RADIUS);

		refreshStyle();

		this.getChildren().addAll(rectBackground, textVBox);
	}

	public GameAnswerColor getGameAnswerColor() {
		return answer;
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
	
	@Override
	public void refreshStyle() {
		StyleManager styleManager = tileGridWord.getGameSessionContext().getStyleManager();
		
		wordListText.setFill(styleManager.colorTextNeutral());
		categoryNameText.setFill(styleManager.colorTextNeutral());
		rectBackground.setFill(styleManager.colorDifficulty(answer.getColor()));
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return tileGridWord.getGameSessionContext();
	}
}
