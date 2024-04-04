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
}
