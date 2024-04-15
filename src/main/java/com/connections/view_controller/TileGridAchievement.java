package com.connections.view_controller;

import com.connections.model.DifficultyColor;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class TileGridAchievement extends Pane implements Modular {
	private GameSessionContext gameSessionContext;
	private GridPane gridPane;
	
	public TileGridAchievement(GameSessionContext gameSessionContext) {
		gridPane = new GridPane();
		gridPane.setHgap(TileGridWord.GAP);
		gridPane.setVgap(TileGridWord.GAP);
		gridPane.setAlignment(Pos.CENTER);
		getChildren().add(gridPane);
		initSampleAchievements();
		refreshStyle();
	}
	
	private void initSampleAchievements() {
		String[] achievementLabels = { "1 standard game completed", "10 standard games completed",
				"50 standard games completed", "100 standard games completed", "1 time trial game completed",
				"10 time trial games completed", "50 time trial games completed", "100 time trial games completed",
				"Solved 1 puzzle with no mistakes", "Solved 10 puzzles with no mistakes",
				"Solved 50 puzzles with no mistakes", "Solved 100 puzzles with no mistakes",
				"Solved 1 time trial puzzle in under 30 seconds", "Solved 10 time trial puzzles in under 30 seconds",
				"Solved 50 time trial puzzles in under 30 seconds",
				"Solved 100 time trial puzzles in under 30 seconds" };
		
		gridPane.getChildren().clear();
		for (int row = 0; row < TileGridWord.ROWS; row++) {
			for (int col = 0; col < TileGridWord.COLS; col++) {
				gridPane.add(new GameTileAchievement(DifficultyColor.YELLOW, achievementLabels[row * TileGridWord.COLS + col], true, this), col, row);
			}
		}
	}
	
	@Override
	public void refreshStyle() {
		setBackground(new Background(new BackgroundFill(gameSessionContext.getStyleManager().colorWholeAchievementsPane(), null, null)));
		for (Node node : gridPane.getChildren()) {
			if(node instanceof Modular) {
				Modular stylableNode = (Modular) node;
				stylableNode.refreshStyle();
			}
		}
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
