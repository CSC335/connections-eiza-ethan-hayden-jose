package com.connections.model;

import java.util.EnumSet;

/**
 * Represents the difficulty level and associated color for a game.
 */
public enum DifficultyColor implements Comparable<DifficultyColor> {
	YELLOW(1), GREEN(2), BLUE(3), PURPLE(4);

	private final int difficultyLevel;

	/**
	 * Constructs a DifficultyColor with the specified difficulty level.
	 *
	 * @param difficultyLevel the difficulty level associated with the color
	 */
	DifficultyColor(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	/**
	 * Returns the difficulty level associated with the color.
	 *
	 * @return the difficulty level
	 */
	public int getDifficultyLevel() {
		return difficultyLevel;
	}

	/**
	 * Returns an EnumSet containing all the DifficultyColor values.
	 *
	 * @return an EnumSet of all DifficultyColor values
	 */
	public static EnumSet<DifficultyColor> getAllColors() {
		return EnumSet.allOf(DifficultyColor.class);
	}
}