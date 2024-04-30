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

/**
 * The StyleManager class is responsible for managing the styles and colors used
 * throughout the application. It provides methods for retrieving colors and
 * styles based on the current theme (light or dark mode). It also handles
 * loading and caching fonts from files.
 */
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

	/**
	 * Retrieves a Font instance for the specified font name, file extension,
	 * weight, and size. Fonts are loaded from files and cached for future use.
	 *
	 * @param fontName      The name of the font.
	 * @param fileExtension The file extension of the font file (e.g., "ttf",
	 *                      "otf").
	 * @param weight        The weight of the font (e.g., 400 for normal, 600 for
	 *                      bold).
	 * @param size          The size of the font in points.
	 * @return The Font instance for the specified parameters.
	 */
	public Font getFont(String fontName, String fileExtension, int weight, int size) {
		try {
			String key = String.format("%s-%s-%d-%d", fontName, fileExtension, size, weight);
			Font font = fontMap.get(key);

			if (font == null) {
				font = Font.loadFont(
						new FileInputStream(String.format("Fonts/%s-%d.%s", fontName, weight, fileExtension)), size);
				fontMap.put(key, font);
			}

			return font;
		} catch (FileNotFoundException e) {
			System.out.printf("ERROR: could not load font %s with weight %d and size %d!\n", fontName, weight, size);
		}

		return Font.font("System", size);
	}

	/**
	 * Retrieves a Font instance for the specified font name, file extension, and
	 * size. Fonts are loaded from files and cached for future use.
	 *
	 * @param fontName      The name of the font.
	 * @param fileExtension The file extension of the font file (e.g., "ttf",
	 *                      "otf").
	 * @param size          The size of the font in points.
	 * @return The Font instance for the specified parameters.
	 */
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

	/**
	 * Retrieves a Font instance for the specified font name, weight, and size. The
	 * file extension is assumed to be "ttf".
	 *
	 * @param fontName The name of the font.
	 * @param weight   The weight of the font (e.g., 400 for normal, 600 for bold).
	 * @param size     The size of the font in points.
	 * @return The Font instance for the specified parameters.
	 */
	public Font getFont(String fontName, int weight, int size) {
		return getFont(fontName, "ttf", weight, size);
	}

	/**
	 * Retrieves a Font instance for the specified font name and size. The file
	 * extension is assumed to be "ttf", and the weight is assumed to be normal.
	 *
	 * @param fontName The name of the font.
	 * @param size     The size of the font in points.
	 * @return The Font instance for the specified parameters.
	 */
	public Font getFont(String fontName, int size) {
		return getFont(fontName, "ttf", size);
	}

	/**
	 * Sets the dark mode state of the application.
	 *
	 * @param darkMode true to enable dark mode, false to enable light mode.
	 */
	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
		if (onDarkModeChange != null) {
			onDarkModeChange.handle(new ActionEvent(this, null));
		}
	}

	/**
	 * Retrieves the current dark mode state of the application.
	 *
	 * @return true if dark mode is enabled, false otherwise.
	 */
	public boolean isDarkMode() {
		return darkMode;
	}

	/**
	 * Sets the event handler to be called when the dark mode state changes.
	 *
	 * @param handler The event handler to be called when dark mode changes.
	 */
	public void setOnDarkModeChange(EventHandler<ActionEvent> handler) {
		onDarkModeChange = handler;
	}

	/**
	 * Retrieves the color associated with the "yellow" difficulty level based on
	 * the current theme.
	 *
	 * @return The color for the "yellow" difficulty level.
	 */
	public Color colorYellow() {
		return darkMode ? YELLOW_DARK : YELLOW_LIGHT;
	}

	/**
	 * Retrieves the color associated with the "green" difficulty level based on the
	 * current theme.
	 *
	 * @return The color for the "green" difficulty level.
	 */
	public Color colorGreen() {
		return darkMode ? GREEN_DARK : GREEN_LIGHT;
	}

	/**
	 * Retrieves the color associated with the "blue" difficulty level based on the
	 * current theme.
	 *
	 * @return The color for the "blue" difficulty level.
	 */
	public Color colorBlue() {
		return darkMode ? BLUE_DARK : BLUE_LIGHT;
	}

	/**
	 * Retrieves the color associated with the "purple" difficulty level based on
	 * the current theme.
	 *
	 * @return The color for the "purple" difficulty level.
	 */
	public Color colorPurple() {
		return darkMode ? PURPLE_DARK : PURPLE_LIGHT;
	}

	/**
	 * Retrieves the color associated with the specified difficulty level based on
	 * the current theme.
	 *
	 * @param dc The difficulty level.
	 * @return The color for the specified difficulty level.
	 */
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

	/**
	 * Retrieves the color associated with the default rectangle based on the
	 * current theme.
	 *
	 * @return The color for the default rectangle.
	 */
	public Color colorDefaultRectangle() {
		return darkMode ? RECTANGLE_DEFAULT_COLOR_DARK : RECTANGLE_DEFAULT_COLOR_LIGHT;
	}

	/**
	 * Retrieves the color associated with the selected rectangle based on the
	 * current theme.
	 *
	 * @return The color for the selected rectangle.
	 */
	public Color colorSelectedRectangle() {
		return darkMode ? RECTANGLE_SELECTED_COLOR_DARK : RECTANGLE_RECT_SELECTED_COLOR_LIGHT;
	}

	/**
	 * Retrieves the color associated with the incorrect rectangle based on the
	 * current theme.
	 *
	 * @return The color for the incorrect rectangle.
	 */
	public Color colorIncorrectRectangle() {
		return darkMode ? RECTANGLE_INCORRECT_COLOR_DARK : RECTANGLE_INCORRECT_COLOR_LIGHT;
	}

	/**
	 * Retrieves the color associated with the text based on the current theme.
	 *
	 * @return The color for the text.
	 */
	public Color colorText() {
		return darkMode ? TEXT_DARK : TEXT_LIGHT;
	}

	/**
	 * Retrieves the color associated with the disabled text based on the current
	 * theme.
	 *
	 * @return The color for the disabled text.
	 */
	public Color colorTextDisabled() {
		return darkMode ? TEXT_DISABLED_DARK : TEXT_DISABLED_LIGHT;
	}

	/**
	 * Retrieves the color associated with the inverted text based on the current
	 * theme.
	 *
	 * @return The color for the inverted text.
	 */
	public Color colorTextInverted() {
		return darkMode ? TEXT_INVERTED_DARK : TEXT_INVERTED_LIGHT;
	}

	/**
	 * Retrieves the color associated with the neutral text based on the current
	 * theme.
	 *
	 * @return The color for the neutral text.
	 */
	public Color colorTextNeutral() {
		return darkMode ? TEXT_NEUTRAL_DARK : TEXT_NEUTRAL_LIGHT;
	}

	/**
	 * Retrieves the color associated with the popup background based on the current
	 * theme.
	 *
	 * @return The color for the popup background.
	 */
	public Color colorPopupBackground() {
		return darkMode ? Color.WHITE : Color.BLACK;
	}

	/**
	 * Retrieves the color associated with the popup text based on the current
	 * theme.
	 *
	 * @return The color for the popup text.
	 */
	public Color colorPopupText() {
		return darkMode ? Color.BLACK : Color.WHITE;
	}

	/**
	 * Retrieves the color associated with the whole achievements pane based on the
	 * current theme.
	 *
	 * @return The color for the whole achievements pane.
	 */
	public Color colorWholeAchievementsPane() {
		return darkMode ? Color.BLACK : Color.WHITE;
	}

	/**
	 * Retrieves the color associated with the whole game background based on the
	 * current theme.
	 *
	 * @return The color for the whole game background.
	 */
	public Color colorWholeGameBackground() {
		return darkMode ? WHOLE_GAME_BACKGROUND_DARK : WHOLE_GAME_BACKGROUND_LIGHT;
	}

	/**
	 * Retrieves the color associated with the SVG fill based on the current theme.
	 *
	 * @return The color for the SVG fill.
	 */
	public Color colorSVGFill() {
		return darkMode ? Color.BLACK : Color.WHITE;
	}

	/**
	 * Retrieves the color associated with the timer background based on the current
	 * theme.
	 *
	 * @return The color for the timer background.
	 */
	public Color timerBackground() {
		return darkMode ? Color.WHITE : Color.BLACK;
	}

	/**
	 * Retrieves the style for buttons based on the current theme.
	 *
	 * @return The style for buttons.
	 */
	public String styleButton() {
		return darkMode ? BUTTON_DARK_MODE : BUTTON_LIGHT_MODE;
	}

	/**
	 * Retrieves the style for the submit button fill based on the current theme.
	 *
	 * @return The style for the submit button fill.
	 */
	public String styleSubmitButtonFill() {
		return darkMode ? SUBMIT_BUTTON_FILL_DARK_MODE : SUBMIT_BUTTON_FILL_LIGHT_MODE;
	}

	/**
	 * Retrieves the style for the share button in the results pane based on the
	 * current theme.
	 *
	 * @return The style for the share button in the results pane.
	 */
	public String styleResultsPaneShareButton() {
		return darkMode ? RESULTS_PANE_SHARE_BUTTON_DARK_MODE : RESULTS_PANE_SHARE_BUTTON_LIGHT_MODE;
	}

	/**
	 * Retrieves the style for the overlay pane based on the current theme.
	 *
	 * @return The style for the overlay pane.
	 */
	public String styleOverlayPane() {
		return darkMode ? OVERLAY_PANE_DARK_MODE : OVERLAY_PANE_LIGHT_MODE;
	}

	/**
	 * Retrieves the style for the whole game based on the current theme.
	 *
	 * @return The style for the whole game.
	 */
	public String styleWholeGame() {
		return darkMode ? WHOLE_GAME_DARK_MODE : WHOLE_GAME_LIGHT_MODE;
	}

	/**
	 * Retrieves the style for labels based on the current theme.
	 *
	 * @return The style for labels.
	 */
	public String styleLabel() {
		return darkMode ? LABEL_DARK_MODE : LABEL_LIGHT_MODE;
	}

	/**
	 * Retrieves the style for circles based on the current theme.
	 *
	 * @return The style for circles.
	 */
	public String styleCircle() {
		return darkMode ? CIRCLE_DARK_MODE : CIRCLE_LIGHT_MODE;
	}
}
