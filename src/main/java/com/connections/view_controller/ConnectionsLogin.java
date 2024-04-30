package com.connections.view_controller;

import com.connections.web.WebContext;
import com.connections.web.WebContextAccessible;
import com.connections.web.WebSession;
import com.connections.web.WebSessionAccessible;
import com.connections.web.WebSessionContext;
import com.connections.web.WebUserAccount;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * The ConnectionsLogin class represents the login screen of the Connections
 * game. It extends the JavaFX BorderPane class and implements the
 * WebContextAccessible and WebSessionAccessible interfaces.
 */
public class ConnectionsLogin extends BorderPane implements WebContextAccessible, WebSessionAccessible {
	private StyleManager styleManager;
	private WebContext webContext;
	private WebSessionContext webSessionContext;

	private GridPane gridLayout;
	private VBox nextSectionLayout;
	private EntryBox usernameBox;
	private EntryBox emailBox;
	private PasswordBox passwordBox;
	private WarningMessage invalidUsernameMessage;
	private WarningMessage invalidPassMessage;
	private WarningMessage invalidEmailMessage;
	private WarningMessage accountErrorMessage;
	private Label loginHeadingLabel;
	private BackMenuButton backButton;
	private BigButton continueButton;
	private BigButton continueButtonPlaceholder;
	private VBox verticalLayout;

	private Font franklinSmall;
	private Font franklinMedium;
	private Font cheltenham;
	private BorderPane window;
	private EventHandler<ActionEvent> onLoginSuccessfully;
	private EventHandler<ActionEvent> onGoBack;
	private boolean isCreatingNewAccount;

	/**
	 * Constructs a ConnectionsLogin object with the specified WebContext and
	 * WebSessionContext.
	 *
	 * @param webContext        the WebContext associated with the login screen
	 * @param webSessionContext the WebSessionContext associated with the login
	 *                          screen
	 */
	public ConnectionsLogin(WebContext webContext, WebSessionContext webSessionContext) {
		setWebContext(webContext);
		setWebSessionContext(webSessionContext);
		initPane();
	}

	/**
	 * The EntryBox class represents an input field with a label in the login
	 * screen. It extends the JavaFX VBox class.
	 */
	private class EntryBox extends VBox {
		private Label label;
		protected TextField field;
		private boolean incorrect;

		/**
		 * Constructs an EntryBox object with the specified label text.
		 *
		 * @param labelText the text to be displayed as the label for the input field
		 */
		public EntryBox(String labelText) {
			label = new Label(labelText);
			label.setFont(franklinSmall);
			label.setTextFill(Color.BLACK);
			initField();

			setIncorrect(false);
			setSpacing(8);
			getChildren().addAll(label, field);
		}

		/**
		 * Initializes the input field of the EntryBox.
		 */
		public void initField() {
			field = new TextField();
			field.setPrefSize(450, 46);
		}

		/**
		 * Sets the incorrect state of the EntryBox.
		 *
		 * @param incorrect true if the input is incorrect, false otherwise
		 */
		public void setIncorrect(boolean incorrect) {
			this.incorrect = incorrect;
			if (incorrect) {
				field.setStyle("-fx-border-color: red; -fx-border-width: 1;");
			} else {
				field.setStyle("-fx-border-color: black; -fx-border-width: 1;");
			}
		}

		/**
		 * Returns the incorrect state of the EntryBox.
		 *
		 * @return true if the input is incorrect, false otherwise
		 */
		public boolean isIncorrect() {
			return incorrect;
		}

		/**
		 * Returns the input text of the EntryBox.
		 *
		 * @return the input text of the EntryBox
		 */
		public String getInput() {
			return field.getText();
		}

		/**
		 * Sets a change listener for the input field of the EntryBox.
		 *
		 * @param listener the change listener to be set
		 */
		public void setListener(ChangeListener<String> listener) {
			field.textProperty().addListener(listener);
		}

		/**
		 * Sets the disabled state of the input field of the EntryBox.
		 *
		 * @param disabled true to disable the input field, false to enable it
		 */
		public void setInputDisabled(boolean disabled) {
			field.setDisable(disabled);
		}
	}

	/**
	 * The PasswordBox class represents a password input field with a label in the
	 * login screen. It extends the EntryBox class.
	 */
	private class PasswordBox extends EntryBox {
		/**
		 * Constructs a PasswordBox object with the specified label text.
		 *
		 * @param labelText the text to be displayed as the label for the password field
		 */
		public PasswordBox(String labelText) {
			super(labelText);
		}

		/**
		 * Initializes the password field of the PasswordBox.
		 */
		@Override
		public void initField() {
			field = new PasswordField();
			field.setPrefSize(450, 46);
		}
	}

	/**
	 * The WarningMessage class represents a warning message displayed in the login
	 * screen. It extends the JavaFX HBox class.
	 */
	private class WarningMessage extends HBox {
		private Label messageLabel;
		private SVGPath warningSVGPath;

		/**
		 * Constructs a WarningMessage object with the specified message text.
		 *
		 * @param message the text to be displayed as the warning message
		 */
		public WarningMessage(String message) {
			messageLabel = new Label(message);
			messageLabel.setFont(franklinSmall);
			messageLabel.setTextFill(Color.RED);

			warningSVGPath = new SVGPath();
			warningSVGPath.setContent("M2 10a8 8 0 1 1 16 0 8 8 0 0 1-16 0Zm7 1V5h2v6H9Zm0 2v2h2v-2H9Z");
			warningSVGPath.setScaleX(0.8);
			warningSVGPath.setScaleY(0.8);
			warningSVGPath.setFill(Color.RED);
			warningSVGPath.setFillRule(javafx.scene.shape.FillRule.EVEN_ODD);

			setVisible(false);
			setAlignment(Pos.CENTER_LEFT);
			setSpacing(5);
			getChildren().addAll(warningSVGPath, messageLabel);
		}

		/**
		 * Sets the message text of the WarningMessage.
		 *
		 * @param message the message text to be set
		 */
		public void setMessage(String message) {
			messageLabel.setText(message);
		}
	}

	/**
	 * The BigButton class represents a large button used in the login screen. It
	 * extends the JavaFX Button class.
	 */
	private class BigButton extends Button {
		/**
		 * Constructs a BigButton object with the specified label text.
		 *
		 * @param labelText the text to be displayed on the button
		 */
		public BigButton(String labelText) {
			setText(labelText);
			setStyle(
					"-fx-background-color: rgba(0, 0, 0, 1); -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50; -fx-font-size: 20px;");
			setPrefHeight(30);
			setPrefWidth(450);
			setPrefSize(450, 44);
			setFont(franklinMedium);
			setTextFill(Color.WHITE);
		}
	}

	/**
	 * Sets the event handler to be invoked when the login is successful.
	 *
	 * @param onLoginSuccessfully the event handler to be set
	 */
	public void setOnLoginSuccessfully(EventHandler<ActionEvent> onLoginSuccessfully) {
		this.onLoginSuccessfully = onLoginSuccessfully;
	}

	/**
	 * Sets the event handler to be invoked when the back button is pressed.
	 *
	 * @param onGoBack the event handler to be set
	 */
	public void setOnGoBack(EventHandler<ActionEvent> onGoBack) {
		this.onGoBack = onGoBack;
	}

	/**
	 * Checks if the specified email exists in the database.
	 *
	 * @param email the email to be checked
	 * @return true if the email exists in the database, false otherwise
	 */
	private boolean emailExistsInDatabase(String email) {
		return WebUserAccount.checkAccountExistsByEmail(webContext, email);
	}

	/**
	 * Checks if the specified username exists in the database.
	 *
	 * @param username the username to be checked
	 * @return true if the username exists in the database, false otherwise
	 */
	private boolean userExistsInDatabase(String username) {
		return WebUserAccount.checkAccountExistsByUserName(webContext, username);
	}

	/**
	 * Performs the animation to show the next section of the login screen.
	 */
	private void showNextSectionAnimation() {
		FadeTransition fadeIn = new FadeTransition(Duration.millis(1250), nextSectionLayout);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);

		TranslateTransition moveButton = new TranslateTransition(Duration.millis(500), continueButton);
		continueButton.setTranslateX(0);
		continueButton.setTranslateY(0);
		moveButton.setToX(continueButtonPlaceholder.getLayoutX() - continueButton.getLayoutX());
		moveButton.setToY(continueButtonPlaceholder.getLayoutY() - continueButton.getLayoutY());

		moveButton.setOnFinished(event -> {
			int fromRow = GridPane.getRowIndex(continueButton);
			int toRow = GridPane.getRowIndex(continueButtonPlaceholder);
			gridLayout.getChildren().removeAll(continueButton, continueButtonPlaceholder);
			gridLayout.add(continueButton, 0, toRow);
			gridLayout.add(continueButtonPlaceholder, 0, fromRow);
			continueButton.setTranslateX(0);
			continueButton.setTranslateY(0);
		});

		ParallelTransition parallel = new ParallelTransition(fadeIn, moveButton);
		parallel.play();
	}

	/**
	 * Displays the menu for creating a new account.
	 */
	private void menuForCreatingAccount() {
		usernameBox.setVisible(true);
		passwordBox.setVisible(true);
		continueButton.setText("Create Account");
		continueButton.setOnAction(event -> {
			accountErrorMessage.setVisible(false);
			boolean valid = true;

			// NOTE: the or-statement is important: it will first check if the input is
			// valid syntax-wise, THEN in terms of the database
			if (!isValidUsername(usernameBox.getInput()) || !isDatabaseValidUsername(usernameBox.getInput())) {
				usernameBox.setIncorrect(true);
				invalidUsernameMessage.setVisible(true);
				valid = false;
			}

			if (!isValidPassword(passwordBox.getInput())) {
				passwordBox.setIncorrect(true);
				invalidPassMessage.setVisible(true);
				valid = false;
			}

			if (valid) {
				WebUserAccount newAccount = new WebUserAccount(webContext, usernameBox.getInput(), emailBox.getInput(),
						passwordBox.getInput(), "");
				WebSession session = webSessionContext.getSession();

				if (session.isSignedIn()) {
					session.logout();
				}

				session.setUser(newAccount);
				boolean success = session.login();

				if (success) {
					newAccount.writeToDatabase();
					setButtonsDisabled(true);
					if (onLoginSuccessfully != null) {
						onLoginSuccessfully.handle(new ActionEvent(this, null));
					}
				} else {
					accountErrorMessage.setVisible(true);
				}
			}
		});
		showNextSectionAnimation();
	}

	/**
	 * Displays the menu for logging in with an existing account.
	 */
	private void menuForLoggingIn() {
		passwordBox.setVisible(true);
		continueButton.setText("Login");
		continueButton.setOnAction(event -> {
			accountErrorMessage.setVisible(false);
			boolean valid = true;

			// NOTE: the or-statement is important: it will first check if the input is
			// valid syntax-wise, THEN in terms of the database
			if (!isValidPassword(passwordBox.getInput()) || !isDatabaseValidPassword(passwordBox.getInput())) {
				passwordBox.setIncorrect(true);
				invalidPassMessage.setVisible(true);
				valid = false;
			}

			if (valid) {
				boolean success = true;

				WebUserAccount existingAccount = WebUserAccount.getUserAccountByCredentials(webContext,
						emailBox.getInput(), passwordBox.getInput());
				if (existingAccount == null) {
					success = false;
				} else {
					WebSession session = webSessionContext.getSession();

					if (session.isSignedIn()) {
						session.logout();
					}

					session.setUser(existingAccount);

					if (!session.login()) {
						success = false;
					}
				}

				if (success) {
					setButtonsDisabled(true);
					if (onLoginSuccessfully != null) {
						onLoginSuccessfully.handle(new ActionEvent(this, null));
					}
				} else {
					accountErrorMessage.setVisible(true);
				}
			}
		});
		showNextSectionAnimation();
	}

	/**
	 * Initializes the layout and components of the login screen.
	 */
	private void initPane() {
		styleManager = new StyleManager();
		franklinSmall = styleManager.getFont("franklin-normal", 700, 14);
		franklinMedium = styleManager.getFont("franklin-normal", 700, 16);
		cheltenham = styleManager.getFont("cheltenham-normal", 400, 30);

		window = new BorderPane();

		gridLayout = new GridPane();
		gridLayout.setHgap(10);
		gridLayout.setVgap(8);
		gridLayout.setAlignment(Pos.CENTER);

		loginHeadingLabel = new Label("Log in or create an account");
		loginHeadingLabel.setFont(cheltenham);
		loginHeadingLabel.setTextFill(Color.BLACK);

		usernameBox = new EntryBox("Username");
		usernameBox.setListener((observable, oldValue, newValue) -> {
			if (usernameBox.isIncorrect() && isValidUsername(usernameBox.getInput())) {
				usernameBox.setIncorrect(false);
				invalidUsernameMessage.setVisible(false);
			}
		});
		emailBox = new EntryBox("Email");
		emailBox.setListener((observable, oldValue, newValue) -> {
			if (emailBox.isIncorrect() && isValidEmail(emailBox.getInput())) {
				emailBox.setIncorrect(false);
				invalidEmailMessage.setVisible(false);
			}
		});
		passwordBox = new PasswordBox("Password");
		passwordBox.setListener((observable, oldValue, newValue) -> {
			if (passwordBox.isIncorrect() && isValidPassword(passwordBox.getInput())) {
				passwordBox.setIncorrect(false);
				invalidPassMessage.setVisible(false);
			}
		});

		// By default the error messages are set to invisible

		invalidUsernameMessage = new WarningMessage("..."); // set by another method
		invalidPassMessage = new WarningMessage("..."); // set by another method
		invalidEmailMessage = new WarningMessage("Please enter a valid email address.");
		accountErrorMessage = new WarningMessage("An unexpected error occurred. Please try again later.");

		continueButton = new BigButton("Continue");
		continueButtonPlaceholder = new BigButton("PLACEHOLDER");
		continueButtonPlaceholder.setVisible(false);

		nextSectionLayout = new VBox(usernameBox, invalidUsernameMessage, passwordBox, invalidPassMessage);
		for (Node node : nextSectionLayout.getChildren()) {
			node.setVisible(false);
		}

		gridLayout.add(emailBox, 0, 0);
		gridLayout.add(invalidEmailMessage, 0, 1);
		gridLayout.add(continueButton, 0, 2);
		gridLayout.add(nextSectionLayout, 0, 3);
		gridLayout.add(continueButtonPlaceholder, 0, 4);
		gridLayout.add(accountErrorMessage, 0, 5);

		continueButton.setOnAction(event -> {
			if (isValidEmail(emailBox.getInput())) {
				emailBox.setInputDisabled(true);
				isCreatingNewAccount = !emailExistsInDatabase(emailBox.getInput());

				if (isCreatingNewAccount) {
					menuForCreatingAccount();
				} else {
					menuForLoggingIn();
				}
			} else {
				emailBox.setIncorrect(true);
				invalidEmailMessage.setVisible(true);
			}
		});

		verticalLayout = new VBox(20);
		verticalLayout.setAlignment(Pos.CENTER);
		verticalLayout.getChildren().addAll(loginHeadingLabel, gridLayout);

		backButton = new BackMenuButton();
		backButton.setStyle("-fx-alignment: center-left;");
		backButton.setMaxWidth(SVGButton.PREF_WIDTH);
		backButton.setOnMouseClicked(event -> {
			if (onGoBack != null) {
				// Only want to disable buttons if there IS a onGoBack.
				setButtonsDisabled(true);
				onGoBack.handle(new ActionEvent(this, null));
			}
		});
		window.setTop(backButton);

		window.setCenter(verticalLayout);

		setStyle("-fx-background-color: white;");
		setPadding(new Insets(10));
		setCenter(window);

	}

	/**
	 * Validates the username input.
	 *
	 * @param username the username to be validated
	 * @return true if the username is valid, false otherwise
	 */
	private boolean isValidUsername(String username) {
		if (username.length() < 1 || username.length() > 20) {
			invalidUsernameMessage.setMessage("Username must be between 1 and 20 characters long.");
			return false;
		}
		return true;
	}

	/**
	 * Validates the username input against the database.
	 *
	 * @param username the username to be validated
	 * @return true if the username is valid and available, false otherwise
	 */
	private boolean isDatabaseValidUsername(String username) {
		if (userExistsInDatabase(username)) {
			invalidUsernameMessage.setMessage("Username has been taken!");
			return false;
		}
		return true;
	}

	/**
	 * Validates the password input.
	 *
	 * @param password the password to be validated
	 * @return true if the password is valid, false otherwise
	 */
	private boolean isValidPassword(String password) {
		if (password.length() < 8) {
			invalidPassMessage.setMessage("Password must be at least 8 characters long.");
			return false;
		}
		return true;
	}

	/**
	 * Validates the password input against the database.
	 *
	 * @param password the password to be validated
	 * @return true if the password is valid for the corresponding account, false
	 *         otherwise
	 */
	private boolean isDatabaseValidPassword(String password) {
		if (!isCreatingNewAccount && !WebUserAccount.checkAccountCredentialsMatch(webContext, emailBox.getInput(),
				passwordBox.getInput())) {
			invalidPassMessage.setMessage("Password is incorrect.");
			return false;
		}
		return true;
	}

	/**
	 * Validates the email input.
	 *
	 * @param email the email to be validated
	 * @return true if the email is valid, false otherwise
	 */
	private boolean isValidEmail(String email) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
		return email.matches(emailRegex);
	}

	/**
	 * Sets the disabled state of the buttons in the Connections login screen.
	 *
	 * @param disabled true to disable the buttons, false to enable them
	 */
	private void setButtonsDisabled(boolean disabled) {
		continueButton.setDisable(disabled);
	}

	/**
	 * Sets the WebContext associated with the login screen.
	 *
	 * @param webContext the WebContext to be set
	 */
	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}

	/**
	 * Returns the WebContext associated with the login screen.
	 *
	 * @return the WebContext associated with the login screen
	 */
	@Override
	public WebContext getWebContext() {
		return webContext;
	}

	/**
	 * Sets the WebSessionContext associated with the login screen.
	 *
	 * @param webSessionContext the WebSessionContext to be set
	 */
	@Override
	public void setWebSessionContext(WebSessionContext webSessionContext) {
		this.webSessionContext = webSessionContext;
	}

	/**
	 * Returns the WebSessionContext associated with the login screen.
	 *
	 * @return the WebSessionContext associated with the login screen
	 */
	@Override
	public WebSessionContext getWebSessionContext() {
		return webSessionContext;
	}
}