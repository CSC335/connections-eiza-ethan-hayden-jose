package com.connections.view_controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The GameBoard class represents the main application class for the game board.
 * It extends the JavaFX Application class and sets up the primary stage and scene.
 */
public class GameBoard extends Application {
	protected static final int STAGE_WIDTH = 800;
	protected static final int STAGE_HEIGHT = 750;
	@Override
	
    /**
     * The main entry point for the JavaFX application.
     * It launches the application.
     *
     * @param primaryStage the primary stage for this application, onto which the application scene can be set
     */
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

    /**
     * The main method that serves as the entry point for the Java application.
     * It calls the launch method to start the JavaFX application.
     *
     * @param args the command line arguments
     */
	public static void main(String[] args) {
		launch(args);
	}
}