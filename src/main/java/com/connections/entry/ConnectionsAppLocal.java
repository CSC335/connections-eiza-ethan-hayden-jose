package com.connections.entry;

import com.connections.model.GameData;
import com.connections.view_controller.ErrorOverlayPane;
import com.connections.view_controller.GameSessionContext;
import com.connections.view_controller.StyleManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ConnectionsAppLocal extends Application {
	public static final int STAGE_WIDTH = 800;
	public static final int STAGE_HEIGHT = 750;
	private StyleManager styleManager;
	private GameData currentGame;

	@Override
    public void start(Stage primaryStage) {
//		initGameData();
//		GameSessionContext gameSessionContext = new GameSessionContext(styleManager, currentGame, null);
//		GameSession gameSession = new GameSession(gameSessionContext);

//		ConnectionsLogin gameSession = new ConnectionsLogin(null, null);

//		Text text = new Text("This is ConnectionsAppLocal, keep this for emergency local testing.");
//		BorderPane pane = new BorderPane();
//		pane.setCenter(text);
//
//		GameSession.GameType gameType = GameSession.GameType.valueOf("CLASSIC");
//
//		Text testText = new Text("Test " + gameType.toString());
//
//		BorderPane underneath = new BorderPane();
//		underneath.setCenter(new Text("BLAH BLAH BLAH BLAH"));
//		underneath.setTop(testText);
//
//		GaussianBlur gaussianBlur = new GaussianBlur();
//		underneath.setEffect(gaussianBlur);
//
		styleManager = new StyleManager();
		GameSessionContext gameSessionContext = new GameSessionContext(styleManager, null, null, null);
//		TimerPane timerPane = new TimerPane(gameSessionContext, 2);
//		timerPane.appearAndStart();

		String header = "Game In Progress";
		String body = "You are currently playing from another browser tab or device under the same user.\nPlease wait until the game is finished and try again.";

		ErrorOverlayPane error = new ErrorOverlayPane(gameSessionContext, header, body);
		error.appear();

//		StackPane layers = new StackPane(underneath, pane, timerPane);
		StackPane layers = new StackPane(error);
		Scene scene = new Scene(layers, STAGE_WIDTH, STAGE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connections");
        primaryStage.setResizable(false);
        primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}