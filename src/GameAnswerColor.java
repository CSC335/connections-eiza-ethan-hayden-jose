
public class GameAnswerColor {
	private DifficultyColor color;
	private String description;
	private String[] hints;
	private String[] words;
	
	public GameAnswerColor(DifficultyColor color, String description, String[] hints, String[] words) {
		this.color = color;
		this.hints = hints;
		this.description = description;
		this.words = words;
	}
	
	public DifficultyColor getColor() {
		return color;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String[] getHints() {
		return hints;
	}
	
	public String[] getWords() {
		return words;
	}
}
