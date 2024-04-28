package com.connections.view_controller;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.connections.model.DifficultyColor;
import com.connections.model.GameAnswerColor;
import com.connections.model.GameSaveState;
import com.connections.model.PlayedGameInfo;
import com.connections.model.PlayedGameInfoClassic;
import com.connections.model.PlayedGameInfoTimed;
import com.connections.model.Word;
import com.connections.web.WebSessionContext;
import com.connections.web.WebUser;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The GameSession class represents a game session within the Connections
 * application. It manages the game state, user interactions, and various UI
 * components related to the game.
 */
public class GameSession extends StackPane implements Modular {
	public static final int STAGE_WIDTH = 800;
	public static final int STAGE_HEIGHT = 750;
	
	private static final int POPUP_DEFAULT_DURATION_MS = 3000;
	private static final int MENU_PANE_HEIGHT = NotificationPane.HEIGHT + 10;

	public static final int TIME_TRIAL_DURATION_SEC = 60;

	private GameSessionContext gameSessionContext;
	private OptionSelectOverlayPane gameTypeOptionSelector;
	private CountDownOverlayPane timeTrialCountDownOverlay;
	private TimerPane timeTrialTimerPane;
	private BorderPane timeTrialTimerLayout;
	private Text mainHeaderText;
	private BorderPane organizationPane;
	private StackPane menuPane;
	private VBox gameContentPane;
	private StackPane tileGridStackPane;
	private CircleRowPane hintsPane;
	private CircleRowPane mistakesPane;
	private HBox gameButtonRowPane;
	private BorderPane menuButtonRowContainerPane;
	private HBox menuButtonRowRightPane;
	private HBox menuButtonRowLeftPane;
	private TileGridAchievement tileGridAchievement;
	private ErrorOverlayPane errorUserInGamePane;
	private MidnightChecker midnightChecker;

	private boolean wonGame;
	private boolean gameActive;

	private TileGridWord tileGridWord;
	private TileGridWordAnimationOverlay tileGridWordAnimationPane;

	private DarkModeToggle darkModeToggleMenuButton;
	private HintMenuButton hintMenuButton;
	private AchievementsMenuButton achievementsMenuButton;
	private LeaderboardMenuButton leaderboardMenuButton;
	private ProfileMenuButton profileMenuButton;
	private BackMenuButton backMenuButton;

	private CircularButton gameSubmitButton;
	private CircularButton gameDeselectButton;
	private CircularButton gameShuffleButton;
	private CircularButton gameViewResultsButton;

	// Keep reference to results pane to avoid re-loading it each time
	private ResultsPane resultsPane;
	private PopupWrapperPane popupPane;

	private int currentPuzzleNumber;
	private boolean gameAlreadyFinished;
	private boolean ranOutOfTime;
	private boolean loadedFromSaveState;
	private boolean blockedStoringSaveState;
	private boolean hintsCannotBeUsedRightNow;
	private GameType gameType;

	private boolean timeKeepingActive;
	private ZonedDateTime gameStartDateTime;
	private ZonedDateTime gameEndDateTime;

	private EventHandler<ActionEvent> onGoBack;
	private EventHandler<ActionEvent> onMidnight;

	// will be null if the game was not finished yet
	private PlayedGameInfo playedGameInfo;
	private GameSaveState loadedSaveState;

	/**
	 * Represents the different types of game modes available.
	 */
	public enum GameType {
		CLASSIC, TIME_TRIAL, NONE
	}

	/**
	 * Constructs a new GameSession with the specified GameSessionContext.
	 *
	 * @param gameSessionContext the GameSessionContext used by the game session
	 */
	public GameSession(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		initAssets();
		initListeners();
		fastForwardAutoLoad();
	}

	/**
	 * Initializes the assets and components of the game session.
	 */
	private void initAssets() {
		getChildren().clear();
		wonGame = false;
		gameActive = false;
		ranOutOfTime = false;

		currentPuzzleNumber = gameSessionContext.getGameData().getPuzzleNumber();

		setPrefSize(STAGE_WIDTH, STAGE_HEIGHT);

		darkModeToggleMenuButton = new DarkModeToggle(gameSessionContext);

		tileGridWord = new TileGridWord(gameSessionContext);
		tileGridWord.initTileWords();

		tileGridWordAnimationPane = new TileGridWordAnimationOverlay(tileGridWord);

		tileGridStackPane = new StackPane(tileGridWord, tileGridWordAnimationPane);

		tileGridAchievement = new TileGridAchievement(gameSessionContext);

		backMenuButton = new BackMenuButton(gameSessionContext);
		backMenuButton.setStyle("-fx-alignment: center-left;");

		hintMenuButton = new HintMenuButton(gameSessionContext);

		achievementsMenuButton = new AchievementsMenuButton(gameSessionContext);

		leaderboardMenuButton = new LeaderboardMenuButton(gameSessionContext);

		profileMenuButton = new ProfileMenuButton(gameSessionContext);

		mainHeaderText = new Text("Create four groups of four!");
		mainHeaderText.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 500, 18));

		hintsPane = new CircleRowPane("Hints remaining:", gameSessionContext);

		mistakesPane = new CircleRowPane("Mistakes remaining:", gameSessionContext);

		gameShuffleButton = new CircularButton("Shuffle", 88, gameSessionContext, false);

		gameDeselectButton = new CircularButton("Deselect all", 120, gameSessionContext, false);

		gameSubmitButton = new CircularButton("Submit", 88, gameSessionContext, true);

		gameViewResultsButton = new CircularButton("View Results", 160, gameSessionContext, false);

		gameButtonRowPane = new HBox(8);
		gameButtonRowPane.setAlignment(Pos.CENTER);

		menuButtonRowLeftPane = new HBox(10, backMenuButton);
		menuButtonRowLeftPane.setAlignment(Pos.CENTER);
		menuButtonRowLeftPane.setMaxHeight(DarkModeToggle.HEIGHT);

		menuButtonRowRightPane = new HBox(10, hintMenuButton, profileMenuButton, leaderboardMenuButton,
				achievementsMenuButton, darkModeToggleMenuButton);
		menuButtonRowRightPane.setAlignment(Pos.CENTER);
		menuButtonRowRightPane.setMaxHeight(DarkModeToggle.HEIGHT);

		menuButtonRowContainerPane = new BorderPane();
		menuButtonRowContainerPane.setLeft(menuButtonRowLeftPane);
		menuButtonRowContainerPane.setRight(menuButtonRowRightPane);
		BorderPane.setAlignment(menuButtonRowLeftPane, Pos.CENTER);
		BorderPane.setAlignment(menuButtonRowRightPane, Pos.CENTER);

		// exclude the hints pane for now
		gameContentPane = new VBox(24);
		gameContentPane.setAlignment(Pos.CENTER);

		menuPane = new StackPane(menuButtonRowContainerPane);
		menuPane.setPrefHeight(MENU_PANE_HEIGHT);

		organizationPane = new BorderPane();
		organizationPane.setTop(menuPane);
		organizationPane.setCenter(gameContentPane);
		organizationPane.setPrefSize(STAGE_WIDTH, STAGE_HEIGHT);
		organizationPane.setPadding(new Insets(10));

		getChildren().add(organizationPane);

		midnightChecker = new MidnightChecker();
		midnightChecker.start();

		timeTrialCountDownOverlay = new CountDownOverlayPane(gameSessionContext);
		timeTrialTimerPane = new TimerPane(gameSessionContext, TIME_TRIAL_DURATION_SEC);

		timeTrialTimerLayout = new BorderPane();
		timeTrialTimerLayout.setTop(timeTrialTimerPane);
		timeTrialTimerLayout.setPadding(new Insets(64));
		BorderPane.setAlignment(timeTrialTimerPane, Pos.CENTER);

		gameTypeOptionSelector = new OptionSelectOverlayPane(gameSessionContext);
		gameTypeOptionSelector.addButton("Classic", 68);
		gameTypeOptionSelector.addButton("Time Trial", 68);

		errorUserInGamePane = new ErrorOverlayPane(gameSessionContext);
		errorUserInGamePane.setHeaderText("Game In Progress");
		errorUserInGamePane.setBodyText(
				"You are currently playing from another browser tab or device under the same user.\nPlease wait until the game is finished and try again.");

		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();
		if(currentUser.getDarkModeStatus()) {
			darkModeToggleMenuButton.toggle();
		}
		
		getChildren().add(0, timeTrialTimerLayout);
		controlsSetNormal();
		refreshStyle();
	}

	/**
	 * Initializes the event listeners for various UI components in the game
	 * session.
	 */
	private void initListeners() {
		tileGridWord.setOnTileWordSelection(event -> {
			helperUpdateGameButtonStatus();
		});
		gameShuffleButton.setOnAction(event -> {
			tileGridWord.shuffleTileWords();
			fastForwardStoreSaveState();
		});
		gameSubmitButton.setOnAction(event -> {
			sessionSubmissionAttempt();
		});
		gameDeselectButton.setOnAction(event -> {
			tileGridWord.deselectTileWords();
		});
		
		StyleManager styleManager = gameSessionContext.getStyleManager();
		
		styleManager.setOnDarkModeChange(event -> {
			WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
			currentUser.readFromDatabase();
			currentUser.setDarkModeStatus(styleManager.isDarkMode());
			currentUser.writeToDatabase();
			refreshStyle();
		});
		
		gameViewResultsButton.setOnAction(event -> {
			screenDisplayResults();
		});
		backMenuButton.setOnMouseClicked(event -> {
			if (onGoBack != null) {
				// Disable buttons only if there IS a onGoBack set.
				helperSetAllInteractablesDisabled(true);
				onGoBack.handle(new ActionEvent(this, null));
			}
		});
		hintMenuButton.setOnMouseClicked(event -> {
			sessionHintUsed();
		});
		achievementsMenuButton.setOnMouseClicked(event -> {
			screenDisplayAchievements();
		});
		leaderboardMenuButton.setOnMouseClicked(event -> {
			screenDisplayLeaderboard();
		});
		profileMenuButton.setOnMouseClicked(event -> {
			screenDisplayProfile();
		});
		gameSessionContext.getWebContext().getWebAPI().addInstanceCloseListener(() -> {
			close();
		});
		gameTypeOptionSelector.setOnDisappear(event -> {
			organizationPane.setEffect(null);
			switch (gameTypeOptionSelector.getOptionSelected()) {
			case "Classic":
				gameType = GameType.CLASSIC;
				sessionBeginNewGame();
				break;
			case "Time Trial":
				gameType = GameType.TIME_TRIAL;
				helperTimeTrialStartCountdown();
				break;
			default:
				gameType = GameType.NONE;
			}
		});
		timeTrialTimerPane.setOnSecondPassedBy(event -> {
			if (gameActive && gameType == GameType.TIME_TRIAL) {
				fastForwardStoreSaveState();
			}
		});
		timeTrialTimerPane.setOnFinishedTimer(event -> {
			if (gameType == GameType.TIME_TRIAL) {
				sessionLostTimeTrial();
			}
		});
		midnightChecker.setOnMidnight(event -> {
			if (onMidnight != null) {
				// Disable buttons only if there IS a onMidnight set.
				helperSetAllInteractablesDisabled(true);
				onMidnight.handle(new ActionEvent(this, null));
			}
		});
	}

	/**
	 * Sets the game controls to the normal state.
	 */
	private void controlsSetNormal() {
		gameButtonRowPane.getChildren().clear();
		gameButtonRowPane.getChildren().addAll(gameShuffleButton, gameDeselectButton, gameSubmitButton);
		gameContentPane.getChildren().clear();
		gameContentPane.getChildren().addAll(mainHeaderText, tileGridStackPane, mistakesPane, hintsPane,
				gameButtonRowPane);
	}

	/**
	 * Sets the game controls to display the "View Results" button only.
	 */
	private void controlsSetViewResultsOnly() {
		gameButtonRowPane.getChildren().clear();
		gameButtonRowPane.getChildren().add(gameViewResultsButton);
		gameContentPane.getChildren().clear();
		gameContentPane.getChildren().addAll(mainHeaderText, tileGridStackPane, gameButtonRowPane);
	}

	/**
	 * Displays the results screen.
	 */
	private void screenDisplayResults() {
		if (!gameActive) {
			if (resultsPane == null) {
				resultsPane = new ResultsPane(gameSessionContext, playedGameInfo);
			}

			helperPopupScreen(resultsPane, "");
		}
	}

	/**
	 * Displays the achievements screen.
	 */
	private void screenDisplayAchievements() {
		tileGridAchievement.animateCompletion();
		helperPopupScreen(tileGridAchievement, "Achievements:");
	}

	/**
	 * Displays the leaderboard screen.
	 */
	private void screenDisplayLeaderboard() {
		new BorderPane();
		helperPopupScreen(new LeaderboardPane(gameSessionContext), "Leaderboard:");
	}

	/**
	 * Displays the profile screen.
	 */
	private void screenDisplayProfile() {
		helperPopupScreen(new ProfilePane(gameSessionContext), "Profile:");
	}

	/**
	 * Displays a popup screen with the specified pane and title.
	 *
	 * @param pane  the pane to be displayed in the popup screen
	 * @param title the title of the popup screen
	 */
	private void helperPopupScreen(Pane pane, String title) {
		if (popupPane == null) {
			popupPane = new PopupWrapperPane(gameSessionContext, pane, title);
			popupPane.setOnGoBackPressed(event -> {
				getChildren().remove(popupPane);
			});
		} else {
			popupPane.setChild(pane);
			popupPane.setTitle(title);
		}

		if (!getChildren().contains(popupPane)) {
			getChildren().add(popupPane);
		}

		popupPane.popup();
	}

	/**
	 * Automatically loads the game session based on the user's save state or checks
	 * if the game has already finished.
	 */
	private void fastForwardAutoLoad() {
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();
		if (currentUser.isCurrentlyInGame()) {
			fastForwardUserCurrentlyIngame();
		} else if (currentUser.hasLatestSaveState()) {
			fastForwardLoadSaveState();
		} else {
			fastForwardCheckGameFinishedAlready();
		}
	}

	/**
	 * Displays an error message indicating that the user is currently in-game.
	 */
	private void fastForwardUserCurrentlyIngame() {
		helperSetAllInteractablesDisabled(true);
		displayPaneWithGaussianBlur(errorUserInGamePane);
		errorUserInGamePane.appear();
	}

	/**
	 * Loads the game session from the user's save state.
	 */
	private void fastForwardLoadSaveState() {
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();

		if (currentUser.hasLatestSaveState() && currentUser.getLatestGameSaveState() != null) {
			loadedSaveState = currentUser.getLatestGameSaveState();
			int puzzleNumberInSave = loadedSaveState.getPuzzleNumber();

			// This occurs when the daily puzzle number has changed (and the previous save
			// state is on some previous puzzle number).
			if (puzzleNumberInSave != currentPuzzleNumber) {
				fastForwardCheckGameFinishedAlready();
				return;
			}

			hintsPane.setNumCircles(loadedSaveState.getHintsLeft());
			mistakesPane.setNumCircles(loadedSaveState.getMistakesLeft());
			tileGridWord.loadFromSaveState(loadedSaveState);
			gameType = loadedSaveState.getGameType();

			helperSetAllInteractablesDisabled(false);

			gameActive = true;
			wonGame = false;
			ranOutOfTime = false;
			gameAlreadyFinished = false;
			loadedFromSaveState = true;

			java.time.Duration previousGameDuration = java.time.Duration.between(loadedSaveState.getGameStartTime(),
					loadedSaveState.getSaveStateCreationTime());

			ZonedDateTime newStartTime = ZonedDateTime.now().minus(previousGameDuration);

			helperTimeKeepingStart(newStartTime);
			helperSetUserInGameStatus(true);
		}
	}

	/**
	 * Stores the current game state as the user's save state.
	 */
	private void fastForwardStoreSaveState() {
		if (gameActive && !gameAlreadyFinished && !blockedStoringSaveState) {
			WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();

			GameSaveState gameSaveState = new GameSaveState(tileGridWord, hintsPane, mistakesPane, gameSessionContext,
					!gameActive, gameType, gameStartDateTime);

			currentUser.readFromDatabase();
			currentUser.setLatestGameSaveState(gameSaveState);
			currentUser.writeToDatabase();

			/*
			 * This is to prevent edge cases where the user loads a state RIGHT before they
			 * ran out of time
			 */
			if (gameType == GameType.TIME_TRIAL && timeTrialTimerPane.getTimeLeft() <= 2) {
				blockedStoringSaveState = true;
			}
		}
	}

	/**
	 * Clears the user's save state.
	 */
	private void fastForwardClearSaveState() {
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();

		if (currentUser.hasLatestSaveState()) {
			currentUser.clearLatestGameSaveState();
			currentUser.writeToDatabase();
		}
	}

	/**
	 * Checks if the game has already finished and updates the game session
	 * accordingly.
	 */
	private void fastForwardCheckGameFinishedAlready() {
		if (!loadedFromSaveState && !gameActive) {

			WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
			currentUser.readFromDatabase();

			gameAlreadyFinished = currentUser.hasPlayedGameByPuzzleNum(currentPuzzleNumber);

			gameActive = false;

			if (gameAlreadyFinished) {
				helperSetAllInteractablesDisabled(false);
				helperSetGameInteractablesDisabled(true);

				playedGameInfo = currentUser.getPlayedGameByPuzzleNum(currentPuzzleNumber);
				gameStartDateTime = playedGameInfo.getGameStartTime();
				gameEndDateTime = playedGameInfo.getGameEndTime();
				gameType = playedGameInfo.getGameType();
				wonGame = playedGameInfo.wasWon();

				tileGridWord.loadFromPlayedGameInfo(playedGameInfo);

				if (gameType == GameType.TIME_TRIAL) {
					PlayedGameInfoTimed playedGameInfoTimed = (PlayedGameInfoTimed) playedGameInfo;
					ranOutOfTime = !playedGameInfoTimed.isCompletedBeforeTimeLimit();
				}

				screenDisplayResults();
				controlsSetViewResultsOnly();
			} else {
				helperSetAllInteractablesDisabled(true);
				helperSetUserInGameStatus(true);
				displayPaneWithGaussianBlur(gameTypeOptionSelector);
				gameTypeOptionSelector.appear();
			}
		}
	}

	/**
	 * Handles the event when a hint is used in the game session.
	 */
	private void sessionHintUsed() {
		if (!hintsCannotBeUsedRightNow && hintsPane.getNumCircles() > 0 && !tileGridWord.hintAnimationIsRunning()) {
			tileGridWord.hintAnimationShow();
			hintsPane.removeCircle();
			fastForwardStoreSaveState();
			tileGridWord.setOnHintAnimationStopped(event -> {
				if (!hintsCannotBeUsedRightNow && hintsPane.getNumCircles() > 0) {
					hintMenuButton.setDisable(false);
					hintMenuButton.refreshStyle();
				}
			});
			hintMenuButton.setDisable(true);
			hintMenuButton.refreshStyle();
		}
	}

	/**
	 * Stops the hint animation in the game session.
	 */
	private void sessionHintsAnimationStop() {
		if (tileGridWord.hintAnimationIsRunning()) {
			tileGridWord.hintAnimationStop();
		}
	}

	/**
	 * Sets the disabled state of the hints in the game session.
	 *
	 * @param disabled true to disable the hints, false to enable them
	 */
	private void sessionHintsSetDisabled(boolean disabled) {
		hintsCannotBeUsedRightNow = disabled;

		if (disabled) {
			hintMenuButton.setDisable(true);
			hintMenuButton.refreshStyle();
			sessionHintsAnimationStop();
		} else if (hintsPane.getNumCircles() > 0) {
			hintMenuButton.setDisable(false);
			hintMenuButton.refreshStyle();
		}
	}

	/**
	 * Begins a new game session.
	 */
	private void sessionBeginNewGame() {
		helperTimeKeepingStart(ZonedDateTime.now());
		gameActive = true;
		wonGame = false;
		blockedStoringSaveState = false;
		helperSetAllInteractablesDisabled(false);
	}

	/**
	 * Handles the end of the game session when the player reaches the end of the
	 * game.
	 */
	private void sessionReachedEndGame() {
		if (gameType == GameType.TIME_TRIAL && timeTrialTimerPane.isVisible()) {
			timeTrialTimerPane.disappear();
		}

		List<Set<Word>> guesses = tileGridWord.getGuesses();
		int mistakesMadeCount = mistakesPane.getMaxNumCircles() - mistakesPane.getNumCircles();
		int hintsUsedCount = hintsPane.getMaxNumCircles() - hintsPane.getNumCircles();
		int connectionsMade = tileGridWord.getCurrentSolvingRow();
		int timeLimit = TIME_TRIAL_DURATION_SEC;

		switch (gameType) {
		case CLASSIC:
			playedGameInfo = new PlayedGameInfoClassic(currentPuzzleNumber, mistakesMadeCount, hintsUsedCount,
					connectionsMade, guesses, wonGame, gameStartDateTime, gameEndDateTime);
			break;
		case TIME_TRIAL:
			playedGameInfo = new PlayedGameInfoTimed(currentPuzzleNumber, mistakesMadeCount, hintsUsedCount,
					connectionsMade, guesses, wonGame, timeLimit, !ranOutOfTime, gameStartDateTime, gameEndDateTime);
			break;
		default:
		}

		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();
		currentUser.addPlayedGame(playedGameInfo);
		currentUser.writeToDatabase();

		fastForwardClearSaveState();

		gameActive = false;

		helperSetUserInGameStatus(false);
		helperSetGameInteractablesDisabled(true);

		boolean noMistakes = (wonGame && tileGridWord.getGuesses().size() == 4);
		int timeTrialTime = (gameType == GameType.TIME_TRIAL) ? timeTrialTimerPane.getElapsedTime() : 0;
		WebSessionContext webSessionContext = gameSessionContext.getWebSessionContext();
		webSessionContext.getSession().updateUserAchievementData(gameType, noMistakes, timeTrialTime, wonGame);

		screenDisplayResults();
		controlsSetViewResultsOnly();
	}

	/**
	 * Handles a submission attempt by the player in the game session.
	 */
	private void sessionSubmissionAttempt() {
		boolean alreadyGuessed = tileGridWord.checkSelectedAlreadyGuessed();

		if (alreadyGuessed) {
			helperDisplayPopupNotifcation("Already Guessed!", 132.09, POPUP_DEFAULT_DURATION_MS);
		} else {
			tileGridWord.saveSelectedAsGuess();

			int matchCount = tileGridWord.checkNumWordsMatchSelected();
			boolean isCorrect = (matchCount == TileGridWord.MAX_SELECTED);
			boolean isOneAway = (matchCount == TileGridWord.MAX_SELECTED - 1);

			if (isCorrect) {
				SequentialTransition animation = helperCreateAnimationSubmissionCorrect();
				animation.play();
			} else {
				boolean lostGame = (mistakesPane.getNumCircles() == 1);

				SequentialTransition animation = helperCreateAnimationSubmissionIncorrect(lostGame, isOneAway);
				animation.play();
			}
		}
	}

	/**
	 * Handles the event when the player loses a time trial game session.
	 */
	private void sessionLostTimeTrial() {
		if (wonGame || gameType != GameType.TIME_TRIAL) {
			return;
		}

		ranOutOfTime = true;

		helperTimeKeepingStop();

		if (timeTrialTimerPane.isTimerActive()) {
			timeTrialTimerPane.stopTimer();
		}

		helperDisplayPopupNotifcation("Time's Up!", 88.13, POPUP_DEFAULT_DURATION_MS);

		PauseTransition autoSolveDelay = new PauseTransition(Duration.millis(5000));
		autoSolveDelay.setOnFinished(event -> {
			helperAutoSolverBegin();
		});

		autoSolveDelay.play();
		helperSetGameInteractablesDisabled(true);
	}

	/**
	 * Sets the user's in-game status.
	 *
	 * @param status true to set the user as in-game, false otherwise
	 */
	private void helperSetUserInGameStatus(boolean status) {
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		String currentInstanceID = gameSessionContext.getWebContext().getWebAPI().getInstanceID();

		currentUser.readFromDatabase();

		// If the user does not have an active instance ID (is not in a game) OR the
		// current instance ID matches the user's active instance ID.
		// This check is needed because we do not want to override an existing instance
		// ID with the current instance ID.
		if (!currentUser.isCurrentlyInGame() || currentInstanceID.equals(currentUser.getActiveInstanceID())) {
			if (status) {
				currentUser.setActiveInstanceID(currentInstanceID);
			} else {
				currentUser.clearActiveInstanceID();
			}
			currentUser.writeToDatabase();
		}
	}

	/**
	 * Displays a pane with a Gaussian blur effect.
	 *
	 * @param pane the pane to be displayed with the Gaussian blur effect
	 */
	private void displayPaneWithGaussianBlur(Pane pane) {
		if (pane == null) {
			return;
		}
		GaussianBlur blurEffect = new GaussianBlur();
		organizationPane.setEffect(blurEffect);
		getChildren().add(pane);
	}

	/**
	 * Starts the time keeping for the game session.
	 *
	 * @param startTime the start time of the game session
	 */
	private void helperTimeKeepingStart(ZonedDateTime startTime) {
		if (!timeKeepingActive) {
			timeKeepingActive = true;
			if (gameType == GameType.TIME_TRIAL && timeTrialTimerPane != null && !timeTrialTimerPane.isTimerActive()) {
				timeTrialTimerLayout.setVisible(true);
				timeTrialTimerPane.appearAndStart(startTime);
			}
			gameStartDateTime = startTime;
			gameEndDateTime = null;
		}
	}

	/**
	 * Stops the time keeping for the game session.
	 */
	private void helperTimeKeepingStop() {
		if (timeKeepingActive) {
			timeKeepingActive = false;
			if (gameType == GameType.TIME_TRIAL && timeTrialTimerPane != null && timeTrialTimerPane.isTimerActive()) {
				timeTrialTimerPane.stopTimer();
			}
			gameEndDateTime = ZonedDateTime.now();
		}
	}

	/**
	 * Starts the countdown for a time trial game session.
	 */
	private void helperTimeTrialStartCountdown() {
		if (timeTrialCountDownOverlay == null) {
			return;
		}

		getChildren().add(timeTrialCountDownOverlay);
		helperSetGameInteractablesDisabled(true);

		timeTrialCountDownOverlay.setOnFinishedCountdown(event -> {
			getChildren().remove(timeTrialCountDownOverlay);
			sessionBeginNewGame();
		});

		timeTrialCountDownOverlay.startCountdown();
	}

	/**
	 * Sets the disabled state of all interactable components in the game session.
	 *
	 * @param disabled true to disable all interactable components, false to enable
	 *                 them
	 */
	private void helperSetAllInteractablesDisabled(boolean disabled) {
		tileGridWord.setTileWordDisable(disabled);
		helperSetGameButtonsDisabled(disabled);
		helperSetMenuButtonsDisabled(disabled);
	}

	/**
	 * Sets the disabled state of game-related interactable components in the game
	 * session.
	 *
	 * @param disabled true to disable game-related interactable components, false
	 *                 to enable them
	 */
	private void helperSetGameInteractablesDisabled(boolean disabled) {
		tileGridWord.setTileWordDisable(disabled);
		helperSetGameButtonsDisabled(disabled);
	}

	/**
	 * Displays a popup notification with the specified message, width, and
	 * duration.
	 *
	 * @param message  the message to be displayed in the popup notification
	 * @param width    the width of the popup notification
	 * @param duration the duration (in milliseconds) for which the popup
	 *                 notification should be displayed
	 */
	private void helperDisplayPopupNotifcation(String message, double width, int duration) {
		NotificationPane popupNotification = new NotificationPane(message, width, gameSessionContext);
		menuPane.getChildren().add(0, popupNotification);
		popupNotification.popup(menuPane, duration);
	}

	/**
	 * Sets the disabled state of menu buttons in the game session.
	 *
	 * @param disabled true to disable the menu buttons, false to enable them
	 */
	private void helperSetMenuButtonsDisabled(boolean disabled) {
		backMenuButton.setDisable(disabled);
		darkModeToggleMenuButton.setDisable(disabled);
		achievementsMenuButton.setDisable(disabled);
		leaderboardMenuButton.setDisable(disabled);
		profileMenuButton.setDisable(disabled);
	}

	/**
	 * Sets the disabled state of game buttons in the game session.
	 *
	 * @param disabled true to disable the game buttons, false to enable them
	 */
	private void helperSetGameButtonsDisabled(boolean disabled) {
		sessionHintsSetDisabled(disabled);

		if (disabled) {
			gameShuffleButton.setDisable(true);
			gameDeselectButton.setDisable(true);
			gameSubmitButton.setDisable(true);
			gameSubmitButton.refreshStyle();
		} else {
			gameShuffleButton.setDisable(false);
			gameDeselectButton.setDisable(tileGridWord.checkNumWordsMatchSelected() == 0);
			gameSubmitButton.setDisable(tileGridWord.checkNumWordsMatchSelected() < TileGridWord.MAX_SELECTED);
			gameSubmitButton.refreshStyle();
		}
	}

	/**
	 * Updates the status of game buttons based on the current game state.
	 */
	private void helperUpdateGameButtonStatus() {
		gameDeselectButton.setDisable(tileGridWord.getSelectedTileWordCount() == 0);
		gameSubmitButton.setDisable(tileGridWord.getSelectedTileWordCount() < TileGridWord.MAX_SELECTED);

		gameDeselectButton.refreshStyle();
		gameSubmitButton.refreshStyle();
	}

	/**
	 * Handles the selection of the next category in the auto solver.
	 *
	 * @param remainingAnswerCategories the list of remaining answer categories
	 */
	private void helperAutoSolverNextCategory(List<GameAnswerColor> remainingAnswerCategories) {
		if (tileGridWord.getCurrentSolvingRow() < TileGridWord.ROWS) {
			GameAnswerColor currentColorAnswer = remainingAnswerCategories.remove(0);

			tileGridWord.deselectTileWords();
			tileGridWord.selectMatchingAnswerWords(currentColorAnswer);

			SequentialTransition sequentialTransition = new SequentialTransition();
			PauseTransition pauseBeforeSwapTransition = new PauseTransition(Duration.millis(350));
			pauseBeforeSwapTransition.setOnFinished(event -> {
				sessionHintsAnimationStop();
			});
			SequentialTransition swapAndAnswerTileSequence = tileGridWordAnimationPane.getSequenceCorrectAnswer();
			PauseTransition pauseAfterSwapTransition = new PauseTransition(Duration.millis(350));
			sequentialTransition.getChildren().addAll(pauseBeforeSwapTransition, swapAndAnswerTileSequence,
					pauseAfterSwapTransition);

			pauseAfterSwapTransition.setOnFinished(event -> {
				helperAutoSolverNextCategory(remainingAnswerCategories);
			});

			sequentialTransition.play();
		} else {
			PauseTransition pauseBeforeResultsTransition = new PauseTransition(Duration.millis(1000));
			pauseBeforeResultsTransition.setOnFinished(event -> {
				sessionReachedEndGame();
			});
			pauseBeforeResultsTransition.play();
		}
	}

	/**
	 * Begins the auto solver to automatically solve the remaining categories.
	 */
	private void helperAutoSolverBegin() {
		List<DifficultyColor> unansweredColor = tileGridWord.getSortedUnansweredDifficultyColor();

		if (unansweredColor.size() > 0) {
			List<GameAnswerColor> remainingAnswerCategories = new ArrayList<>();
			for (DifficultyColor color : unansweredColor) {
				GameAnswerColor colorAnswer = gameSessionContext.getGameData().getAnswerForColor(color);
				remainingAnswerCategories.add(colorAnswer);
			}

			helperAutoSolverNextCategory(remainingAnswerCategories);
		}
	}

	/**
	 * Creates an animation sequence for an incorrect submission attempt.
	 *
	 * @param lostGame  true if the player has lost the game, false otherwise
	 * @param isOneAway true if the submission is one away from the correct answer,
	 *                  false otherwise
	 * @return the created animation sequence for an incorrect submission attempt
	 */
	private SequentialTransition helperCreateAnimationSubmissionIncorrect(boolean lostGame, boolean isOneAway) {
		SequentialTransition sequentialIncorrectTrans = new SequentialTransition();

		PauseTransition placeholderPause = new PauseTransition(Duration.millis(5));
		placeholderPause.setOnFinished(event -> {
			sessionHintsAnimationStop();
			helperSetGameInteractablesDisabled(true);
			if (lostGame) {
				helperTimeKeepingStop();
			}
		});

		ParallelTransition jumpTransition = tileGridWord.getTransitionTileWordJump();

		PauseTransition pauseAfterJump = new PauseTransition(Duration.millis(500));

		SequentialTransition shakeTransition = tileGridWord.getTransitionTileWordShake();
		shakeTransition.setOnFinished(event -> {
			tileGridWord.unsetIncorrectTileWords();
		});

		PauseTransition deselectDelay = new PauseTransition(Duration.millis(500));
		deselectDelay.setOnFinished(event -> {
			tileGridWord.deselectTileWords();
		});

		PauseTransition removeCircleDelay = new PauseTransition(Duration.millis(500));
		removeCircleDelay.setOnFinished(removeCircleEvent -> {
			mistakesPane.removeCircle();

			if (lostGame) {
				if ((gameType == GameType.TIME_TRIAL && !ranOutOfTime) || gameType != GameType.TIME_TRIAL) {
					helperDisplayPopupNotifcation("Next Time", 88.13, POPUP_DEFAULT_DURATION_MS);

					PauseTransition autoSolveDelay = new PauseTransition(Duration.millis(500));
					autoSolveDelay.setOnFinished(event -> {
						helperAutoSolverBegin();
					});

					autoSolveDelay.play();
					helperSetGameInteractablesDisabled(true);
				}
			} else {
				if (isOneAway) {
					helperDisplayPopupNotifcation("One Away...", 96.09, POPUP_DEFAULT_DURATION_MS);
				}
				helperSetGameInteractablesDisabled(false);
				fastForwardStoreSaveState();
			}
		});

		sequentialIncorrectTrans.getChildren().addAll(placeholderPause, jumpTransition, pauseAfterJump, shakeTransition,
				deselectDelay, removeCircleDelay);
		return sequentialIncorrectTrans;
	}

	/**
	 * Creates an animation sequence for a correct submission attempt.
	 *
	 * @return the created animation sequence for a correct submission attempt
	 */
	private SequentialTransition helperCreateAnimationSubmissionCorrect() {
		boolean wonGameSet = tileGridWord.checkAllCategoriesGuessed();

		SequentialTransition sequentialCorrectTrans = new SequentialTransition();
		PauseTransition placeholderPause = new PauseTransition(Duration.millis(5));
		placeholderPause.setOnFinished(event -> {
			sessionHintsAnimationStop();
			helperSetGameInteractablesDisabled(true);
			if (wonGameSet) {
				helperTimeKeepingStop();
			}
		});

		ParallelTransition jumpTransition = tileGridWord.getTransitionTileWordJump();
		SequentialTransition swapAndAnswerTileSequence = tileGridWordAnimationPane.getSequenceCorrectAnswer();
		PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
		PauseTransition endPauseTransition = new PauseTransition(Duration.millis(500));

		endPauseTransition.setOnFinished(event -> {
			if (wonGameSet) {
				wonGame = true;
				sessionReachedEndGame();
			} else {
				helperSetGameInteractablesDisabled(false);
				fastForwardStoreSaveState();
			}
		});

		sequentialCorrectTrans.getChildren().addAll(placeholderPause, jumpTransition, pauseTransition,
				swapAndAnswerTileSequence, endPauseTransition);
		return sequentialCorrectTrans;
	}

	/**
	 * Recursively refreshes the style of a node and its children.
	 *
	 * @param styleManager the StyleManager used to refresh the styles
	 * @param node         the node to refresh the style for
	 */
	private void helperRefreshStyle(StyleManager styleManager, Node node) {
		if (node == null) {
			return;
		}

		if (node instanceof Modular) {
			((Modular) node).refreshStyle();
		} else if (node instanceof Parent) {
			for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
				helperRefreshStyle(styleManager, child);
			}
		} else if (node instanceof Text) {
			((Text) node).setFill(styleManager.colorText());
		}
	}

	/**
	 * Refreshes the style of the game session and its components.
	 */
	@Override
	public void refreshStyle() {
		StyleManager styleManager = gameSessionContext.getStyleManager();

		setBackground(new Background(new BackgroundFill(styleManager.colorWholeGameBackground(), null, null)));

		/*
		 * NOTE: If there is anything that is not properly being updated with the dark
		 * mode setting, add it into this array.
		 */
		/*
		 * NOTE: Anything that is a Pane will have its children be updated recursively
		 * (so most likely even if a Node is not already in this list, it will still be
		 * updated, but it may not be properly updated if it is not a child or currently
		 * added to another Pane).
		 */
		Node[] completeComponentList = { gameTypeOptionSelector, timeTrialCountDownOverlay, timeTrialTimerPane,
				timeTrialTimerLayout, mainHeaderText, organizationPane, menuPane, gameContentPane, tileGridStackPane,
				hintsPane, mistakesPane, gameButtonRowPane, menuButtonRowContainerPane, menuButtonRowRightPane,
				menuButtonRowLeftPane, tileGridAchievement, errorUserInGamePane, tileGridWord,
				tileGridWordAnimationPane, darkModeToggleMenuButton, hintMenuButton, achievementsMenuButton,
				leaderboardMenuButton, profileMenuButton, backMenuButton, gameSubmitButton, gameDeselectButton,
				gameShuffleButton, gameViewResultsButton, resultsPane, popupPane };

		for (Node node : completeComponentList) {
			helperRefreshStyle(styleManager, node);
		}
	}

	/**
	 * Closes everything related to the game session.
	 */
	public void close() {
		fastForwardStoreSaveState();
		helperSetUserInGameStatus(false);
		gameActive = false;
		helperTimeKeepingStop();
		midnightChecker.stop();
	}

	/**
	 * Sets the event handler to be invoked when the back button is pressed.
	 * 
	 * @param onGoBack the event handler to be set
	 */
	public void setOnGoBack(EventHandler<ActionEvent> onGoBack) {
		this.onGoBack = onGoBack;
	}

	/**
	 * Sets the event handler to be invoked when midnight strikes.
	 * 
	 * @param onMidnight the event handler to be set
	 */
	public void setOnMidnight(EventHandler<ActionEvent> onMidnight) {
		this.onMidnight = onMidnight;
	}

	/**
	 * Returns the GameSessionContext used by the game session.
	 *
	 * @return the GameSessionContext used by the game session
	 */
	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
