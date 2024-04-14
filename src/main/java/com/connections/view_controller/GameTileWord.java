package com.connections.view_controller;

import com.connections.model.*;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameTileWord extends StackPane {
	protected static final int FILL_TRANSITION_MS = 100;
	private boolean selected;
	private boolean incorrect;
	private boolean styleChangeable;
	private Rectangle rectangle;
	private Text text;
	private Font font;
	private Word word;
	private StyleManager styleManager;
	private GameBoard gameBoard;

	public GameTileWord(GameTileWord other) {
		selected = other.selected;
		incorrect = other.incorrect;
		styleChangeable = other.styleChangeable;
		styleManager = other.styleManager;
		gameBoard = other.gameBoard;
		word = other.word;
		font = other.font;

		initAssets();
		enable();
		refreshStyle();
	}

	public GameTileWord(Font font, GameBoard gameBoard) {
		this.word = null;
		this.font = font;
		this.gameBoard = gameBoard;
		this.styleManager = gameBoard.getStyleManager();
		this.styleChangeable = true;
		initAssets();
		enable();
	}

	public GameTileWord(Word word, Font font, GameBoard gameBoard) {
		this(font, gameBoard);
		setWord(word);
	}

	public void setWord(Word word) {
		if (word != null) {
			this.word = word;
			text.setText(word.getText().toUpperCase());
		}
	}

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
		rectangle = new Rectangle(GameBoard.RECTANGLE_WIDTH, GameBoard.RECTANGLE_HEIGHT);
		rectangle.setArcWidth(GameBoard.CORNER_RADIUS);
		rectangle.setArcHeight(GameBoard.CORNER_RADIUS);
		rectangle.setFill(styleManager.colorDefaultRectangle());

		text = new Text();
		text.setFont(font);
		text.setFill(styleManager.colorText());
		setWord(word);

		this.getChildren().addAll(rectangle, text);
	}

	private void setStyleDefault() {
		styleTransition(styleManager.colorDefaultRectangle(), styleManager.colorText());
	}

	private void setStyleSelected() {
		styleTransition(styleManager.colorSelectedRectangle(), styleManager.colorTextInverted());
	}

	private void setStyleIncorrect() {
		styleTransition(styleManager.colorIncorrectRectangle(), styleManager.colorTextInverted());
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

	public void disable() {
		this.setDisable(true);
		this.setOnMouseClicked(null);
		this.setOnMouseEntered(null);
		this.setOnMouseExited(null);
	}

	public void enable() {
		this.setDisable(false);
		this.setOnMouseClicked(event -> {
			if (!selected && gameBoard.getSelectedCount() < GameBoard.MAX_SELECTED) {
				setSelectedStatus(true);
				gameBoard.incrementSelectedCount();
			} else if (selected) {
				setSelectedStatus(false);
				gameBoard.decrementSelectedCount();
			}

			Button deselectButton = gameBoard.getDeselectButton();
			Button submitButton = gameBoard.getSubmitButton();

			deselectButton.setDisable(gameBoard.getSelectedCount() == 0);
			submitButton.setDisable(gameBoard.getSelectedCount() != GameBoard.MAX_SELECTED);

			if (gameBoard.getSelectedCount() != 0) {
				deselectButton.setStyle(styleManager.getButton());
			} else {
				deselectButton.setStyle(styleManager.getButton());
			}

			if (gameBoard.getSelectedCount() == GameBoard.MAX_SELECTED) {
				submitButton.setStyle(styleManager.getSubmitButton());
			} else {
				submitButton.setStyle(styleManager.getButton());
			}
		});

		this.setOnMouseEntered(event -> {
			this.setCursor(Cursor.HAND);
		});

		this.setOnMouseExited(event -> {
			this.setCursor(Cursor.DEFAULT);
		});
	}

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

	public void fadeInWordText(ParallelTransition fadeInTransition) {
		text.setOpacity(0);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), text);
		fadeTransition.setFromValue(0);
		fadeTransition.setToValue(1);
		fadeInTransition.getChildren().add(fadeTransition);
	}
}
