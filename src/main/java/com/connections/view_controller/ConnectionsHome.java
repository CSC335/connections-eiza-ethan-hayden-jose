package com.connections.view_controller;

import com.connections.model.GameData;
import com.connections.web.WebContext;
import com.connections.web.WebContextAccessible;
import com.connections.web.WebDebugDatabaseView;
import com.connections.web.WebSession;
import com.connections.web.WebSessionAccessible;
import com.connections.web.WebSessionContext;
import com.connections.web.WebUtils;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * The ConnectionsHome class represents the home screen of the Connections game.
 * It extends the JavaFX BorderPane class and implements the
 * WebContextAccessible and WebSessionAccessible interfaces.
 */
public class ConnectionsHome extends BorderPane implements WebContextAccessible, WebSessionAccessible {
	private StyleManager styleManager;
	private WebContext webContext;
	private WebSessionContext webSessionContext;
	private StackPane centerStackPane;
	private BorderPane window;
	private VBox centerBox;
	private Font karnak_condensed;
	private Font franklin600_16;
	private Font karnak;
	private ImageView logoImageView;
	private Label title;
	private Label howTo;
	private MenuButton loginButton;
	private MenuButton logoutButton;
	private MenuButton playButton;
	private MenuButton showDebugInfoButton;
	private WebDebugDatabaseView debugDatabaseViewer;
	private boolean debugInfoShown;

	/**
	 * The MenuButton class represents a customized button used in the Connections
	 * home screen. It extends the JavaFX Button class.
	 */
	private class MenuButton extends Button {
		/**
		 * Constructs a MenuButton object with the specified text and fill style.
		 *
		 * @param text the text to be displayed on the button
		 * @param fill a boolean indicating whether the button should have a filled
		 *             background
		 */
		public MenuButton(String text, boolean fill) {
			setText(text);
			setPrefSize(150, 58);
			setFont(franklin600_16);
			if (fill) {
				setTextFill(Color.WHITE);
				setStyle("-fx-background-color: black; -fx-background-radius: 50; -fx-font-size: 20px;");
			} else {
				setStyle(
						"-fx-background-color: rgba(179, 166, 254, 1); -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50; -fx-font-size: 20px;");
			}
		}
	}

	private static final Color LOGIN_BACKGROUND_COLOR = Color.rgb(179, 166, 254);

	/**
	 * Constructs a ConnectionsHome object with the specified WebContext and
	 * WebSessionContext.
	 *
	 * @param webContext        the WebContext associated with the Connections home
	 *                          screen
	 * @param webSessionContext the WebSessionContext associated with the
	 *                          Connections home screen
	 */
	public ConnectionsHome(WebContext webContext, WebSessionContext webSessionContext) {
		setWebContext(webContext);
		setWebSessionContext(webSessionContext);
		initPane();
	}

	/**
	 * Checks if the user is currently signed into an account.
	 *
	 * @return true if the user is signed into an account, false otherwise
	 */
	private boolean currentlySignedInAccount() {
		WebSession session = webSessionContext.getSession();
		return session.isSignedIntoAccount();
	}

	/**
	 * Shows the specified screen by adding it to the center stack pane and
	 * animating its appearance.
	 *
	 * @param screen the screen to be shown
	 */
	private void showScreen(Pane screen) {
		setButtonsDisabled(true);
		screen.setVisible(true);
		centerStackPane.getChildren().add(screen);

		TranslateTransition scroll = new TranslateTransition(Duration.millis(500), screen);
		screen.setTranslateX(0);
		screen.setTranslateY(getHeight());
		scroll.setToX(0);
		scroll.setToY(0);

		scroll.play();
	}

	/**
	 * Hides the specified screen by animating its disappearance and removing it
	 * from the center stack pane. Will trigger onHide upon closing.
	 *
	 * @param screen the screen to be hidden
	 */
	private void hideScreen(Pane screen, EventHandler<ActionEvent> onHide) {
		TranslateTransition scroll = new TranslateTransition(Duration.millis(500), screen);
		screen.setTranslateX(0);
		screen.setTranslateY(0);
		scroll.setToX(0);
		scroll.setToY(getHeight());

		scroll.setOnFinished(event -> {
			screen.setVisible(false);
			setButtonsDisabled(false);
			centerStackPane.getChildren().remove(screen);
			if (onHide != null) {
				onHide.handle(new ActionEvent(this, null));
			}
		});

		scroll.play();
	}

	/**
	 * Hides the specified screen by animating its disappearance and removing it
	 * from the center stack pane.
	 *
	 * @param screen the screen to be hidden
	 */
	private void hideScreen(Pane screen) {
		hideScreen(screen, null);
	}

	/**
	 * Checks the session status and performs appropriate actions based on the
	 * session state.
	 */
	private void checkSession() {
		WebSession session = webSessionContext.getSession();

		// Has NO user (neither guest nor account).
		if (session.isEmpty()) {
			// Will log in as a guest (default action of login).
			session.login();
		}
	}

	/**
	 * Initializes the layout and components of the Connections home screen.
	 */
	private void initPane() {
		debugDatabaseViewer = new WebDebugDatabaseView(webContext);

		styleManager = new StyleManager();
		window = new BorderPane();
		layoutConfigs();

		karnak_condensed = styleManager.getFont("karnakpro-condensedblack", 65);
		franklin600_16 = styleManager.getFont("franklin-normal", 600, 65);
		karnak = styleManager.getFont("KarnakPro-Medium_400", "otf", 65);

		title = new Label("Connections");
		title.setFont(karnak_condensed);
		title.setTextFill(Color.BLACK);

		howTo = new Label("Group words that share a common thread.");
		howTo.setFont(Font.font(karnak.getFamily(), FontWeight.THIN, 20));
		howTo.setTextFill(Color.BLACK);

		playButton = new MenuButton("Play", true);
		loginButton = new MenuButton("Log In", false);
		logoutButton = new MenuButton("Log Out", false);
		showDebugInfoButton = new MenuButton("Debug Info", false);

		logoImageView = createLogoImageView();

		centerBox = new VBox(20);
		centerBox.setAlignment(Pos.CENTER);
		centerBox.getChildren().addAll(logoImageView, title, howTo, playButton);

		if (WebUtils.debugIsEnabled(webContext)) {
			System.out.println("CONNECTIONS: debug mode is ENABLED, ConnectionsHome now has the debug view button.");
			centerBox.getChildren().add(showDebugInfoButton);
		}

		layoutAdjustLoginLogoutButtons();
		initListeners();

		centerStackPane = new StackPane(centerBox);
		window.setCenter(centerStackPane);
		setCenter(window);
	}

	/**
	 * Initializes the listeners for the main assets in the home screen.
	 */
	private void initListeners() {
		showDebugInfoButton.setOnAction(event -> {
			handleDebugScreen();
		});

		loginButton.setOnAction(event -> {
			initLoginScreen();
		});

		logoutButton.setOnAction(event -> {
			webSessionContext.getSession().logout();
			layoutAdjustLoginLogoutButtons();
		});

		playButton.setOnAction(event -> {
			initGameSession();
		});
	}

	/**
	 * Handles the debug screen of whether or not it should be shown or removed from
	 * view.
	 */
	private void handleDebugScreen() {
		debugInfoShown = !debugInfoShown;
		if (debugInfoShown) {
			setTop(debugDatabaseViewer);
		} else {
			getChildren().remove(debugDatabaseViewer);
		}
	}

	/**
	 * Initializes the login screen and shows it over the home screen.
	 */
	private void initLoginScreen() {
		ConnectionsLogin loginScreen = new ConnectionsLogin(webContext, webSessionContext);
		loginScreen.setOnGoBack(event -> {
			hideScreen(loginScreen);
			layoutAdjustLoginLogoutButtons();
		});
		loginScreen.setOnLoginSuccessfully(event -> {
			hideScreen(loginScreen);
			layoutAdjustLoginLogoutButtons();
		});
		showScreen(loginScreen);
	}

	/**
	 * Initializes the game session screen and shows it over the home screen.
	 */
	private void initGameSession() {
		checkSession();
		GameData gameDataLoadWith = WebUtils.gameGetByPuzzleNumber(webContext,
				WebUtils.dailyPuzzleNumberGet(webContext));
		GameSessionContext gameSessionContext = new GameSessionContext(styleManager, gameDataLoadWith, webContext,
				webSessionContext);
		GameSession gameSession = new GameSession(gameSessionContext);
		gameSession.setOnGoBack(event -> {
			hideScreen(gameSession, onHide -> {
				gameSession.close();
			});
		});
		gameSession.setOnMidnight(event -> {
			hideScreen(gameSession, onHide -> {
				gameSession.close();
				setButtonsDisabled(true);
			});

			PauseTransition delay = new PauseTransition(Duration.millis(2000));
			delay.setOnFinished(onFinished -> {
				WebUtils.dailyPuzzleNumberIncrementIfNeeded(webContext);
				setButtonsDisabled(false);
			});
			delay.play();
		});
		showScreen(gameSession);
	}

	/**
	 * Display either the login or logout button depending on the current signed in
	 * state.
	 */
	private void layoutAdjustLoginLogoutButtons() {
		if (centerBox != null && loginButton != null && logoutButton != null) {
			centerBox.getChildren().removeAll(loginButton, logoutButton);
			loginButton.setVisible(false);
			logoutButton.setVisible(false);

			if (currentlySignedInAccount()) {
				centerBox.getChildren().add(4, logoutButton);
				logoutButton.setVisible(true);
			} else {
				centerBox.getChildren().add(4, loginButton);
				loginButton.setVisible(true);
			}
		}
	}

	/**
	 * Configures the layout properties of the Connections home screen.
	 */
	private void layoutConfigs() {
		Background background = new Background(new BackgroundFill(LOGIN_BACKGROUND_COLOR, null, null));
		window.setBackground(background);
	}

	/**
	 * Creates an ImageView containing the logo image.
	 *
	 * @return the ImageView with the logo image
	 */
	private ImageView createLogoImageView() {
		Image logoImage = new Image("file:img/conn_logo.png");
		ImageView logoImageView = new ImageView(logoImage);
		logoImageView.setFitWidth(100);
		logoImageView.setPreserveRatio(true);
		return logoImageView;
	}

	/**
	 * Sets the disabled state of the buttons in the Connections home screen.
	 *
	 * @param disabled true to disable the buttons, false to enable them
	 */
	private void setButtonsDisabled(boolean disabled) {
		loginButton.setDisable(disabled);
		playButton.setDisable(disabled);
		showDebugInfoButton.setDisable(disabled);
	}

	/**
	 * Sets the WebContext associated with the Connections home screen.
	 *
	 * @param webContext the WebContext to be set
	 */
	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}

	/**
	 * Returns the WebContext associated with the Connections home screen.
	 *
	 * @return the WebContext associated with the Connections home screen
	 */
	@Override
	public WebContext getWebContext() {
		return webContext;
	}

	/**
	 * Sets the WebSessionContext associated with the Connections home screen.
	 *
	 * @param webSessionContext the WebSessionContext to be set
	 */
	@Override
	public void setWebSessionContext(WebSessionContext webSessionContext) {
		this.webSessionContext = webSessionContext;
	}

	/**
	 * Returns the WebSessionContext associated with the Connections home screen.
	 *
	 * @return the WebSessionContext associated with the Connections home screen
	 */
	@Override
	public WebSessionContext getWebSessionContext() {
		return webSessionContext;
	}
}