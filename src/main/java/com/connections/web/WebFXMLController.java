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
		} catch (Exception e) {
			System.out.println(
					"CONNECTIONS (WARNING): WebFXMLController could not connect to the database at " + mongoURL);
		}
		return null;
	}

	private void entry(Stage stage) {
		MongoDatabase mongoDatabase = connectDatabase();		
		WebContext webContext = new WebContext(mongoDatabase, jproApplication.getWebAPI());
		
		if(!WebUtils.checkDatabaseInit(webContext)) {
			System.out.println("CONNECTIONS: WebFXMLController initialized the database.");
			WebUtils.initDatabase(webContext);
		}
		
		WebSession session = new WebSession(webContext);
		WebSessionContext webSessionContext = new WebSessionContext(session);
		ConnectionsHome home = new ConnectionsHome(webContext, webSessionContext);
		Scene scene = new Scene(home, STAGE_WIDTH, STAGE_HEIGHT);
		stage.setScene(scene);
		stage.setTitle("Connections Debug");
		stage.show();
	}

	public void init(JProApplication jproApplication, Stage stage) {
		System.out.println("CONNECTIONS: WebFXMLController init() method reached!");
		this.jproApplication = jproApplication;
		entry(stage);
	}
}
