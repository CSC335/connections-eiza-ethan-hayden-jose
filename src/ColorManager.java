import javafx.scene.paint.Color;

public class ColorManager {
	private boolean darkMode;

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

	protected static final Color RECTANGLE_DEFAULT_COLOR_DARK = Color.rgb(239, 239, 230);
	protected static final Color RECTANGLE_SELECTED_COLOR_DARK = Color.rgb(90, 89, 78);
	protected static final Color RECTANGLE_INCORRECT_COLOR_DARK = Color.rgb(130, 131, 122);

	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
	}

	public Color yellow() {
		return darkMode ? YELLOW_DARK : YELLOW_LIGHT;
	}

	public Color green() {
		return darkMode ? GREEN_DARK : GREEN_LIGHT;
	}

	public Color blue() {
		return darkMode ? BLUE_DARK : BLUE_LIGHT;
	}

	public Color purple() {
		return darkMode ? PURPLE_DARK : PURPLE_LIGHT;
	}

	public Color difficultyColor(DifficultyColor dc) {
		switch (dc) {
		case YELLOW:
			return yellow();
		case GREEN:
			return green();
		case BLUE:
			return blue();
		case PURPLE:
			return purple();
		}
		
		return defaultRectangleColor();
	}

	public Color defaultRectangleColor() {
		return darkMode ? RECTANGLE_DEFAULT_COLOR_DARK : RECTANGLE_DEFAULT_COLOR_LIGHT;
	}

	public Color selectedRectangleColor() {
		return darkMode ? RECTANGLE_SELECTED_COLOR_DARK : RECTANGLE_RECT_SELECTED_COLOR_LIGHT;
	}

	public Color incorrectRectangleColor() {
		return darkMode ? RECTANGLE_INCORRECT_COLOR_DARK : RECTANGLE_INCORRECT_COLOR_LIGHT;
	}
}
