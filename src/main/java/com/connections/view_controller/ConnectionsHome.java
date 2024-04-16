package com.connections.view_controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ConnectionsHome extends Application {
    private BorderPane window;
    private static final Color LOGIN_BACKGROUND_COLOR = Color.rgb(179, 166, 254);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        window = new BorderPane();
        layoutConfigs();

        Font karnak_condensed = Font.loadFont(new FileInputStream("./Fonts/karnakpro-condensedblack.ttf"), 65);
        Font franklin600_16 = Font.loadFont(new FileInputStream("./Fonts/franklin-normal-600.ttf"), 16);
        Font karnak = Font.loadFont(new FileInputStream("./Fonts/KarnakPro-Medium_400.otf"), 20);

        Label title = new Label("Connections");
        title.setFont(karnak_condensed);
        title.setTextFill(Color.BLACK);

        Label how_to = new Label("Group words that share a common thread.");
        how_to.setFont(Font.font(karnak.getFamily(), FontWeight.THIN, 20));
        how_to.setTextFill(Color.BLACK);

        Button loginButton = new Button("Log In");
        loginButton.setStyle(
            "-fx-background-color: rgba(179, 166, 254, 1); -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50; -fx-font-size: 20px;");
        loginButton.setPrefHeight(58);
        loginButton.setPrefWidth(150);
        loginButton.setFont(franklin600_16);
        //button.setText make

        loginButton.setOnAction(event -> {
            try {
                Stage newStage = new Stage();
                ConnectionsLogin gameBoard = new ConnectionsLogin();
                gameBoard.start(newStage);

                // kill current
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });



        Button playButton = new Button("Play");
        playButton.setStyle(
            "-fx-background-color: black; -fx-background-radius: 50; -fx-font-size: 20px;");
        playButton.setPrefHeight(58);
        playButton.setPrefWidth(150);
        playButton.setFont(franklin600_16);
        playButton.setTextFill(Color.WHITE);
        playButton.setOnAction(event -> {
            try {

                Stage newStage = new Stage();
//                ConnectionsLogin login = new ConnectionsLogin();
//                login.start(newStage);
                GameBoard game = new GameBoard();
                game.start(newStage);

                // kill current stage
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        //ImageView logoImageView = createLogoImageView();

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        //centerBox.getChildren().addAll(logoImageView, title, how_to, playButton, loginButton);
        centerBox.getChildren().addAll(title, how_to, playButton, loginButton);

        StackPane centerStackPane = new StackPane(centerBox);
        window.setCenter(centerStackPane);

        Scene scene = new Scene(window, 800, 750);
        stage.setScene(scene);
        stage.show();
    }

    private void layoutConfigs() {
        Background background = new Background(new BackgroundFill(LOGIN_BACKGROUND_COLOR, null, null));
        window.setBackground(background);
    }
}