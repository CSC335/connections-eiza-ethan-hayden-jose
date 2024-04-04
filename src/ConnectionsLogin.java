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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        BorderPane window = new BorderPane();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        Font franklin600_16 = Font.loadFont(new FileInputStream("./Fonts/franklin-normal-600.ttf"), 16);
        Font franklin700 = Font.loadFont(new FileInputStream("./Fonts/franklin-normal-700.ttf"), 12);
        Font karnak = Font.loadFont(new FileInputStream("./Fonts/KarnakPro-Medium_400.otf"), 30);

        Label login = new Label("Log in or create an account");
        login.setFont(karnak);

        Label email = new Label("Email Address");
        email.setFont(franklin700);

        TextField tEmail = new TextField();

        Label password = new Label("Password");
        password.setFont(franklin700);
        password.setVisible(false);

        tPassword = new PasswordField();
        tPassword.setVisible(false);

        Button cont = new Button("Continue");
        cont.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 1); -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50; -fx-font-size: 20px;");
        cont.setPrefHeight(30);
        cont.setPrefWidth(450);
        cont.setTextFill(Color.WHITE);

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
                    GridPane.setRowIndex(cont, 4);
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

        Scene scene = new Scene(window, 800, 750);
        stage.setScene(scene);
        stage.show();
    }
}