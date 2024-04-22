package com.connections.view_controller;

import java.util.List;

import com.connections.web.WebUser;
import com.connections.web.WebUserAccount;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class LeaderboardPane extends StackPane implements Modular {
    private GameSessionContext gameSessionContext;
    private GridPane leaderboardGrid;
    private StyleManager styleManager = new StyleManager();

    public LeaderboardPane(GameSessionContext gameSessionContext) {
        this.gameSessionContext = gameSessionContext;
        initializeLeaderboard();
        getChildren().add(leaderboardGrid);
    }

	private void initializeLeaderboard() {
        leaderboardGrid = new GridPane();
        leaderboardGrid.setHgap(10);
        leaderboardGrid.setVgap(5);
        leaderboardGrid.setAlignment(Pos.TOP_CENTER);

        Label rankLabel = new Label("Rank");
        rankLabel.setFont(gameSessionContext.getStyleManager().getFont("karnakpro-condensedblack", 36));
        Label nameLabel = new Label("Name");
        nameLabel.setFont(gameSessionContext.getStyleManager().getFont("karnakpro-condensedblack", 36));
        Label scoreLabel = new Label("Score");
        scoreLabel.setFont(gameSessionContext.getStyleManager().getFont("karnakpro-condensedblack", 36));

        leaderboardGrid.add(rankLabel, 0, 0);
        leaderboardGrid.add(nameLabel, 1, 0);
        leaderboardGrid.add(scoreLabel, 2, 0);

        List<WebUser> topUsers = WebUser.getTopUsers(gameSessionContext.getWebContext(), 5);

        for (int i = 0; i < topUsers.size(); i++) {
            WebUser user = topUsers.get(i);

            Label rankValueLabel = new Label(String.valueOf(i + 1) + ".");
            rankValueLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 20));
            Label nameValueLabel;
            Label scoreValueLabel = new Label(String.valueOf(user.getNumAllGamesForAchievements()));
            scoreValueLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 20));


            if (user instanceof WebUserAccount) {
                WebUserAccount userAccount = (WebUserAccount) user;
                nameValueLabel = new Label(userAccount.getUserName());
            } else {
                nameValueLabel = new Label("Guest");
            }

            nameValueLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 20));
            leaderboardGrid.add(rankValueLabel, 0, i + 1);
            leaderboardGrid.add(nameValueLabel, 1, i + 1);
            leaderboardGrid.add(scoreValueLabel, 2, i + 1);
        }
    }

	@Override
	public void refreshStyle() {
		// Implement any style refreshing logic here
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}