import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.SVGPath;
import javafx.geometry.Bounds;

public class GameBoard extends Application {
	protected static final int ROWS = 4;
	protected static final int COLS = 4;
	protected static final int RECTANGLE_WIDTH = 150;
	protected static final int RECTANGLE_HEIGHT = 80;
	protected static final int GAP = 8;
	protected static final int CORNER_RADIUS = 10;
	protected static final int STAGE_WIDTH = 800;
	protected static final int STAGE_HEIGHT = 750;
	protected static final int MAX_SELECTED = 4;

	private StyleManager styleManager = new StyleManager();
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
	private int guessCount = 0;
	private int currentRow = 0;
	private int incorrectGuessCount = 0;
	private boolean wonGame = false;
	private boolean gameLost = false;
	private StackPane wholeGameStackPane;

	private void initGridPane() {
		gridPane = new GridPane();
		gridPane.setHgap(GAP);
		gridPane.setVgap(GAP);
		gridPane.setAlignment(Pos.CENTER);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				gridPane.add(new GameTileWord(styleManager.getFont("franklin-normal", 700, 18), this), col, row);
			}
		}
	}

	private void initWordTiles(GameData game) {
		List<Word> words = new ArrayList<>();
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			GameAnswerColor answer = game.getAnswerForColor(color);
			for (String wordText : answer.getWords()) {
				words.add(new Word(wordText, color));
			}
		}

		Collections.shuffle(words);

		int wordIndex = 0;
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.setWord(words.get(wordIndex));
				wordIndex++;
			}
		}
	}

	private void initGameData() {
		GameDataCollection collection = new GameDataCollection("nyt-connections-games.txt");
		if (!collection.getGameList().isEmpty()) {
			currentGame = collection.getGameList().get(0);
			initWordTiles(currentGame);
		}
	}

	public void gameDeselect() {
		gridPane.getChildren().forEach(node -> {
			if(node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.setSelectedStatus(false);
			}
		});
		selectedCount = 0;
	}
	
	private void gameShuffleWords() {
		ObservableList<Node> children = gridPane.getChildren();
		List<StackPane> stackPanes = children.stream().filter(node -> node instanceof GameTileWord)
				.map(node -> (GameTileWord) node).collect(Collectors.toList());

		Collections.shuffle(stackPanes);

		int index = 0;
		for (int row = currentRow; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				GridPane.setRowIndex(stackPanes.get(index), row);
				GridPane.setColumnIndex(stackPanes.get(index), col);
				index++;
			}
		}
	}
	
	private void gameSubmitSelectedWords() {
		guessCount++;
		Set<Word> currentGuess = new HashSet<>(getSelectedWords());

		if (previousGuesses.contains(currentGuess)) {
			Rectangle alreadyGuessedRect = new Rectangle(132.09, 42);
			alreadyGuessedRect.setArcWidth(10);
			alreadyGuessedRect.setArcHeight(10);
			alreadyGuessedRect.setFill(Color.BLACK);

			Text alreadyGuessedText = new Text("Already guessed!");
			alreadyGuessedText.setFill(Color.WHITE);
			alreadyGuessedText.setFont(styleManager.getFont("franklin-normal", 600, 16));

			StackPane alreadyGuessedPane = new StackPane(alreadyGuessedRect, alreadyGuessedText);

			alreadyGuessedPane.setTranslateY(-(alreadyGuessedRect.getHeight()) + 5);
			wholeGameStackPane.getChildren().add(alreadyGuessedPane);

			PauseTransition pause = new PauseTransition(Duration.millis(1000));
			pause.setOnFinished(pauseEvent -> {
				wholeGameStackPane.getChildren().remove(alreadyGuessedPane);
				deselectButton.fire();
				gameDeselect();
			});
			pause.play();
		} else {
			int matchCount = checkSelectedWords(currentGuess);
			if (matchCount != 4) {
				incorrectGuessCount++;
			}
			previousGuesses.add(currentGuess);
			if (incorrectGuessCount < 4) {
				if (checkAllCategoriesGuessed()) {
					wonGame = true;
					animateCorrectGuess();
					try {
						disableGameBoard();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					if (matchCount == 4) {
						animateCorrectGuess();
					} else {
						animateIncorrectGuess(matchCount);
					}
				}
			} else {
				gameLost = true;
				animateIncorrectGuess(matchCount);
				try {
					disableGameBoard();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void initListeners() {
		deselectButton.setOnAction(event -> {
			gameDeselect();
			deselectButton.setDisable(true);
			deselectButton.setStyle(
					"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50;");
			submitButton.setDisable(true);
			submitButton.setStyle(
					"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50;");
		});

		shuffleButton.setOnAction(event -> {
			gameShuffleWords();
		});

		submitButton.setOnAction(event -> {
			gameSubmitSelectedWords();
		});
	}

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {
		initGridPane();
		
		animPane = new AnimationPane(this);
		animPane.setVisible(false);
		mainStackPane = new StackPane(gridPane, animPane);
		
		initGameData();
		
		Text topText = new Text("Create four groups of four!");
		topText.setFont(styleManager.getFont("franklin-normal", 500, 18));

		Text bottomText = new Text("Mistakes remaining:");
		bottomText.setFont(styleManager.getFont("franklin-normal", 500, 16));

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
		deselectButton = createButton("Deselect all", 120);
		submitButton = createButton("Submit", 88);
		deselectButton.setDisable(true);
		submitButton.setDisable(true);

		HBox buttonBox = new HBox(8);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(shuffleButton, deselectButton, submitButton);

		VBox vbox = new VBox(24, topText, mainStackPane, bottomBox, buttonBox);
		vbox.setAlignment(Pos.CENTER);

		StackPane wholeGameStackPane = new StackPane(vbox);
		wholeGameStackPane.setStyle("-fx-background-color: white;");
		this.wholeGameStackPane = wholeGameStackPane;

		initListeners();

		Scene scene = new Scene(wholeGameStackPane, STAGE_WIDTH, STAGE_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connections");
		primaryStage.setResizable(false);
		primaryStage.show();

//		SEE RESULTS PANE UPON LOAD FOR DEBUGGING

//		try {
//			showResultsPane((Stage) wholeGameStackPane.getScene().getWindow());
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private Button createButton(String text, double width) throws FileNotFoundException {
		Button button = new Button(text);
		button.setStyle(
				"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50;");
		button.setPrefHeight(48);
		button.setPrefWidth(width);
		button.setFont(styleManager.getFont("franklin-normal", 600, 16));

		button.setOnMouseEntered(event -> {
			button.setCursor(Cursor.HAND);
		});

		button.setOnMouseExited(event -> {
			button.setCursor(Cursor.DEFAULT);
		});

		return button;
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
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				if(tileWord.getSelectedStatus()) {
					selectedWords.add(tileWord.getWord());
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

	private void disableGameBoard() throws FileNotFoundException {
		gridPane.getChildren().forEach(node -> {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.disable();
			}
		});

		VBox vbox = (VBox) wholeGameStackPane.getChildren().get(0);
		HBox buttonBox = (HBox) vbox.getChildren().get(3);
		buttonBox.getChildren().clear();

		HBox bottomBox = (HBox) vbox.getChildren().get(2);
		vbox.getChildren().remove(bottomBox);

		Button viewResultsButton = new Button("View Results");
		viewResultsButton.setStyle(
				"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50;");
		viewResultsButton.setPrefSize(160, 48);
		viewResultsButton.setFont(styleManager.getFont("franklin-normal", 600, 16));

		viewResultsButton.setOnMouseEntered(event -> {
			viewResultsButton.setCursor(Cursor.HAND);
		});
		viewResultsButton.setOnMouseClicked(event -> {
			try {
				showResultsPane((Stage) wholeGameStackPane.getScene().getWindow());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().add(viewResultsButton);
	}

	private void showResultsPane(Stage stage) throws FileNotFoundException {
		VBox resultsLayout = new VBox(0);
		resultsLayout.setAlignment(Pos.TOP_CENTER);

		Label titleLabel = wonGame ? new Label("Perfect!") : new Label("Next Time!");
		titleLabel.setFont(styleManager.getFont("karnakpro-condensedblack", 36));
		titleLabel.setTextFill(Color.BLACK);
		VBox.setMargin(titleLabel, new Insets(80, 0, 0, 0));

		// get the real one later on
		int puzzleNumber = 294;
		Label connectionsLabel = new Label("Connections #" + puzzleNumber);
		connectionsLabel.setFont(styleManager.getFont("franklin-normal", 500, 20));
		VBox.setMargin(connectionsLabel, new Insets(18, 0, 0, 0));

		GridPane gridPane = new GridPane();
		gridPane.setVgap(GAP);
		gridPane.setAlignment(Pos.CENTER);
		VBox.setMargin(gridPane, new Insets(20, 0, 0, 0));

		Color[] colors = { Color.rgb(249, 223, 109), // Yellow
				Color.rgb(160, 195, 90), // Green
				Color.rgb(176, 195, 238), // Blue
				Color.rgb(186, 128, 197) // Purple
		};

		int i = 0;
		for (Set<Word> previousGuess : previousGuesses) {
			int j = 0;
			for (Word guess : previousGuess) {
				String color = guess.getColor().toString();
				Color rectangleColor = null;
				if (color.equalsIgnoreCase("yellow")) {
					rectangleColor = colors[0];
				} else if (color.equalsIgnoreCase("green")) {
					rectangleColor = colors[1];
				} else if (color.equalsIgnoreCase("blue")) {
					rectangleColor = colors[2];
				} else if (color.equalsIgnoreCase("purple")) {
					rectangleColor = colors[3];
				}
				Rectangle square = new Rectangle(40, 40, rectangleColor);
				square.setArcWidth(10);
				square.setArcHeight(10);
				gridPane.add(square, j, i);
				j++;
			}
			i++;
		}

		Label nextPuzzleInLabel = new Label("NEXT PUZZLE IN");
		nextPuzzleInLabel.setFont(styleManager.getFont("franklin-normal", 600, 20));
		nextPuzzleInLabel.setAlignment(Pos.CENTER);
		VBox.setMargin(nextPuzzleInLabel, new Insets(20, 0, 0, 0));

		Label timerLabel = new Label();
		timerLabel.setFont(styleManager.getFont("franklin-normal", 600, 40));
		timerLabel.setAlignment(Pos.CENTER);
		VBox.setMargin(timerLabel, new Insets(-10, 0, 0, 0));

		VBox timerBox = new VBox(5, nextPuzzleInLabel, timerLabel);
		timerBox.setAlignment(Pos.CENTER);

		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime midnight = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);

			long nowMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			long midnightMillis = midnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

			Duration duration = Duration.millis(midnightMillis - nowMillis);

			long hours = (long) duration.toHours();
			long minutes = (long) (duration.toMinutes() % 60);
			long seconds = (long) (duration.toSeconds() % 60);

			String timerText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
			timerLabel.setText(timerText);
		}));

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		Button shareButton = new Button("Share Your Results");
		shareButton.setPrefSize(162, 48);
		shareButton.setFont(styleManager.getFont("franklin-normal", 600, 16));
		shareButton.setStyle(
		        "-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-min-height: 48px; -fx-max-height: 48px;");
		VBox.setMargin(shareButton, new Insets(21, 0, 20, 0));

		shareButton.setTranslateY(4);

		resultsLayout.getChildren().addAll(titleLabel, connectionsLabel, gridPane, timerBox, shareButton);
		
		StackPane resultsPane = new StackPane(resultsLayout);
		
		shareButton.setOnMouseEntered(e -> {
			shareButton.setCursor(Cursor.HAND);
		    shareButton.setStyle(
		            "-fx-background-color: rgb(18,18,18); -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-cursor: hand;");
		});

		shareButton.setOnMouseExited(e -> {
			shareButton.setCursor(Cursor.DEFAULT);
		    shareButton.setStyle(
		            "-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50;");
		});
		shareButton.setOnAction(e -> {
		    Rectangle copiedRect = new Rectangle(204.54, 42);
		    copiedRect.setArcWidth(10);
		    copiedRect.setArcHeight(10);
		    copiedRect.setFill(Color.BLACK);

		    Text copiedText = new Text("Copied results to clipboard");
		    copiedText.setFill(Color.WHITE);
		    copiedText.setFont(styleManager.getFont("franklin-normal", 600, 16));

		    StackPane copiedPane = new StackPane(copiedRect, copiedText);
		    resultsPane.getChildren().add(copiedPane);

		    PauseTransition displayCopied = new PauseTransition(Duration.millis(1000));
		    displayCopied.setOnFinished(event -> resultsPane.getChildren().remove(copiedPane));
		    displayCopied.play();
		    
		    final Clipboard clipboard = Clipboard.getSystemClipboard();
		    final ClipboardContent content = new ClipboardContent();
		    String copiedString = "Connections\nPuzzle #" + puzzleNumber + "\n";
		    for (Set<Word> previousGuess : previousGuesses) {
		        for (Word guess : previousGuess) {
		            String color = guess.getColor().toString();
		            if (color.equalsIgnoreCase("yellow")) {
		                copiedString += "\ud83d\udfe8";
		            } else if (color.equalsIgnoreCase("green")) {
		                copiedString += "\ud83d\udfe9";
		            } else if (color.equalsIgnoreCase("blue")) {
		                copiedString += "\ud83d\udfe6";
		            } else if (color.equalsIgnoreCase("purple")) {
		                copiedString += "\ud83d\udfea";
		            }
		        }
		        copiedString += "\n";
		    }
		    content.putString(copiedString);
		    clipboard.setContent(content);
		});
		
		resultsPane.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, black, 20, 0, 0, 0);");
		resultsPane.setPrefSize(667, 402 + (guessCount * 40) + ((guessCount - 1) * GAP));
		resultsPane.setMaxWidth(667);
		resultsPane.setMaxHeight(402 + (guessCount * 40) + ((guessCount - 1) * GAP));

		SVGPath xPath = new SVGPath();
		xPath.setContent(
				"M18.717 6.697l-1.414-1.414-5.303 5.303-5.303-5.303-1.414 1.414 5.303 5.303-5.303 5.303 1.414 1.414 5.303-5.303 5.303 5.303 1.414-1.414-5.303-5.303z");
		xPath.setScaleX(0.8);
		xPath.setScaleY(0.8);
		xPath.setOnMouseEntered(e -> {
			xPath.setCursor(Cursor.HAND);
		});
		xPath.setOnMouseExited(e -> {
			xPath.setCursor(Cursor.DEFAULT);
		});

		HBox backToPuzzleBox = new HBox(10);
		backToPuzzleBox.setAlignment(Pos.CENTER);

		Text backToPuzzleText = new Text("Back to puzzle");
		backToPuzzleText.setFont(styleManager.getFont("franklin-normal", 600, 16));
		backToPuzzleText.setOnMouseEntered(e -> {
			backToPuzzleText.setUnderline(true);
			backToPuzzleText.setCursor(Cursor.HAND);
		});
		backToPuzzleText.setOnMouseExited(e -> {
			backToPuzzleText.setUnderline(false);
			backToPuzzleText.setCursor(Cursor.DEFAULT);
		});

		xPath.setScaleX(1);
		xPath.setScaleY(1);
		xPath.setTranslateY(4);

		backToPuzzleBox.getChildren().addAll(backToPuzzleText, xPath);

		resultsPane.getChildren().add(backToPuzzleBox);
		backToPuzzleBox.setStyle("-fx-alignment: top-right;");
		StackPane.setMargin(backToPuzzleBox, new Insets(19.2, 19.2, 0, 0));

		StackPane overlayPane = new StackPane(wholeGameStackPane, resultsPane);
		overlayPane.setAlignment(Pos.CENTER);
		
		overlayPane.setOnMouseMoved(event -> {
            Bounds bounds = overlayPane.getBoundsInLocal();
            double mouseX = event.getX();
            double mouseY = event.getY();
            
            double minMouseX = backToPuzzleText.getLayoutX();
            double maxMouseX = resultsPane.getLayoutX() + resultsPane.getWidth();
            double minMouseY = resultsPane.getLayoutY() + 19;
            double maxMouseY = resultsPane.getLayoutY() + 36;
            
            if (mouseX >= minMouseX && mouseX <= maxMouseX && mouseY >= minMouseY && mouseY <= maxMouseY) {
//            	System.out.println("at " + mouseX);
//            	System.out.println("at " + mouseY);
            	backToPuzzleBox.setMouseTransparent(false);
            } else {
//            	System.out.printf("X[%f %f] Y[%f %f]\n", minMouseX, maxMouseX, minMouseY, maxMouseY);
//            	System.out.println("not at " + mouseX);
//            	System.out.println("not at " + mouseY);
            	backToPuzzleBox.setMouseTransparent(true);
            }
        });

		Scene scene = new Scene(overlayPane, STAGE_WIDTH, STAGE_HEIGHT);
		stage.setScene(scene);

		backToPuzzleText.setOnMouseClicked(e -> {
			overlayPane.getChildren().remove(resultsPane);
		});
		
		xPath.setOnMouseClicked(e -> {
			overlayPane.getChildren().remove(resultsPane);
		});
	}

	private void animateIncorrectGuess(int matchCount) {
		disableButtons();
		disableRectangles();
		SequentialTransition sequentialTransition = new SequentialTransition();
		ParallelTransition jumpTransition = createJumpTransition();
		PauseTransition pauseAfterJump = new PauseTransition(Duration.millis(500));

		sequentialTransition.getChildren().addAll(jumpTransition, pauseAfterJump);

		sequentialTransition.setOnFinished(event -> {
			if (matchCount == 3 && incorrectGuessCount < 4) {
				Rectangle oneAwayRect = new Rectangle(96.09, 42);
				oneAwayRect.setArcWidth(10);
				oneAwayRect.setArcHeight(10);
				oneAwayRect.setFill(Color.BLACK);

				Text oneAwayText = new Text("One away...");
				oneAwayText.setFill(Color.WHITE);
				oneAwayText.setFont(styleManager.getFont("franklin-normal", 600, 16));
//				oneAwayText.setFont(Font.font(16));

				StackPane oneAwayPane = new StackPane(oneAwayRect, oneAwayText);
				mainStackPane.getChildren().add(oneAwayPane);

				PauseTransition displayOneAway = new PauseTransition(Duration.millis(1000));
				displayOneAway.setOnFinished(e -> mainStackPane.getChildren().remove(oneAwayPane));
				displayOneAway.play();
			}
			if (gameLost) {
				Rectangle nextTimeRect = new Rectangle(88.13, 42);
				nextTimeRect.setArcWidth(10);
				nextTimeRect.setArcHeight(10);
				nextTimeRect.setFill(Color.BLACK);

				Text nextTimeText = new Text("Next Time");
				nextTimeText.setFill(Color.WHITE);
				nextTimeText.setFont(styleManager.getFont("franklin-normal", 600, 16));

				StackPane nextTimePane = new StackPane(nextTimeRect, nextTimeText);
				mainStackPane.getChildren().add(nextTimePane);

				PauseTransition displayNextTime = new PauseTransition(Duration.millis(1000));
				displayNextTime.setOnFinished(e -> mainStackPane.getChildren().remove(nextTimePane));
				displayNextTime.play();
			}
			ParallelTransition shakeTransition = createShakeTransition();
			shakeTransition.setOnFinished(shakeEvent -> {
				for (Node node : gridPane.getChildren()) {
					if (node instanceof GameTileWord) {
						GameTileWord tileWord = (GameTileWord) node;
						if(tileWord.getIncorrectStatus()) {
							tileWord.setIncorrectStatus(false);
							// implies
							// tileWord.setStyleDefault();
						}
					}
				}
				PauseTransition deselectDelay = new PauseTransition(Duration.millis(500));
				deselectDelay.setOnFinished(deselectEvent -> {
					deselectButton.fire();
					gameDeselect();
					PauseTransition removeCircleDelay = new PauseTransition(Duration.millis(500));
					removeCircleDelay.setOnFinished(removeCircleEvent -> {
						removeCircle(circlePane);
						enableButtons();
						enableRectangles();
						if (gameLost) {
							PauseTransition delay = new PauseTransition(Duration.millis(500));
							delay.setOnFinished(e -> {
								try {
									showResultsPane((Stage) wholeGameStackPane.getScene().getWindow());
								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							});
							delay.play();
						}
					});
					removeCircleDelay.play();
				});
				deselectDelay.play();
			});
			shakeTransition.play();
		});
		sequentialTransition.play();
	}

	private ParallelTransition createShakeTransition() {
		ParallelTransition shakeTransition = new ParallelTransition();
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				
				if(tileWord.getSelectedStatus()) {
					tileWord.setSelectedStatus(false);
					tileWord.setIncorrectStatus(true);
					TranslateTransition individualShakeTransition = new TranslateTransition(Duration.millis(100),
							tileWord);
					individualShakeTransition.setByX(8);
					individualShakeTransition.setAutoReverse(true);
					individualShakeTransition.setCycleCount(4);
					shakeTransition.getChildren().add(individualShakeTransition);
				}
			}
		}
		return shakeTransition;
	}

	private void animateCorrectGuess() {
		disableButtons();
		disableRectangles();
		if (!wonGame) {
			SequentialTransition sequentialTransition = new SequentialTransition();
			ParallelTransition jumpTransition = createJumpTransition();
			PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
			sequentialTransition.getChildren().addAll(jumpTransition, pauseTransition);
			sequentialTransition.getChildren().addAll(animPane.getSwapTransitions());
			sequentialTransition.play();
			sequentialTransition.setOnFinished(event -> {
				enableButtons();
				enableRectangles();
			});
		} else {
			SequentialTransition sequentialTransition = new SequentialTransition();
			ParallelTransition jumpTransition = createJumpTransition();
			PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
			sequentialTransition.getChildren().addAll(jumpTransition, pauseTransition);
			sequentialTransition.setOnFinished(event -> {
				if (wonGame) {
					gameDeselect();
					try {
						showResultsPane((Stage) wholeGameStackPane.getScene().getWindow());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					enableButtons();
					enableRectangles();
				}
			});
			sequentialTransition.play();
		}
	}

	private ParallelTransition createJumpTransition() {
		ParallelTransition jumpTransition = new ParallelTransition();
		int delay = 0;
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				
				if(tileWord.getSelectedStatus()) {
					TranslateTransition individualJumpTransition = new TranslateTransition(Duration.millis(200),
							tileWord);
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

	private void disableButtons() {
		shuffleButton.setDisable(true);
		deselectButton.setDisable(true);
		submitButton.setDisable(true);
		submitButton.setStyle(
				"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-background-radius: 50; -fx-border-radius: 50;");
	}

	private void enableButtons() {
		shuffleButton.setDisable(false);
		deselectButton.setDisable(false);
		selectedCount = 0;
	}

	private void disableRectangles() {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				node.setDisable(true);
			}
		}
	}

	private void enableRectangles() {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				node.setDisable(false);
			}
		}
	}
	
	public Button getDeselectButton() {
		return deselectButton;
	}
	
	public Button getSubmitButton() {
		return submitButton;
	}

	public int getSelectedCount() {
		return selectedCount;
	}
	
	public void incrementSelectedCount() {
		selectedCount++;
	}
	
	public void decrementSelectedCount() {
		selectedCount--;
	}
	
	public int getCurrentRow() {
		return currentRow;
	}

	public void advanceRow() {
		currentRow++;
	}
	
	public StyleManager getStyleManager() {
		return styleManager;
	}
	
	public GridPane getWordGridPane() {
		return gridPane;
	}
	
	public GameData getCurrentGame() {
		return currentGame;
	}

	public static void main(String[] args) {
		launch(args);
	}
}