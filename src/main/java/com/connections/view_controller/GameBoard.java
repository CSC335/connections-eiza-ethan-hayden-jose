package com.connections.view_controller;

import com.connections.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.SVGPath;

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
	protected static final int GRID_ANIM_PANE_WIDTH = RECTANGLE_WIDTH * 4 + GAP * 3;

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
	private boolean achievementsVisible = false;
	private boolean gameLost = false;
	private StackPane wholeGameStackPane;
	private DarkModeToggle darkModeToggle;
	private Pane wholeAchievementsPane;
	private VBox wholeGameVbox;
	private Text topText;
	private SequentialTransition sequentialIncorrectTrans;
	private SequentialTransition sequentialCorrectTrans;
	private CircleRowPane hintsPane;
	private CircleRowPane mistakesPane;
	private HintButton hintButton;
	private AchievementsButton achievementsButton;
	private LeaderboardButton leaderboardButton;
	private boolean hintDisplayActive;

	private void initGridPane() {
		gridPane = new GridPane();
		gridPane.setHgap(GAP);
		gridPane.setVgap(GAP);
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setMaxWidth(GRID_ANIM_PANE_WIDTH);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				gridPane.add(new GameTileWord(styleManager.getFont("franklin-normal", 700, 18), this), col, row);
			}
		}
	}

	private void initAnimPane() {
		animPane = new AnimationPane(this);
		animPane.setVisible(false);
		animPane.setMaxWidth(GRID_ANIM_PANE_WIDTH);
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
			if (node instanceof GameTileWord) {
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

		ParallelTransition fadeInTransition = new ParallelTransition();

		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.fadeInWordText(fadeInTransition);
			}
		}

		fadeInTransition.play();
	}

	private void gameSubmitSelectedWords() {
		Set<Word> currentGuess = new HashSet<>(getSelectedWords());

		if (previousGuesses.contains(currentGuess)) {
			Rectangle alreadyGuessedRect = new Rectangle(132.09, 42);
			alreadyGuessedRect.setArcWidth(10);
			alreadyGuessedRect.setArcHeight(10);
			alreadyGuessedRect.setFill(styleManager.colorPopupBackground());

			Text alreadyGuessedText = new Text("Already guessed!");
			alreadyGuessedText.setFill(styleManager.colorPopupText());
			alreadyGuessedText.setFont(styleManager.getFont("franklin-normal", 600, 16));

			StackPane alreadyGuessedPane = new StackPane(alreadyGuessedRect, alreadyGuessedText);
			alreadyGuessedPane.getStyleClass().add("popup-pane");
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
			guessCount++;
			int matchCount = checkSelectedWords(currentGuess);
			if (matchCount != 4) {
				incorrectGuessCount++;
			}
			previousGuesses.add(currentGuess);
			if (incorrectGuessCount < 4) {
				if (matchCount == 4) {
					if (checkAllCategoriesGuessed()) {
						wonGame = true;
						animateCorrectGuess();
//						disableGameBoard();
					} else {
						animateCorrectGuess();
					}
				} else {
					animateIncorrectGuess(matchCount);
				}
			} else {
				gameLost = true;
				animateIncorrectGuess(matchCount);
				disableGameBoard();
			}
		}
	}

	private void initListeners() {
		deselectButton.setOnAction(event -> {
			gameDeselect();
			deselectButton.setDisable(true);
			submitButton.setDisable(true);

		});

		shuffleButton.setOnAction(event -> {
			gameShuffleWords();
		});

		submitButton.setOnAction(event -> {
			gameSubmitSelectedWords();
		});

		achievementsButton.setOnMouseClicked(event -> {
			if (achievementsVisible) {
				if (wholeAchievementsPane != null) {
					mainStackPane.getChildren().remove(wholeAchievementsPane);
					wholeAchievementsPane = null;
				}
				for (Node node : wholeGameVbox.getChildren()) {
					node.setVisible(true);
				}
				topText.setText("Create four groups of four!");
				animPane.setAllowChangeVisibility(true);
				if (animPane.getPaneShouldBeVisible()) {
					animPane.setVisible(true);
				}
			} else {
				wholeAchievementsPane = createAchievementsPane();
				wholeAchievementsPane.setBackground(
						new Background(new BackgroundFill(styleManager.colorWholeAchievementsPane(), null, null)));
				mainStackPane.getChildren().add(wholeAchievementsPane);
				for (Node node : wholeGameVbox.getChildren()) {
					node.setVisible(false);
				}
				mainStackPane.setVisible(true);
				topText.setVisible(true);
				topText.setText("Achievements:");
				animPane.setAllowChangeVisibility(false);
				animPane.setVisible(false);
			}

			achievementsVisible = !achievementsVisible;
		});
		
		hintButton.setOnMouseClicked(event -> {
			useHint();
		});
	}

	private void initDarkModeToggle() {
		darkModeToggle = new DarkModeToggle(this);
	}

	private void applyDarkOrLightMode() {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				((GameTileWord) node).refreshStyle();
			} else if (node instanceof GameTileAnswer) {
				((GameTileAnswer) node).refreshStyle();
			}
		}
		animPane.refreshStyle();
		this.refreshStyle();
	}

	public void refreshStyle() {
		wholeGameStackPane.setStyle(styleManager.wholeGameStyle());
		shuffleButton.setStyle(styleManager.buttonStyle());
		deselectButton.setStyle(styleManager.buttonStyle());
		hintButton.refreshStyle();
		achievementsButton.refreshStyle();
		leaderboardButton.refreshStyle();
		
		if (this.getSelectedCount() == GameBoard.MAX_SELECTED && !submitButton.isDisabled()) {
			submitButton.setStyle(styleManager.submitButtonFillStyle());
		} else {
			submitButton.setStyle(styleManager.buttonStyle());
		}
		
		BorderPane borderPane = (BorderPane) wholeGameStackPane.getChildren().get(0);
		VBox vbox = (VBox) borderPane.getChildren().get(1);
		HBox buttonBox = (HBox) vbox.getChildren().get(2);
		if (buttonBox.getChildren().size() > 0 && buttonBox.getChildren().get(0) instanceof Button) {
			Button viewResultsButton = (Button) buttonBox.getChildren().get(0);
			viewResultsButton.setStyle(styleManager.buttonStyle());
		}
		
		hintsPane.refreshStyle();
		mistakesPane.refreshStyle();
		topText.setFill(styleManager.colorText());
		
		updateAchievementsPaneStyle();
		updateResultsPaneStyle();
		updatePopupStyle();
	}

	private void updateAchievementsPaneStyle() {
		if (wholeAchievementsPane != null) {
			wholeAchievementsPane.setBackground(
					new Background(new BackgroundFill(styleManager.colorWholeAchievementsPane(), null, null)));
			for (Node node : wholeAchievementsPane.getChildren()) {
				if (node instanceof GridPane) {
					GridPane achievementsGrid = (GridPane) node;
					for (Node achievementNode : achievementsGrid.getChildren()) {
						if (achievementNode instanceof StackPane) {
							StackPane achievementPane = (StackPane) achievementNode;
							Rectangle rect = (Rectangle) achievementPane.getChildren().get(0);
							Label label = (Label) achievementPane.getChildren().get(1);

							rect.setFill(styleManager.colorDefaultRectangle());
							label.setTextFill(styleManager.colorText());
						}
					}
				}
			}
		}
	}

	private void updatePopupStyle() {
		for (Node node : wholeGameStackPane.lookupAll(".popup-pane")) {
			if (node instanceof StackPane) {
				StackPane popupPane = (StackPane) node;
				if (popupPane.getChildren().size() == 2) {
					Node firstChild = popupPane.getChildren().get(0);
					Node secondChild = popupPane.getChildren().get(1);
					if (firstChild instanceof Rectangle && secondChild instanceof Text) {
						Rectangle popupRect = (Rectangle) firstChild;
						Text popupText = (Text) secondChild;
						popupRect.setFill(styleManager.colorPopupBackground());
						popupText.setFill(styleManager.colorPopupText());
					}
				}
			}
		}
	}

	private void updateResultsPaneStyle() {
		if (wholeGameStackPane.getParent() != null) {
			StackPane resultsPane = (StackPane) wholeGameStackPane.getParent().lookup(".results-pane");
			if (resultsPane != null) {
				VBox resultsLayout = (VBox) resultsPane.getChildren().get(0);
				for (Node child : resultsLayout.getChildren()) {
					if (child instanceof Label) {
						Label label = (Label) child;
						label.setTextFill(styleManager.colorText());
					} else if (child instanceof Text) {
						Text text = (Text) child;
						text.setFill(styleManager.colorText());
					} else if (child instanceof VBox) {
						VBox timerBox = (VBox) child;
						for (Node timerChild : timerBox.getChildren()) {
							if (timerChild instanceof Label) {
								Label label = (Label) timerChild;
								label.setTextFill(styleManager.colorText());
							}
						}
					} else if (child instanceof Button) {
						Button shareButton = (Button) child;
						shareButton.setStyle(styleManager.resultsPaneShareButtonStyle());
					}
				}

				HBox backToPuzzleBox = (HBox) resultsPane.lookup(".back-to-puzzle-box");
				if (backToPuzzleBox != null) {
					Text backToPuzzleText = (Text) backToPuzzleBox.getChildren().get(0);
					backToPuzzleText.setFill(styleManager.colorText());
				}

				SVGPath xPath = (SVGPath) resultsPane.lookup(".x-path");
				if (xPath != null) {
					xPath.setFill(styleManager.colorText());
				}

				resultsPane.setStyle(styleManager.resultsPaneStyle());
			}
			if (resultsPane != null) {
				for (Node node : resultsPane.lookupAll(".popup-pane")) {
					if (node instanceof StackPane) {
						StackPane popupPane = (StackPane) node;
						if (popupPane.getChildren().size() == 2) {
							Node firstChild = popupPane.getChildren().get(0);
							Node secondChild = popupPane.getChildren().get(1);
							if (firstChild instanceof Rectangle && secondChild instanceof Text) {
								Rectangle popupRect = (Rectangle) firstChild;
								Text popupText = (Text) secondChild;
								popupRect.setFill(styleManager.colorPopupBackground());
								popupText.setFill(styleManager.colorPopupText());
							}
						}
					}
				}
			}
		}
	}

	public void applyDarkMode() {
		applyDarkOrLightMode();
	}

	public void applyLightMode() {
		applyDarkOrLightMode();
	}

	@Override
	public void start(Stage primaryStage) {
		initGridPane();
		initAnimPane();
		mainStackPane = new StackPane(gridPane, animPane);

		initGameData();
		
		hintButton = new HintButton(this);

		topText = new Text("Create four groups of four!");
		topText.setFont(styleManager.getFont("franklin-normal", 500, 18));
		
		hintsPane = new CircleRowPane("Hints remaining:", this);
		mistakesPane = new CircleRowPane("Mistakes remaining:", this);

		shuffleButton = createButton("Shuffle", 88);
		deselectButton = createButton("Deselect all", 120);
		submitButton = createButton("Submit", 88);
		deselectButton.setDisable(true);
		submitButton.setDisable(true);

		HBox buttonBox = new HBox(8);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(shuffleButton, deselectButton, submitButton);

		// Initialize the dark mode toggle
		initDarkModeToggle();
		
		Border redBorder = new Border(new javafx.scene.layout.BorderStroke(
	                Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

		// Create an HBox to hold the dark mode toggle, achievements, and leader board

		leaderboardButton = new LeaderboardButton(this);
		achievementsButton = new AchievementsButton(this);
		
		HBox cornerButtonBox = new HBox(10, hintButton, leaderboardButton, achievementsButton, darkModeToggle);
		cornerButtonBox.setStyle("-fx-alignment: center-right;");

		wholeGameVbox = new VBox(24, topText, mainStackPane, hintsPane, mistakesPane, buttonBox);
		wholeGameVbox.setAlignment(Pos.CENTER);

		BorderPane mainContentPane = new BorderPane();
		mainContentPane.setPadding(new Insets(10));
		mainContentPane.setTop(cornerButtonBox);
		mainContentPane.setCenter(wholeGameVbox);

		StackPane wholeGameStackPane = new StackPane(mainContentPane);
		wholeGameStackPane.setStyle(styleManager.wholeGameStyle());
		this.wholeGameStackPane = wholeGameStackPane;

		initListeners();

		Scene scene = new Scene(wholeGameStackPane, STAGE_WIDTH, STAGE_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connections");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	public void useHint() {
		if(hintsPane.getNumCircles() > 0) {
			if(hintDisplayActive) {
				
			} else {
				hintDisplayActive = true;
				for(Node node : gridPane.getChildren()) {
					if(node instanceof GameTileWord) {
						GameTileWord tileWord = (GameTileWord) node;
						ParallelTransition pulseTransition = tileWord.getPulseAnswerColorAnimation();
						pulseTransition.setOnFinished(event -> {
							hintsPane.removeCircle();
							hintDisplayActive = false;
						});
						pulseTransition.play();
					}
				}
			}
		}
	}

	private Pane createAchievementsPane() {
		GridPane achievementsGrid = new GridPane();
		achievementsGrid.setHgap(GAP);
		achievementsGrid.setVgap(GAP);
		achievementsGrid.setAlignment(Pos.CENTER);

		String[] achievementLabels = { "1 standard game completed", "10 standard games completed",
				"50 standard games completed", "100 standard games completed", "1 time trial game completed",
				"10 time trial games completed", "50 time trial games completed", "100 time trial games completed",
				"Solved 1 puzzle with no mistakes", "Solved 10 puzzles with no mistakes",
				"Solved 50 puzzles with no mistakes", "Solved 100 puzzles with no mistakes",
				"Solved 1 time trial puzzle in under 30 seconds", "Solved 10 time trial puzzles in under 30 seconds",
				"Solved 50 time trial puzzles in under 30 seconds",
				"Solved 100 time trial puzzles in under 30 seconds" };

		int row = 0;
		int col = 0;
		for (String labelText : achievementLabels) {
			Rectangle rect = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
			rect.setArcWidth(CORNER_RADIUS);
			rect.setArcHeight(CORNER_RADIUS);
			rect.setFill(styleManager.colorDefaultRectangle());

			Label label = new Label(labelText);
			label.setFont(styleManager.getFont("franklin-normal", 500, 14));
			label.setTextFill(styleManager.colorText());
			label.setWrapText(true);
			label.setStyle("-fx-text-alignment: center;");
			label.setMaxWidth(RECTANGLE_WIDTH - 20);

			StackPane achievementPane = new StackPane(rect, label);
			achievementsGrid.add(achievementPane, col, row);

			col++;
			if (col == COLS) {
				col = 0;
				row++;
			}
		}

		StackPane stackPane = new StackPane(achievementsGrid);
		return stackPane;
	}

	private Button createButton(String text, double width) {
		Button button = new Button(text);
		button.setStyle(styleManager.buttonStyle());
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

	private List<Word> getSelectedWords() {
		List<Word> selectedWords = new ArrayList<>();
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				if (tileWord.getSelectedStatus()) {
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

	private void disableGameBoard() {
		gridPane.getChildren().forEach(node -> {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.disable();
			}
		});

		BorderPane borderPane = (BorderPane) wholeGameStackPane.getChildren().get(0);
		VBox vbox = (VBox) borderPane.getChildren().get(1);
		HBox buttonBox = (HBox) vbox.getChildren().get(3);
		buttonBox.getChildren().clear();

		vbox.getChildren().remove(mistakesPane);
		
		if(hintsPane != null && vbox.getChildren().contains(hintsPane)) {
			vbox.getChildren().remove(hintsPane);
		}

		Button viewResultsButton = new Button("View Results");
		viewResultsButton.setStyle(styleManager.buttonStyle());
		viewResultsButton.setPrefSize(160, 48);
		viewResultsButton.setFont(styleManager.getFont("franklin-normal", 600, 16));

		viewResultsButton.setOnMouseEntered(event -> {
			viewResultsButton.setCursor(Cursor.HAND);
		});
		viewResultsButton.setOnMouseClicked(event -> {
			showResultsPane((Stage) wholeGameStackPane.getScene().getWindow());
		});

		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().add(viewResultsButton);
	}

	private void showResultsPane(Stage stage) {
		VBox resultsLayout = new VBox(0);
		resultsLayout.setAlignment(Pos.TOP_CENTER);

		Label titleLabel = wonGame ? new Label("Perfect!") : new Label("Next Time!");
		titleLabel.setFont(styleManager.getFont("karnakpro-condensedblack", 36));
		VBox.setMargin(titleLabel, new Insets(80, 0, 0, 0));

		int puzzleNumber = 294;
		Label connectionsLabel = new Label("Connections #" + puzzleNumber);
		connectionsLabel.setFont(styleManager.getFont("franklin-normal", 500, 20));
		VBox.setMargin(connectionsLabel, new Insets(18, 0, 0, 0));

		GridPane gridPane = new GridPane();
		gridPane.setVgap(GAP);
		gridPane.setAlignment(Pos.CENTER);
		VBox.setMargin(gridPane, new Insets(20, 0, 0, 0));

		int i = 0;
		for (Set<Word> previousGuess : previousGuesses) {
			int j = 0;
			for (Word guess : previousGuess) {
				String color = guess.getColor().toString();
				Color rectangleColor = null;
				if (color.equalsIgnoreCase("yellow")) {
					rectangleColor = styleManager.colorYellow();
				} else if (color.equalsIgnoreCase("green")) {
					rectangleColor = styleManager.colorGreen();
				} else if (color.equalsIgnoreCase("blue")) {
					rectangleColor = styleManager.colorBlue();
				} else if (color.equalsIgnoreCase("purple")) {
					rectangleColor = styleManager.colorPurple();
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
		shareButton.setStyle(styleManager.resultsPaneShareButtonStyle());
		VBox.setMargin(shareButton, new Insets(21, 0, 20, 0));

		shareButton.setTranslateY(4);

		resultsLayout.getChildren().addAll(titleLabel, connectionsLabel, gridPane, timerBox, shareButton);

		StackPane resultsPane = new StackPane(resultsLayout);
		resultsPane.setStyle(styleManager.resultsPaneStyle());
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

		xPath.getStyleClass().add("x-path");

		HBox backToPuzzleBox = new HBox(10);
		backToPuzzleBox.setAlignment(Pos.CENTER);
		backToPuzzleBox.getStyleClass().add("back-to-puzzle-box");

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
		resultsPane.getStyleClass().add("results-pane");

		StackPane overlayPane = new StackPane(wholeGameStackPane, resultsPane);
		overlayPane.setAlignment(Pos.CENTER);

		TranslateTransition resultsPaneMoveUp = new TranslateTransition(Duration.millis(150), resultsPane);
		resultsPane.setTranslateX(0);
		resultsPane.setTranslateY(45);
		resultsPaneMoveUp.setToX(0);
		resultsPaneMoveUp.setToY(0);

		FadeTransition resultsPaneFadeIn = new FadeTransition(Duration.millis(150), resultsPane);
		resultsPaneFadeIn.setFromValue(0);
		resultsPaneFadeIn.setToValue(1);

		ParallelTransition resultsAppearTransition = new ParallelTransition(resultsPaneMoveUp, resultsPaneFadeIn);

		overlayPane.setOnMouseMoved(event -> {
			double mouseX = event.getX();
			double mouseY = event.getY();

			double minMouseX = backToPuzzleText.getLayoutX();
			double maxMouseX = resultsPane.getLayoutX() + resultsPane.getWidth();
			double minMouseY = resultsPane.getLayoutY() + 19;
			double maxMouseY = resultsPane.getLayoutY() + 36;

			if (mouseX >= minMouseX && mouseX <= maxMouseX && mouseY >= minMouseY && mouseY <= maxMouseY) {
				backToPuzzleBox.setMouseTransparent(false);
			} else {
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

		shareButton.setOnMouseEntered(e -> {
			shareButton.setCursor(Cursor.HAND);
		});

		shareButton.setOnMouseExited(e -> {
			shareButton.setCursor(Cursor.DEFAULT);
		});

		shareButton.setOnAction(e -> {
			Rectangle copiedRect = new Rectangle(204.54, 42);
			copiedRect.setArcWidth(10);
			copiedRect.setArcHeight(10);
			copiedRect.setFill(styleManager.colorPopupBackground());

			Text copiedText = new Text("Copied results to clipboard");
			copiedText.setFill(styleManager.colorPopupText());
			copiedText.setFont(styleManager.getFont("franklin-normal", 600, 16));

			StackPane copiedPane = new StackPane(copiedRect, copiedText);
			copiedPane.getStyleClass().add("popup-pane");
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

		updateResultsPaneStyle();

		if (darkModeToggle.isDarkMode()) {
			xPath.setFill(styleManager.colorText());
			titleLabel.setTextFill(styleManager.colorText());
			connectionsLabel.setTextFill(styleManager.colorText());
			nextPuzzleInLabel.setTextFill(styleManager.colorText());
			timerLabel.setTextFill(styleManager.colorText());
			backToPuzzleText.setFill(styleManager.colorText());
		} else {
			xPath.setFill(Color.BLACK);
			titleLabel.setTextFill(Color.BLACK);
			connectionsLabel.setTextFill(Color.BLACK);
			nextPuzzleInLabel.setTextFill(Color.BLACK);
			timerLabel.setTextFill(Color.BLACK);
			backToPuzzleText.setFill(Color.BLACK);
		}

		resultsAppearTransition.play();
	}

	private void animateIncorrectGuess(int matchCount) {
		disableButtons();
		disableRectangles();
		sequentialIncorrectTrans = new SequentialTransition();
		ParallelTransition jumpTransition = createJumpTransition();
		PauseTransition pauseAfterJump = new PauseTransition(Duration.millis(500));

		sequentialIncorrectTrans.getChildren().addAll(jumpTransition, pauseAfterJump);

		sequentialIncorrectTrans.setOnFinished(event -> {
			if (matchCount == 3 && incorrectGuessCount < 4) {
				Rectangle oneAwayRect = new Rectangle(96.09, 42);
				oneAwayRect.setArcWidth(10);
				oneAwayRect.setArcHeight(10);
				oneAwayRect.setFill(styleManager.colorPopupBackground());

				Text oneAwayText = new Text("One away...");
				oneAwayText.setFill(styleManager.colorPopupText());
				oneAwayText.setFont(styleManager.getFont("franklin-normal", 600, 16));

				StackPane oneAwayPane = new StackPane(oneAwayRect, oneAwayText);
				oneAwayPane.getStyleClass().add("popup-pane");
				mainStackPane.getChildren().add(oneAwayPane);

				PauseTransition displayOneAway = new PauseTransition(Duration.millis(1000));
				displayOneAway.setOnFinished(e -> mainStackPane.getChildren().remove(oneAwayPane));
				displayOneAway.play();
			}
			if (gameLost) {
				Rectangle nextTimeRect = new Rectangle(88.13, 42);
				nextTimeRect.setArcWidth(10);
				nextTimeRect.setArcHeight(10);
				nextTimeRect.setFill(styleManager.colorPopupBackground());

				Text nextTimeText = new Text("Next Time");
				nextTimeText.setFill(styleManager.colorPopupText());
				nextTimeText.setFont(styleManager.getFont("franklin-normal", 600, 16));

				StackPane nextTimePane = new StackPane(nextTimeRect, nextTimeText);
				nextTimePane.getStyleClass().add("popup-pane");
				mainStackPane.getChildren().add(nextTimePane);

				PauseTransition displayNextTime = new PauseTransition(Duration.millis(1000));
				displayNextTime.setOnFinished(e -> {
					mainStackPane.getChildren().remove(nextTimePane);
				});

				displayNextTime.play();
			}
			ParallelTransition shakeTransition = createShakeTransition();
			shakeTransition.setOnFinished(shakeEvent -> {
				for (Node node : gridPane.getChildren()) {
					if (node instanceof GameTileWord) {
						GameTileWord tileWord = (GameTileWord) node;
						if (tileWord.getIncorrectStatus()) {
							tileWord.setIncorrectStatus(false);
						}
					}
				}
				PauseTransition deselectDelay = new PauseTransition(Duration.millis(500));
				deselectDelay.setOnFinished(deselectEvent -> {
					deselectButton.fire();
					gameDeselect();
					PauseTransition removeCircleDelay = new PauseTransition(Duration.millis(500));
					removeCircleDelay.setOnFinished(removeCircleEvent -> {
						mistakesPane.removeCircle();
						enableButtons();
						enableRectangles();
						if (gameLost) {
							PauseTransition delay = new PauseTransition(Duration.millis(500));
							delay.setOnFinished(e -> {
								animateAutoSolve();
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
		sequentialIncorrectTrans.play();
	}

	private ParallelTransition createShakeTransition() {
		ParallelTransition shakeTransition = new ParallelTransition();
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;

				if (tileWord.getSelectedStatus()) {
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

		sequentialCorrectTrans = new SequentialTransition();
		ParallelTransition jumpTransition = createJumpTransition();
		SequentialTransition swapAndAnswerTileSequence = animPane.getSequenceCorrectAnswer();
		PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
		sequentialCorrectTrans.getChildren().addAll(jumpTransition, pauseTransition, swapAndAnswerTileSequence);

		if (!wonGame) {
			sequentialCorrectTrans.setOnFinished(event -> {
				enableButtons();
				enableRectangles();
			});
		} else {
			PauseTransition endPauseTransition = new PauseTransition(Duration.millis(1250));
			sequentialCorrectTrans.getChildren().add(endPauseTransition);
			sequentialCorrectTrans.setOnFinished(event -> {
				if (wonGame) {
					disableGameBoard();
					gameDeselect();
					showResultsPane((Stage) wholeGameStackPane.getScene().getWindow());
				} else {
					enableButtons();
					enableRectangles();
				}
			});
		}

		sequentialCorrectTrans.play();
	}

	private void animateAutoSolvePart(List<GameAnswerColor> remainingAnswerCategories) {
		if (currentRow < ROWS) {
			GameAnswerColor currentColorAnswer = remainingAnswerCategories.remove(0);
			Set<String> wordStringSet = new HashSet<>(Arrays.asList(currentColorAnswer.getWords()));
			gameDeselect();

			for (Node node : gridPane.getChildren()) {
				if (node instanceof GameTileWord) {
					GameTileWord tileWord = (GameTileWord) node;
					String tileWordText = tileWord.getWord().getText().toLowerCase();
					if (wordStringSet.contains(tileWordText)) {
						tileWord.setSelectedStatus(true);
					}
				}
			}

			SequentialTransition sequentialTransition = new SequentialTransition();
			PauseTransition pauseBeforeSwapTransition = new PauseTransition(Duration.millis(350));
			SequentialTransition swapAndAnswerTileSequence = animPane.getSequenceCorrectAnswer();
			PauseTransition pauseAfterSwapTransition = new PauseTransition(Duration.millis(350));
			sequentialTransition.getChildren().addAll(pauseBeforeSwapTransition, swapAndAnswerTileSequence,
					pauseAfterSwapTransition);

			pauseAfterSwapTransition.setOnFinished(event -> {
				animateAutoSolvePart(remainingAnswerCategories);
			});

			sequentialTransition.play();
		} else {
			PauseTransition pauseBeforeResultsTransition = new PauseTransition(Duration.millis(1000));
			pauseBeforeResultsTransition.setOnFinished(event -> {
				showResultsPane((Stage) wholeGameStackPane.getScene().getWindow());
				setWordTileStyleChangeable(true);
			});
			pauseBeforeResultsTransition.play();
		}
	}

	private void animateAutoSolve() {
		List<DifficultyColor> unansweredColor = new ArrayList<>(DifficultyColor.getAllColors());

		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileAnswer) {
				GameTileAnswer tileAnswer = (GameTileAnswer) node;
				unansweredColor.remove(tileAnswer.getGameAnswerColor().getColor());
			}
		}

		if (unansweredColor.size() > 0) {
			// Sort in order of difficulty (YELLOW, GREEN, BLUE, PURPLE);
			Collections.sort(unansweredColor);

			List<GameAnswerColor> remainingAnswerCategories = new ArrayList<>();
			for (DifficultyColor color : unansweredColor) {
				GameAnswerColor colorAnswer = currentGame.getAnswerForColor(color);
				remainingAnswerCategories.add(colorAnswer);
			}

			setWordTileStyleChangeable(false);
			animateAutoSolvePart(remainingAnswerCategories);
		}
	}

	private ParallelTransition createJumpTransition() {
		ParallelTransition jumpTransition = new ParallelTransition();
		int delay = 0;
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;

				if (tileWord.getSelectedStatus()) {
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
		submitButton.setStyle(styleManager.buttonStyle());
	}

	private void enableButtons() {
		shuffleButton.setDisable(false);
		if (selectedCount > 0) {
			deselectButton.setDisable(false);
		}
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

	public void setWordTileStyleChangeable(boolean changeable) {
		for (Node node : gridPane.getChildren()) {
			if (node instanceof GameTileWord) {
				GameTileWord tileWord = (GameTileWord) node;
				tileWord.setStyleChangeable(changeable);
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

	public DarkModeToggle getDarkModeToggle() {
		return darkModeToggle;
	}

	public static void main(String[] args) {
		launch(args);
	}
}