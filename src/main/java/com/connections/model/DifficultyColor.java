package com.connections.model;
import java.util.EnumSet;

public enum DifficultyColor implements Comparable<DifficultyColor> {
	YELLOW(1),
    GREEN(2),
    BLUE(3),
    PURPLE(4);

    private final int difficultyLevel;

    DifficultyColor(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

	public static EnumSet<DifficultyColor> getAllColors() {
      return EnumSet.allOf(DifficultyColor.class);
	}
}