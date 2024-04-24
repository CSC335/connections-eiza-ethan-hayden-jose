package com.connections.view_controller;

import java.util.ArrayList;
import java.util.List;

import com.connections.web.WebUser;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * The LeaderboardPane class represents a pane that displays the leaderboard.
 * It shows the top users and their ranks, names, and scores.
 */
public class LeaderboardPane extends StackPane implements Modular {
    private GameSessionContext gameSessionContext;
    private GridPane leaderboardGrid;
    private Label rankLabel;
    private Label nameLabel;
    private Label scoreLabel;
    private List<Label> rankValueLabels;
    private List<Label> nameValueLabels;
    private List<Label> scoreValueLabels;

    /**
     * Constructs a new LeaderboardPane with the specified GameSessionContext.
     *
     * @param gameSessionContext the GameSessionContext used by the leaderboard pane
     */
    public LeaderboardPane(GameSessionContext gameSessionContext) {
        this.gameSessionContext = gameSessionContext;
        initializeLeaderboard();
        getChildren().add(leaderboardGrid);
    }

    /**
     * Initializes the leaderboard by creating the grid and populating it with data.
     */
    private void initializeLeaderboard() {
        leaderboardGrid = new GridPane();
        leaderboardGrid.setHgap(10);
        leaderboardGrid.setVgap(5);
        leaderboardGrid.setAlignment(Pos.TOP_CENTER);

        rankLabel = new Label("Rank");
        rankLabel.setFont(gameSessionContext.getStyleManager().getFont("karnakpro-condensedblack", 36));

        nameLabel = new Label("Name");
        nameLabel.setFont(gameSessionContext.getStyleManager().getFont("karnakpro-condensedblack", 36));

        scoreLabel = new Label("Score");
        scoreLabel.setFont(gameSessionContext.getStyleManager().getFont("karnakpro-condensedblack", 36));

        leaderboardGrid.add(rankLabel, 0, 0);
        leaderboardGrid.add(nameLabel, 1, 0);
        leaderboardGrid.add(scoreLabel, 2, 0);

        rankValueLabels = new ArrayList<>();
        nameValueLabels = new ArrayList<>();
        scoreValueLabels = new ArrayList<>();

        List<WebUser> topUsers = WebUser.getTopUsers(gameSessionContext.getWebContext(), 5);

        for (int i = 0; i < topUsers.size(); i++) {
            WebUser user = topUsers.get(i);

            Label rankValueLabel = new Label(String.valueOf(i + 1) + ".");
            rankValueLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 20));
            GridPane.setHalignment(rankValueLabel, HPos.CENTER);
            rankValueLabels.add(rankValueLabel);

            Label nameValueLabel = new Label(user.getUserName());
            nameValueLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 20));
            GridPane.setHalignment(nameValueLabel, HPos.CENTER);
            nameValueLabels.add(nameValueLabel);

            Label scoreValueLabel = new Label(String.valueOf(user.getNumAllGamesForAchievements()));
            scoreValueLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 20));
            GridPane.setHalignment(scoreValueLabel, HPos.CENTER);
            scoreValueLabels.add(scoreValueLabel);

            leaderboardGrid.add(rankValueLabel, 0, i + 1);
            leaderboardGrid.add(nameValueLabel, 1, i + 1);
            leaderboardGrid.add(scoreValueLabel, 2, i + 1);
        }
    }

    /**
     * Refreshes the style of the leaderboard pane based on the current style manager.
     */
    @Override
    public void refreshStyle() {
        StyleManager styleManager = gameSessionContext.getStyleManager();

        rankLabel.setTextFill(styleManager.colorText());
        nameLabel.setTextFill(styleManager.colorText());
        scoreLabel.setTextFill(styleManager.colorText());

        for (Label label : rankValueLabels) {
            label.setTextFill(styleManager.colorText());
        }

        for (Label label : nameValueLabels) {
            label.setTextFill(styleManager.colorText());
        }

        for (Label label : scoreValueLabels) {
            label.setTextFill(styleManager.colorText());
        }
    }

    /**
     * Returns the GameSessionContext associated with the leaderboard pane.
     *
     * @return the GameSessionContext associated with the leaderboard pane
     */
    @Override
    public GameSessionContext getGameSessionContext() {
        return gameSessionContext;
    }
}