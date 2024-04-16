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
		this.gameSessionContext = gameSessionContext;
		initAssets();
		initSampleAchievements();
		refreshStyle();
	}
	
	private void initAssets() {
		gridPane = new GridPane();
		gridPane.setHgap(TileGridWord.GAP);
		gridPane.setVgap(TileGridWord.GAP);
		gridPane.setAlignment(Pos.CENTER);
		
		gridPane.setMaxSize(TileGridWord.PANE_WIDTH, TileGridWord.PANE_HEIGHT);
		setMaxSize(TileGridWord.PANE_WIDTH, TileGridWord.PANE_HEIGHT);
		
		getChildren().add(gridPane);
	}
	
	private void initSampleAchievements() {
		String[] achievementLabels = {
			    "1 Standard Game Completed", "10 Standard Games Completed",
			    "50 Standard Games Completed", "100 Standard Games Completed", "1 Time Trial Game Completed",
			    "10 Time Trial Games Completed", "50 Time Trial Games Completed", "100 Time Trial Games Completed",
			    "Solved 1 Puzzle with No Mistakes", "Solved 10 Puzzles with No Mistakes",
			    "Solved 50 Puzzles with No Mistakes", "Solved 100 Puzzles with No Mistakes",
			    "Solved 1 Time Trial Puzzle in Under 30 Seconds", "Solved 10 Time Trial Puzzles in Under 30 Seconds",
			    "Solved 50 Time Trial Puzzles in Under 30 Seconds",
			    "Solved 100 Time Trial Puzzles in Under 30 Seconds"
			};
		
		DifficultyColor[] colorIntensity = {DifficultyColor.YELLOW, DifficultyColor.GREEN, DifficultyColor.BLUE, DifficultyColor.PURPLE};
		
		gridPane.getChildren().clear();
		for (int row = 0; row < TileGridWord.ROWS; row++) {
			for (int col = 0; col < TileGridWord.COLS; col++) {
				DifficultyColor color = colorIntensity[col];
				gridPane.add(new GameTileAchievement(color, achievementLabels[row * TileGridWord.COLS + col], col <= row, this), col, row);
			}
		}
	}
	
	public void animateCompletion() {
		for (Node node : gridPane.getChildren()) {
			if(node instanceof GameTileAchievement) {
				GameTileAchievement tileAchievement = (GameTileAchievement) node;
				tileAchievement.animateCompletion();
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
