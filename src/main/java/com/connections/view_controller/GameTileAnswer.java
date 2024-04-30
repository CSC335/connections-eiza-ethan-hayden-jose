package com.connections.view_controller;

import com.connections.model.GameAnswerColor;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The GameTileAnswer class represents an answer tile in the game. It displays
 * the category name and the list of words associated with an answer color.
 */
public class GameTileAnswer extends StackPane implements Modular {
	private static final int POP_UP_MS = 125;
	private static final int FADE_IN_MS = 500;
	private GameAnswerColor answer;
	private Text categoryNameText;
	private Text wordListText;
	private VBox textVBox;
	private Rectangle rectBackground;
	private TileGridWord tileGridWord;

	/**
	 * Constructs a new GameTileAnswer with the specified GameAnswerColor and
	 * TileGridWord.
	 *
	 * @param answer       the GameAnswerColor associated with the answer tile
	 * @param tileGridWord the TileGridWord containing this answer tile
	 */
	public GameTileAnswer(GameAnswerColor answer, TileGridWord tileGridWord) {
		this.tileGridWord = tileGridWord;
		this.answer = answer;

		categoryNameText = new Text(answer.getDescription().toUpperCase());
		categoryNameText
				.setFont(tileGridWord.getGameSessionContext().getStyleManager().getFont("franklin-normal", 700, 20));

		wordListText = new Text(answer.getWordListString());
		wordListText
				.setFont(tileGridWord.getGameSessionContext().getStyleManager().getFont("franklin-normal", 500, 20));

		textVBox = new VBox(categoryNameText, wordListText);
		textVBox.setAlignment(Pos.CENTER);
		rectBackground = new Rectangle(TileGridWord.PANE_WIDTH, GameTile.RECTANGLE_HEIGHT);
		rectBackground.setArcWidth(GameTile.CORNER_RADIUS);
		rectBackground.setArcHeight(GameTile.CORNER_RADIUS);

		refreshStyle();

		this.getChildren().addAll(rectBackground, textVBox);
	}

	/**
	 * Returns the GameAnswerColor associated with the answer tile.
	 *
	 * @return the GameAnswerColor associated with the answer tile
	 */
	public GameAnswerColor getGameAnswerColor() {
		return answer;
	}

	/**
	 * Creates and returns an animation for the appearance of the answer tile.
	 *
	 * @return a ParallelTransition representing the appearance animation of the
	 *         answer tile
	 */
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

	/**
	 * Refreshes the style of the answer tile based on the current style manager.
	 */
	@Override
	public void refreshStyle() {
		StyleManager styleManager = tileGridWord.getGameSessionContext().getStyleManager();

		wordListText.setFill(styleManager.colorTextNeutral());
		categoryNameText.setFill(styleManager.colorTextNeutral());
		rectBackground.setFill(styleManager.colorDifficulty(answer.getColor()));
	}

	/**
	 * Returns the GameSessionContext associated with the answer tile.
	 *
	 * @return the GameSessionContext associated with the answer tile
	 */
	@Override
	public GameSessionContext getGameSessionContext() {
		return tileGridWord.getGameSessionContext();
	}
}
