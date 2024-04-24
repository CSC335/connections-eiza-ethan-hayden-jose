package com.connections.view_controller;

import com.connections.model.Word;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameTileWord extends StackPane implements Modular {
	protected static final int FILL_TRANSITION_MS = 100;
	protected static final int FILL_PULSE_TRANSITION_MS = 750;
	protected static final double PULSE_SCALE_FACTOR = 1.05;
	protected static final double PULSE_COLOR_DARK_FACTOR = 1.3;
	protected static final double PULSE_FADE_FACTOR = 0.75;
	protected static final int PULSE_REPEAT_COUNT = 4;
	private boolean selected;
	private boolean incorrect;
	private boolean styleChangeable;
	private Rectangle rectangle;
	private Text text;
	private Font font;
	private Word word;
	private TileGridWord tileGridWord;
	private StyleStatus styleStatus;

	private enum StyleStatus {
		DEFAULT, SELECTED, INCORRECT
	}

	/*
	 * NOTE: it is >>NOT<< good that GameTileWord currently has a constructor that
	 * takes in a Font. GameTileWord needs to set its own font with the
	 * StyleManager, not the parent. This needs to be fixed later.
	 */

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

	public void setStyleChangeable(boolean styleChangeable) {
		this.styleChangeable = styleChangeable;
	}

	public void setSelectedStatus(boolean selected) {
		this.selected = selected;
		refreshStyle();
	}

	public boolean getSelectedStatus() {
		return selected;
	}

	public void setIncorrectStatus(boolean incorrect) {
		this.incorrect = incorrect;
		refreshStyle();
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

	private void updateStyleStatus() {
		if (selected && incorrect) {
			styleStatus = StyleStatus.DEFAULT;
		} else if (selected) {
			styleStatus = StyleStatus.SELECTED;
		} else if (incorrect) {
			styleStatus = StyleStatus.INCORRECT;
		} else {
			styleStatus = StyleStatus.DEFAULT;
		}
	}

	private Color getRectFill() {
		StyleManager styleManager = tileGridWord.getGameSessionContext().getStyleManager();

		switch (styleStatus) {
		case SELECTED:
			return styleManager.colorSelectedRectangle();
		case INCORRECT:
			return styleManager.colorIncorrectRectangle();
		default:
		}
		return styleManager.colorDefaultRectangle();
	}

	private Color getTextFill() {
		StyleManager styleManager = tileGridWord.getGameSessionContext().getStyleManager();

		switch (styleStatus) {
		case SELECTED:
			return styleManager.colorTextInverted();
		case INCORRECT:
			return styleManager.colorTextInverted();
		default:
		}
		return styleManager.colorText();
	}

	private void updateStyleColor() {
		FillTransition rectangleFillTransition = new FillTransition(Duration.millis(FILL_TRANSITION_MS), rectangle);
		rectangleFillTransition.setToValue(getRectFill());
		FillTransition textFillTransition = new FillTransition(Duration.millis(FILL_TRANSITION_MS), text);
		textFillTransition.setToValue(getTextFill());
		ParallelTransition parallelTransition = new ParallelTransition(rectangleFillTransition, textFillTransition);
		parallelTransition.play();
	}

	public SequentialTransition getHintPulseAnimation() {
		StyleManager styleManager = tileGridWord.getGameSessionContext().getStyleManager();

		updateStyleStatus();
		
		Color answerColor = styleManager.colorDifficulty(word.getColor());
		Color answerColorDark = Color.rgb((int) (255 * answerColor.getRed() / PULSE_COLOR_DARK_FACTOR),
				(int) (255 * answerColor.getGreen() / PULSE_COLOR_DARK_FACTOR),
				(int) (255 * answerColor.getBlue() / PULSE_COLOR_DARK_FACTOR));
		
		// Initial Pulse

		ScaleTransition initialScale = new ScaleTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), this);
		initialScale.setFromX(1.0);
		initialScale.setFromY(1.0);
		initialScale.setToX(PULSE_SCALE_FACTOR);
		initialScale.setToY(PULSE_SCALE_FACTOR);

		FillTransition initialRectFill = new FillTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), rectangle);
		initialRectFill.setFromValue(getRectFill());
		initialRectFill.setToValue(answerColor);

		FillTransition initialTextFill = new FillTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), text);
		initialTextFill.setFromValue(getTextFill());
		initialTextFill.setToValue(styleManager.colorTextNeutral());
		
		ParallelTransition initialParallel = new ParallelTransition(initialScale, initialRectFill,
				initialTextFill);

		// Repeated Pulse by Using the Answer Color Only

		int cycleCount = PULSE_REPEAT_COUNT * 2;

		ScaleTransition continueScale = new ScaleTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), this);
		continueScale.setToX(1.0);
		continueScale.setToY(1.0);

		FadeTransition continueFade = new FadeTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), rectangle);
		continueFade.setToValue(PULSE_FADE_FACTOR);

		FillTransition continueRectFill = new FillTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), rectangle);
		continueRectFill.setToValue(answerColorDark);

		FillTransition continueTextFill = new FillTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), text);
		continueTextFill.setToValue(styleManager.colorTextInverted());
		
		ParallelTransition continueParallel = new ParallelTransition(continueScale, continueFade, continueRectFill, continueTextFill);
		continueParallel.setAutoReverse(true);
		continueParallel.setCycleCount(cycleCount);

		SequentialTransition sequence = new SequentialTransition(initialParallel, continueParallel);
		
		return sequence;
	}

	public ParallelTransition getHintReturnNormalAnimation() {
		updateStyleStatus();

		ScaleTransition continueScale = new ScaleTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), this);
		continueScale.setToX(1.0);
		continueScale.setToY(1.0);

		FadeTransition continueFade = new FadeTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), rectangle);
		continueFade.setToValue(1.0);

		FillTransition continueRectFill = new FillTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), rectangle);
		continueRectFill.setToValue(getRectFill());
		
		FillTransition continueTextFill = new FillTransition(Duration.millis(FILL_PULSE_TRANSITION_MS), text);
		continueTextFill.setToValue(getTextFill());

		ParallelTransition continueParallel = new ParallelTransition(continueScale, continueFade, continueRectFill, continueTextFill);

		return continueParallel;
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
		if(styleChangeable) {
			updateStyleStatus();
			updateStyleColor();
		}
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return tileGridWord.getGameSessionContext();
	}
}
