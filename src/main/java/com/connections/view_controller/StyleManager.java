package com.connections.view_controller;

import com.connections.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.text.Font;
import javafx.scene.paint.Color;

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

	protected static final Color TEXT_LIGHT = Color.BLACK;
	protected static final Color TEXT_DARK = Color.rgb(176, 247, 121);

	protected static final Color TEXT_INVERTED_LIGHT = Color.WHITE;
	protected static final Color TEXT_INVERTED_DARK = Color.WHITE;

	protected static final Color TEXT_NEUTRAL_LIGHT = Color.BLACK;
	protected static final Color TEXT_NEUTRAL_DARK = Color.BLACK;

	protected static final String buttonNormalMode = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50;";
	protected static final String buttonDarkMode = "-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 1px; -fx-border-radius: 50;";
	protected static final String submitButtonFillDarkMode = "-fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 50; -fx-border-radius: 50;";
	protected static final String submitButtonFillNormalMode = "-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50;";
	protected static final String resultsPaneShareButtonNormalMode = "-fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 50; -fx-border-radius: 50; -fx-min-height: 48px; -fx-max-height: 48px;";
	protected static final String resultsPaneShareButtonDarkMode = "-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-min-height: 48px; -fx-max-height: 48px;";
	protected static final String overlayPaneNormalMode = "-fx-background-color: white; -fx-effect: dropshadow(gaussian, black, 20, 0, 0, 0);";
	protected static final String overlayPaneDarkMode = "-fx-background-color: black; -fx-effect: dropshadow(gaussian, rgb(176, 247, 121), 20, 0, 0, 0);";
	protected static final String wholeGameDarkMode = "-fx-background-color: black;";
	protected static final String wholeGameNormalMode = "-fx-background-color: white;";
	protected static final String labelNormalMode = "-fx-background-color: #ebebeb; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 5), dropshadow(gaussian, rgba(255, 255, 255, 0.4), 5, 0, 0, -5);";
	protected static final String labelDarkMode = "-fx-background-color: #242424; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(176, 247, 21, 0.4), 10, 0, 0, 5), dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 5);";
	protected static final String circleLightMode = "-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #ffcc89, #d8860b); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);";
	protected static final String circleDarkMode = "-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #777, #3a3a3a); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);";

	private boolean darkMode;
	private Map<String, Font> fontMap = new HashMap<>();

	public Font getFont(String fontName, int weight, int size) {
		try {
			String key = String.format("%s-%d-%d", fontName, size, weight);
			Font font = fontMap.get(key);

			if (font == null) {
				font = Font.loadFont(new FileInputStream(String.format("Fonts/%s-%d.ttf", fontName, weight)), size);
			}

			return font;
		} catch (FileNotFoundException e) {
			System.out.printf("ERROR: could not load font %s with weight %d and size %d!\n", fontName, weight, size);
		}

		return Font.font("System", size);
	}

	public Font getFont(String fontName, int size) {
		try {
			String key = String.format("%s-%d", fontName, size);
			Font font = fontMap.get(key);

			if (font == null) {
				font = Font.loadFont(new FileInputStream(String.format("Fonts/%s.ttf", fontName)), size);
			}

			return font;
		} catch (FileNotFoundException e) {
			System.out.printf("ERROR: could not load font %s with size %d!\n", fontName, size);
		}

		return Font.font("System", size);
	}

	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
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

	public String getButton() {
		return darkMode ? buttonDarkMode : buttonNormalMode;
	}

	public String getSubmitButton() {
		return darkMode ? submitButtonFillDarkMode : submitButtonFillNormalMode;
	}

	public String getResultsPaneShareButton() {
		return darkMode ? resultsPaneShareButtonDarkMode : resultsPaneShareButtonNormalMode;
	}

	public String overlayPane() {
		return darkMode ? overlayPaneDarkMode : overlayPaneNormalMode;
	}

	public String getWholeGame() {
		return darkMode ? wholeGameDarkMode : wholeGameNormalMode;
	}

	public String getLabel() {
		return darkMode ? labelDarkMode : labelNormalMode;
	}

	public String getCircle() {
		return darkMode ? circleDarkMode : circleLightMode;
	}

	public Color getwholeAchievementsPane() {
		return darkMode ? Color.BLACK : Color.WHITE;
	}

	public Color getSVGFill() {
		return darkMode ? Color.BLACK : Color.WHITE;
	}
}
