package com.connections.view_controller;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.connections.web.WebBridge;
import com.connections.web.WebBridgeSession;
import com.connections.web.WebContext;
import com.connections.web.WebContextAccessible;

public class ConnectionsHome extends BorderPane implements WebContextAccessible {
	private StyleManager styleManager;
	private WebContext webContext;
	private BorderPane window;
	private Font karnak_condensed;
	private Font franklin600_16;
	private Font karnak;
	private Label title;
	private Label howTo;
	private MenuButton loginButton;
	private MenuButton playButton;

//    private Label title;
//    private Label howTo;
//    private Button loginButton;
//    private Button loginButton;
//    

	private class MenuButton extends Button {
		public MenuButton(String text, boolean fill) {
			setText(text);
			setPrefSize(150, 58);
			setFont(franklin600_16);
			if(fill) {
				setTextFill(Color.WHITE);
				setStyle("-fx-background-color: black; -fx-background-radius: 50; -fx-font-size: 20px;");
			} else {
				setStyle(
						"-fx-background-color: rgba(179, 166, 254, 1); -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50; -fx-font-size: 20px;");
			}
		}
	}

	private static final Color LOGIN_BACKGROUND_COLOR = Color.rgb(179, 166, 254);

	public ConnectionsHome(WebContext webContext) {
		setWebContext(webContext);
		initPane();
	}
	
	private boolean isSignedInAccount() {
		if(!WebBridge.cookieIsEmpty(webContext) && !WebBridgeSession.checkSessionIDCookieExists(webContext)) {
			
		}
	}

	private void initPane() {
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

		loginButton.setOnAction(event -> {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		playButton.setOnAction(event -> {
			try {

//                Stage newStage = new Stage();
//                ConnectionsLogin login = new ConnectionsLogin();
//                login.start(newStage);
//                GameBoard game = new GameBoard();
//                game.start(newStage);

				// kill current stage
//                stage.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// ImageView logoImageView = createLogoImageView();

		VBox centerBox = new VBox(20);
		centerBox.setAlignment(Pos.CENTER);
		// centerBox.getChildren().addAll(logoImageView, title, howTo, playButton,
		// loginButton);
		centerBox.getChildren().addAll(title, howTo, playButton, loginButton);

		StackPane centerStackPane = new StackPane(centerBox);
		window.setCenter(centerStackPane);
		setCenter(window);
	}

	private void layoutConfigs() {
		Background background = new Background(new BackgroundFill(LOGIN_BACKGROUND_COLOR, null, null));
		window.setBackground(background);
	}

	private ImageView createLogoImageView() {
		Image logoImage = new Image("./img/conn_logo.png");
		/// empty-repo-haydenjoseeizaethan/img
		ImageView logoImageView = new ImageView(logoImage);
		logoImageView.setFitWidth(100);
		logoImageView.setPreserveRatio(true);
		return logoImageView;
	}

	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}

	@Override
	public WebContext getWebContext() {
		return webContext;
	}
}