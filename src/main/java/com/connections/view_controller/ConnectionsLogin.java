package com.connections.view_controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.connections.web.WebContextAccessible;
import com.connections.web.WebBridgeUserAccount;
import com.connections.web.WebContext;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import javafx.util.Duration;

public class ConnectionsLogin extends BorderPane implements WebContextAccessible {
	private StyleManager styleManager = new StyleManager();
	private WebContext webContext;

	private GridPane gridLayout;
	private VBox nextSectionLayout;

	private EntryBox usernameBox;
	private EntryBox emailBox;
	private PasswordBox passwordBox;
	private WarningMessage invalidUsernameMessage;
	private WarningMessage invalidPassMessage;
	private WarningMessage invalidEmailMessage;
	private Label loginHeadingLabel;
	private BigButton continueButton;
	private BigButton continueButtonPlaceholder;
	private VBox verticalLayout;

	private Font franklin700_14;
	private Font franklin700_16;
	private Font cheltenham;

	private boolean isCreatingNewAccount;

	public ConnectionsLogin(WebContext webContext) {
		setWebContext(webContext);
		initPane();
	}

	private class EntryBox extends VBox {
		private Label label;
		protected TextField field;
		private boolean incorrect;

		public EntryBox(String labelText) {
			label = new Label(labelText);
			label.setFont(franklin700_14);
			label.setTextFill(Color.BLACK);
			initField();

			setIncorrect(false);
			setSpacing(8);
			getChildren().addAll(label, field);
		}

		public void initField() {
			field = new TextField();
			field.setPrefSize(450, 46);
		}

		public void setIncorrect(boolean incorrect) {
			this.incorrect = incorrect;
			if (incorrect) {
				field.setStyle("-fx-border-color: red; -fx-border-width: 1;");
			} else {
				field.setStyle("-fx-border-color: black; -fx-border-width: 1;");
			}
		}

		public boolean isIncorrect() {
			return incorrect;
		}

		public String getInput() {
			return field.getText();
		}

		public void setListener(ChangeListener<String> listener) {
			field.textProperty().addListener(listener);
		}
	}

	private class PasswordBox extends EntryBox {
		public PasswordBox(String labelText) {
			super(labelText);
		}

		@Override
		public void initField() {
			field = new PasswordField();
			field.setPrefSize(450, 46);
		}
	}

	private class WarningMessage extends HBox {
		private Label messageLabel;
		private SVGPath warningSVGPath;

		public WarningMessage(String message) {
			messageLabel = new Label(message);
			messageLabel.setFont(franklin700_14);
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
	}

	private class BigButton extends Button {
		public BigButton(String labelText) {
			setText(labelText);
			setStyle(
					"-fx-background-color: rgba(0, 0, 0, 1); -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50; -fx-font-size: 20px;");
			setPrefHeight(30);
			setPrefWidth(450);
			setPrefSize(450, 44);
			setFont(franklin700_16);
			setTextFill(Color.WHITE);
		}
	}

	// === === === === === === === === === === === ===
	// BEGIN MAJOR EIZA MODIFICATIONS HERE:
	// === === === === === === === === === === === ===

	private boolean emailExistsInDatabase(String email) {
		return true;
//    	return WebBridgeUserAccount.checkAccountExistsByEmail(webContext, email);
	}
	
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

	private void menuForCreatingAccount() {
		usernameBox.setVisible(true);
		passwordBox.setVisible(true);
		continueButton.setText("Create Account");
		showNextSectionAnimation();
	}

	private void menuForLoggingIn() {
		passwordBox.setVisible(true);
		continueButton.setText("Login");
		showNextSectionAnimation();
	}

	private void initPane() {
    	franklin700_14 = styleManager.getFont("franklin-normal", 700, 14);
        franklin700_16 = styleManager.getFont("franklin-normal", 700, 16);
        cheltenham = styleManager.getFont("cheltenham-normal", 400, 30);
    	
    	BorderPane window = new BorderPane();
    	
        gridLayout = new GridPane();
        gridLayout.setHgap(10);
        gridLayout.setVgap(8);
        
        loginHeadingLabel = new Label("Log in or create an account");
        loginHeadingLabel.setFont(cheltenham);
        loginHeadingLabel.setTextFill(Color.BLACK);
        
        usernameBox = new EntryBox("Username");
        usernameBox.setListener((observable, oldValue, newValue) -> {
        	if(usernameBox.isIncorrect() && isValidUsername(usernameBox.getInput())) {
        		usernameBox.setIncorrect(false);
        		invalidUsernameMessage.setVisible(false);
        	}
        });
        emailBox = new EntryBox("Email");
        emailBox.setListener((observable, oldValue, newValue) -> {
        	if(emailBox.isIncorrect() && isValidEmail(emailBox.getInput())) {
        		emailBox.setIncorrect(false);
        		invalidEmailMessage.setVisible(false);
        	}
        });
        passwordBox = new PasswordBox("Password");
        passwordBox.setListener((observable, oldValue, newValue) -> {
        	if(passwordBox.isIncorrect() && isValidPassword(passwordBox.getInput())) {
        		passwordBox.setIncorrect(false);
        		invalidPassMessage.setVisible(false);
        	}
        });
        
        invalidUsernameMessage = new WarningMessage("Username must be between 1 and 20 characters long.");
        invalidPassMessage = new WarningMessage("Password must be at least 8 characters long."); 
        invalidEmailMessage = new WarningMessage("Please enter a valid email address.");
        
        continueButton = new BigButton("Continue");
        continueButtonPlaceholder = new BigButton("PLACEHOLDER");
        continueButtonPlaceholder.setVisible(false);
        gridLayout.add(continueButtonPlaceholder, 0, 4);
        gridLayout.setAlignment(Pos.CENTER);
        
        nextSectionLayout = new VBox(usernameBox, invalidUsernameMessage, passwordBox, invalidPassMessage);
        for(Node node : nextSectionLayout.getChildren()) {
        	node.setVisible(false);
        }
        
        gridLayout.add(emailBox, 0, 0);
        gridLayout.add(invalidEmailMessage, 0, 1);
        gridLayout.add(continueButton, 0, 2);
        gridLayout.add(nextSectionLayout, 0, 3);
        
//        GridPane.setMargin(continueButton, new javafx.geometry.Insets(16, 0, 0, 0));

        continueButton.setOnAction(event -> {
        	if(isValidEmail(emailBox.getInput())) {
        		isCreatingNewAccount = !emailExistsInDatabase(emailBox.getInput());
            	
            	if(isCreatingNewAccount) {
            		menuForCreatingAccount();
            	} else {
            		menuForLoggingIn();
            	}
        	} else {
        		emailBox.setIncorrect(true);
        		invalidEmailMessage.setVisible(true);
        	}
        });
        	
//            if (tPassword.isVisible()) {
//                String pass = tPassword.getText();
//                String sEmail = tEmail.getText();
//                Boolean matches = isValidEmail(sEmail);
//                if (pass.length() > 7 && matches) {
//                    // loginHeadingLabel
////                    Stage newStage = new Stage();
////                    GameBoard gameBoard = new GameBoard();
////                    gameBoard.start(newStage);
//                    // kill current
////                    stage.close();
//                } else {
//                    if (pass.length() < 8) {
//                     
//                        // Add the password error HBox to the gridLayout
//                        gridLayout.add(passwordErrorBox, 0, 5);
//                        GridPane.setMargin(passwordErrorBox, new javafx.geometry.Insets(5, 0, 0, 0));
//                        tPassword.setStyle("-fx-border-color: red; -fx-border-width: 1;");
//
//                        tPassword.textProperty().addListener((observable, oldValue, newValue) -> {
//                            if (newValue.length() > 7) {
//                                gridLayout.getChildren().remove(passwordErrorBox);
//                                tPassword.setStyle("-fx-border-color: black; -fx-border-width: 1;");
//                            }
//                        });
//                    }
//                    if (!matches) {
//
//                        // Add the email error HBox to the gridLayout
//                        gridLayout.add(errorBox, 0, 2);
//                        GridPane.setMargin(errorBox, new javafx.geometry.Insets(5, 0, 0, 0));
//                        
//                        tEmail.setStyle("-fx-border-color: red; -fx-border-width: 1;");
//
//
//                        tEmail.textProperty().addListener((observable, oldValue, newValue) -> {
//                            if (isValidEmail(newValue)) {
//                                gridLayout.getChildren().remove(errorBox);
//                                GridPane.setRowIndex(continueButton, 5);
//                                tEmail.setStyle("-fx-border-color: black; -fx-border-width: 1;");
//
//                            }
//                        });
//                    }
//                }
//
//            } else {
//                String sEmail = tEmail.getText();
//                Boolean matches = isValidEmail(sEmail);
//                if (matches) {
//                	
//                	
//                    TranslateTransition transition = new TranslateTransition(Duration.millis(500), continueButton);
//                    transition.setByY(40);
//                    transition.setOnFinished(e -> {
//                        password.setVisible(true);
//                        tPassword.setVisible(true);
//                        GridPane.setRowIndex(continueButton, 5);
//                        GridPane.setMargin(password, new javafx.geometry.Insets(10, 0, 0, 0));
//                    });
//                    transition.play();
//                } else {
//
//                  // Add the email error HBox to the gridLayout
//                  gridLayout.add(errorBox, 0, 2);
//                  GridPane.setRowIndex(continueButton, 3);
//                  
//                  tEmail.setStyle("-fx-border-color: red; -fx-border-width: 1;");
//
//                  // Add a listener to the email text field
//                  tEmail.textProperty().addListener((observable, oldValue, newValue) -> {
//                      if (isValidEmail(newValue)) {
//                          gridLayout.getChildren().remove(errorBox);
//                          GridPane.setRowIndex(continueButton, 2);
//                          tEmail.setStyle("-fx-border-color: black; -fx-border-width: 1;");
//                      }
//                  });
//                }
//            }
//        });
        
        verticalLayout = new VBox(20);
        verticalLayout.setAlignment(Pos.CENTER);
        verticalLayout.getChildren().addAll(loginHeadingLabel, gridLayout);

        window.setCenter(verticalLayout);
        setCenter(window);
    }

	private boolean isValidUsername(String username) {
		return username.length() >= 1 && username.length() <= 20;
	}

	private boolean isValidPassword(String password) {
		return password.length() >= 8;
	}

	private boolean isValidEmail(String email) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
		return email.matches(emailRegex);
	}

	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext = webContext;
	}

	@Override
	public WebContext getWebContext() {
		return webContext;
	}
}