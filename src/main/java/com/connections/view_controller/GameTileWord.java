package com.connections.view_controller;

import com.connections.model.Word;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameTileWord extends StackPane implements Modular {
	protected static final int FILL_TRANSITION_MS = 100;
	protected static final int FILL_PULSE_TRANSITION_MS = 750;
	private boolean selected;
	private boolean incorrect;
	private boolean styleChangeable;
	private Rectangle rectangle;
	private Text text;
	private Font font;
	private Word word;
	private TileGridWord tileGridWord;

	public GameTileWord(GameTileWord other) {
		selected = other.selected;
		incorrect = other.incorrect;
		styleChangeable = other.styleChangeable;
		tileGridWord = other.tileGridWord;
		word = other.word;
		font = other.font;

		initAssets();
		enable();
		refreshStyle();
	}

	public GameTileWord(Font font, TileGridWord tileGridWord) {
		this.word = null;
		this.font = font;
		this.tileGridWord = tileGridWord;
		this.styleChangeable = true;
		initAssets();
		enable();
	}

	public GameTileWord(Word word, Font font, TileGridWord tileGridWord) {
		this(font, tileGridWord);
		setWord(word);
	}

	public void setWord(Word word) {
		if (word != null) {
			this.word = word;
			text.setText(word.getText().toUpperCase());
		}
	}

	// Method likely no longer needed
	// Disabling/enabling style changeable during auto solve caused darkmode issues
	public void setStyleChangeable(boolean styleChangeable) {
		this.styleChangeable = styleChangeable;
	}

	public void setSelectedStatus(boolean selected) {
		this.selected = selected;

		if (selected) {
			setStyleSelected();
		} else {
			setStyleDefault();
		}
	}

	public boolean getSelectedStatus() {
		return selected;
	}

	public void setIncorrectStatus(boolean incorrect) {
		this.incorrect = incorrect;

		if (incorrect) {
			setStyleIncorrect();
		} else {
			setStyleDefault();
		}
	}

	public boolean getIncorrectStatus() {
		return incorrect;
	}

	public Word getWord() {
		return word;
	}

	private void initAssets() {
		rectangle = new Rectangle(GameTile.RECTANGLE_WIDTH, GameTile.RECTANGLE_HEIGHT);
		rectangle.setArcWidth(GameTile.CORNER_RADIUS);
		rectangle.setArcHeight(GameTile.CORNER_RADIUS);
		rectangle.setFill(tileGridWord.getGameSessionContext().getStyleManager().colorDefaultRectangle());

		text = new Text();
		text.setFont(font);
		text.setFill(tileGridWord.getGameSessionContext().getStyleManager().colorText());
		setWord(word);

		this.getChildren().addAll(rectangle, text);
	}

	private void setStyleDefault() {
		styleTransition(tileGridWord.getGameSessionContext().getStyleManager().colorDefaultRectangle(), tileGridWord.getGameSessionContext().getStyleManager().colorText());
	}

	private void setStyleSelected() {
		styleTransition(tileGridWord.getGameSessionContext().getStyleManager().colorSelectedRectangle(), tileGridWord.getGameSessionContext().getStyleManager().colorTextInverted());
	}

	private void setStyleIncorrect() {
		styleTransition(tileGridWord.getGameSessionContext().getStyleManager().colorIncorrectRectangle(), tileGridWord.getGameSessionContext().getStyleManager().colorTextInverted());
	}

	private void styleTransition(Color rectangleFill, Color textFill) {
		if (styleChangeable) {
			FillTransition rectangleFillTransition = new FillTransition(Duration.millis(FILL_TRANSITION_MS), rectangle);
			rectangleFillTransition.setToValue(rectangleFill);
			FillTransition textFillTransition = new FillTransition(Duration.millis(FILL_TRANSITION_MS), text);
			textFillTransition.setToValue(textFill);
			ParallelTransition parallelTransition = new ParallelTransition(rectangleFillTransition, textFillTransition);
			parallelTransition.play();
		}
	}

	public ParallelTransition getPulseAnswerColorAnimation() {
		if (word == null || !styleChangeable) {
			return new ParallelTransition();
		}
		Color answerColor = tileGridWord.getGameSessionContext().getStyleManager().colorDifficulty(word.getColor());
		ScaleTransition pieceScaleTransition = new ScaleTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), this);
		pieceScaleTransition.setFromX(1);
		pieceScaleTransition.setFromY(1);
		pieceScaleTransition.setToX(1.3);
		pieceScaleTransition.setToY(1.3);
		pieceScaleTransition.setCycleCount(8);
		pieceScaleTransition.setAutoReverse(true);
		FadeTransition pieceFadeTransition = new FadeTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), this);
		pieceFadeTransition.setFromValue(1.0);
		pieceFadeTransition.setToValue(0.75);
		pieceFadeTransition.setCycleCount(8);
		pieceFadeTransition.setAutoReverse(true);
		FillTransition rectangleFillTransition = new FillTransition(Duration.millis(FILL_PULSE_TRANSITION_MS),
				rectangle);
		rectangleFillTransition.setToValue(answerColor);
		rectangleFillTransition.setCycleCount(8);
		rectangleFillTransition.setAutoReverse(true);
		FillTransition textFillTransition = new FillTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), text);
		textFillTransition.setToValue(tileGridWord.getGameSessionContext().getStyleManager().colorTextNeutral());
		textFillTransition.setCycleCount(8);
		textFillTransition.setAutoReverse(true);
		ParallelTransition parallelTransition = new ParallelTransition(pieceScaleTransition, pieceFadeTransition,
				rectangleFillTransition, textFillTransition);
		return parallelTransition;
	}

	public void disable() {
		this.setDisable(true);
		this.setOnMouseClicked(null);
		this.setOnMouseEntered(null);
		this.setOnMouseExited(null);
	}

	public void enable() {
		this.setDisable(false);
		this.setOnMouseClicked(event -> {
			if (!selected && tileGridWord.getSelectedTileWordCount() < TileGridWord.MAX_SELECTED) {
				setSelectedStatus(true);
				tileGridWord.incrementSelectedTileWordCount();
			} else if (selected) {
				setSelectedStatus(false);
				tileGridWord.decrementSelectedTileWordCount();
			}
		});

		this.setOnMouseEntered(event -> {
			this.setCursor(Cursor.HAND);
		});

		this.setOnMouseExited(event -> {
			this.setCursor(Cursor.DEFAULT);
		});
	}

	public void fadeInWordText(ParallelTransition fadeInTransition) {
		text.setOpacity(0);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), text);
		fadeTransition.setFromValue(0);
		fadeTransition.setToValue(1);
		fadeInTransition.getChildren().add(fadeTransition);
	}

	@Override
	public void refreshStyle() {
		if (selected && incorrect) {
			setStyleDefault();
		} else if (selected) {
			setStyleSelected();
		} else if (incorrect) {
			setStyleIncorrect();
		} else {
			setStyleDefault();
		}
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return tileGridWord.getGameSessionContext();
	}
}
