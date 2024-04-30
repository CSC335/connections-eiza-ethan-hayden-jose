package com.connections.view_controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The PopupWrapperPane class represents a pane that wraps a child pane and
 * displays it as a popup. It provides a title, a "Go Back" button, and animates
 * the popup appearance.
 */
public class PopupWrapperPane extends BorderPane implements Modular {
	protected static final double POPUP_EDGE_CUTOFF = 125;
	protected static final double INSETS_MARGIN = 10;
	protected static final double MENU_HEIGHT = 30;
	protected static final double POPUP_WIDTH = GameSession.STAGE_WIDTH - POPUP_EDGE_CUTOFF;
	protected static final double POPUP_HEIGHT = GameSession.STAGE_HEIGHT - POPUP_EDGE_CUTOFF;
	protected static final double CONTAINER_WIDTH = POPUP_WIDTH - INSETS_MARGIN * 2;
	protected static final double CONTAINER_HEIGHT = POPUP_HEIGHT - INSETS_MARGIN * 2 - MENU_HEIGHT * 1.5;

	private static final int FADE_MS = 150;

	private GameSessionContext gameSessionContext;
	private Pane childPane;
	private StackPane containerPane;
	private StackPane menuPane;

	private Text titleText;
	private HBox goBackLayout;
	private SVGPath goBackCross;

	private String title;
	private Text goBackText;

	/**
	 * Constructs a new PopupWrapperPane with the specified GameSessionContext,
	 * child pane, and title.
	 *
	 * @param gameSessionContext the GameSessionContext used by the popup wrapper
	 *                           pane
	 * @param childPane          the child pane to be displayed inside the popup
	 * @param title              the title of the popup
	 */
	public PopupWrapperPane(GameSessionContext gameSessionContext, Pane childPane, String title) {
		this.title = title;
		this.childPane = childPane;
		this.gameSessionContext = gameSessionContext;

		initAssets();
		initMenuPane();

		setSizeFixed(false);
		refreshStyle();
	}

	/**
	 * Sets the size of the popup wrapper pane to be fixed or variable.
	 *
	 * @param fixedSize true to set a fixed size, false to allow variable size
	 */
	public void setSizeFixed(boolean fixedSize) {
		setMaxSize(POPUP_WIDTH, POPUP_HEIGHT);
		containerPane.setMaxSize(CONTAINER_WIDTH, CONTAINER_HEIGHT);

		if (fixedSize) {
			setMinSize(POPUP_WIDTH, POPUP_HEIGHT);
			containerPane.setPrefSize(CONTAINER_WIDTH, CONTAINER_HEIGHT);
		} else {
			setPrefWidth(Region.USE_COMPUTED_SIZE);
			setPrefHeight(Region.USE_COMPUTED_SIZE);
			containerPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
			containerPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
		}
	}

	/**
	 * Sets the child pane to be displayed inside the popup wrapper pane.
	 *
	 * @param pane the child pane to be displayed
	 */
	public void setChild(Pane pane) {
		this.childPane = pane;
		containerPane.getChildren().clear();
		containerPane.getChildren().add(pane);
		refreshStyle();
	}

	/**
	 * Sets the title of the popup wrapper pane.
	 *
	 * @param title the title of the popup
	 */
	public void setTitle(String title) {
		this.title = title;
		titleText.setText(title);
	}

	/**
	 * Animates the popup appearance with a slide-up and fade-in transition.
	 */
	public void popup() {
		TranslateTransition slideUp = new TranslateTransition(Duration.millis(FADE_MS), this);
		setTranslateX(0);
		setTranslateY(45);
		slideUp.setToX(0);
		slideUp.setToY(0);

		FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_MS), this);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);

		ParallelTransition combined = new ParallelTransition(slideUp, fadeIn);
		combined.play();
	}

	/**
	 * Initializes the assets and components of the popup wrapper pane.
	 */
	private void initAssets() {
		containerPane = new StackPane(childPane);
		setCenter(containerPane);
		setPadding(new Insets(INSETS_MARGIN));
	}

	/**
	 * Initializes the menu pane with the title and "Go Back" button.
	 */
	private void initMenuPane() {
		goBackCross = new SVGPath();
		goBackCross.setContent(
				"M18.717 6.697l-1.414-1.414-5.303 5.303-5.303-5.303-1.414 1.414 5.303 5.303-5.303 5.303 1.414 1.414 5.303-5.303 5.303 5.303 1.414-1.414-5.303-5.303z");
		goBackCross.setScaleX(0.8);
		goBackCross.setScaleY(0.8);
		goBackCross.setScaleX(1);
		goBackCross.setScaleY(1);
		goBackCross.setTranslateY(4);

		goBackText = new Text("Go Back");
		goBackText.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));

		goBackLayout = new HBox(10, goBackText, goBackCross);
		goBackLayout.setAlignment(Pos.CENTER);
		goBackLayout.setStyle("-fx-alignment: top-right;");

		goBackLayout.setOnMouseEntered(e -> {
			goBackText.setUnderline(true);
			goBackLayout.setCursor(Cursor.HAND);
		});
		goBackLayout.setOnMouseExited(e -> {
			goBackText.setUnderline(false);
			goBackLayout.setCursor(Cursor.DEFAULT);
		});

		titleText = new Text(title);
		titleText.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 18));

		menuPane = new StackPane(titleText, goBackLayout);
		menuPane.setPrefHeight(MENU_HEIGHT);
		menuPane.setPadding(new Insets(19.2));

		setTop(menuPane);
	}

	/**
	 * Sets the event handler to be called when the "Go Back" button is pressed.
	 *
	 * @param handler the event handler to be called when the "Go Back" button is
	 *                pressed
	 */
	public void setOnGoBackPressed(EventHandler<MouseEvent> handler) {
		goBackLayout.setOnMouseClicked(handler);
	}

	/**
	 * Refreshes the style of the popup wrapper pane and its child pane based on the
	 * current style manager.
	 */
	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();
		setStyle(styleManager.styleOverlayPane());
		titleText.setFill(styleManager.colorText());
		goBackCross.setFill(styleManager.colorText());
		goBackText.setFill(styleManager.colorText());

		if (childPane != null && childPane instanceof Modular) {
			((Modular) childPane).refreshStyle();
		}
	}

	/**
	 * Returns the GameSessionContext associated with the popup wrapper pane.
	 *
	 * @return the GameSessionContext associated with the popup wrapper pane
	 */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
