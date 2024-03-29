import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameBoard extends Application {

    private static final int ROWS = 4;
    private static final int COLS = 4;
    private static final int RECTANGLE_WIDTH = 150;
    private static final int RECTANGLE_HEIGHT = 80;
    private static final int GAP = 8;
    private static final int CORNER_RADIUS = 10;
    private static final Color DEFAULT_COLOR = Color.rgb(239, 239, 230);
    private static final Color SELECTED_COLOR = Color.rgb(90, 89, 78);
    private static final int STAGE_WIDTH = 800;
    private static final int STAGE_HEIGHT = 600;
    private static final int MAX_SELECTED = 4;

    private int selectedCount = 0;

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(GAP);
        gridPane.setVgap(GAP);
        gridPane.setAlignment(Pos.CENTER);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle rectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                rectangle.setFill(DEFAULT_COLOR);
                rectangle.setArcWidth(CORNER_RADIUS);
                rectangle.setArcHeight(CORNER_RADIUS);
                gridPane.add(rectangle, col, row);
            }
        }

        Text topText = new Text("Create four groups of four!");
        topText.setFont(Font.font(24));

        Text bottomText = new Text("Mistakes remaining:");
        bottomText.setFont(Font.font(24));

        HBox circleBox = new HBox(5);
        circleBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < 4; i++) {
            Circle circle = new Circle(8);
            circle.setFill(Color.rgb(90, 89, 78));
            circleBox.getChildren().add(circle);
        }

        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().addAll(bottomText, circleBox);

        Button shuffleButton = createButton("Shuffle");
        
        shuffleButton.setOnAction(event -> {
            ObservableList<Node> children = gridPane.getChildren();
            List<Rectangle> rectangles = children.stream()
                    .map(node -> (Rectangle) node)
                    .collect(Collectors.toList());

            Collections.shuffle(rectangles);

            int index = 0;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    GridPane.setRowIndex(rectangles.get(index), row);
                    GridPane.setColumnIndex(rectangles.get(index), col);
                    index++;
                }
            }
        });
        
        Button deselectButton = createButton("Deselect all");
        Button submitButton = createButton("Submit");

        deselectButton.setDisable(true);
        
        deselectButton.setOnAction(event -> {
            gridPane.getChildren().forEach(node -> {
                Rectangle rectangle = (Rectangle) node;
                rectangle.setFill(DEFAULT_COLOR);
            });
            selectedCount = 0;
            deselectButton.setDisable(true);
            deselectButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
            submitButton.setDisable(true);
            submitButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
        });
        
        submitButton.setDisable(true);

        gridPane.getChildren().forEach(node -> {
            Rectangle rectangle = (Rectangle) node;
            rectangle.setOnMouseClicked(event -> {
                if (rectangle.getFill() == DEFAULT_COLOR && selectedCount < MAX_SELECTED) {
                    rectangle.setFill(SELECTED_COLOR);
                    selectedCount++;
                } else if (rectangle.getFill() == SELECTED_COLOR) {
                    rectangle.setFill(DEFAULT_COLOR);
                    selectedCount--;
                }

                deselectButton.setDisable(selectedCount == 0);
                submitButton.setDisable(selectedCount != MAX_SELECTED);

                if (selectedCount != 0) {
                    deselectButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
                }

                if (selectedCount == MAX_SELECTED) {
                    submitButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
                }
            });

            rectangle.setOnMouseEntered(event -> {
                rectangle.setCursor(Cursor.HAND);
            });

            rectangle.setOnMouseExited(event -> {
                rectangle.setCursor(Cursor.DEFAULT);
            });
        });

        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(shuffleButton, deselectButton, submitButton);

        VBox vbox = new VBox(24, topText, gridPane, bottomBox, buttonBox);
        vbox.setAlignment(Pos.CENTER);

        StackPane stackPane = new StackPane(vbox);
        stackPane.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(stackPane, STAGE_WIDTH, STAGE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game Board");
        primaryStage.show();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
        button.setPrefHeight(48);
        button.setPrefWidth(Region.USE_COMPUTED_SIZE);
        button.setFont(Font.font(18));

        button.setOnMouseEntered(event -> {
            button.setCursor(Cursor.HAND);
        });

        button.setOnMouseExited(event -> {
            button.setCursor(Cursor.DEFAULT);
        });

        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}