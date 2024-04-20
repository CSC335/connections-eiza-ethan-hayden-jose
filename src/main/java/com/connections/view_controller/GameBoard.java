package com.connections.view_controller;

import com.connections.model.GameData;
import com.connections.model.GameDataCollection;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class GameBoard extends Application {
	protected static final int STAGE_WIDTH = 800;
	protected static final int STAGE_HEIGHT = 750;
	private StyleManager styleManager;
	private GameData currentGame;

	@Override
    public void start(Stage primaryStage) {
		
		Text text = new Text("Please use the website version!");
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