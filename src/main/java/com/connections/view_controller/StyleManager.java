package com.connections.view_controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.connections.model.DifficultyColor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class StyleManager {
	protected static final Color YELLOW_LIGHT = Color.rgb(249, 223, 109);
	protected static final Color GREEN_LIGHT = Color.rgb(160, 195, 90);
	protected static final Color BLUE_LIGHT = Color.rgb(176, 195, 238);
	protected static final Color PURPLE_LIGHT = Color.rgb(186, 128, 197);

	protected static final Color RECTANGLE_DEFAULT_COLOR_LIGHT = Color.rgb(239, 239, 230);
	protected static final Color RECTANGLE_RECT_SELECTED_COLOR_LIGHT = Color.rgb(90, 89, 78);
	protected static final Color RECTANGLE_INCORRECT_COLOR_LIGHT = Color.rgb(130, 131, 122);

	protected static final Color YELLOW_DARK = Color.rgb(249, 223, 109);
	protected static final Color GREEN_DARK = Color.rgb(160, 195, 90);
	protected static final Color BLUE_DARK = Color.rgb(176, 195, 238);
	protected static final Color PURPLE_DARK = Color.rgb(186, 128, 197);

	protected static final Color RECTANGLE_DEFAULT_COLOR_DARK = Color.rgb(50, 50, 50);
	protected static final Color RECTANGLE_SELECTED_COLOR_DARK = Color.rgb(150, 150, 150);
	protected static final Color RECTANGLE_INCORRECT_COLOR_DARK = Color.rgb(90, 90, 90);

	protected static final Color WHOLE_GAME_BACKGROUND_LIGHT = Color.WHITE;
	protected static final Color WHOLE_GAME_BACKGROUND_DARK = Color.BLACK;

	protected static final Color TEXT_LIGHT = Color.BLACK;
	protected static final Color TEXT_DARK = Color.rgb(176, 247, 121);
	
	protected static final Color TEXT_DISABLED_LIGHT = Color.rgb(200, 200, 200);
	protected static final Color TEXT_DISABLED_DARK = Color.rgb(126, 197, 81);

	protected static final Color TEXT_INVERTED_LIGHT = Color.WHITE;
	protected static final Color TEXT_INVERTED_DARK = Color.WHITE;

	protected static final Color TEXT_NEUTRAL_LIGHT = Color.BLACK;
	protected static final Color TEXT_NEUTRAL_DARK = Color.BLACK;

	protected static final String BUTTON_LIGHT_MODE = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50;";
	protected static final String BUTTON_DARK_MODE = "-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 1px; -fx-border-radius: 50;";

	protected static final String SUBMIT_BUTTON_FILL_LIGHT_MODE = "-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50;";
	protected static final String SUBMIT_BUTTON_FILL_DARK_MODE = "-fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 50; -fx-border-radius: 50;";

	protected static final String RESULTS_PANE_SHARE_BUTTON_LIGHT_MODE = "-fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 50; -fx-border-radius: 50; -fx-min-height: 48px; -fx-max-height: 48px;";
	protected static final String RESULTS_PANE_SHARE_BUTTON_DARK_MODE = "-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-min-height: 48px; -fx-max-height: 48px;";

	protected static final String OVERLAY_PANE_LIGHT_MODE = "-fx-background-color: white; -fx-effect: dropshadow(gaussian, black, 20, 0, 0, 0);";
	protected static final String OVERLAY_PANE_DARK_MODE = "-fx-background-color: black; -fx-effect: dropshadow(gaussian, rgb(176, 247, 121), 20, 0, 0, 0);";

	protected static final String WHOLE_GAME_LIGHT_MODE = "-fx-background-color: white;";
	protected static final String WHOLE_GAME_DARK_MODE = "-fx-background-color: black;";

	protected static final String LABEL_LIGHT_MODE = "-fx-background-color: #ebebeb; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 5), dropshadow(gaussian, rgba(255, 255, 255, 0.4), 5, 0, 0, -5);";
	protected static final String LABEL_DARK_MODE = "-fx-background-color: #242424; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(176, 247, 21, 0.4), 10, 0, 0, 5), dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 5);";

	protected static final String CIRCLE_LIGHT_MODE = "-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #ffcc89, #d8860b); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);";
	protected static final String CIRCLE_DARK_MODE = "-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #777, #3a3a3a); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);";

	private boolean darkMode;
	private EventHandler<ActionEvent> onDarkModeChange;
	private Map<String, Font> fontMap = new HashMap<>();

	public Font getFont(String fontName, String fileExtension, int weight, int size) {
		try {
			String key = String.format("%s-%s-%d-%d", fontName, fileExtension, size, weight);
			Font font = fontMap.get(key);

			if (font == null) {
				font = Font.loadFont(new FileInputStream(String.format("Fonts/%s-%d.%s", fontName, weight, fileExtension)), size);
				fontMap.put(key, font);
			}

			return font;
		} catch (FileNotFoundException e) {
			System.out.printf("ERROR: could not load font %s with weight %d and size %d!\n", fontName, weight, size);
		}

		return Font.font("System", size);
	}

	public Font getFont(String fontName, String fileExtension, int size) {
		try {
			String key = String.format("%s-%s-%d", fontName, fileExtension, size);
			Font font = fontMap.get(key);

			if (font == null) {
				font = Font.loadFont(new FileInputStream(String.format("Fonts/%s.%s", fontName, fileExtension)), size);
				fontMap.put(key, font);
			}

			return font;
		} catch (FileNotFoundException e) {
			System.out.printf("ERROR: could not load font %s with size %d!\n", fontName, size);
		}

		return Font.font("System", size);
	}

	public Font getFont(String fontName, int weight, int size) {
		return getFont(fontName, "ttf", weight, size);
	}

	public Font getFont(String fontName, int size) {
		return getFont(fontName, "ttf", size);
	}

	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
		if(onDarkModeChange != null) {
			onDarkModeChange.handle(new ActionEvent(this, null));
		}
	}

	public boolean isDarkMode() {
		return darkMode;
	}

	public void setOnDarkModeChange(EventHandler<ActionEvent> handler) {
		onDarkModeChange = handler;
	}

	public Color colorYellow() {
		return darkMode ? YELLOW_DARK : YELLOW_LIGHT;
	}

	public Color colorGreen() {
		return darkMode ? GREEN_DARK : GREEN_LIGHT;
	}

	public Color colorBlue() {
		return darkMode ? BLUE_DARK : BLUE_LIGHT;
	}

	public Color colorPurple() {
		return darkMode ? PURPLE_DARK : PURPLE_LIGHT;
	}

	public Color colorDifficulty(DifficultyColor dc) {
		switch (dc) {
		case YELLOW:
			return colorYellow();
		case GREEN:
			return colorGreen();
		case BLUE:
			return colorBlue();
		case PURPLE:
			return colorPurple();
		}

		return colorDefaultRectangle();
	}

	public Color colorDefaultRectangle() {
		return darkMode ? RECTANGLE_DEFAULT_COLOR_DARK : RECTANGLE_DEFAULT_COLOR_LIGHT;
	}

	public Color colorSelectedRectangle() {
		return darkMode ? RECTANGLE_SELECTED_COLOR_DARK : RECTANGLE_RECT_SELECTED_COLOR_LIGHT;
	}

	public Color colorIncorrectRectangle() {
		return darkMode ? RECTANGLE_INCORRECT_COLOR_DARK : RECTANGLE_INCORRECT_COLOR_LIGHT;
	}

	public Color colorText() {
		return darkMode ? TEXT_DARK : TEXT_LIGHT;
	}
	
	public Color colorTextDisabled() {
		return darkMode ? TEXT_DISABLED_DARK : TEXT_DISABLED_LIGHT;
	}

	public Color colorTextInverted() {
		return darkMode ? TEXT_INVERTED_DARK : TEXT_INVERTED_LIGHT;
	}

	public Color colorTextNeutral() {
		return darkMode ? TEXT_NEUTRAL_DARK : TEXT_NEUTRAL_LIGHT;
	}

	public Color colorPopupBackground() {
		return darkMode ? Color.WHITE : Color.BLACK;
	}

	public Color colorPopupText() {
		return darkMode ? Color.BLACK : Color.WHITE;
	}

	public Color colorWholeAchievementsPane() {
		return darkMode ? Color.BLACK : Color.WHITE;
	}

	public Color colorWholeGameBackground() {
		return darkMode ? WHOLE_GAME_BACKGROUND_DARK :WHOLE_GAME_BACKGROUND_LIGHT;
	}

	public Color colorSVGFill() {
		return darkMode ? Color.BLACK : Color.WHITE;
	}

	public String styleButton() {
		return darkMode ? BUTTON_DARK_MODE : BUTTON_LIGHT_MODE;
	}

	public String styleSubmitButtonFill() {
	    return darkMode ? SUBMIT_BUTTON_FILL_DARK_MODE : SUBMIT_BUTTON_FILL_LIGHT_MODE;
	}

	public String styleResultsPaneShareButton() {
	    return darkMode ? RESULTS_PANE_SHARE_BUTTON_DARK_MODE : RESULTS_PANE_SHARE_BUTTON_LIGHT_MODE;
	}

	public String styleOverlayPane() {
	    return darkMode ? OVERLAY_PANE_DARK_MODE : OVERLAY_PANE_LIGHT_MODE;
	}

	public String styleWholeGame() {
	    return darkMode ? WHOLE_GAME_DARK_MODE : WHOLE_GAME_LIGHT_MODE;
	}

	public String styleLabel() {
	    return darkMode ? LABEL_DARK_MODE : LABEL_LIGHT_MODE;
	}

	public String styleCircle() {
	    return darkMode ? CIRCLE_DARK_MODE : CIRCLE_LIGHT_MODE;
	}
}
