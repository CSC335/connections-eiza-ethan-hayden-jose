package com.connections.view_controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.FadeTransition;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

public class OptionSelectOverlayPane extends StackPane implements Modular {
	private Pane blurredBackgroundPane;
	private Text titleText;
	private HBox optionsLayout;
	private int optionsWidthTotal;
	private VBox entireLayout;
	private GameSessionContext gameSessionContext;
	private String optionSelected;
	private EventHandler<ActionEvent> onDisappear;

	public OptionSelectOverlayPane(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		initAssets();
	}

	private void initAssets() {
		titleText = new Text("Select a Game Mode");
		
		blurredBackgroundPane = new Pane();
//		GaussianBlur gaussianBlur = new GaussianBlur();
//		blurredBackgroundPane.setEffect(gaussianBlur);
		blurredBackgroundPane.setOpacity(0.25);
		blurredBackgroundPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, null)));
		
		optionsWidthTotal = 130;
		
		optionsLayout = new HBox(10);
		optionsLayout.setAlignment(Pos.CENTER);
		optionsLayout.setPadding(new Insets(20));
		optionsLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), null)));
		
		entireLayout = new VBox(10, titleText, optionsLayout);
		entireLayout.setAlignment(Pos.CENTER);
		getChildren().addAll(blurredBackgroundPane, entireLayout);
		
		refreshStyle();
	}

	public void addButton(String text, int width) {
		CircularButton button = new CircularButton(text, width, gameSessionContext, false);
		optionsLayout.getChildren().add(button);
		button.setOnAction(event -> {
			optionSelected = text;
			disappear();
		});
		optionsWidthTotal += (width + 5);
		optionsLayout.setMaxWidth(optionsWidthTotal);
	}

	public void appear() {
		FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		fadeIn.play();
		
		setVisible(true);
	}

	public void disappear() {
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.play();
		
		fadeOut.setOnFinished(event -> {
			if(onDisappear != null) {
				onDisappear.handle(new ActionEvent(this, null));
			}
			setVisible(false);
		});
	}
	
	public String getOptionSelected() {
		return optionSelected;
	}
	
	public void setOnDisappear(EventHandler<ActionEvent> onDisappear) {
		this.onDisappear = onDisappear;
	}

	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();
		
		Font karnakFont = styleManager.getFont("KarnakPro-Medium_400", "otf", 65);
		titleText.setFont(Font.font(karnakFont.getFamily(), FontWeight.THIN, 20));
		titleText.setFill(Color.WHITE);
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
