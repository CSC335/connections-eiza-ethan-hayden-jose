import java.util.EnumSet;

public enum DifficultyColor {
	YELLOW,
	GREEN,
	BLUE,
	PURPLE;
	
	public static EnumSet<DifficultyColor> getAllColors() {
      return EnumSet.allOf(DifficultyColor.class);
	}
}