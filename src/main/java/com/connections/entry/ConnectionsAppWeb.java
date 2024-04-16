package com.connections.entry;

import com.connections.web.WebFXMLController;
import com.jpro.webapi.JProApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ConnectionsAppWeb extends JProApplication {
	@Override
	public void start(Stage stage) {
		// load user interface as FXML file
		// IMPORTANT: for some reason, getResource() fails for ANY FILE anywhere in the project
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/connections/web/fxml/webapp.fxml"));

		
		// TEMPORARY WORKAROUND: simply use File(), which starts at the very root of the entire project (above src/)
		try {
			File fxmlFile = new File("src/main/resources/com/connections/web/fxml/webapp.fxml");
			
			System.out.println("XXX HELLO CONNECTIONS XXX");
			System.out.println("Using file-uri-url: " + fxmlFile.toURI().toURL().toString());
			
			FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
			
			System.out.println("FXML LOADER OK");

			Scene scene = null;
			try {
				Parent root = loader.load();
				WebFXMLController controller = loader.getController();
				System.out.println("CONNECTIONS: ConnectionsAppWeb success get controller");
				controller.init(this, stage);
			} catch (IOException e) {
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
