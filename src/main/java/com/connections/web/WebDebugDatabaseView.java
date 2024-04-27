package com.connections.web;

import java.util.concurrent.atomic.AtomicInteger;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * A JavaFX Pane containing controls for viewing and interacting with the
 * MongoDB database directly.
 */
public class WebDebugDatabaseView extends VBox {
	private WebContext webContext;
	private GridPane gridPane;
	private Text currentPuzzleNum;
	private static final int SPACING = 3;
	private static final int PADDING = 3;
	private static final double FONT_SIZE_SCALE = 1;

	/**
	 * Constructs a WebDebugDatabaseView with the specified WebContext.
	 * 
	 * @param webContext The WebContext used to interact with the database and web
	 *                   application.
	 */
	public WebDebugDatabaseView(WebContext webContext) {
		this.webContext = webContext;

		Text title = new Text("WebDebugDatabaseView");

		Button initDatabase = new Button("Initialize Database (With Games and Other Items)");
		initDatabase.setOnAction(event -> {
			WebUtils.initDatabase(webContext);
			refreshView();
		});

		Button clearDatabase = new Button("CLEAR THE DATABASE WITHOUT INITIALIZATION");
		clearDatabase.setOnAction(event -> {
			WebUtils.clearDatabase(webContext);
			refreshView();
		});

		Button refreshAll = new Button("Refresh All");
		refreshAll.setOnAction(event -> {
			refreshView();
		});

		Button dailyPuzzleIncrement = new Button("Increment Puzzle Num");
		dailyPuzzleIncrement.setOnAction(event -> {
			WebUtils.dailyPuzzleNumberIncrement(webContext);
			refreshView();
		});

		Button dailyPuzzleIncrementMuch = new Button("+500");
		dailyPuzzleIncrementMuch.setOnAction(event -> {
			AtomicInteger counter = new AtomicInteger(0);
			PauseTransition pause = new PauseTransition(Duration.millis(250));
			pause.setOnFinished(pauseEvent -> {
				WebUtils.dailyPuzzleNumberIncrement(webContext);
				if (counter.incrementAndGet() < 500) {
					pause.play();
				}
				refreshView();
			});
			pause.play();
		});

		Button dailyPuzzleDateSub = new Button("Rewind Date by 5 Hours");
		dailyPuzzleDateSub.setOnAction(event -> {
			WebUtils.dailyPuzzleNumberRewindClockHours(webContext, 5);
			refreshView();
		});

		Button dailyPuzzleDateCheck = new Button("Check Date");
		dailyPuzzleDateCheck.setOnAction(event -> {
			WebUtils.dailyPuzzleNumberIncrementIfNeeded(webContext);
			refreshView();
		});

		Button markTodayGamePlayed = new Button("Mark Today's Game as Played");
		markTodayGamePlayed.setOnAction(event -> {
			WebUtils.debugMarkTodayGamePlayed(webContext);
			refreshView();
		});

		currentPuzzleNum = new Text("...");

		int maxCols = 3;
		int currentRow = 0;
		int currentCol = 1;

		double maxWidth = 1280;

		gridPane = new GridPane();
		gridPane.setHgap(SPACING);
		gridPane.setVgap(SPACING);

		gridPane.add(new CookieView(webContext, 500, 100), 0, 0);

		for (String collectionName : WebUtils.COLLECTIONS) {
			gridPane.add(new CollectionView(webContext, collectionName, maxWidth / maxCols, 100), currentCol,
					currentRow);

			currentCol++;
			if (currentCol >= maxCols) {
				currentCol = 0;
				currentRow++;
			}
		}

		HBox mainControlBox = new HBox(SPACING, initDatabase, clearDatabase, refreshAll);
		HBox dateControlBox = new HBox(SPACING, dailyPuzzleIncrement, dailyPuzzleIncrementMuch, currentPuzzleNum,
				dailyPuzzleDateSub, dailyPuzzleDateCheck);
		HBox gameControlBox = new HBox(SPACING, markTodayGamePlayed);

		VBox tallControlBox = new VBox(SPACING * 2, mainControlBox, dateControlBox, gameControlBox);
		for (Node node : tallControlBox.getChildren()) {
			if (node instanceof HBox) {
				HBox hbox = (HBox) node;
				hbox.setStyle("-fx-border-color: red;");
				hbox.setPadding(new Insets(PADDING));
			}
		}

		getChildren().addAll(title, tallControlBox, gridPane);

		setPadding(new Insets(PADDING));
		setSpacing(SPACING);
		setStyle("-fx-border-color: blue;");
		refreshView();
	}

	/**
	 * Refreshes the view by updating the displayed data.
	 */
	public void refreshView() {
		currentPuzzleNum.setText("Current Puzzle Num: " + WebUtils.dailyPuzzleNumberGet(webContext));

		for (Node node : gridPane.getChildren()) {
			if (node instanceof GroupView) {
				((GroupView) node).refreshView();
			}
		}
	}

	private abstract class GroupView extends VBox {
		protected WebContext webContext;
		protected Text title;
		protected Button reloadButton;
		protected Button clearButton;
		protected HBox controlBox;
		protected VBox contentBox;
		protected ScrollPane scrollPane;

		/**
		 * Constructs a GroupView with the specified WebContext, text, width, and
		 * height.
		 * 
		 * @param webContext The WebContext used to interact with the database and web
		 *                   application.
		 * @param text       The text to be displayed in the title of the GroupView.
		 * @param width      The preferred width of the GroupView.
		 * @param height     The preferred height of the GroupView.
		 */
		public GroupView(WebContext webContext, String text, double width, double height) {
			title = new Text(text);
			title.setFont(Font.font("Arial", (int) (FONT_SIZE_SCALE * 16)));

			reloadButton = new Button("Refresh View");
			reloadButton.setOnAction(event -> {
				refreshView();
			});
			clearButton = new Button("Delete All");
			clearButton.setOnAction(event -> {
				clearAll();
			});

			controlBox = new HBox(reloadButton, clearButton);
			controlBox.setSpacing(SPACING);

			contentBox = new VBox();
			contentBox.setPadding(new Insets(PADDING));
			contentBox.setSpacing(SPACING);
			scrollPane = new ScrollPane(contentBox);
			scrollPane.setPrefHeight(height);
			scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
			scrollPane.setHmax(1);

			getChildren().addAll(title, controlBox, scrollPane);

			setPrefWidth(width);

			this.webContext = webContext;
			setSpacing(SPACING);
			setPadding(new Insets(PADDING));
			setStyle("-fx-border-color: blue;");
		}

		/**
		 * Creates a Text object with the specified text string and applies formatting.
		 * 
		 * @param textString The text content of the Text object.
		 * @return The created Text object with formatting applied.
		 */
		public static Text makeText(String textString) {
			Text text = new Text(textString);
			text.setFont(Font.font("Arial", (int) (FONT_SIZE_SCALE * 10)));
			return text;
		}

		/**
		 * Refreshes the view by updating the displayed data.
		 */
		public abstract void refreshView();

		/**
		 * Clears all data associated with the GroupView.
		 */
		public abstract void clearAll();
	}

	private class CookieView extends GroupView {
		/**
		 * Constructs a CookieView with the specified WebContext, width, and height.
		 * 
		 * @param webContext The WebContext used to interact with the database and web
		 *                   application.
		 * @param width      The preferred width of the CookieView.
		 * @param height     The preferred height of the CookieView.
		 */
		public CookieView(WebContext webContext, double width, double height) {
			super(webContext, "Cookies", width, height);
			refreshView();
		}

		@Override
		public void refreshView() {
			contentBox.getChildren().clear();
			ObservableMap<String, String> map = WebUtils.cookieGetMap(webContext);
			for (String key : map.keySet()) {
				Text entry = makeText(String.format("[%s = %s]", key, map.get(key)));
				contentBox.getChildren().add(entry);
			}
		}

		@Override
		public void clearAll() {
			WebUtils.cookieClear(webContext);
			refreshView();
		}
	}

	private class CollectionView extends GroupView {
		private String collectionName;

		/**
		 * Constructs a CollectionView with the specified WebContext, collection name,
		 * width, and height.
		 * 
		 * @param webContext     The WebContext used to interact with the database and
		 *                       web application.
		 * @param collectionName The name of the MongoDB collection to be displayed in
		 *                       the CollectionView.
		 * @param width          The preferred width of the CollectionView.
		 * @param height         The preferred height of the CollectionView.
		 */
		public CollectionView(WebContext webContext, String collectionName, double width, double height) {
			super(webContext, "Mongo Collection: " + collectionName, width, height);
			this.collectionName = collectionName;
			refreshView();
		}

		@Override
		public void refreshView() {
			contentBox.getChildren().clear();
			MongoCollection<Document> collection = webContext.getMongoDatabase().getCollection(collectionName);
			FindIterable<Document> results = collection.find();
			for (Document doc : results) {
				String content = "";

				for (String key : doc.keySet()) {
					Object value = doc.get(key);
					String valueString = (value == null ? "null" : value.toString());
					content += String.format("[%s = %s]", key, valueString);
				}

				Text entry = makeText(content);
				contentBox.getChildren().add(entry);
			}
		}

		@Override
		public void clearAll() {
			WebUtils.helperCollectionDrop(webContext, collectionName);
			refreshView();
		}
	}
}
