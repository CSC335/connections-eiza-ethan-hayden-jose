package com.connections.web;

import com.connections.model.GameData;
import com.connections.model.GameDataCollection;
import com.connections.view_controller.GameSession;
import com.connections.view_controller.GameSessionContext;
import com.connections.view_controller.StyleManager;
import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class WebFXMLController implements Initializable {
	protected static final int STAGE_WIDTH = 800;
	protected static final int STAGE_HEIGHT = 750;

	@FXML
	protected StackPane root;

	protected JProApplication jproApplication;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	private void entry(Stage stage) {
		ObservableMap<String, String> cookies = jproApplication.getWebAPI().getCookies();
		
		System.out.println("attempt cookie read...");
		if(cookies.get("CONNECTIONS_TEST") != null) {
			System.out.println("\tread success!");
			System.out.println(cookies.get("CONNECTIONS_TEST"));
		}
		
		System.out.println("attempt cookie write...");
		jproApplication.getWebAPI().setCookie("CONNECTIONS_TEST", "" + System.currentTimeMillis());
		
		GameDataCollection collection = new GameDataCollection("nyt-connections-games.txt");
		if (!collection.getGameList().isEmpty()) {
			GameData currentGame = collection.getGameList().get(0);
			StyleManager styleManager = new StyleManager();
			GameSessionContext gameSessionContext = new GameSessionContext(styleManager, currentGame);
			GameSession gameSession = new GameSession(gameSessionContext);

			Scene scene = new Scene(gameSession, STAGE_WIDTH, STAGE_HEIGHT);
			stage.setScene(scene);
			stage.setTitle("Connections");
			stage.show();
		}
	}

	public void init(JProApplication jproApplication, Stage stage) {
		System.out.println("CONNECTIONS: WebFXMLController init");
		this.jproApplication = jproApplication;
		entry(stage);
	}
}
