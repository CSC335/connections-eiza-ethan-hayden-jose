package com.connections.view_controller;

import com.connections.model.*;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameTileAchievement extends StackPane implements Modular {
	private static final int APPEAR_POPUP_MS = 250;
	
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

	public GameTileAchievement(DifficultyColor difficultyColor, String achievementDescription, boolean completedStatus,
			TileGridAchievement tileGridAchievement) {
		this.difficultyColor = difficultyColor;
		this.tileGridAchievement = tileGridAchievement;
		this.completedStatus = completedStatus;
		this.achievementDescription = achievementDescription;

		StyleManager styleManager = tileGridAchievement.getGameSessionContext().getStyleManager();

		backgroundRectangle = new Rectangle(GameTile.RECTANGLE_WIDTH, GameTile.RECTANGLE_HEIGHT);
		backgroundRectangle.setArcWidth(GameTile.CORNER_RADIUS);
		backgroundRectangle.setArcHeight(GameTile.CORNER_RADIUS);

		statusFont = styleManager.getFont("franklin-normal", 700, 17);
		achievementFont = styleManager.getFont("franklin-normal", 500, 12);

		statusLabel = new Label();
		statusLabel.setFont(statusFont);

		achievementLabel = new Label(achievementDescription);
		achievementLabel.setFont(achievementFont);
		achievementLabel.setStyle("-fx-text-alignment: center;");
		achievementLabel.setMaxWidth(GameTile.RECTANGLE_WIDTH - 5);
		achievementLabel.setWrapText(true);
		achievementLabel.setTextOverrun(OverrunStyle.CLIP);

		textBorderPane = new BorderPane();
		textBorderPane.setPadding(new Insets(5));
		textBorderPane.setTop(achievementLabel);
		textBorderPane.setBottom(statusLabel);
		BorderPane.setAlignment(achievementLabel, Pos.CENTER);
		BorderPane.setAlignment(statusLabel, Pos.CENTER);

		getChildren().addAll(backgroundRectangle, textBorderPane);

		setCompleted(completedStatus);
		refreshStyle();
	}

	public void setCompleted(boolean status) {
		completedStatus = status;
		refreshStyle();
	}

	public void animateCompletion() {
		if (!completedStatus) {
			return;
		}

		PauseTransition pause = new PauseTransition(Duration.millis(750));

		ScaleTransition scale = new ScaleTransition(Duration.millis(APPEAR_POPUP_MS), this);
		scale.setFromX(1);
		scale.setFromX(1);
		scale.setToX(1.05);
		scale.setToY(1.05);
		scale.setAutoReverse(true);
		scale.setCycleCount(4);

		FadeTransition fade = new FadeTransition(Duration.millis(APPEAR_POPUP_MS), this);
		fade.setFromValue(1);
		fade.setToValue(0.8);
		fade.setAutoReverse(true);
		fade.setCycleCount(4);

		ParallelTransition popupTogether = new ParallelTransition(scale, fade);

		SequentialTransition sequence = new SequentialTransition(pause, popupTogether);
		sequence.play();
	}

	@Override
	public void refreshStyle() {
		StyleManager styleManager = tileGridAchievement.getGameSessionContext().getStyleManager();

		if (completedStatus) {
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
