package com.connections.web;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class WebTempLoginScreen extends BorderPane {
	private HBox topBarLayout;
	private GridPane mainLayout;
	private Text tempLoginTitleText;
	private Text menuTitleText;
	private Button backButton;
	private WebContext webContext;
	private DatabaseView databaseView;

	// If logged out and coming back for first time, can either (1) use account,
	// (2) use guest.
	// If guest, can either (1) use an account, (2) start new guest session, (3)
	// continue as guest, (3) log out / start over.
	// If account, can either (1) make new account (2) log into another account, (3)
	// log out / start over

	private class DataEntry extends HBox {
		private Text text = new Text();
		private TextField field = new TextField();

		public DataEntry(String label) {
			field.setPrefColumnCount(100);
			field.setPrefHeight(20);
			text.setText(label);
			setSpacing(10);
			setPadding(new Insets(10));
			getChildren().addAll(text, field);
			setPrefSize(350, 20);
			setStyle("-fx-border-color: red;");
		}

		public String getInput() {
			return field.getText();
		}
	}

	private class CollectionCookiesView extends VBox {		
		public CollectionCookiesView() {
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
			
			for(String key : map.keySet()) {
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
		
		public CollectionView(String name) {
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
			
			for(Document doc : results) {
				String content = "";
				
				for(String key : doc.keySet()) {
					content += String.format("[%s = %s]", key, doc.get(key));
				}
				
				Text entry = new Text(content);
				getChildren().add(entry);
			}
			
			Button reload = new Button("Reload");
			reload.setOnAction(event -> {
				reload();
			});
			
			getChildren().add(reload);
		}
	}
	
	private class DatabaseView extends VBox {
		public DatabaseView() {
			getChildren().add(new CollectionCookiesView());
			for(String collectionName : WebBridge.COLLECTIONS) {
				getChildren().add(new CollectionView(collectionName));
			}
			setPadding(new Insets(10));
			setSpacing(10);
			setStyle("-fx-border-color: blue;");
		}
		
		public void reload() {
			for(Node node : getChildren()) {
				if(node instanceof CollectionView) {
					((CollectionView) node).reload();
				}
				if(node instanceof CollectionCookiesView) {
					((CollectionCookiesView) node).reload();
				}
			}
		}
	}
	
	public WebTempLoginScreen(WebContext webContext) {
		this.webContext = webContext;
		initAssets();
	}

	public void initAssets() {
		tempLoginTitleText = new Text("Temporary Login Screen");
		backButton = new Button("Go Back To Previous Menu");
		menuTitleText = new Text("Init");

		topBarLayout = new HBox(30, backButton, tempLoginTitleText, menuTitleText);

		mainLayout = new GridPane();
		mainLayout.setHgap(20);
		mainLayout.setVgap(20);

		databaseView = new DatabaseView();
		
		setTop(topBarLayout);
		setCenter(mainLayout);
		setBottom(databaseView);
		setPadding(new Insets(30));
	}
	
	public void start() {
//		if(WebBridge.cookiesEmpty(webContext) || WebBridge.cookiesGetSessionID(webContext) == null) {
//			setStateEnteredSingedOut();
//		} else {
//			String sessionID = WebBridge.cookiesGetSessionID(webContext);
//			String sessionIDType = WebBridge.checkSessionIDUserType(webContext, sessionID);
//			if(sessionIDType.equals(WebBridge.IS_SESSION_ID_FOR_ACCOUNT)) {
//				setStateEnteredAsAccount();
//			} else if(sessionIDType.equals(WebBridge.IS_SESSION_ID_FOR_GUEST)) {
//				setStateEnteredAsGuest();
//			} else {
//				setStateEnteredSingedOut();
//			}
//		}
	}
	
	public void pauseBeforeEnteringGame() {
		PauseTransition pause = new PauseTransition(Duration.millis(2000));
		pause.setOnFinished(event -> {
			enteringGame();
		});
		pause.play();
	}
	
	public void enteringGame() {
		getChildren().clear();
		
		setTop(new Text("Entering Game..."));
		
		PauseTransition pause = new PauseTransition(Duration.millis(2000));
		pause.setOnFinished(event -> {
			
		});
		pause.play();
	}

	public void setStateEnteredSingedOut() {
//		databaseView.reload();
//		backButton.setDisable(true);
//		mainLayout.getChildren().clear();
//		menuTitleText.setText("MAIN MENU / SIGNED OUT MENU");
//
//		Button accountButton = new Button("Continue to Accounts...");
//		accountButton.setOnAction(event -> {
//			setStateAccountsScreen();
//		});
//		Button guestButton = new Button("Start New Guest Session");
//		guestButton.setOnAction(event -> {
//			setStateNewGuest();
//		});
//		Button clearData = new Button("CLEAR ENTIRE DATBASE");
//		clearData.setOnAction(event -> {
//			WebBridge.dropAllAndReInitialize(webContext);
//			clearData.setText("CLEAR ENTIRE DATBASE (cleared)");
//			databaseView.reload();
//		});
//		Button clearCookies = new Button("CLEAR COOKIES");
//		clearCookies.setOnAction(event -> {
//			WebBridge.cookiesClear(webContext);
//			clearCookies.setText("CLEAR COOKIES (cleared)");
//			databaseView.reload();
//		});
//
//
//		mainLayout.add(accountButton, 0, 1);
//		mainLayout.add(guestButton, 0, 2);
//		mainLayout.add(clearData, 0, 3);
//		mainLayout.add(clearCookies, 0, 4);
	}

	public void setStateEnteredAsGuest() {
//		databaseView.reload();
//		backButton.setDisable(true);
//		mainLayout.getChildren().clear();
//		menuTitleText.setText("ENTERED AS GUEST");
//
//		Button a1 = new Button("Continue to Game");
//		a1.setOnAction(event -> {
//			enteringGame();
//		});
//		Button a2 = new Button("Log Out (Clears Cookies)");
//		a2.setOnAction(event -> {
//			WebBridge.sessionSignOut(webContext, true, true);
//			setStateEnteredSingedOut();
//		});
//
//		mainLayout.add(a1, 0, 1);
//		mainLayout.add(a2, 0, 2);
	}

	public void setStateEnteredAsAccount() {
//		databaseView.reload();
//		backButton.setDisable(true);
//		mainLayout.getChildren().clear();
//		menuTitleText.setText("ENTERED AS ACCOUNT (SIGNED IN)");
//
//		Button a1 = new Button("Continue to Game");
//		a1.setOnAction(event -> {
//			enteringGame();
//		});
//		Button a2 = new Button("Log Out (Clears Cookies)");
//		a2.setOnAction(event -> {
//			WebBridge.sessionSignOut(webContext, true, true);
//			setStateEnteredSingedOut();
//		});
//
//		mainLayout.add(a1, 0, 1);
//		mainLayout.add(a2, 0, 2);
	}

	public void setStateNewGuest() {
//		databaseView.reload();
//		backButton.setDisable(true);
//		mainLayout.getChildren().clear();
//		menuTitleText.setText("LEAVING: STARTING GAME AS NEW GUEST");
//
//		String guestID = WebBridge.generateUnusedGuestID(webContext);
//		WebBridge.storeGuest(webContext, guestID);
//		WebBridge.sessionGuestBegin(webContext, guestID, true, true);
//		databaseView.reload();
//		
//		Button go = new Button("Continue");
//		go.setOnAction(event -> {
//			enteringGame();
//		});
//		Text status = new Text("Your guest ID is " + guestID);
//
//		mainLayout.add(status, 0, 1);
//		mainLayout.add(go, 0, 2);
	}

	public void setStateAccountsScreen() {
		databaseView.reload();
		backButton.setOnAction(event -> {
			setStateEnteredSingedOut();
		});
		backButton.setDisable(false);

		mainLayout.getChildren().clear();

		menuTitleText.setText("ACCOUNTS MENU");

		Button a1 = new Button("New Account");
		Button a2 = new Button("Log Into Existing Account");

		a1.setOnAction(event -> {
			setStateNewAccount();
		});
		a2.setOnAction(event -> {
			setStateLogIntoAccount();
		});

		mainLayout.add(a1, 0, 1);
		mainLayout.add(a2, 0, 2);
	}

	public void setStateNewAccount() {
//		databaseView.reload();
//		backButton.setOnAction(event -> {
//			setStateAccountsScreen();
//		});
//		backButton.setDisable(false);
//
//		mainLayout.getChildren().clear();
//
//		menuTitleText.setText("MAKE NEW ACCOUNT");
//
//		DataEntry email = new DataEntry("email");
//		DataEntry username = new DataEntry("username");
//		DataEntry password = new DataEntry("password");
//
//		mainLayout.add(email, 0, 1);
//		mainLayout.add(username, 0, 2);
//		mainLayout.add(password, 0, 3);
//		
//		Button enter = new Button("Create");
//		Text status = new Text("");
//		enter.setOnAction(event -> {
//			if(WebBridge.notEmpty(WebBridge.findUserByName(webContext, username.getInput()))) {
//				status.setText("An account of that user name already exists!");
//				return;
//			}
//			if(WebBridge.notEmpty(WebBridge.findUserByEmail(webContext, email.getInput()))) {
//				status.setText("An account of that email already exists!");
//				return;
//			}
//			WebBridge.storeAccount(webContext, username.getInput(), email.getInput(), password.getInput());
//			status.setText("Created account!");
//			databaseView.reload();
//
//			Button go = new Button("Make New Session, Enter Game");
//			go.setOnAction(event2 -> {
//				WebBridge.sessionAccountBegin(webContext, username.getInput(), true, true);
//				databaseView.reload();
//				go.setText("Hold On...");
//				pauseBeforeEnteringGame();
//			});
//			mainLayout.add(go, 0, 7);
//		});
//
//		mainLayout.add(enter, 0, 5);
//		mainLayout.add(status, 0, 6);
	}

	public void setStateLogIntoAccount() {
//		databaseView.reload();
//		backButton.setOnAction(event -> {
//			setStateAccountsScreen();
//		});
//		backButton.setDisable(false);
//
//		mainLayout.getChildren().clear();
//
//		menuTitleText.setText("LOG INTO ACCOUNT");
//
//		DataEntry username = new DataEntry("username");
//		DataEntry password = new DataEntry("password");
//
//		mainLayout.add(username, 0, 2);
//		mainLayout.add(password, 0, 3);
//		
//		Button enter = new Button("Log In");
//		Text status = new Text("");
//		enter.setOnAction(event -> {
//			if(!WebBridge.checkAccountCredentialsMatch(webContext, username.getInput(), password.getInput())) {
//				status.setText("Could not find matching username and password!");
//				return;
//			}
//			status.setText("Found account!");
//			databaseView.reload();
//			
//			Button go = new Button("Make New Session, Enter Game");
//			go.setOnAction(event2 -> {
//				WebBridge.sessionAccountBegin(webContext, username.getInput(), true, true);
//				databaseView.reload();
//				go.setText("Hold On...");
//				pauseBeforeEnteringGame();
//			});
//			mainLayout.add(go, 0, 7);
//		});
//
//		mainLayout.add(enter, 0, 5);
//		mainLayout.add(status, 0, 6);
	}
}
