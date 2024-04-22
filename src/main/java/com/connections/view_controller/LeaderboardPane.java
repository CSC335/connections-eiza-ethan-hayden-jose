package com.connections.view_controller;

import com.connections.web.WebUser;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class LeaderboardPane extends StackPane implements Modular {
	private GameSessionContext gameSessionContext;
	private VBox leaderboardLayout;

	public LeaderboardPane(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		initializeLeaderboard();
		getChildren().add(leaderboardLayout);
		
	}
	
	/*
	 * quick note from eiza:
	 * in case you ever need to get the username of the current user:
	 * gameSessionContext.getWebSessionContext().getSession().getUser().getUserName()
	 */

	private void initializeLeaderboard() {
		leaderboardLayout = new VBox(5);
		leaderboardLayout.setAlignment(Pos.TOP_CENTER);
		List<WebUser> topUsers = WebUser.getTopUsers(gameSessionContext.getWebContext(), 5);

		for (int i = 0; i < topUsers.size(); i++) {
			WebUser user = topUsers.get(i);
			Label userLabel;
			boolean isAccount = user.checkUserTypeByUserID(gameSessionContext.getWebContext(), user.getUserID())
					.equals(WebUser.UserType.ACCOUNT);
			
			if (isAccount) {
				userLabel = new Label((i + 1) + ". " + "Guest " + user.getNumAllGamesForAchievements());
			} else {
				//somehow need to actually get the username and not the userID
				userLabel = new Label(
						(i + 1) + ". " + user.getUserID() + " " + user.getNumAllGamesForAchievements());
			}
			leaderboardLayout.getChildren().add(userLabel);
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