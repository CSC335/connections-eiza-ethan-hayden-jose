package com.connections.web;

import com.connections.model.GameData;
import com.connections.model.GameDataCollection;
import com.connections.view_controller.GameSession;
import com.connections.view_controller.GameSessionContext;
import com.connections.view_controller.StyleManager;
import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;

import com.mongodb.client.MongoClients;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneOptions;
import org.bson.Document;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class WebFXMLController implements Initializable {
	protected static final int STAGE_WIDTH = 800;
	protected static final int STAGE_HEIGHT = 750;

	@FXML
	protected StackPane root;

	protected JProApplication jproApplication;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	private MongoDatabase connectDatabase() {
		String mongoURL = "mongodb://localhost:27017/";
		
		try {
			MongoClient mongoClient = MongoClients.create(mongoURL);
			return mongoClient.getDatabase(WebUtils.DATABASE_NAME);
		} catch(Exception e) {
			System.out.println("Connections (WARNING): WebFXMLController could not connect to the database at " + mongoURL);
		}
		return null;
	}

	private void entry(Stage stage) {
		ObservableMap<String, String> cookies = jproApplication.getWebAPI().getCookies();
		
		MongoDatabase mongoDatabase = connectDatabase();
		
		MongoCollection<Document> collectionSessionID = mongoDatabase.getCollection(WebUtils.COLLECTION_SESSION_ID_NAME);
		
//		String savedSessionID = cookies.get(WebUtils.COOKIE_SESSION_ID_NAME);
		
		WebContext webContext = new WebContext(mongoDatabase, jproApplication.getWebAPI());
		WebTempLoginScreen tempLoginScreen = new WebTempLoginScreen(webContext);
//		
//		if(savedSessionID == null) {
//			// need to generate new session ID
//			// same as being logged out
//			tempLoginScreen.setStateEnteredSingedOut();
//		} else {
//			if(WebUtils.collectionContains(collectionSessionID, WebUtils.KEY_SESSION_ID, savedSessionID)) {
//				tempLoginScreen.setStateEnteredAsAccount();
//			} else {
//				// need to generate new session ID
//				// same as being logged out
//				tempLoginScreen.setStateEnteredSingedOut();
//			}
//		}
		
		BorderPane borderPane = new BorderPane(tempLoginScreen);
		
		tempLoginScreen.start();
		
		Scene scene = new Scene(borderPane, STAGE_WIDTH, STAGE_HEIGHT);
		stage.setScene(scene);
		stage.setTitle("Connections Debug");
		stage.show();
		
//		GameDataCollection collection = new GameDataCollection("nyt-connections-games.txt");
//		if (!collection.getGameList().isEmpty()) {
//			GameData currentGame = collection.getGameList().get(0);
//			StyleManager styleManager = new StyleManager();
//			GameSessionContext gameSessionContext = new GameSessionContext(styleManager, currentGame, webContext);
//			GameSession gameSession = new GameSession(gameSessionContext);
//
//			Scene scene = new Scene(gameSession, STAGE_WIDTH, STAGE_HEIGHT);
//			stage.setScene(scene);
//			stage.setTitle("Connections");
//			stage.show();
//		}
	}

	public void init(JProApplication jproApplication, Stage stage) {
		System.out.println("CONNECTIONS: WebFXMLController init() method reached!");
		this.jproApplication = jproApplication;
		entry(stage);
	}
}
