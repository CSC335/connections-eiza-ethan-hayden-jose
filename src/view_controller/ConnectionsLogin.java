package view_controller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ConnectionsLogin extends Application {
    private PasswordField tPassword;
    private StyleManager styleManager = new StyleManager();

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

        Label password = new Label("Password");
        password.setFont(franklin700_14);
        password.setVisible(false);

        tPassword = new PasswordField();
        tPassword.setPrefSize(450, 46);
        tPassword.setVisible(false);

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
            if (tPassword.isVisible()) {
                // login
                Stage newStage = new Stage();
                GameBoard gameBoard = new GameBoard();
                gameBoard.start(newStage);
                // kill current
                stage.close();
            } else {
                TranslateTransition transition = new TranslateTransition(Duration.millis(500), cont);
                transition.setByY(40);
                transition.setOnFinished(e -> {
                    password.setVisible(true);
                    tPassword.setVisible(true);
                    GridPane.setRowIndex(cont, 5);
                    GridPane.setMargin(password, new javafx.geometry.Insets(10, 0, 0, 0)); 
                });
                transition.play();
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
}