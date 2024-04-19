package com.connections.entry;

import java.io.File;
import java.io.IOException;

import com.connections.web.WebFXMLController;
import com.jpro.webapi.JProApplication;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ConnectionsAppWeb extends JProApplication {
	@Override
	public void start(Stage stage) {
		// load user interface as FXML file
		// IMPORTANT: for some reason, getResource() fails for ANY FILE anywhere in the project
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/connections/web/fxml/webapp.fxml"));
		
		System.out.println("CONNECTIONS: ConnectionsAppWeb has reached start() method...");
		
		// TEMPORARY WORKAROUND: simply use File(), which starts at the very root of the entire project (above src/)
		try {
			File fxmlFile = new File("src/main/resources/com/connections/web/fxml/webapp.fxml");
			
			if(fxmlFile.exists()) {
				System.out.println("CONNECTIONS: FXML file has been found and exists...");
			} else {
				System.out.println("CONNECTIONS (WARNING): FXML file could not be found!");
			}
			
			FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
			Scene scene = null;
			
			try {
				Parent root = loader.load();
				WebFXMLController controller = loader.getController();
				controller.init(this, stage);
			} catch (IOException e) {
				System.out.println("CONNECTIONS (WARNING): the FXML controller could not be properly initialized!");
				e.printStackTrace();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
