package com.connections.view_controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ConnectionsLogin extends Application {
    private PasswordField tPassword;
    private StyleManager styleManager = new StyleManager();
    private SVGPath exclSVG;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        BorderPane window = new BorderPane();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);

        Font franklin700_14 = styleManager.getFont("franklin-normal", 700, 14);
        Font franklin700_16 = styleManager.getFont("franklin-normal", 700, 16);
        Font cheltenham = styleManager.getFont("cheltenham-normal", 400, 30);

        Label login = new Label("Log in or create an account");
        login.setFont(cheltenham);
        login.setTextFill(Color.BLACK);

        Label email = new Label("Email Address");
        email.setFont(franklin700_14);

        TextField tEmail = new TextField();
        tEmail.setPrefSize(450, 46);
        tEmail.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        Label password = new Label("Password");
        password.setFont(franklin700_14);
        password.setVisible(false);

        tPassword = new PasswordField();
        tPassword.setPrefSize(450, 46);
        tPassword.setVisible(false);
        tPassword.setStyle("-fx-border-color: black; -fx-border-width: 1;");


        Button cont = new Button("Continue");
        cont.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 1); -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50; -fx-font-size: 20px;");
        cont.setPrefHeight(30);
        cont.setPrefWidth(450);
        cont.setPrefSize(450, 44);
        cont.setFont(franklin700_16);
        cont.setTextFill(Color.WHITE);
        
        GridPane.setMargin(cont, new javafx.geometry.Insets(16, 0, 0, 0));

        cont.setOnAction(event -> {
        	Label invalidPass = new Label("Password must be at least 8 characters long.");
            invalidPass.setTextFill(Color.RED);
            invalidPass.setFont(franklin700_14);

            Label errorLabel = new Label("Please enter a valid email address.");
            errorLabel.setTextFill(Color.RED);
            errorLabel.setFont(franklin700_14);

            SVGPath passwordExclSVG = new SVGPath();
            passwordExclSVG.setContent("M2 10a8 8 0 1 1 16 0 8 8 0 0 1-16 0Zm7 1V5h2v6H9Zm0 2v2h2v-2H9Z");
            passwordExclSVG.setScaleX(0.8);
            passwordExclSVG.setScaleY(0.8);
            passwordExclSVG.setFill(Color.RED);
            passwordExclSVG.setFillRule(javafx.scene.shape.FillRule.EVEN_ODD);

            SVGPath emailExclSVG = new SVGPath();
            emailExclSVG.setFillRule(javafx.scene.shape.FillRule.EVEN_ODD);
            emailExclSVG.setContent("M2 10a8 8 0 1 1 16 0 8 8 0 0 1-16 0Zm7 1V5h2v6H9Zm0 2v2h2v-2H9Z");
            emailExclSVG.setScaleX(0.8);
            emailExclSVG.setScaleY(0.8);
            emailExclSVG.setFill(Color.RED);

            HBox passwordErrorBox = new HBox(5);
            passwordErrorBox.setAlignment(Pos.CENTER_LEFT);
            passwordErrorBox.getChildren().addAll(passwordExclSVG, invalidPass);

            HBox errorBox = new HBox(5);
            errorBox.setAlignment(Pos.CENTER_LEFT);
            errorBox.getChildren().addAll(emailExclSVG, errorLabel);
            
            
            if (tPassword.isVisible()) {
                String pass = tPassword.getText();
                String sEmail = tEmail.getText();
                Boolean matches = isValidEmail(sEmail);
                if (pass.length() > 7 && matches) {
                    // login
                    Stage newStage = new Stage();
//                    GameBoard gameBoard = new GameBoard();
//                    gameBoard.start(newStage);
                    // kill current
                    stage.close();
                } else {
                   

                    if (pass.length() < 8) {
                     
                        // Add the password error HBox to the grid
                        grid.add(passwordErrorBox, 0, 5);
                        GridPane.setMargin(passwordErrorBox, new javafx.geometry.Insets(5, 0, 0, 0));
                        tPassword.setStyle("-fx-border-color: red; -fx-border-width: 1;");

                        tPassword.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue.length() > 7) {
                                grid.getChildren().remove(passwordErrorBox);
                                tPassword.setStyle("-fx-border-color: black; -fx-border-width: 1;");
                            }
                        });
                    }
                    if (!matches) {

                        // Add the email error HBox to the grid
                        grid.add(errorBox, 0, 2);
                        GridPane.setMargin(errorBox, new javafx.geometry.Insets(5, 0, 0, 0));
                        
                        tEmail.setStyle("-fx-border-color: red; -fx-border-width: 1;");


                        tEmail.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (isValidEmail(newValue)) {
                                grid.getChildren().remove(errorBox);
                                GridPane.setRowIndex(cont, 5);
                                tEmail.setStyle("-fx-border-color: black; -fx-border-width: 1;");

                            }
                        });
                    }
                }

            } else {
                String sEmail = tEmail.getText();
                Boolean matches = isValidEmail(sEmail);
                if (matches) {
                    TranslateTransition transition = new TranslateTransition(Duration.millis(500), cont);
                    transition.setByY(40);
                    transition.setOnFinished(e -> {
                        password.setVisible(true);
                        tPassword.setVisible(true);
                        GridPane.setRowIndex(cont, 5);
                        GridPane.setMargin(password, new javafx.geometry.Insets(10, 0, 0, 0));
                    });
                    transition.play();
                } else {

                  // Add the email error HBox to the grid
                  grid.add(errorBox, 0, 2);
                  GridPane.setRowIndex(cont, 3);
                  
                  tEmail.setStyle("-fx-border-color: red; -fx-border-width: 1;");

                  // Add a listener to the email text field
                  tEmail.textProperty().addListener((observable, oldValue, newValue) -> {
                      if (isValidEmail(newValue)) {
                          grid.getChildren().remove(errorBox);
                          GridPane.setRowIndex(cont, 2);
                          tEmail.setStyle("-fx-border-color: black; -fx-border-width: 1;");
                      }
                  });
                }
            }
        });


        grid.add(email, 0, 0);
        grid.add(tEmail, 0, 1);
        GridPane.setRowIndex(cont, 2);
        grid.add(cont, 0, 2);
        grid.add(password, 0, 3);
        grid.add(tPassword, 0, 4);
        grid.setAlignment(Pos.CENTER);

        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(login, grid);

        window.setCenter(box);
        Scene scene = new Scene(window, 800, 750, Color.WHITE);
        stage.setScene(scene);
        stage.show();
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}