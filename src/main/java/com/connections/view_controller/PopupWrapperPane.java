package com.connections.view_controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
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

public class PopupWrapperPane extends BorderPane implements Modular {
	protected static final double POPUP_EDGE_CUTOFF = 175;
	protected static final double INSETS_MARGIN = 10;
	protected static final double MENU_HEIGHT = 30;
	protected static final double POPUP_WIDTH = GameBoard.STAGE_WIDTH - POPUP_EDGE_CUTOFF;
	protected static final double POPUP_HEIGHT = GameBoard.STAGE_HEIGHT - POPUP_EDGE_CUTOFF;
	protected static final double CONTAINER_WIDTH = POPUP_WIDTH - INSETS_MARGIN * 2;
	protected static final double CONTAINER_HEIGHT = POPUP_HEIGHT - INSETS_MARGIN * 2 - MENU_HEIGHT * 1.5;
	
	private static final int FADE_MS = 150;
	
	private GameSessionContext gameSessionContext;
	private Pane childPane;
	private StackPane containerPane;
	private BorderPane menuPane;
	
	private HBox goBackLayout;
	private SVGPath goBackCross;
	private Text goBackText;
	
	private boolean fixedSize;
	
	public PopupWrapperPane(GameSessionContext gameSessionContext, Pane childPane) {
		this.childPane = childPane;
		this.gameSessionContext = gameSessionContext;
		setMaxSize(POPUP_WIDTH, POPUP_HEIGHT);
		setPadding(new Insets(INSETS_MARGIN));
		containerPane = new StackPane(childPane);
		
		setSizeFixed(false);
		setCenter(containerPane);
		initMenuPane();
		refreshStyle();
	}
	
	public void setSizeFixed(boolean fixedSize) {
		this.fixedSize = fixedSize;
		
		if(fixedSize) {
			setMinSize(POPUP_WIDTH, POPUP_HEIGHT);
			containerPane.setPrefSize(CONTAINER_WIDTH, CONTAINER_HEIGHT);
		} else {
			setPrefWidth(Region.USE_COMPUTED_SIZE);
	        setPrefHeight(Region.USE_COMPUTED_SIZE);
	        containerPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
	        containerPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
		}
	}
	
	public void setChild(Pane pane) {
		this.childPane = pane;
		containerPane.getChildren().clear();
		containerPane.getChildren().add(pane);
	}
	
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
		StackPane.setMargin(goBackLayout, new Insets(19.2, 19.2, 0, 0));

		goBackLayout.setOnMouseEntered(e -> {
			goBackText.setUnderline(true);
			goBackLayout.setCursor(Cursor.HAND);
		});
		goBackLayout.setOnMouseExited(e -> {
			goBackText.setUnderline(false);
			goBackLayout.setCursor(Cursor.DEFAULT);
		});
		
		menuPane = new BorderPane();
		menuPane.setRight(goBackLayout);
		menuPane.setPrefHeight(MENU_HEIGHT);
		
		setTop(menuPane);
	}
	
	public void setOnGoBackPressed(EventHandler <MouseEvent> handler) {
		goBackLayout.setOnMouseClicked(handler);
	}
	
	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();
		setStyle(styleManager.styleOverlayPane());
		goBackCross.setFill(styleManager.colorText());
		goBackText.setFill(styleManager.colorText());
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
