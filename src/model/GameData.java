package model;
import java.util.Map;

public class GameData {
	private Map<DifficultyColor, GameAnswerColor> answerMap;
	
	public GameData(Map<DifficultyColor, GameAnswerColor> answerMap) {
		this.answerMap = answerMap;
	}
	
	public GameAnswerColor getAnswerForColor(DifficultyColor color) {
		return answerMap.get(color);
	}
	
	public Map<DifficultyColor, GameAnswerColor> getAnswerMap() {
		return answerMap;
	}
}
