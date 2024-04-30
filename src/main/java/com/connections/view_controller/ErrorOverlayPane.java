package com.connections.view_controller;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * The ErrorOverlayPane class represents a pane that displays an error message
 * as an overlay. It provides a visual representation of the error using an SVG
 * icon and labels for the header and body text.
 */
public class ErrorOverlayPane extends StackPane implements Modular {
	public static final int BACKGROUND_PANE_WIDTH = 500;
	public static final int BACKGROUND_PANE_HEIGHT = 400;
	private Pane backgroundPane;
	private VBox contentLayout;
	private SVGPath errorSVG;
	private Label headerLabel;
	private Label bodyLabel;
	private GameSessionContext gameSessionContext;

	private EventHandler<ActionEvent> onDisappear;
	private String headerText;
	private String bodyText;

	/**
	 * Constructs a new ErrorOverlayPane with the specified GameSessionContext,
	 * header text, and body text.
	 *
	 * @param gameSessionContext the GameSessionContext used by the error overlay
	 *                           pane
	 * @param headerText         the text to be displayed as the header of the error
	 *                           message
	 * @param bodyText           the text to be displayed as the body of the error
	 *                           message
	 */
	public ErrorOverlayPane(GameSessionContext gameSessionContext, String headerText, String bodyText) {
		this.gameSessionContext = gameSessionContext;
		this.headerText = headerText;
		this.bodyText = bodyText;
		initAssets();
	}

	/**
	 * Constructs a new ErrorOverlayPane with the specified GameSessionContext and
	 * default header and body text.
	 *
	 * @param gameSessionContext the GameSessionContext used by the error overlay
	 *                           pane
	 */
	public ErrorOverlayPane(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		this.headerText = "Error";
		this.bodyText = "Error";
		initAssets();
	}

	/**
	 * Sets the header text of the error message.
	 *
	 * @param headerText the text to be displayed as the header of the error message
	 */
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
		headerLabel.setText(headerText);
		refreshStyle();
	}

	/**
	 * Sets the body text of the error message.
	 *
	 * @param bodyText the text to be displayed as the body of the error message
	 */
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
		bodyLabel.setText(bodyText);
		refreshStyle();
	}

	/**
	 * Initializes the assets and components of the error overlay pane.
	 */
	private void initAssets() {
		backgroundPane = new Pane();
		backgroundPane.setMinSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);
		backgroundPane.setMaxSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);
		setMinSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);
		setMaxSize(BACKGROUND_PANE_WIDTH, BACKGROUND_PANE_HEIGHT);

		headerLabel = new Label(headerText);
		headerLabel.setWrapText(true);

		bodyLabel = new Label(bodyText);
		bodyLabel.setWrapText(true);

		errorSVG = new SVGPath();
		errorSVG.setContent(
				"M4 21V18.5C4 15.4624 6.46243 13 9.5 13H15M8 21V18M16.5 17V15M16.5 19.2V19M16 6.5C16 8.70914 14.2091 10.5 12 10.5C9.79086 10.5 8 8.70914 8 6.5C8 4.29086 9.79086 2.5 12 2.5C14.2091 2.5 16 4.29086 16 6.5ZM12.309 21H20.691C21.0627 21 21.3044 20.6088 21.1382 20.2764L16.9472 11.8944C16.763 11.5259 16.237 11.5259 16.0528 11.8944L11.8618 20.2764C11.6956 20.6088 11.9373 21 12.309 21Z");
		errorSVG.setStrokeLineCap(StrokeLineCap.ROUND);
		errorSVG.setStrokeLineJoin(StrokeLineJoin.ROUND);
		errorSVG.setStrokeWidth(1.5);
		errorSVG.setScaleX(4);
		errorSVG.setScaleY(4);

		contentLayout = new VBox(32, errorSVG, headerLabel, bodyLabel);
		contentLayout.setPadding(new Insets(32));
		contentLayout.setAlignment(Pos.CENTER);

		getChildren().addAll(backgroundPane, contentLayout);

		setVisible(false);

		refreshStyle();
	}

	/**
	 * Makes the error overlay pane appear with a fade-in animation.
	 */
	public void appear() {
		FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		fadeIn.play();

		setVisible(true);
	}

	/**
	 * Makes the error overlay pane disappear with a fade-out animation. It triggers
	 * the onDisappear event handler when the animation is finished.
	 */
	public void disappear() {
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.play();

		fadeOut.setOnFinished(event -> {
			if (onDisappear != null) {
				onDisappear.handle(new ActionEvent(this, null));
			}
			setVisible(false);
		});
	}

	/**
	 * Sets the event handler to be called when the error overlay pane disappears.
	 *
	 * @param onDisappear the event handler to be called when the error overlay pane
	 *                    disappears
	 */
	public void setOnDisappear(EventHandler<ActionEvent> onDisappear) {
		this.onDisappear = onDisappear;
	}

	/**
	 * Refreshes the style of the error overlay pane based on the current style
	 * manager.
	 */
	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();

		errorSVG.setStroke(styleManager.colorPopupText());
		errorSVG.setFill(Color.TRANSPARENT);

		backgroundPane.setOpacity(0.5);
		backgroundPane.setBackground(
				new Background(new BackgroundFill(styleManager.colorPopupBackground(), new CornerRadii(15), null)));

		Font headerFont = styleManager.getFont("franklin-normal", 700, 32);
		Font bodyFont = styleManager.getFont("franklin-normal", 600, 22);

		headerLabel.setFont(headerFont);
		headerLabel.setTextFill(styleManager.colorPopupText());
		bodyLabel.setFont(bodyFont);
		bodyLabel.setTextFill(styleManager.colorPopupText());
	}

	/**
	 * Returns the GameSessionContext used by the error overlay pane.
	 *
	 * @return the GameSessionContext used by the error overlay pane
	 */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
