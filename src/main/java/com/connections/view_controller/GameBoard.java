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
	private SVGPath achievementsIconSVG;
	private SVGPath leaderBoardIconSVG;
	private VBox wholeGameVbox;
	private Text topText;
	private Pane achievementsSVGPane;
	private Pane leaderSVGPane;
	private SequentialTransition sequentialIncorrectTrans;
	private SequentialTransition sequentialCorrectTrans;

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

		achievementsSVGPane.setOnMouseEntered(event -> {
			achievementsSVGPane.setCursor(Cursor.HAND);
		});

		achievementsSVGPane.setOnMouseExited(event -> {
			achievementsSVGPane.setCursor(Cursor.DEFAULT);
		});

		leaderSVGPane.setOnMouseEntered(event -> {
			leaderSVGPane.setCursor(Cursor.HAND);
		});

		leaderSVGPane.setOnMouseExited(event -> {
			leaderSVGPane.setCursor(Cursor.DEFAULT);
		});

		achievementsSVGPane.setOnMouseClicked(event -> {
			if (achievementsVisible) {
				StackPane achievementsPane = (StackPane) wholeGameStackPane.getParent().lookup(".achievements-pane");
				if (achievementsPane != null) {
					((Pane) wholeGameStackPane.getParent()).getChildren().remove(achievementsPane);
				}
				animPane.setAllowChangeVisibility(true);
				animPane.setVisible(true);
			} else {
				// Check if the results pane is visible and wholeGameStackPane has a parent,
				// also do leaderboard check later
				if (wholeGameStackPane.getParent() != null) {
					StackPane resultsPane = (StackPane) wholeGameStackPane.getParent().lookup(".results-pane");
					if (resultsPane != null) {
						((Pane) wholeGameStackPane.getParent()).getChildren().remove(resultsPane);
					}
				}
				showachievementsPane((Stage) wholeGameStackPane.getScene().getWindow());
				animPane.setAllowChangeVisibility(false);
				animPane.setVisible(false);
			}

			achievementsVisible = !achievementsVisible;
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
		wholeGameStackPane.setStyle(styleManager.getWholeGame());
		achievementsIconSVG.setStroke(styleManager.colorText());
		achievementsIconSVG.setFill(styleManager.getSVGFill());
		leaderBoardIconSVG.setStroke(styleManager.colorText());
		leaderBoardIconSVG.setFill(styleManager.getSVGFill());
		shuffleButton.setStyle(styleManager.getButton());
		deselectButton.setStyle(styleManager.getButton());

		if (this.getSelectedCount() == GameBoard.MAX_SELECTED && !submitButton.isDisabled()) {
			submitButton.setStyle(styleManager.getSubmitButton());
		} else {
			submitButton.setStyle(styleManager.getButton());
		}

		BorderPane borderPane = (BorderPane) wholeGameStackPane.getChildren().get(0);
		VBox vbox = (VBox) borderPane.getChildren().get(1);
		HBox buttonBox = (HBox) vbox.getChildren().get(2);
		if (buttonBox.getChildren().size() > 0 && buttonBox.getChildren().get(0) instanceof Button) {
			Button viewResultsButton = (Button) buttonBox.getChildren().get(0);
			viewResultsButton.setStyle(styleManager.getButton());
		}

		for (Node node : wholeGameStackPane.getChildren()) {
			if (node instanceof BorderPane) {
				BorderPane gameBorderPane = (BorderPane) node;
				VBox gameVbox = (VBox) gameBorderPane.getCenter();
				Text topText = (Text) gameVbox.getChildren().get(0);

				if (gameVbox.getChildren().size() > 2) {
					HBox bottomBox = (HBox) gameVbox.getChildren().get(2);
					if (bottomBox.getChildren().size() > 0 && bottomBox.getChildren().get(0) instanceof Text) {
						Text bottomText = (Text) bottomBox.getChildren().get(0);
						bottomText.setFill(styleManager.colorText());
					}
				}
				topText.setFill(styleManager.colorText());
			}
		}
		updateAchievementsPaneStyle();
		updateResultsPaneStyle();
		updatePopupStyle();
	}

	private void updateAchievementsPaneStyle() {
		if (wholeGameStackPane.getParent() != null) {
			StackPane achievementsPane = (StackPane) wholeGameStackPane.getParent().lookup(".achievements-pane");
			if (achievementsPane != null) {
				GridPane achievementsGrid = (GridPane) achievementsPane.lookup(".achievements-grid");
				if (achievementsGrid != null) {
					for (Node node : achievementsGrid.getChildren()) {
						if (node instanceof StackPane) {
							StackPane achievementPane = (StackPane) node;
							if (achievementPane.getChildren().size() == 2) {
								Node firstChild = achievementPane.getChildren().get(0);
								Node secondChild = achievementPane.getChildren().get(1);
								if (firstChild instanceof Rectangle && secondChild instanceof Label) {
									Rectangle rect = (Rectangle) firstChild;
									Label label = (Label) secondChild;
									rect.setFill(styleManager.colorDefaultRectangle());
									label.setTextFill(styleManager.colorText());
								}
							}
						}
					}
				}

				HBox backToPuzzleBox = (HBox) achievementsPane.lookup(".back-to-puzzle-box");
				if (backToPuzzleBox != null) {
					Text backToPuzzleText = (Text) backToPuzzleBox.getChildren().get(0);
					backToPuzzleText.setFill(styleManager.colorText());
				}

				SVGPath xPath = (SVGPath) achievementsPane.lookup(".x-path");
				if (xPath != null) {
					xPath.setFill(styleManager.colorText());
				}

				achievementsPane.setStyle(styleManager.overlayPane());
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
						shareButton.setStyle(styleManager.getResultsPaneShareButton());
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

				resultsPane.setStyle(styleManager.overlayPane());
			}
			if (resultsPane != null) {
				updatePopupStyle();
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

		animPane = new AnimationPane(this);
		animPane.setVisible(false);
		initAnimPane();
		mainStackPane = new StackPane(gridPane, animPane);

		initGameData();

		topText = new Text("Create four groups of four!");
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

		// Initialize the dark mode toggle
		initDarkModeToggle();

		// Create an HBox to hold the dark mode toggle, achievements, and leader board
		achievementsIconSVG = new SVGPath();
		achievementsIconSVG.setContent(
				"M12 14V17M12 14C9.58104 14 7.56329 12.2822 7.10002 10M12 14C14.419 14 16.4367 12.2822 16.9 10M17 5H19.75C19.9823 5 20.0985 5 20.1951 5.01921C20.5918 5.09812 20.9019 5.40822 20.9808 5.80491C21 5.90151 21 6.01767 21 6.25C21 6.94698 21 7.29547 20.9424 7.58527C20.7056 8.77534 19.7753 9.70564 18.5853 9.94236C18.2955 10 17.947 10 17.25 10H17H16.9M7 5H4.25C4.01767 5 3.90151 5 3.80491 5.01921C3.40822 5.09812 3.09812 5.40822 3.01921 5.80491C3 5.90151 3 6.01767 3 6.25C3 6.94698 3 7.29547 3.05764 7.58527C3.29436 8.77534 4.22466 9.70564 5.41473 9.94236C5.70453 10 6.05302 10 6.75 10H7H7.10002M12 17C12.93 17 13.395 17 13.7765 17.1022C14.8117 17.3796 15.6204 18.1883 15.8978 19.2235C16 19.605 16 20.07 16 21H8C8 20.07 8 19.605 8.10222 19.2235C8.37962 18.1883 9.18827 17.3796 10.2235 17.1022C10.605 17 11.07 17 12 17ZM7.10002 10C7.03443 9.67689 7 9.34247 7 9V4.57143C7 4.03831 7 3.77176 7.09903 3.56612C7.19732 3.36201 7.36201 3.19732 7.56612 3.09903C7.77176 3 8.03831 3 8.57143 3H15.4286C15.9617 3 16.2282 3 16.4339 3.09903C16.638 3.19732 16.8027 3.36201 16.901 3.56612C17 3.77176 17 4.03831 17 4.57143V9C17 9.34247 16.9656 9.67689 16.9 10");
		achievementsIconSVG.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
		achievementsIconSVG.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
		achievementsIconSVG.setStrokeWidth(2);
		achievementsIconSVG.setStroke(Color.BLACK);
		achievementsIconSVG.setFill(Color.WHITE);
		achievementsIconSVG.setScaleX(1.58571428571);
		achievementsIconSVG.setScaleY(1.58571428571);
		achievementsIconSVG.setTranslateX(3);
		achievementsIconSVG.setTranslateY(7);

		achievementsSVGPane = new Pane(achievementsIconSVG);
		achievementsSVGPane.setPrefWidth(30);
		achievementsSVGPane.prefHeightProperty().bind(achievementsSVGPane.widthProperty());

		leaderBoardIconSVG = new SVGPath();
		leaderBoardIconSVG.setContent(
				"M15 21H9V12.6C9 12.2686 9.26863 12 9.6 12H14.4C14.7314 12 15 12.2686 15 12.6V21Z M20.4 21H15V18.1C15 17.7686 15.2686 17.5 15.6 17.5H20.4C20.7314 17.5 21 17.7686 21 18.1V20.4C21 20.7314 20.7314 21 20.4 21Z M9 21V16.1C9 15.7686 8.73137 15.5 8.4 15.5H3.6C3.26863 15.5 3 15.7686 3 16.1V20.4C3 20.7314 3.26863 21 3.6 21H9Z M10.8056 5.11325L11.7147 3.1856C11.8314 2.93813 12.1686 2.93813 12.2853 3.1856L13.1944 5.11325L15.2275 5.42427C15.4884 5.46418 15.5923 5.79977 15.4035 5.99229L13.9326 7.4917L14.2797 9.60999C14.3243 9.88202 14.0515 10.0895 13.8181 9.96099L12 8.96031L10.1819 9.96099C9.94851 10.0895 9.67568 9.88202 9.72026 9.60999L10.0674 7.4917L8.59651 5.99229C8.40766 5.79977 8.51163 5.46418 8.77248 5.42427L10.8056 5.11325Z");
		leaderBoardIconSVG.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
		leaderBoardIconSVG.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
		leaderBoardIconSVG.setStrokeWidth(2);
		leaderBoardIconSVG.setStroke(Color.BLACK);
		leaderBoardIconSVG.setFill(Color.WHITE);
		leaderBoardIconSVG.setScaleX(1.58571428571);
		leaderBoardIconSVG.setScaleY(1.58571428571);
		leaderBoardIconSVG.setTranslateX(3);
		leaderBoardIconSVG.setTranslateY(7);

		leaderSVGPane = new Pane(leaderBoardIconSVG);
		leaderSVGPane.setPrefWidth(30);
		leaderSVGPane.prefHeightProperty().bind(leaderSVGPane.widthProperty());

		HBox cornerButtonBox = new HBox(10, leaderSVGPane, achievementsSVGPane, darkModeToggle);
		cornerButtonBox.setStyle("-fx-alignment: center-right;");

		wholeGameVbox = new VBox(24, topText, mainStackPane, bottomBox, buttonBox);
		wholeGameVbox.setAlignment(Pos.CENTER);

		BorderPane mainContentPane = new BorderPane();
		mainContentPane.setPadding(new Insets(10));
		mainContentPane.setTop(cornerButtonBox);
		mainContentPane.setCenter(wholeGameVbox);

		StackPane wholeGameStackPane = new StackPane(mainContentPane);
		wholeGameStackPane.setStyle(styleManager.getWholeGame());
		this.wholeGameStackPane = wholeGameStackPane;

		initListeners();

		Scene scene = new Scene(wholeGameStackPane, STAGE_WIDTH, STAGE_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connections");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private void showachievementsPane(Stage stage) {
		GridPane achievementsGrid = new GridPane();
		achievementsGrid.getStyleClass().add("achievements-grid");
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

		VBox achievementsLayout = new VBox(0);
		achievementsLayout.setAlignment(Pos.CENTER);

		achievementsLayout.getChildren().addAll(stackPane);

		StackPane achievementsPane = new StackPane(achievementsLayout);

		achievementsPane.setStyle(styleManager.overlayPane());

		achievementsPane.setPrefSize(667, 630);
		achievementsPane.setMaxWidth(667);
		achievementsPane.setMaxHeight(630);

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

		achievementsPane.getChildren().add(backToPuzzleBox);
		backToPuzzleBox.setStyle("-fx-alignment: top-right;");
		StackPane.setMargin(backToPuzzleBox, new Insets(19.2, 19.2, 0, 0));
		achievementsPane.getStyleClass().add("achievements-pane");

		StackPane overlayPane = new StackPane(wholeGameStackPane, achievementsPane);
		overlayPane.setAlignment(Pos.CENTER);

		TranslateTransition achievementsPaneMoveUp = new TranslateTransition(Duration.millis(150), achievementsPane);
		achievementsPane.setTranslateX(0);
		achievementsPane.setTranslateY(45);
		achievementsPaneMoveUp.setToX(0);
		achievementsPaneMoveUp.setToY(0);

		FadeTransition achievementsPaneFadeIn = new FadeTransition(Duration.millis(150), achievementsPane);
		achievementsPaneFadeIn.setFromValue(0);
		achievementsPaneFadeIn.setToValue(1);

		ParallelTransition achievementsAppearTransition = new ParallelTransition(achievementsPaneMoveUp,
				achievementsPaneFadeIn);

		overlayPane.setOnMouseMoved(event -> {
			double mouseX = event.getX();
			double mouseY = event.getY();

			double minMouseX = backToPuzzleText.getLayoutX();
			double maxMouseX = achievementsPane.getLayoutX() + achievementsPane.getWidth();
			double minMouseY = achievementsPane.getLayoutY() + 19;
			double maxMouseY = achievementsPane.getLayoutY() + 36;

			if (mouseX >= minMouseX && mouseX <= maxMouseX && mouseY >= minMouseY && mouseY <= maxMouseY) {
				backToPuzzleBox.setMouseTransparent(false);
			} else {
				backToPuzzleBox.setMouseTransparent(true);
			}
		});

		Scene scene = new Scene(overlayPane, STAGE_WIDTH, STAGE_HEIGHT);
		stage.setScene(scene);

		backToPuzzleText.setOnMouseClicked(e -> {
			overlayPane.getChildren().remove(achievementsPane);
			achievementsVisible = !achievementsVisible;
		});

		xPath.setOnMouseClicked(e -> {
			overlayPane.getChildren().remove(achievementsPane);
			achievementsVisible = !achievementsVisible;
		});

		updateAchievementsPaneStyle();

		if (darkModeToggle.isDarkMode()) {
			xPath.setFill(styleManager.colorText());
			backToPuzzleText.setFill(styleManager.colorText());
		} else {
			xPath.setFill(Color.BLACK);
			backToPuzzleText.setFill(Color.BLACK);
		}

		achievementsAppearTransition.play();
	}

	private Button createButton(String text, double width) {
		Button button = new Button(text);
		button.setStyle(styleManager.getButton());
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

		HBox bottomBox = (HBox) vbox.getChildren().get(2);
		vbox.getChildren().remove(bottomBox);

		Button viewResultsButton = new Button("View Results");
		viewResultsButton.setStyle(styleManager.getButton());

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
		if (achievementsVisible) {
			StackPane achievementsPane = (StackPane) wholeGameStackPane.getParent().lookup(".achievements-pane");
			if (achievementsPane != null) {
				((Pane) wholeGameStackPane.getParent()).getChildren().remove(achievementsPane);
			}
			achievementsVisible = false;
		}

		// add leaderboard check later here

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
		shareButton.setStyle(styleManager.getResultsPaneShareButton());

		VBox.setMargin(shareButton, new Insets(21, 0, 20, 0));

		shareButton.setTranslateY(4);

		resultsLayout.getChildren().addAll(titleLabel, connectionsLabel, gridPane, timerBox, shareButton);

		StackPane resultsPane = new StackPane(resultsLayout);

		resultsPane.setStyle(styleManager.overlayPane());

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
						removeCircle(circlePane);
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
		submitButton.setStyle(styleManager.getButton());
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