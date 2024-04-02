import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
	
	public boolean wordMatchesSet(Set<String> otherWordsSet) {
		Set<String> wordsSet = new HashSet<>(Arrays.asList(words));
		return otherWordsSet.equals(wordsSet);
	}
	
	public String getWordListString() {
		String result = "";
		for (int i = 0; i < words.length; i++) {
            result += words[i];
            if (i < words.length - 1) {
                result += ", ";
            }
        }
		return result.toUpperCase();
	}
}
