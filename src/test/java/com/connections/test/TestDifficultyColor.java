package com.connections.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.connections.model.DifficultyColor;

class TestDifficultyColor {

	@Test
	void testListShuffleAndSort() {
		List<DifficultyColor> colors = new ArrayList<>(DifficultyColor.getAllColors());
		Collections.shuffle(colors);
		Collections.sort(colors);

		List<DifficultyColor> expected = List.of(DifficultyColor.YELLOW, DifficultyColor.GREEN, DifficultyColor.BLUE,
				DifficultyColor.PURPLE);

		assertEquals(expected, colors);
	}

	@Test
	void testGetAllColors() {
		EnumSet<DifficultyColor> allColors = DifficultyColor.getAllColors();
		EnumSet<DifficultyColor> expected = EnumSet.allOf(DifficultyColor.class);

		assertEquals(expected, allColors);
	}

	@Test
	void testGetDifficultyLevel() {
		assertEquals(1, DifficultyColor.YELLOW.getDifficultyLevel());
		assertEquals(2, DifficultyColor.GREEN.getDifficultyLevel());
		assertEquals(3, DifficultyColor.BLUE.getDifficultyLevel());
		assertEquals(4, DifficultyColor.PURPLE.getDifficultyLevel());
	}

	@Test
	void testComparable() {
		DifficultyColor[] sortedColors = DifficultyColor.values();
		assertArrayEquals(new DifficultyColor[] { DifficultyColor.YELLOW, DifficultyColor.GREEN, DifficultyColor.BLUE,
				DifficultyColor.PURPLE }, sortedColors);
	}
}