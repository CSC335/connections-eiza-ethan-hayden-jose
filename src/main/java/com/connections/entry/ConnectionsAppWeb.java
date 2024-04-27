package com.connections.entry;

import java.io.File;
import java.io.IOException;

import com.connections.web.WebFXMLController;
import com.jpro.webapi.JProApplication;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

/**
 * The entry point for JPro One when a user loads in Connections.
 */
public class ConnectionsAppWeb extends JProApplication {
	/**
	 * Initializes the JavaFX assets for the Connections page.
	 * 
	 * @param stage the primary stage of the JavaFX view
	 */
	@Override
	public void start(Stage stage) {
		System.out.println("CONNECTIONS: ConnectionsAppWeb has reached start() method...");

		try {
			File fxmlFile = new File("src/main/resources/com/connections/web/fxml/webapp.fxml");

			if (fxmlFile.exists()) {
				System.out.println("CONNECTIONS: FXML file has been found and exists...");
			} else {
				System.out.println("CONNECTIONS (WARNING): FXML file could not be found!");
			}

			FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
			try {
				loader.load();
				WebFXMLController controller = loader.getController();
				controller.init(this, stage);
			} catch (IOException e) {
				System.out.println("CONNECTIONS (WARNING): the FXML controller could not be properly initialized!");
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The entry point method that JPro One will first call when a user loads in
	 * Connections.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
