package com.connections.entry;

import com.connections.model.GameData;
import com.connections.model.GameDataCollection;
import com.connections.view_controller.StyleManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
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
//		initGameData();
//		styleManager = new StyleManager();
//		GameSessionContext gameSessionContext = new GameSessionContext(styleManager, currentGame, null); 
//		GameSession gameSession = new GameSession(gameSessionContext);
		
//		ConnectionsLogin gameSession = new ConnectionsLogin(null, null); 
		

		Text text = new Text("This is ConnectionsAppLocal, keep this for emergency local testing.");
		BorderPane pane = new BorderPane();
		pane.setCenter(text);
		
		Scene scene = new Scene(pane, STAGE_WIDTH, STAGE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connections");
        primaryStage.setResizable(false);
        primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}