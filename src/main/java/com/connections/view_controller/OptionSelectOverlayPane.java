package com.connections.view_controller;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The OptionSelectOverlayPane class represents a pane that displays options for selecting a game mode.
 * It appears as an overlay with a blurred background and provides buttons for each option.
 */
public class OptionSelectOverlayPane extends StackPane implements Modular {
	private Pane blurredBackgroundPane;
	private Text titleText;
	private HBox optionsLayout;
	private int optionsWidthTotal;
	private VBox entireLayout;
	private GameSessionContext gameSessionContext;
	private String optionSelected;
	private EventHandler<ActionEvent> onDisappear;

    /**
     * Constructs a new OptionSelectOverlayPane with the specified GameSessionContext.
     *
     * @param gameSessionContext the GameSessionContext used by the option select overlay pane
     */
	public OptionSelectOverlayPane(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		initAssets();
	}

	/*
	 * todo: finish the dark mode support of this class
	 */

    /**
     * Initializes the assets and components of the option select overlay pane.
     */
	private void initAssets() {
		titleText = new Text("Select a Game Mode");

		blurredBackgroundPane = new Pane();
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

    /**
     * Adds a button to the options layout with the specified text and width.
     *
     * @param text  the text to be displayed on the button
     * @param width the width of the button
     */
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

    /**
     * Makes the option select overlay pane appear with a fade-in animation.
     */
	public void appear() {
		FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		fadeIn.play();

		fadeIn.setOnFinished(event -> {
			setOptionsDisabled(false);
		});

		setVisible(true);
	}

    /**
     * Sets the disabled state of the options in the option select overlay pane.
     *
     * @param disabled true to disable the options, false to enable them
     */
	private void setOptionsDisabled(boolean disabled) {
		for(Node node : optionsLayout.getChildren()) {
			node.setDisable(disabled);
		}
	}

    /**
     * Makes the option select overlay pane disappear with a fade-out animation.
     */
	public void disappear() {
		setOptionsDisabled(true);

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

    /**
     * Returns the selected option.
     *
     * @return the selected option
     */
	public String getOptionSelected() {
		return optionSelected;
	}

    /**
     * Sets the event handler to be called when the option select overlay pane disappears.
     *
     * @param onDisappear the event handler to be called when the option select overlay pane disappears
     */
	public void setOnDisappear(EventHandler<ActionEvent> onDisappear) {
		this.onDisappear = onDisappear;
	}

    /**
     * Refreshes the style of the option select overlay pane based on the current style manager.
     */
	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();

		Font karnakFont = styleManager.getFont("KarnakPro-Medium_400", "otf", 65);
		titleText.setFont(Font.font(karnakFont.getFamily(), FontWeight.THIN, 20));
		titleText.setFill(Color.WHITE);
	}

    /**
     * Returns the GameSessionContext associated with the option select overlay pane.
     *
     * @return the GameSessionContext associated with the option select overlay pane
     */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
