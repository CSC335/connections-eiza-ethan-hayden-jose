import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
	private static final Color INCORRECT_COLOR = Color.rgb(130, 131, 122);
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
	private Pane circlePane;

	private AnimationPane animPane;
	private StackPane mainStackPane;

	private class AnimationPane extends Pane {
		private static final int SWAP_TRANS_MS = 1000;
		private static final int BUFFER_MS = 50;
		private static final int PLACEHOLDER_MS = 5;
		private static final int SHOW_CORRECT_MS = 750;
		private Set<StackPane> usedOriginalPieces = new HashSet<>();
		private Set<StackPane> usedGhostPieces = new HashSet<>();
		private List<Integer> swapUnselectedCol = new ArrayList<>();
		private List<Integer> swapSelectedRow = new ArrayList<>();
		private List<Integer> swapSelectedCol = new ArrayList<>();
//		private List<TranslateTransition> swapPieceTransitions = new ArrayList<>();
		private int currentRow;
		private boolean animActive;
		private GridPane watchGridPane;

		public AnimationPane(GridPane watchGridPane) {
			this.watchGridPane = watchGridPane;
		}

		private SequentialTransition getSwapTransitions() {
			if (animActive) {
				return null;
			}
			SequentialTransition sequence = new SequentialTransition();

			PauseTransition preparePause = new PauseTransition(Duration.millis(PLACEHOLDER_MS));
			preparePause.setOnFinished(event -> {
				animActive = true;
				this.setVisible(true);
				for (StackPane piece : usedOriginalPieces) {
					piece.setVisible(false);
				}
				for (StackPane piece : usedGhostPieces) {
					piece.setVisible(true);
				}
//				
//				for(TranslateTransition transition : swapPieceTransitions) {
//					transition.play();
//				}
			});

			PauseTransition pauseForSwapping = new PauseTransition(Duration.millis(SWAP_TRANS_MS + BUFFER_MS));
			pauseForSwapping.setOnFinished(event -> {
				transitionFinished();
			});

			PauseTransition pauseDisplayCorrect = new PauseTransition(Duration.millis(SHOW_CORRECT_MS));
			pauseDisplayCorrect.setOnFinished(event -> {
				System.out.println("woohoo");
			});

			sequence.getChildren().add(preparePause);

			swapUnselectedCol = new ArrayList<>();
			swapSelectedRow = new ArrayList<>();
			swapSelectedCol = new ArrayList<>();
			usedOriginalPieces = new HashSet<>();
			usedGhostPieces = new HashSet<>();
//			swapPieceTransitions = new ArrayList<>();
			ParallelTransition swapPieceParallel = new ParallelTransition();

			for (int c = 0; c < COLS; c++) {
				StackPane stackPanePiece = (StackPane) getGridNode(currentRow, c);
				Rectangle rect = (Rectangle) stackPanePiece.getChildren().get(0);
				if (rect.getFill().equals(DEFAULT_COLOR)) {
					swapUnselectedCol.add(c);
					usedOriginalPieces.add(stackPanePiece);
				}
			}

			for (int r = currentRow + 1; r < ROWS; r++) {
				for (int c = 0; c < COLS; c++) {
					StackPane stackPanePiece = (StackPane) getGridNode(r, c);
					Rectangle rect = (Rectangle) stackPanePiece.getChildren().get(0);
					if (rect.getFill().equals(SELECTED_COLOR)) {
						swapSelectedRow.add(r);
						swapSelectedCol.add(c);
						usedOriginalPieces.add(stackPanePiece);
					}
				}
			}

			for (int i = 0; i < swapUnselectedCol.size(); i++) {
				int destRow = swapSelectedRow.get(i);
				int destCol = swapSelectedCol.get(i);
				int sourceRow = currentRow;
				int sourceCol = swapUnselectedCol.get(i);

				StackPane sourcePiece = createGhostPiece(sourceRow, sourceCol);
				StackPane destPiece = createGhostPiece(destRow, destCol);

				sourcePiece.setTranslateX(0);
				sourcePiece.setTranslateY(0);

				destPiece.setTranslateX(0);
				destPiece.setTranslateY(0);

				sourcePiece.setVisible(false);
				destPiece.setVisible(false);

				TranslateTransition sourceTrans = new TranslateTransition(Duration.millis(SWAP_TRANS_MS), sourcePiece);
				sourceTrans.setToX(destPiece.getLayoutX() - sourcePiece.getLayoutX());
				sourceTrans.setToY(destPiece.getLayoutY() - sourcePiece.getLayoutY());

				TranslateTransition destTrans = new TranslateTransition(Duration.millis(SWAP_TRANS_MS), destPiece);
				destTrans.setToX(sourcePiece.getLayoutX() - destPiece.getLayoutX());
				destTrans.setToY(sourcePiece.getLayoutY() - destPiece.getLayoutY());

//				swapPieceTransitions.add(sourceTrans);
//				swapPieceTransitions.add(destTrans);
				usedGhostPieces.add(destPiece);
				usedGhostPieces.add(sourcePiece);
				swapPieceParallel.getChildren().addAll(sourceTrans, destTrans);
			}

			sequence.getChildren().addAll(swapPieceParallel, pauseForSwapping, pauseDisplayCorrect);

			return sequence;
		}

		private void transitionFinished() {
			if (!animActive) {
				return;
			}

			animActive = false;

			this.getChildren().removeAll(usedGhostPieces);

			for (int i = 0; i < swapUnselectedCol.size(); i++) {
				swapGridNode(currentRow, swapUnselectedCol.get(i), swapSelectedRow.get(i), swapSelectedCol.get(i));
			}

			for (Node node : watchGridPane.getChildren()) {
				if (GridPane.getRowIndex(node) >= currentRow) {
					node.setVisible(true);
				}
			}

			swapUnselectedCol = new ArrayList<>();
			swapSelectedRow = new ArrayList<>();
			swapSelectedCol = new ArrayList<>();
			usedOriginalPieces = new HashSet<>();

			this.setVisible(false);
			deselectButton.fire();
			currentRow++;
		}

		private StackPane createGhostPiece(int row, int col) {
			StackPane original = (StackPane) getGridNode(row, col);

			Rectangle originalRectangle = ((Rectangle) original.getChildren().get(0));
			Rectangle clonedRectangle = new Rectangle(originalRectangle.getWidth(), originalRectangle.getHeight());
			clonedRectangle.setFill(originalRectangle.getFill());
			clonedRectangle.setArcWidth(CORNER_RADIUS);
			clonedRectangle.setArcHeight(CORNER_RADIUS);

			Text originalText = ((Text) original.getChildren().get(1));
			Text clonedText = new Text(originalText.getText());
			clonedText.setFont(originalText.getFont()); // Copy font from original text
			clonedText.setFill((Color) originalText.getFill());

			StackPane clonedStackPane = new StackPane(clonedRectangle, clonedText);
			clonedStackPane.setPrefSize(original.getPrefWidth(), original.getPrefHeight());

			this.getChildren().add(clonedStackPane);
			clonedStackPane.setLayoutX(original.getLayoutX());
			clonedStackPane.setLayoutY(original.getLayoutY());

			return clonedStackPane;
		}

		private void setGridNodeVisible(int row, int col, boolean visible) {
			Node node = getGridNode(row, col);
			if (node != null) {
				node.setVisible(visible);
			}
		}

		private void swapGridNode(int sourceRow, int sourceCol, int destRow, int destCol) {
			Node node1 = getGridNode(sourceRow, sourceCol);
			Node node2 = getGridNode(destRow, destCol);

			gridPane.getChildren().removeAll(node1, node2);

			gridPane.add(node1, destCol, destRow);
			gridPane.add(node2, sourceCol, sourceRow);
		}

		private Node getGridNode(int row, int column) {
			for (Node node : watchGridPane.getChildren()) {
				if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
					return node;
				}
			}
			return null;
		}
	}

	@Override
	public void start(Stage primaryStage) {
		gridPane = new GridPane();
		gridPane.setHgap(GAP);
		gridPane.setVgap(GAP);
		gridPane.setAlignment(Pos.CENTER);

		animPane = new AnimationPane(gridPane);
		animPane.setVisible(false);
		mainStackPane = new StackPane(gridPane, animPane);

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
		topText.setFont(Font.font(18));

		Text bottomText = new Text("Mistakes remaining:");
		bottomText.setFont(Font.font(16));

		circlePane = new Pane();
		circlePane.setPrefWidth(100);

		for (int i = 0; i < 4; i++) {
			Circle circle = new Circle(8);
			circle.setFill(Color.rgb(90, 89, 78));
			circle.setLayoutX(i * 28 + 10);
			circle.setLayoutY(circlePane.getPrefHeight() / 2 + 12);
			circlePane.getChildren().add(circle);
		}

		HBox bottomBox = new HBox(10);
		bottomBox.setAlignment(Pos.CENTER);
		bottomBox.getChildren().addAll(bottomText, circlePane);

		shuffleButton = createButton("Shuffle", 88);

		shuffleButton.setOnAction(event -> {
			ObservableList<Node> children = gridPane.getChildren();
			List<StackPane> stackPanes = children.stream().filter(node -> node instanceof StackPane)
					.map(node -> (StackPane) node).collect(Collectors.toList());

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
			deselectButton.setStyle(
					"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
			submitButton.setDisable(true);
			submitButton.setStyle(
					"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
		});

		submitButton.setDisable(true);

		HBox buttonBox = new HBox(8);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(shuffleButton, deselectButton, submitButton);

		VBox vbox = new VBox(24, topText, mainStackPane, bottomBox, buttonBox);
		vbox.setAlignment(Pos.CENTER);

		StackPane stackPane = new StackPane(vbox);
		stackPane.setStyle("-fx-background-color: white;");

		submitButton.setOnAction(event -> {
			Set<Word> currentGuess = new HashSet<>(getSelectedWords());

			if (previousGuesses.contains(currentGuess)) {
				Rectangle alreadyGuessedRect = new Rectangle(132.09, 42);
				alreadyGuessedRect.setArcWidth(10);
				alreadyGuessedRect.setArcHeight(10);
				alreadyGuessedRect.setFill(Color.BLACK);

				Text alreadyGuessedText = new Text("Already guessed!");
				alreadyGuessedText.setFill(Color.WHITE);
				alreadyGuessedText.setFont(Font.font(16));

				StackPane alreadyGuessedPane = new StackPane(alreadyGuessedRect, alreadyGuessedText);

				alreadyGuessedPane.setTranslateY(-(alreadyGuessedRect.getHeight()) + 5);
				stackPane.getChildren().add(alreadyGuessedPane);

				PauseTransition pause = new PauseTransition(Duration.millis(1000));
				pause.setOnFinished(pauseEvent -> {
					stackPane.getChildren().remove(alreadyGuessedPane);
					deselectButton.fire();
				});
				pause.play();
			} else {
				int matchCount = checkSelectedWords(currentGuess);
				if (circlePane.getChildren().size() > 1) {
					previousGuesses.add(currentGuess);

					if (checkAllCategoriesGuessed()) {
						showAlert("Congratulations!", "You have guessed all categories correctly!");
						disableGameBoard();
					} else {
						
						if (matchCount == 4) {
							animateCorrectGuess();
						} else {
							animateIncorrectGuess(matchCount);
						}
					}
				} else {
					if (checkAllCategoriesGuessed()) {
						showAlert("Congratulations!", "You have guessed all categories correctly!");
						disableGameBoard();
					} else {
						showAlert("Game Over!", "You have no more remaining guesses.");
						animateIncorrectGuess(matchCount);
						disableGameBoard();
					}
				}
			}
		});

		Scene scene = new Scene(stackPane, STAGE_WIDTH, STAGE_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Game Board");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private Button createButton(String text, double width) {
		Button button = new Button(text);
		button.setStyle(
				"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
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
					text.setFont(Font.font("System", FontWeight.BOLD, 18));
					Word word = words.get(index);
					text.setText(word.getText().toUpperCase());
					rectangle.setUserData(word);
				} else if (node instanceof Rectangle) {
					Rectangle rectangle = (Rectangle) node;
					Word word = words.get(index);
					Text text = new Text(word.getText().toUpperCase());
					text.setFont(Font.font("System", FontWeight.BOLD, 18));
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
							deselectButton.setStyle(
									"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
						}

						if (selectedCount == MAX_SELECTED) {
							submitButton.setStyle(
									"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
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

	private int checkSelectedWords(Set<Word> selectedWords) {
		int maxMatchCount = 0;
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			GameAnswerColor answer = currentGame.getAnswerForColor(color);
			List<String> colorWords = Arrays.asList(answer.getWords());
			int matchCount = (int) selectedWords.stream().filter(word -> colorWords.contains(word.getText())).count();
			maxMatchCount = Math.max(maxMatchCount, matchCount);
		}
		return maxMatchCount;
	}

	private void showAlert(String title, String content) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.INFORMATION);
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
	        if (checkSelectedWords(guess) == 4) {
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
		deselectButton.setStyle(
				"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
		submitButton.setDisable(true);
		submitButton.setStyle(
				"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
		shuffleButton.setDisable(true);
		shuffleButton.setStyle(
				"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 32px;");
	}
	
	private void animateIncorrectGuess(int matchCount) {
	    SequentialTransition sequentialTransition = new SequentialTransition();
	    ParallelTransition jumpTransition = createJumpTransition();
	    PauseTransition pauseAfterJump = new PauseTransition(Duration.millis(500));

	    sequentialTransition.getChildren().addAll(jumpTransition, pauseAfterJump);

	    ParallelTransition shakeTransition = createShakeTransition();

	    if (matchCount == 3) {
	        Rectangle oneAwayRect = new Rectangle(96.09, 42);
	        oneAwayRect.setArcWidth(10);
	        oneAwayRect.setArcHeight(10);
	        oneAwayRect.setFill(Color.BLACK);

	        Text oneAwayText = new Text("One away...");
	        oneAwayText.setFill(Color.WHITE);
	        oneAwayText.setFont(Font.font(16));

	        StackPane oneAwayPane = new StackPane(oneAwayRect, oneAwayText);
	        
	        sequentialTransition.setOnFinished(event -> {
	            mainStackPane.getChildren().add(oneAwayPane);
	            shakeTransition.play();

	            PauseTransition displayOneAway = new PauseTransition(Duration.millis(1000));
	            displayOneAway.setOnFinished(e -> mainStackPane.getChildren().remove(oneAwayPane));
	            displayOneAway.play();
	        });
	    } else {
	        sequentialTransition.setOnFinished(event -> shakeTransition.play());
	    }

	    sequentialTransition.play();
	}
	
	private void animateCorrectGuess() {
		SequentialTransition sequentialTransition = new SequentialTransition();
		ParallelTransition jumpTransition = createJumpTransition();
		PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
		sequentialTransition.getChildren().addAll(jumpTransition, pauseTransition);
		sequentialTransition.getChildren().addAll(animPane.getSwapTransitions());
		sequentialTransition.play();
	}

	private ParallelTransition createJumpTransition() {
		ParallelTransition jumpTransition = new ParallelTransition();
		int delay = 0;
		for (Node node : gridPane.getChildren()) {
			if (node instanceof StackPane) {
				StackPane stackPane = (StackPane) node;
				Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
				if (rectangle.getFill() == SELECTED_COLOR) {
					TranslateTransition individualJumpTransition = new TranslateTransition(Duration.millis(200),
							stackPane);
					individualJumpTransition.setByY(-8);
					individualJumpTransition.setAutoReverse(true);
					individualJumpTransition.setCycleCount(2);
					individualJumpTransition.setDelay(Duration.millis(delay));
					jumpTransition.getChildren().add(individualJumpTransition);
					delay += 50;
				}
			}
		}
		return jumpTransition;
	}

	private ParallelTransition createShakeTransition() {
		ParallelTransition shakeTransition = new ParallelTransition();
		for (Node node : gridPane.getChildren()) {
			if (node instanceof StackPane) {
				StackPane stackPane = (StackPane) node;
				Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
				Text text = (Text) stackPane.getChildren().get(1);
				if (rectangle.getFill() == SELECTED_COLOR) {
					rectangle.setFill(INCORRECT_COLOR);
					TranslateTransition individualShakeTransition = new TranslateTransition(Duration.millis(100),
							stackPane);
					individualShakeTransition.setByX(8);
					individualShakeTransition.setAutoReverse(true);
					individualShakeTransition.setCycleCount(2);
					shakeTransition.getChildren().add(individualShakeTransition);
				}
			}
		}
		shakeTransition.setOnFinished(event -> {
			for (Node node : gridPane.getChildren()) {
				if (node instanceof StackPane) {
					StackPane stackPane = (StackPane) node;
					Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
					Text text = (Text) stackPane.getChildren().get(1);
					if (rectangle.getFill() == INCORRECT_COLOR) {
						rectangle.setFill(DEFAULT_COLOR);
						text.setFill(Color.BLACK);
					}
				}
			}
			PauseTransition deselectDelay = new PauseTransition(Duration.millis(500));
			deselectDelay.setOnFinished(deselectEvent -> {
				deselectButton.fire();
				PauseTransition removeCircleDelay = new PauseTransition(Duration.millis(500));
				removeCircleDelay.setOnFinished(removeCircleEvent -> removeCircle(circlePane));
				removeCircleDelay.play();
			});
			deselectDelay.play();
		});
		return shakeTransition;
	}

	public static void main(String[] args) {
		launch(args);
	}
}