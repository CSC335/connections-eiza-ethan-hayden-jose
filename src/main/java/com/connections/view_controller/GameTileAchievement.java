package com.connections.view_controller;

import com.connections.model.*;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class GameTileAchievement extends StackPane implements Modular {
	private boolean completedStatus;
	private BorderPane textBorderPane;
	private Label achievementLabel;
	private Font achievementFont;
	private Label statusLabel;
	private Font statusFont;
	private Rectangle backgroundRectangle;
	private TileGridAchievement tileGridAchievement;
	private String achievementDescription;
	private DifficultyColor difficultyColor;

	public GameTileAchievement(DifficultyColor difficultyColor, String achievementDescription, boolean completedStatus, TileGridAchievement tileGridAchievement) {
		this.difficultyColor = difficultyColor;
		this.tileGridAchievement = tileGridAchievement;
		this.completedStatus = completedStatus;
		this.achievementDescription = achievementDescription;
		
		StyleManager styleManager = tileGridAchievement.getGameSessionContext().getStyleManager();
		
		backgroundRectangle = new Rectangle(GameTile.RECTANGLE_WIDTH, GameTile.RECTANGLE_HEIGHT);
		backgroundRectangle.setArcWidth(GameTile.CORNER_RADIUS);
		backgroundRectangle.setArcHeight(GameTile.CORNER_RADIUS);
		
		textBorderPane = new BorderPane();
		textBorderPane.setPadding(new Insets(20));
		textBorderPane.setTop(achievementLabel);
		textBorderPane.setBottom(statusLabel);
		
		statusFont = styleManager.getFont("franklin-normal", 700, 14);
		achievementFont = styleManager.getFont("franklin-normal", 500, 11);
		
		statusLabel = new Label();
		statusLabel.setFont(statusFont);
		statusLabel.setStyle("-fx-text-alignment: center;");
		
		achievementLabel = new Label(achievementDescription);
		achievementLabel.setFont(achievementFont);
		achievementLabel.setStyle("-fx-text-alignment: center;");
		achievementLabel.setMaxWidth(GameTile.RECTANGLE_WIDTH - 20);
		
		getChildren().addAll(backgroundRectangle, textBorderPane);
		
		setCompleted(false);
		refreshStyle();
	}
	
	public void setCompleted(boolean status) {
		completedStatus = status;
		refreshStyle();
	}
	
	@Override
	public void refreshStyle() {
		StyleManager styleManager = tileGridAchievement.getGameSessionContext().getStyleManager();
		
		if(completedStatus) {
			statusLabel.setText("COMPLETED!");
			backgroundRectangle.setFill(styleManager.colorDifficulty(difficultyColor));
			statusLabel.setTextFill(styleManager.colorTextNeutral());
			achievementLabel.setTextFill(styleManager.colorTextNeutral());
		} else {
			statusLabel.setText("INCOMPLETE");
			backgroundRectangle.setFill(styleManager.colorDefaultRectangle());
			statusLabel.setTextFill(styleManager.colorText());
			achievementLabel.setTextFill(styleManager.colorText());
		}
	}
	
	@Override
	public GameSessionContext getGameSessionContext() {
		return tileGridAchievement.getGameSessionContext();
	}
}
