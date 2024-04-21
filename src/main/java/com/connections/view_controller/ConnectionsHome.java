package com.connections.view_controller;

import com.connections.model.GameData;
import com.connections.model.GameSaveState;
import com.connections.web.WebContext;
import com.connections.web.WebContextAccessible;
import com.connections.web.WebDebugDatabaseView;
import com.connections.web.WebSession;
import com.connections.web.WebSessionAccessible;
import com.connections.web.WebSessionContext;
import com.connections.web.WebUser;
import com.connections.web.WebUtils;

import javafx.animation.TranslateTransition;
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

public class ConnectionsHome extends BorderPane implements WebContextAccessible, WebSessionAccessible {
	private StyleManager styleManager;
	private WebContext webContext;
	private WebSessionContext webSessionContext;
	private ConnectionsLogin loginScreen;
	private GameSession gameSession;
	private StackPane centerStackPane;
	private BorderPane window;
	private Font karnak_condensed;
	private Font franklin600_16;
	private Font karnak;
	private Label title;
	private Label howTo;
	private MenuButton loginButton;
	private MenuButton playButton;
	private MenuButton showDebugInfoButton;
	private WebDebugDatabaseView debugDatabaseViewer;
	private boolean debugInfoShown;

	private class MenuButton extends Button {
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

	public ConnectionsHome(WebContext webContext, WebSessionContext webSessionContext) {
		setWebContext(webContext);
		setWebSessionContext(webSessionContext);
		initPane();
	}

	private boolean currentlySignedInAccount() {
		WebSession session = webSessionContext.getSession();
		return session.isSignedIntoAccount();
	}

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

	private void hideScreen(Pane screen) {
		TranslateTransition scroll = new TranslateTransition(Duration.millis(500), screen);
		screen.setTranslateX(0);
		screen.setTranslateY(0);
		scroll.setToX(0);
		scroll.setToY(getHeight());

		scroll.setOnFinished(event -> {
			screen.setVisible(false);
			setButtonsDisabled(false);
			centerStackPane.getChildren().remove(screen);
		});

		scroll.play();
	}

	private void checkSession() {
		WebSession session = webSessionContext.getSession();

		// has NO user (neither guest nor account)
		if (session.isEmpty()) {
			session.login();
		}
	}

	private void initPane() {
		System.out.println(webSessionContext.getSession().getSessionID());
		System.out.println(webSessionContext.getSession().getAsDatabaseFormat());
		if (webSessionContext.getSession().getUser() != null) {
			System.out.println(webSessionContext.getSession().getUser().getAsDatabaseFormat());
		}
		debugDatabaseViewer = new WebDebugDatabaseView(webContext);

		styleManager = new StyleManager();
		window = new BorderPane();
		layoutConfigs();

		loginScreen = new ConnectionsLogin(webContext, webSessionContext);
		loginScreen.setVisible(false);
		loginScreen.setOnLoginSuccessfully(event -> {
			hideScreen(loginScreen);
			loginButton.setVisible(false);
		});

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
		showDebugInfoButton = new MenuButton("Debug Info", false);
		showDebugInfoButton.setOnAction(event -> {
			debugInfoShown = !debugInfoShown;
			if (debugInfoShown) {
				setTop(debugDatabaseViewer);
			} else {
				getChildren().remove(debugDatabaseViewer);
			}
		});

		if (currentlySignedInAccount()) {
			loginButton.setVisible(false);
		}

		loginButton.setOnAction(event -> {
			try {
				showScreen(loginScreen);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		playButton.setOnAction(event -> {
			try {
				checkSession();
				GameData gameDataLoadWith = WebUtils.gameGetByPuzzleNumber(webContext,
						WebUtils.dailyPuzzleNumberGet(webContext));
				
//				WebUser user = webSessionContext.getSession().getUser();
//				GameSaveState saveState = null;
//				if(user.hasLatestSaveState()) {
//					saveState = user.getLatestGameSaveState();
//				}
				
				GameSessionContext gameSessionContext = new GameSessionContext(styleManager, gameDataLoadWith,
						webContext, webSessionContext);
				
				gameSession = new GameSession(gameSessionContext);
				showScreen(gameSession);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		ImageView logoImageView = createLogoImageView();

		VBox centerBox = new VBox(20);
		centerBox.setAlignment(Pos.CENTER);
		// centerBox.getChildren().addAll(logoImageView, title, howTo, playButton,
		// loginButton);
		centerBox.getChildren().addAll(logoImageView, title, howTo, playButton, loginButton, showDebugInfoButton);

		centerStackPane = new StackPane(centerBox);
		window.setCenter(centerStackPane);
		setCenter(window);
	}

	private void layoutConfigs() {
		Background background = new Background(new BackgroundFill(LOGIN_BACKGROUND_COLOR, null, null));
		window.setBackground(background);
	}

	private ImageView createLogoImageView() {
		Image logoImage = new Image("file:img/conn_logo.png");
		ImageView logoImageView = new ImageView(logoImage);
		logoImageView.setFitWidth(100);
		logoImageView.setPreserveRatio(true);
		return logoImageView;
	}
	
	private void setButtonsDisabled(boolean disabled) {
		loginButton.setDisable(disabled);
		playButton.setDisable(disabled);
		showDebugInfoButton.setDisable(disabled);
	}

	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}

	@Override
	public WebContext getWebContext() {
		return webContext;
	}

	@Override
	public void setWebSessionContext(WebSessionContext webSessionContext) {
		this.webSessionContext = webSessionContext;
	}

	@Override
	public WebSessionContext getWebSessionContext() {
		return webSessionContext;
	}
}