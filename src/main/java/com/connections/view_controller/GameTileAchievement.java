package com.connections.view_controller;

import com.connections.model.DifficultyColor;
import com.connections.web.WebUser;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

/**
 * The GameTileAchievement class represents an achievement tile in the game.
 * It displays the achievement description and completion status.
 */
public class GameTileAchievement extends StackPane implements Modular {

    private static final int APPEAR_POPUP_MS = 250;

    private boolean completedStatus;
    private BorderPane textBorderPane;
    private Label achievementLabel;
    private Rectangle backgroundRectangle;
    private TileGridAchievement tileGridAchievement;
    private DifficultyColor difficultyColor;
    private int row;

    /**
     * Constructs a new GameTileAchievement with the specified parameters.
     *
     * @param difficultyColor      the difficulty color of the achievement
     * @param achievementDescription the description of the achievement
     * @param user                 the WebUser associated with the achievement
     * @param tileGridAchievement  the TileGridAchievement containing this achievement tile
     * @param row                  the row index of the achievement tile
     */
    public GameTileAchievement(DifficultyColor difficultyColor, String achievementDescription,
                               WebUser user, TileGridAchievement tileGridAchievement, int row) {
    	this.row = row;
        this.difficultyColor = difficultyColor;
        this.tileGridAchievement = tileGridAchievement;
        StyleManager styleManager = tileGridAchievement.getGameSessionContext().getStyleManager();

        backgroundRectangle = new Rectangle(GameTile.RECTANGLE_WIDTH, GameTile.RECTANGLE_HEIGHT);
        backgroundRectangle.setArcWidth(GameTile.CORNER_RADIUS);
        backgroundRectangle.setArcHeight(GameTile.CORNER_RADIUS);

        String[] parts = achievementDescription.split("completed", 2);
        Text part1 = new Text(parts[0]);
        part1.setFont(styleManager.getFont("franklin-normal", 500, 14));
        Text completedText = new Text("completed");
        completedText.setFont(styleManager.getFont("franklin-normal", 500, 14));
        Text part2 = new Text(parts.length > 1 ? parts[1] : "");
        part2.setFont(styleManager.getFont("franklin-normal", 500, 14));

        TextFlow textFlow = new TextFlow(part1, completedText, part2);
        textFlow.setTextAlignment(TextAlignment.CENTER);

        achievementLabel = new Label();
        achievementLabel.setGraphic(textFlow);
        achievementLabel.setTextFill(styleManager.colorText());
        achievementLabel.setWrapText(true);
        achievementLabel.setStyle("-fx-text-alignment: center;");
        achievementLabel.setMaxWidth(GameTile.RECTANGLE_WIDTH - 5);
        achievementLabel.setTextOverrun(OverrunStyle.CLIP);

        textBorderPane = new BorderPane();
        textBorderPane.setPadding(new Insets(5));
        textBorderPane.setCenter(achievementLabel);

        getChildren().addAll(backgroundRectangle, textBorderPane);

        setCompleted();
        refreshStyle();
    }

    /**
     * Checks if the achievement is completed by the user.
     *
     * @param user the WebUser to check the achievement completion for
     * @return true if the achievement is completed, false otherwise
     */
    private boolean checkAchievementCompletion(WebUser user) {
        user.readFromDatabase();
        if (row == 0) {
            return user.hasCompletedRegularGameAchievement(difficultyColor);
        } else if (row == 1) {
            return user.hasCompletedTimeTrialAchievement(difficultyColor);
        } else if (row == 2) {
            return user.hasCompletedNoMistakesAchievement(difficultyColor);
        } else if (row == 3) {
            return user.hasCompletedTimeTrialUnderTimeAchievement(difficultyColor);
        } else {
            return false;
        }
    }

    /**
     * Sets the completion status of the achievement tile based on the user's achievement data.
     */
    public void setCompleted() {
        WebUser user = tileGridAchievement.getGameSessionContext().getWebSessionContext().getSession().getUser();
        completedStatus = checkAchievementCompletion(user);
        refreshStyle();
    }

    /**
     * Animates the completion of the achievement tile.
     */
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

    /**
     * Refreshes the style of the achievement tile based on the current style manager.
     */
    @Override
    public void refreshStyle() {
        StyleManager styleManager = tileGridAchievement.getGameSessionContext().getStyleManager();

        if (completedStatus) {
            backgroundRectangle.setFill(styleManager.colorDifficulty(difficultyColor));
            backgroundRectangle.setOpacity(1.0);
            achievementLabel.setOpacity(1.0);
        } else {
            backgroundRectangle.setFill(styleManager.colorDefaultRectangle());
            backgroundRectangle.setOpacity(0.45);
            achievementLabel.setOpacity(0.45);
        }
        achievementLabel.setTextFill(styleManager.colorText());

        TextFlow textFlow = (TextFlow) achievementLabel.getGraphic();
        Text completedText = (Text) textFlow.getChildren().get(1);
        if (completedStatus) {
            completedText.setFont(styleManager.getFont("franklin-normal", 700, 16));
        } else {
            for (javafx.scene.Node node : textFlow.getChildren()) {
                if (node instanceof Text) {
                    ((Text) node).setFill(styleManager.colorText());
                }
            }
            completedText.setFont(styleManager.getFont("franklin-normal", 500, 14));
        }
    }

    /**
     * Returns the GameSessionContext associated with the achievement tile.
     *
     * @return the GameSessionContext associated with the achievement tile
     */
    @Override
    public GameSessionContext getGameSessionContext() {
        return tileGridAchievement.getGameSessionContext();
    }
}