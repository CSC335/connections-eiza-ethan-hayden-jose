package com.connections.view_controller;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.connections.entry.ConnectionsAppLocal;
import com.connections.model.DifficultyColor;
import com.connections.model.GameAnswerColor;
import com.connections.model.GameData;
import com.connections.model.GameSaveState;
import com.connections.model.PlayedGameInfo;
import com.connections.model.PlayedGameInfoClassic;
import com.connections.model.PlayedGameInfoTimed;
import com.connections.model.Word;
import com.connections.web.WebSessionContext;
import com.connections.web.WebUser;
import com.jpro.webapi.InstanceInfo;
import com.jpro.webapi.WebAPI;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
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

public class GameSession extends StackPane implements Modular {
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
	private HBox menuButtonRowPane;
	private TileGridAchievement tileGridAchievement;

	private ErrorOverlayPane errorUserInGamePane;

	private boolean wonGame;
	private boolean gameActive;

	private TileGridWord tileGridWord;
	private TileGridWordAnimationOverlay tileGridWordAnimationPane;

	private DarkModeToggle darkModeToggleMenuButton;
	private HintMenuButton hintMenuButton;
	private AchievementsMenuButton achievementsMenuButton;
	private LeaderboardMenuButton leaderboardMenuButton;
	private ProfileMenuButton profileMenuButton;

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
	private boolean isBrowserClosed;
	private boolean blockedStoringSaveState;
	private GameType gameType;

	private boolean timeKeepingActive;
	private ZonedDateTime gameStartDateTime;
	private ZonedDateTime gameEndDateTime;

	// will be null if the game was not finished yet
	private PlayedGameInfo playedGameInfo;
	private GameSaveState loadedSaveState;

	public enum GameType {
		CLASSIC, TIME_TRIAL, NONE
	}

	public GameSession(GameSessionContext gameSessionContext) {
		this.gameSessionContext = gameSessionContext;
		initAssets();
		initListeners();
		fastForwardAutoLoad();
	}

	// === === === === === === === === === === === ===
	// === === === === INIT METHODS
	// === === === === === === === === === === === ===

	private void initAssets() {
		getChildren().clear();
		wonGame = false;
		gameActive = false;
		ranOutOfTime = false;

		currentPuzzleNumber = gameSessionContext.getGameData().getPuzzleNumber();

		setPrefSize(ConnectionsAppLocal.STAGE_WIDTH, ConnectionsAppLocal.STAGE_HEIGHT);

		darkModeToggleMenuButton = new DarkModeToggle(gameSessionContext);

		tileGridWord = new TileGridWord(gameSessionContext);
		tileGridWord.initTileWords();

		tileGridWordAnimationPane = new TileGridWordAnimationOverlay(tileGridWord);

		tileGridStackPane = new StackPane(tileGridWord, tileGridWordAnimationPane);

		tileGridAchievement = new TileGridAchievement(gameSessionContext);

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
		gameButtonRowPane.getChildren().addAll(gameShuffleButton, gameDeselectButton, gameSubmitButton);

		menuButtonRowPane = new HBox(10, hintMenuButton, profileMenuButton, leaderboardMenuButton,
				achievementsMenuButton, darkModeToggleMenuButton);
		menuButtonRowPane.setAlignment(Pos.CENTER);
		menuButtonRowPane.setMaxHeight(DarkModeToggle.HEIGHT);
		menuButtonRowPane.setStyle("-fx-alignment: center-right;");

		// exclude the hints pane for now
		gameContentPane = new VBox(24, mainHeaderText, tileGridStackPane, mistakesPane, gameButtonRowPane);
		gameContentPane.setAlignment(Pos.CENTER);

		menuPane = new StackPane(menuButtonRowPane);
		menuPane.setPrefHeight(MENU_PANE_HEIGHT);

		organizationPane = new BorderPane();
		organizationPane.setTop(menuPane);
		organizationPane.setCenter(gameContentPane);
		organizationPane.setPrefSize(ConnectionsAppLocal.STAGE_WIDTH, ConnectionsAppLocal.STAGE_HEIGHT);
		organizationPane.setPadding(new Insets(10));

		getChildren().add(organizationPane);

		// === NEW STUFF FOR WEB === (will make neater later)

		gameTypeOptionSelector = new OptionSelectOverlayPane(gameSessionContext);
		gameTypeOptionSelector.addButton("Classic", 68);
		gameTypeOptionSelector.addButton("Time Trial", 68);
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

		timeTrialCountDownOverlay = new CountDownOverlayPane(gameSessionContext);
		timeTrialTimerPane = new TimerPane(gameSessionContext, 60);
		timeTrialTimerPane.setOnFinishedTimer(event -> {
			sessionLostTimeTrial();
		});
		timeTrialTimerPane = new TimerPane(gameSessionContext, TIME_TRIAL_DURATION_SEC);
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

		timeTrialTimerLayout = new BorderPane();
		timeTrialTimerLayout.setTop(timeTrialTimerPane);
		timeTrialTimerLayout.setPadding(new Insets(64));
		BorderPane.setAlignment(timeTrialTimerPane, Pos.CENTER);
		gameTypeOptionSelector = new OptionSelectOverlayPane(gameSessionContext);
		gameTypeOptionSelector.addButton("Classic", 68);
		gameTypeOptionSelector.addButton("Time Trial", 68);
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

		errorUserInGamePane = new ErrorOverlayPane(gameSessionContext);
		errorUserInGamePane.setHeaderText("Game In Progress");
		errorUserInGamePane.setBodyText(
				"You are currently playing from another browser tab or device under the same user.\nPlease wait until the game is finished and try again.");

		timeTrialCountDownOverlay = new CountDownOverlayPane(gameSessionContext);
//		timeTrialTimerPane = new TimerPane(gameSessionContext, TIME_TRIAL_DURATION_SEC);
		timeTrialTimerPane = new TimerPane(gameSessionContext, 7);
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

		timeTrialTimerLayout = new BorderPane();
		timeTrialTimerLayout.setTop(timeTrialTimerPane);
		timeTrialTimerLayout.setPadding(new Insets(64));
		BorderPane.setAlignment(timeTrialTimerPane, Pos.CENTER);
		getChildren().add(0, timeTrialTimerLayout);
		// gameContentPane.getChildren().add(timeTrialTimerPane);

		// === NEW STUFF FOR WEB ===

		// helperSetGameButtonsDisabled(false);
		controlsSetNormal();
		refreshStyle();
	}

	private void initListeners() {
		tileGridWord.setOnTileWordSelection(event -> {
			helperUpdateGameButtonStatus();
		});
		gameShuffleButton.setOnAction(event -> {
			// save the now-shuffled grid into the save state
			tileGridWord.shuffleTileWords();
			fastForwardStoreSaveState();
		});
		gameSubmitButton.setOnAction(event -> {
			sessionSubmissionAttempt();
		});
		gameDeselectButton.setOnAction(event -> {
			tileGridWord.deselectTileWords();
		});
		gameSessionContext.getStyleManager().setOnDarkModeChange(event -> {
			refreshStyle();
		});
		gameViewResultsButton.setOnAction(event -> {
			screenDisplayResults();
		});
		hintMenuButton.setOnMouseClicked(event -> {
			debugSimulateResultsPane();
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
	}

	// === === === === === === === === === === === ===
	// === === === === SET CONTROLS
	// === === === === === === === === === === === ===

	private void controlsSetNormal() {
		gameButtonRowPane.getChildren().clear();
		gameButtonRowPane.getChildren().addAll(gameShuffleButton, gameDeselectButton, gameSubmitButton);
		gameContentPane.getChildren().clear();
		gameContentPane.getChildren().addAll(mainHeaderText, tileGridStackPane, mistakesPane, gameButtonRowPane);
	}

	private void controlsSetViewResultsOnly() {
		gameButtonRowPane.getChildren().clear();
		gameButtonRowPane.getChildren().add(gameViewResultsButton);
		gameContentPane.getChildren().clear();
		gameContentPane.getChildren().addAll(mainHeaderText, tileGridStackPane, gameButtonRowPane);
	}

	// === === === === === === === === === === === ===
	// === === === === SCREEN METHODS
	// === === === === === === === === === === === ===

	private void screenDisplayResults() {
		if (!gameActive) {
			if (resultsPane == null) {
				resultsPane = new ResultsPane(gameSessionContext, playedGameInfo);
			}

			helperPopupScreen(resultsPane, "");
		}
	}

	private void screenDisplayAchievements() {
		tileGridAchievement.animateCompletion();
		helperPopupScreen(tileGridAchievement, "Achievements:");
	}

	private void screenDisplayLeaderboard() {
		new BorderPane();
//		pane.setCenter(new Text("LEADERS"));
		helperPopupScreen(new LeaderboardPane(gameSessionContext), "Leaderboard:");
	}

	private void screenDisplayProfile() {
		BorderPane pane = new BorderPane();
		pane.setCenter(new Text("PROFILE"));
		helperPopupScreen(pane, "Profile:");
	}

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

	// === === === === === === === === === === === ===
	// === === === === "FAST-FORWARD" METHODS: SAVE-STATE HANDLING
	// === === === === === === === === === === === ===

	public void fastForwardAutoLoad() {
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();
//		// Commented this out to prevent the error pane from loading in, but allows the user to load a previous save
//		if (currentUser.isCurrentlyInGame()) {
//			fastForwardUserCurrentlyIngame();
//		}
		if (currentUser.hasLatestSaveState()) {
			fastForwardLoadSaveState();
		} else {
			fastForwardCheckGameFinishedAlready();
		}
	}

	public void fastForwardUserCurrentlyIngame() {
		System.out.println("fastForwardUserCurrentlyIngame");
		helperSetAllInteractablesDisabled(true);
		displayPaneWithGaussianBlur(errorUserInGamePane);
		errorUserInGamePane.appear();
	}

	/*
	 * can be called at any time, but preferably immediately after the GameSession
	 * is initialized to avoid unexpected bugs MUST BE CALLED IMMEDIATELY AFTER
	 * INITIALIZING THE MAIN ASSETS
	 */
	public void fastForwardLoadSaveState() {
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();

		if (currentUser.hasLatestSaveState() && currentUser.getLatestGameSaveState() != null) {
			loadedSaveState = currentUser.getLatestGameSaveState();
			loadedFromSaveState = true;

			int puzzleNumberInSave = loadedSaveState.getPuzzleNumber();

			if (puzzleNumberInSave != currentPuzzleNumber) {
				System.out.println("ERROR: GameSession was asked to load from the save state, but it cannot because the"
						+ " puzzle number (and likely the puzzle itself) of the save state and provided game data are different!");
				return;
			}
//			initAssets();
//			initListeners();
			hintsPane.setNumCircles(loadedSaveState.getHintsLeft());
			mistakesPane.setNumCircles(loadedSaveState.getMistakesLeft());
			tileGridWord.loadFromSaveState(loadedSaveState);
			gameType = loadedSaveState.getGameType();

			helperSetGameButtonsDisabled(false);
			tileGridWord.setTileWordDisable(false);
//			helperSetGameInteractablesDisabled(false);
			helperSetAllInteractablesDisabled(false);

			gameActive = true;
			wonGame = false;
			ranOutOfTime = false;
			gameAlreadyFinished = false;

			java.time.Duration previousGameDuration = java.time.Duration.between(loadedSaveState.getGameStartTime(),
					loadedSaveState.getSaveStateCreationTime());

			ZonedDateTime newStartTime = ZonedDateTime.now().minus(previousGameDuration);

			helperTimeKeepingStart(newStartTime);
			helperSetUserInGameStatus(true);
		}
	}

	public void fastForwardStoreSaveState() {
		if (gameActive && !gameAlreadyFinished && !blockedStoringSaveState) {
			WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();

			// implement this later, track the time the user has spent playing for both
			// classic and time trial
			int timeCompleted = 0;

			GameSaveState gameSaveState = new GameSaveState(tileGridWord, hintsPane, mistakesPane, gameSessionContext,
					!gameActive, timeCompleted, gameType, gameStartDateTime);

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

		if (sessionCheckUserClosedBrowser()) {
			sessionCloseEverything();
		}
	}

	public void fastForwardClearSaveState() {
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();

		if (currentUser.hasLatestSaveState()) {
			currentUser.clearLatestGameSaveState();
			currentUser.writeToDatabase();
		}
	}

	/*
	 * CANNOT be called after loading from a save state MUST BE CALLED IMMEDIATELY
	 * AFTER INITIALIZING THE MAIN ASSETS
	 */
	public void fastForwardCheckGameFinishedAlready() {
		if (!loadedFromSaveState && !gameActive) {
//			initAssets();
//			initListeners();

			WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
			currentUser.readFromDatabase();

			gameAlreadyFinished = currentUser.hasPlayedGameByPuzzleNum(currentPuzzleNumber);

			helperSetAllInteractablesDisabled(true);


			gameActive = false;

			if (gameAlreadyFinished) {
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

	// === === === === === === === === === === === ===
	// === === === === MAIN "SESSION" METHODS
	// === === === === === === === === === === === ===

	public void sessionBeginNewGame() {
		helperTimeKeepingStart(ZonedDateTime.now());
		gameActive = true;
		wonGame = false;
		blockedStoringSaveState = false;
		helperSetAllInteractablesDisabled(false);
	}

	public void sessionReachedEndGame() {
		if (gameType == GameType.TIME_TRIAL && timeTrialTimerPane.isVisible()) {
			timeTrialTimerPane.disappear();
		}

		List<Set<Word>> guesses = tileGridWord.getGuesses();
		int mistakesMadeCount = mistakesPane.getMaxNumCircles() - mistakesPane.getNumCircles();
		int hintsUsedCount = hintsPane.getMaxNumCircles() - hintsPane.getNumCircles();
		int connectionsMade = tileGridWord.getCurrentSolvingRow();

		// implement this properly later
		// we will have some static, final constant value that will be used for the time
		// trial time
		int timeLimit = TIME_TRIAL_DURATION_SEC;

		// implement this properly later
		// this will be tracked for both time trial and classic users for the
		// leaderboard

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

		// save the game to the user's list of played games
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();
		currentUser.addPlayedGame(playedGameInfo);
		currentUser.writeToDatabase();

		fastForwardClearSaveState();

		gameActive = false;

		helperSetGameButtonsDisabled(true);
		tileGridWord.setTileWordDisable(true);
		helperSetUserInGameStatus(false);

		helperSetGameInteractablesDisabled(true);

		boolean noMistakes = (wonGame && tileGridWord.getGuesses().size() == 4);
		int timeTrialTime = (gameType == GameType.TIME_TRIAL) ? timeTrialTimerPane.getElapsedTime() : 0;
		WebSessionContext webSessionContext = gameSessionContext.getWebSessionContext();
		webSessionContext.getSession().updateUserAchievementData(gameType, noMistakes, timeTrialTime, wonGame);

		screenDisplayResults();
		controlsSetViewResultsOnly();
	}

	public void sessionSubmissionAttempt() {
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

	public void sessionLostTimeTrial() {
		// to prevent you "losing" after finishing on time somehow
		if (wonGame || gameType != GameType.TIME_TRIAL) {
			return;
		}

		ranOutOfTime = true;

		helperTimeKeepingStop();

		// The timer should already be stopped by helperTimeKeepingStop() but this is
		// here for good measure
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

	public void sessionHintUsed() {

	}

	/*
	 * This NEEDS to be a lot more assertive: it needs to somehow prevent everything
	 * from executing further. There should probably be a boolean in the
	 * GameSessionContext that all objects need to check
	 */
	public void sessionCloseEverything() {
		if (helperGetUserInGameStatus()) {
			helperSetUserInGameStatus(false);
		}
		gameActive = false;
		helperTimeKeepingStop();
		try {
			gameSessionContext.getWebContext().getJProApplication().stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean sessionCheckUserClosedBrowser() {
		WebAPI webAPI = gameSessionContext.getWebContext().getWebAPI();
		InstanceInfo instanceInfo = webAPI.getInstanceInfo();
		isBrowserClosed = instanceInfo.isAfk() || instanceInfo.isBackground();
		return isBrowserClosed;
	}

	// === === === === === === === === === === === ===
	// === === === === HELPER METHODS
	// === === === === === === === === === === === ===

	private void helperSetUserInGameStatus(boolean status) {
		System.out.println("helperSetUserInGameStatus " + status);
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();
		currentUser.setCurrentlyInGameStatus(status);
		currentUser.writeToDatabase();
	}

	private boolean helperGetUserInGameStatus() {
		WebUser currentUser = gameSessionContext.getWebSessionContext().getSession().getUser();
		currentUser.readFromDatabase();
		return currentUser.isCurrentlyInGame();
	}

	private void displayPaneWithGaussianBlur(Pane pane) {
		if (pane == null) {
			return;
		}
		GaussianBlur blurEffect = new GaussianBlur();
		organizationPane.setEffect(blurEffect);
		getChildren().add(pane);
	}

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

	private void helperTimeKeepingStop() {
		if (timeKeepingActive) {
			timeKeepingActive = false;
			if (gameType == GameType.TIME_TRIAL && timeTrialTimerPane != null && timeTrialTimerPane.isTimerActive()) {
				timeTrialTimerPane.stopTimer();
			}
			gameEndDateTime = ZonedDateTime.now();
		}
	}

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

		// just to be safe, viewing the countdown is enough to be considered in game
	}

	private void helperSetAllInteractablesDisabled(boolean disabled) {
		tileGridWord.setTileWordDisable(disabled);
		helperSetGameButtonsDisabled(disabled);
		helperSetMenuButtonsDisabled(disabled);
	}

	private void helperSetGameInteractablesDisabled(boolean disabled) {
		tileGridWord.setTileWordDisable(disabled);
		helperSetGameButtonsDisabled(disabled);
	}

	private void helperDisplayPopupNotifcation(String message, double width, int duration) {
		NotificationPane popupNotification = new NotificationPane(message, width, gameSessionContext);
		menuPane.getChildren().add(0, popupNotification);
		popupNotification.popup(menuPane, duration);
	}

	private void helperSetMenuButtonsDisabled(boolean disabled) {
		darkModeToggleMenuButton.setDisable(disabled);
		hintMenuButton.setDisable(disabled);
		achievementsMenuButton.setDisable(disabled);
		leaderboardMenuButton.setDisable(disabled);
		profileMenuButton.setDisable(disabled);
	}

	private void helperSetGameButtonsDisabled(boolean disabled) {
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

	private void helperUpdateGameButtonStatus() {
		gameDeselectButton.setDisable(tileGridWord.getSelectedTileWordCount() == 0);
		gameSubmitButton.setDisable(tileGridWord.getSelectedTileWordCount() < TileGridWord.MAX_SELECTED);

		gameDeselectButton.refreshStyle();
		gameSubmitButton.refreshStyle();
	}

	// === === === === === === === === === === === === === === === === === === ===
	// === === === === HELPER METHODS FOR ANIMATIONS (INCLUDING AUTO SOLVER)
	// === === === === === === === === === === === === === === === === === === ===

	private void helperAutoSolverNextCategory(List<GameAnswerColor> remainingAnswerCategories) {
		if (tileGridWord.getCurrentSolvingRow() < TileGridWord.ROWS) {
			GameAnswerColor currentColorAnswer = remainingAnswerCategories.remove(0);

			tileGridWord.deselectTileWords();
			tileGridWord.selectMatchingAnswerWords(currentColorAnswer);

			SequentialTransition sequentialTransition = new SequentialTransition();
			PauseTransition pauseBeforeSwapTransition = new PauseTransition(Duration.millis(350));
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

	private SequentialTransition helperCreateAnimationSubmissionIncorrect(boolean lostGame, boolean isOneAway) {
		SequentialTransition sequentialIncorrectTrans = new SequentialTransition();

		PauseTransition placeholderPause = new PauseTransition(Duration.millis(5));
		placeholderPause.setOnFinished(event -> {
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

	private SequentialTransition helperCreateAnimationSubmissionCorrect() {
		boolean wonGameSet = tileGridWord.checkAllCategoriesGuessed();

		SequentialTransition sequentialCorrectTrans = new SequentialTransition();
		PauseTransition placeholderPause = new PauseTransition(Duration.millis(5));
		placeholderPause.setOnFinished(event -> {
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

	// === === === === === === === === === === === ===
	// === === === === DEBUG METHODS
	// === === === === === === === === === === === ===

	private void debugSimulateResultsPane() {
		Random randGen = new Random();
		GameData gameData = gameSessionContext.getGameData();
		int numGuesses = 3 + randGen.nextInt(1, 5);
		List<Set<Word>> guesses = new ArrayList<>();
		for (int i = 0; i < numGuesses; i++) {
			Set<Word> set = new HashSet<>();

			DifficultyColor[] possibleColors = { DifficultyColor.YELLOW, DifficultyColor.GREEN, DifficultyColor.BLUE,
					DifficultyColor.PURPLE };
			while (set.size() < 4) {
				DifficultyColor randomColor = possibleColors[randGen.nextInt(4)];
				GameAnswerColor randAnswer = gameData.getAnswerForColor(randomColor);
				String[] words = randAnswer.getWords();
				Word randomWord = new Word(words[randGen.nextInt(words.length)], randomColor);
				if (!set.contains(randomWord)) {
					set.add(randomWord);
				}
			}
			guesses.add(set);
		}
		PlayedGameInfo tempPlayedGame = new PlayedGameInfoClassic(123, 0, 0, 0, guesses, false, ZonedDateTime.now(),
				ZonedDateTime.now());
		resultsPane = new ResultsPane(gameSessionContext, tempPlayedGame);
		screenDisplayResults();
	}

	// === === === === === === === === === === === ===
	// === === === === OTHER METHODS
	// === === === === === === === === === === === ===
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
		Node[] completeComponentList = { mainHeaderText, organizationPane, menuPane, gameContentPane, tileGridStackPane,
				hintsPane, mistakesPane, gameButtonRowPane, menuButtonRowPane, tileGridAchievement, tileGridWord,
				tileGridWordAnimationPane, darkModeToggleMenuButton, hintMenuButton, achievementsMenuButton,
				leaderboardMenuButton, profileMenuButton, gameSubmitButton, gameDeselectButton, gameShuffleButton,
				gameViewResultsButton, resultsPane, popupPane };

		for (Node node : completeComponentList) {
			helperRefreshStyle(styleManager, node);
		}
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return gameSessionContext;
	}
}
