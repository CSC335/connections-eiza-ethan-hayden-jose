import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public enum DifficultyColor {
	YELLOW,
	GREEN,
	BLUE,
	PURPLE;
	
	public static EnumSet<DifficultyColor> getAllColors() {
      return EnumSet.allOf(DifficultyColor.class);
	}
}