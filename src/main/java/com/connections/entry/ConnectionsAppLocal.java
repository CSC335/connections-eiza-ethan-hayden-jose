package com.connections.entry;

import com.connections.model.GameData;
import com.connections.model.GameDataCollection;
import com.connections.view_controller.GameSession;
import com.connections.view_controller.GameSessionContext;
import com.connections.view_controller.StyleManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ConnectionsAppLocal extends Application {	
	public static final int STAGE_WIDTH = 800;
	public static final int STAGE_HEIGHT = 750;
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