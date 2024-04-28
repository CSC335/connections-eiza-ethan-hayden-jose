package com.connections.view_controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The CountDownOverlayPane class represents a pane that displays a countdown animation.
 * It is designed to be used as an overlay during a game session.
 */
public class CountDownOverlayPane extends StackPane implements Modular {
	public static final int BACKGROUND_PANE_WIDTH = 500;
	public static final int BACKGROUND_PANE_HEIGHT = 400;
	private Pane backgroundPane;
	private Text countDownText;
	private GameSessionContext gameSessionContext;
	private EventHandler<ActionEvent> onFinishedCountdown;
	
    /**
     * Constructs a new CountDownOverlayPane with the specified GameSessionContext.
     *
     * @param gameSessionContext the GameSessionContext used by the countdown overlay pane
     */
	public CountDownOverlayPane(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		initAssets();
	}

	/**
     * Initializes the assets and components of the countdown overlay pane.
     */
	private void initAssets() {
		backgroundPane = new Pane();
		backgroundPane.setMinSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);
		backgroundPane.setMaxSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);

		countDownText = new Text("...");
		countDownText.setVisible(false);
		countDownText.setTranslateY(-16);

		setAlignment(Pos.CENTER);
		getChildren().addAll(backgroundPane, countDownText);
		setVisible(false);
		refreshStyle();
	}

    /**
     * Creates a shrink animation sequence for the countdown text.
     *
     * @param text   the text to display during the animation
     * @param isLast a boolean indicating if this is the last animation in the sequence
     * @return a SequentialTransition representing the shrink animation sequence
     */
	private SequentialTransition getShrinkAnimation(String text, boolean isLast) {
		PauseTransition startupPause = new PauseTransition(Duration.millis(5));
		startupPause.setOnFinished(event -> {
			countDownText.setText(text);
			countDownText.setVisible(true);
		});
		ScaleTransition scaleShrink = new ScaleTransition(Duration.millis(900), countDownText);
		scaleShrink.setFromX(4);
		scaleShrink.setFromY(4);
		scaleShrink.setToX(1);
		scaleShrink.setToY(1);

		FadeTransition fadeOut = new FadeTransition(Duration.millis(900), countDownText);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0);

		ParallelTransition parallel = new ParallelTransition(scaleShrink, fadeOut);
		PauseTransition pause = new PauseTransition(Duration.millis(100));
		pause.setOnFinished(event -> {
			if (isLast) {
				fadeOutAndTrigger();
			}
			countDownText.setVisible(false);
		});

		SequentialTransition sequence = new SequentialTransition(startupPause, parallel, pause);
		return sequence;
	}

    /**
     * Fades out the countdown overlay pane and triggers the onFinishedCountdown event.
     */
	private void fadeOutAndTrigger() {
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.play();

		fadeOut.setOnFinished(event -> {
			if (onFinishedCountdown != null) {
				onFinishedCountdown.handle(new ActionEvent(this, null));
			}
			setVisible(false);
		});
	}

    /**
     * Plays the countdown animation sequence.
     */
	private void playCountDownAnimation() {
		SequentialTransition countdownSequence = new SequentialTransition(
				getShrinkAnimation("3", false),
				getShrinkAnimation("2", false),
				getShrinkAnimation("1", false),
				getShrinkAnimation("GO!", true)
		);
		countdownSequence.play();
	}

    /**
     * Starts the countdown by displaying the countdown overlay pane and playing the animation.
     */
	public void startCountdown() {
		PauseTransition delay = new PauseTransition(Duration.millis(1000));
		delay.setOnFinished(event -> {
			setVisible(true);
		});

		FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);

		fadeIn.setOnFinished(event -> {
			playCountDownAnimation();
		});

		SequentialTransition sequence = new SequentialTransition(delay, fadeIn);
		sequence.play();
	}

	/**
     * Sets the event handler to be called when the countdown finishes.
     *
     * @param onFinishedCountdown the event handler to be called when the countdown finishes
     */
	public void setOnFinishedCountdown(EventHandler<ActionEvent> onFinishedCountdown) {
		this.onFinishedCountdown = onFinishedCountdown;
	}
	
    /**
     * Refreshes the style of the countdown overlay pane based on the current style manager.
     */
	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();
		
		backgroundPane.setOpacity(0.5);
		backgroundPane.setBackground(new Background(new BackgroundFill(styleManager.colorPopupBackground(), new CornerRadii(15), null)));

		Font karnakFont = styleManager.getFont("KarnakPro-Medium_400", "otf", 64);
		countDownText.setFont(Font.font(karnakFont.getFamily(), FontWeight.THIN, 64));
		countDownText.setFill(styleManager.colorPopupText());
	}

	/**
     * Returns the GameSessionContext used by the countdown overlay pane.
     *
     * @return the GameSessionContext used by the countdown overlay pane
     */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
