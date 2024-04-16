package com.connections.view_controller;

import com.connections.model.GameData;
import com.connections.model.GameDataCollection;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class GameBoard extends Application {
	protected static final int STAGE_WIDTH = 800;
	protected static final int STAGE_HEIGHT = 750;
	private StyleManager styleManager;
	private GameData currentGame;

	private void initGameData() {
		GameDataCollection collection = new GameDataCollection("nyt-connections-games.txt");
		if (!collection.getGameList().isEmpty()) {
			currentGame = collection.getGameList().get(0);
		}
	}

	@Override
    public void start(Stage primaryStage) {
		initGameData();
		styleManager = new StyleManager();
		GameSessionContext gameSessionContext = new GameSessionContext(styleManager, currentGame);
		GameSession gameSession = new GameSession(gameSessionContext);

		Scene scene = new Scene(gameSession, STAGE_WIDTH, STAGE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connections");
        primaryStage.setResizable(false);
        primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}