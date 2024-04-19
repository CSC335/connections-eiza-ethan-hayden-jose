package com.connections.web;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class WebDebugDatabaseView extends VBox {
	private WebContext webContext;

	public WebDebugDatabaseView(WebContext webContext) {
			getChildren().add(new CollectionCookiesView(webContext));
			for(String collectionName : WebBridge.COLLECTIONS) {
				getChildren().add(new CollectionView(webContext, collectionName));
			}
			setPadding(new Insets(10));
			setSpacing(10);
			setStyle("-fx-border-color: blue;");
		}

	public void reload() {
		for (Node node : getChildren()) {
			if (node instanceof CollectionView) {
				((CollectionView) node).reload();
			}
			if (node instanceof CollectionCookiesView) {
				((CollectionCookiesView) node).reload();
			}
		}
	}

	private class CollectionCookiesView extends VBox {
		private WebContext webContext;

		public CollectionCookiesView(WebContext webContext) {
			this.webContext = webContext;
			setSpacing(10);
			setPadding(new Insets(10));
			setStyle("-fx-border-color: pink;");
			reload();
		}

		public void reload() {
			getChildren().clear();

			Text title = new Text("Cookies");
			title.setFont(Font.font("Arial", 18));

			getChildren().add(title);

			ObservableMap<String, String> map = WebBridge.cookieGetMap(webContext);

			for (String key : map.keySet()) {
				Text entry = new Text(String.format("[%s = %s]", key, map.get(key)));
				getChildren().add(entry);
			}

			Button reload = new Button("Reload");
			reload.setOnAction(event -> {
				reload();
			});

			getChildren().add(reload);
		}
	}

	private class CollectionView extends VBox {
		private String name;
		private MongoCollection<Document> collection;

		private WebContext webContext;

		public CollectionView(WebContext webContext, String name) {
			this.webContext = webContext;
			this.name = name;
			collection = webContext.getMongoDatabase().getCollection(name);
			setSpacing(10);
			setPadding(new Insets(10));
			setStyle("-fx-border-color: orange;");
			reload();
		}

		public void reload() {
			FindIterable<Document> results = collection.find();
			getChildren().clear();

			Text title = new Text("Listing for Collection " + name);
			title.setFont(Font.font("Arial", 18));

			getChildren().add(title);

			for (Document doc : results) {
				String content = "";

				for (String key : doc.keySet()) {
					content += String.format("[%s = %s]", key, doc.get(key).toString());
				}

				Text entry = new Text(content);
				getChildren().add(entry);
			}

			Button reload = new Button("Reload");
			reload.setOnAction(event -> {
				reload();
			});
			
			Button clear = new Button("Clear");
			clear.setOnAction(event -> {
				WebBridge.helperCollectionDrop(webContext, name);
				reload();
			});

			getChildren().add(reload);
			getChildren().add(clear);
		}
	}
}
