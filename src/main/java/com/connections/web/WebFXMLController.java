package com.connections.web;

import java.net.URL;
import java.util.ResourceBundle;

import com.connections.view_controller.ConnectionsHome;
import com.jpro.webapi.JProApplication;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The WebFXMLController class is a JavaFX controller that handles the
 * initialization and setup of the web application. It is responsible for
 * connecting to the database, creating a WebSession and WebSessionContext, and
 * setting up the main application scene.
 */
public class WebFXMLController implements Initializable {
	protected static final int STAGE_WIDTH = 800;
	protected static final int STAGE_HEIGHT = 750;

	@FXML
	protected StackPane root;
	protected JProApplication jproApplication;

	/**
	 * Initializes the WebFXMLController.
	 * 
	 * @param location  The location used to resolve relative paths for the root
	 *                  object, or null if the location is not known.
	 * @param resources The resources used to localize the root object, or null if
	 *                  the root object was not localized.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * Returns the MongoDatabase to be used for Connections.
	 * 
	 * @return The MongoDatabase object representing the connected database, or null
	 *         if the connection fails.
	 */
	private MongoDatabase connectDatabase() {
		String mongoURL = "mongodb://localhost:27017/";

		try {
			MongoClient mongoClient = MongoClients.create(mongoURL);
			return mongoClient.getDatabase(WebUtils.DATABASE_NAME);
		} catch (Exception e) {
			System.out.println(
					"CONNECTIONS (WARNING): WebFXMLController could not connect to the database at " + mongoURL);
		}
		return null;
	}

	/**
	 * Entry point of the WebFXMLController. Initializes the database if needed,
	 * increments the daily puzzle number, creates a new WebSession and
	 * WebSessionContext, and sets up the main application scene.
	 * 
	 * @param stage The primary stage for the application.
	 */
	private void entry(Stage stage) {
		MongoDatabase mongoDatabase = connectDatabase();
		WebContext webContext = new WebContext(mongoDatabase, jproApplication.getWebAPI(), jproApplication);

		if (!WebUtils.checkDatabaseInit(webContext)) {
			System.out.println("CONNECTIONS: WebFXMLController initialized the database.");
			WebUtils.initDatabase(webContext);
		}
		
		WebUtils.dailyPuzzleNumberIncrementIfNeeded(webContext);

		WebSession session = new WebSession(webContext);
		WebSessionContext webSessionContext = new WebSessionContext(session);

		WebSession.clearExpiredSessions(webContext);
		
		ConnectionsHome home = new ConnectionsHome(webContext, webSessionContext);
		Scene scene = new Scene(home, STAGE_WIDTH, STAGE_HEIGHT);
		stage.setScene(scene);
		stage.setTitle("Connections Debug");
		stage.show();
	}

	/**
	 * Called by the website entry point of JPro to begin running the web-compatible
	 * JavaFX application. This method initializes the necessary components and
	 * starts the application.
	 * 
	 * @param jproApplication The JProApplication object representing the web
	 *                        application.
	 * @param stage           The primary stage for the application.
	 */
	public void init(JProApplication jproApplication, Stage stage) {
		System.out.println("CONNECTIONS: WebFXMLController init() method reached!");
		this.jproApplication = jproApplication;
		entry(stage);
	}
}
