import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.ScaleTransition;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private GameData currentGame;
    private Button deselectButton;
    private Button submitButton;
    private Button shuffleButton;
    private GridPane gridPane;
    private List<Set<Word>> previousGuesses = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        gridPane = new GridPane();
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
        
        GameDataCollection collection = new GameDataCollection("nyt-connections-games.txt");
        if (!collection.getGameList().isEmpty()) {
            currentGame = collection.getGameList().get(0);
            placeWordsInRectangles(currentGame, gridPane);
        }

        Text topText = new Text("Create four groups of four!");
        topText.setFont(Font.font(24));
        
        Text bottomText = new Text("Mistakes remaining:");
        bottomText.setFont(Font.font(24));

        Pane circlePane = new Pane();
        circlePane.setPrefWidth(100);

        for (int i = 0; i < 4; i++) {
            Circle circle = new Circle(8);
            circle.setFill(Color.rgb(90, 89, 78));
            circle.setLayoutX(i * 28 + 10);
            circle.setLayoutY(circlePane.getPrefHeight() / 2 + 17);
            circlePane.getChildren().add(circle);
        }

        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().addAll(bottomText, circlePane);

        shuffleButton = createButton("Shuffle", 88);
        
        shuffleButton.setOnAction(event -> {
            ObservableList<Node> children = gridPane.getChildren();
            List<StackPane> stackPanes = children.stream()
                    .filter(node -> node instanceof StackPane)
                    .map(node -> (StackPane) node)
                    .collect(Collectors.toList());

            Collections.shuffle(stackPanes);

            int index = 0;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    GridPane.setRowIndex(stackPanes.get(index), row);
                    GridPane.setColumnIndex(stackPanes.get(index), col);
                    index++;
                }
            }
        });
        
        deselectButton = createButton("Deselect all", 120);
        submitButton = createButton("Submit", 88);
        
        submitButton.setOnAction(event -> {
            Set<Word> currentGuess = new HashSet<>(getSelectedWords());

            if (previousGuesses.contains(currentGuess)) {
                showAlert("Duplicate Guess!", "You have already made this guess before.");
            } else {
                if (circlePane.getChildren().size() > 1) {
                    previousGuesses.add(currentGuess);

                    if (checkAllCategoriesGuessed()) {
                        showAlert("Congratulations!", "You have guessed all categories correctly!");
                        disableGameBoard();
                    } else {
                        if (checkSelectedWords(currentGuess)) {
                            showAlert("Correct!", "You guessed correctly!");
                        } else {
                            showAlert("Incorrect!", "You guessed incorrectly!");
                            removeCircle(circlePane);
                        }
                    }
                } else {
                    if (checkAllCategoriesGuessed()) {
                        showAlert("Congratulations!", "You have guessed all categories correctly!");
                        disableGameBoard();
                    } else {
                        showAlert("Game Over!", "You have no more remaining guesses.");
                        removeCircle(circlePane);
                        disableGameBoard();
                    }
                }
            }
            deselectButton.fire();
        });

        deselectButton.setDisable(true);
        
        deselectButton.setOnAction(event -> {
            gridPane.getChildren().forEach(node -> {
                if (node instanceof StackPane) {
                    StackPane stackPane = (StackPane) node;
                    Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
                    Text text = (Text) stackPane.getChildren().get(1);
                    rectangle.setFill(DEFAULT_COLOR);
                    text.setFill(Color.BLACK);
                }
            });
            selectedCount = 0;
            deselectButton.setDisable(true);
            deselectButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
            submitButton.setDisable(true);
            submitButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
        });
        
        submitButton.setDisable(true);

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

    private Button createButton(String text, double width) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
        button.setPrefHeight(48);
        button.setPrefWidth(width);
        button.setFont(Font.font(18));

        button.setOnMouseEntered(event -> {
            button.setCursor(Cursor.HAND);
        });

        button.setOnMouseExited(event -> {
            button.setCursor(Cursor.DEFAULT);
        });

        return button;
    }
    
    private void placeWordsInRectangles(GameData game, GridPane gridPane) {
        List<Word> words = new ArrayList<>();
        for (DifficultyColor color : DifficultyColor.getAllColors()) {
            GameAnswerColor answer = game.getAnswerForColor(color);
            for (String wordText : answer.getWords()) {
                words.add(new Word(wordText, color));
            }
        }

        Collections.shuffle(words);

        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Node node = getNodeByRowColumnIndex(row, col, gridPane);
                if (node instanceof StackPane) {
                    StackPane stackPane = (StackPane) node;
                    Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
                    Text text = (Text) stackPane.getChildren().get(1);
                    Word word = words.get(index);
                    text.setText(word.getText().toUpperCase());
                    rectangle.setUserData(word);
                } else if (node instanceof Rectangle) {
                    Rectangle rectangle = (Rectangle) node;
                    Word word = words.get(index);
                    Text text = new Text(word.getText().toUpperCase());
                    text.setFont(Font.font(18));
                    StackPane stackPane = new StackPane(rectangle, text);
                    gridPane.add(stackPane, col, row);
                    rectangle.setUserData(word);

                    stackPane.setOnMouseClicked(event -> {
                        if (rectangle.getFill() == DEFAULT_COLOR && selectedCount < MAX_SELECTED) {
                            rectangle.setFill(SELECTED_COLOR);
                            text.setFill(Color.WHITE);
                            selectedCount++;
                        } else if (rectangle.getFill() == SELECTED_COLOR) {
                            rectangle.setFill(DEFAULT_COLOR);
                            text.setFill(Color.BLACK);
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

                    stackPane.setOnMouseEntered(event -> {
                        stackPane.setCursor(Cursor.HAND);
                    });

                    stackPane.setOnMouseExited(event -> {
                        stackPane.setCursor(Cursor.DEFAULT);
                    });
                }
                index++;
            }
        }
    }

    private Node getNodeByRowColumnIndex(int row, int column, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }
    
    private boolean checkSelectedWords(Set<Word> selectedWords) {
        for (DifficultyColor color : DifficultyColor.getAllColors()) {
            GameAnswerColor answer = currentGame.getAnswerForColor(color);
            List<String> colorWords = Arrays.asList(answer.getWords());
            if (selectedWords.size() == colorWords.size() &&
                    selectedWords.stream().allMatch(word -> colorWords.contains(word.getText()))) {
                return true;
            }
        }
        return false;
    }
    
    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
        
    private void removeCircle(Pane circlePane) {
        if (!circlePane.getChildren().isEmpty()) {
            Circle circle = (Circle) circlePane.getChildren().get(circlePane.getChildren().size() - 1);
            
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), circle);
            scaleTransition.setFromX(1.0);
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(0.0);
            scaleTransition.setToY(0.0);
            scaleTransition.setOnFinished(event -> circlePane.getChildren().remove(circle));
            
            scaleTransition.play();
        }
    }
    
    private List<Word> getSelectedWords() {
        List<Word> selectedWords = new ArrayList<>();
        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane) {
                StackPane stackPane = (StackPane) node;
                Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
                if (rectangle.getFill() == SELECTED_COLOR) {
                    Word word = (Word) rectangle.getUserData();
                    selectedWords.add(word);
                }
            }
        }
        return selectedWords;
    }

    private boolean checkAllCategoriesGuessed() {
        Set<DifficultyColor> guessedColors = new HashSet<>();
        for (Set<Word> guess : previousGuesses) {
            if (checkSelectedWords(guess)) {
                guessedColors.add(guess.iterator().next().getColor());
            }
        }
        return guessedColors.size() == DifficultyColor.getAllColors().size();
    }

    private void disableGameBoard() {
        gridPane.getChildren().forEach(node -> {
            if (node instanceof StackPane) {
                node.setDisable(true);
                node.setOnMouseClicked(null);
                node.setOnMouseEntered(null);
                node.setOnMouseExited(null);
            }
        });
        deselectButton.setDisable(true);
        deselectButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
        submitButton.setDisable(true);
        submitButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
        shuffleButton.setDisable(true);
        shuffleButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}