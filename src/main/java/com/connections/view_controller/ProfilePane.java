package com.connections.view_controller;

import com.connections.web.WebContext;
import com.connections.web.WebUser;
import com.connections.web.WebUserAccount;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * The ProfilePane class represents a pane that displays and allows editing of a user's profile information.
 * It shows the user's username, email, password, and bio, and provides options to edit each field.
 */
public class ProfilePane extends VBox implements Modular {
    private GameSessionContext gameSessionContext;
    private WebContext webContext;
    private WebUser user;

    private Label guestErrorMessageLabel;
    private Label usernameLabel;
    private Label emailLabel;
    private Label passwordLabel;
    private Label bioLabel;
    private Label usernameFieldLabel;
    private Label emailFieldLabel;
    private Label passwordFieldLabel;
    private Label bioFieldLabel;

    private TextField usernameTextField;
    private TextField emailTextField;
    private PasswordField passwordField;
    private TextField passwordTextField;
    private TextField bioTextField;

    private CircularButton editUsernameButton;
    private CircularButton editEmailButton;
    private CircularButton editPasswordButton;
    private CircularButton editBioButton;
    private CircularButton saveUsernameButton;
    private CircularButton cancelUsernameButton;
    private CircularButton saveEmailButton;
    private CircularButton cancelEmailButton;
    private CircularButton savePasswordButton;
    private CircularButton cancelPasswordButton;
    private CircularButton saveBioButton;
    private CircularButton cancelBioButton;
    private CircularButton showPasswordButton;
    private CircularButton hidePasswordButton;

    private WarningMessage invalidUsernameMessage;
    private WarningMessage invalidEmailMessage;
    private WarningMessage invalidPasswordMessage;
    
    private GridPane gridPane;
    
    /**
     * The WarningMessage class represents a warning message displayed when a field input is invalid.
     * It consists of a label and a warning SVG icon.
     */
    private class WarningMessage extends HBox {
		private Label messageLabel;
		private SVGPath warningSVGPath;

        /**
         * Constructs a new WarningMessage with the specified message.
         *
         * @param message the warning message to be displayed
         */
		public WarningMessage(String message) {
			messageLabel = new Label(message);
			messageLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 700, 14));
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
         * Sets the warning message to be displayed.
         *
         * @param message the warning message to be set
         */
		public void setMessage(String message) {
			messageLabel.setText(message);
		}
	}

    /**
     * Constructs a new ProfilePane with the specified GameSessionContext.
     *
     * @param gameSessionContext the GameSessionContext used by the profile pane
     */
    public ProfilePane(GameSessionContext gameSessionContext) {
        this.gameSessionContext = gameSessionContext;
        this.webContext = gameSessionContext.getWebContext();
        user = gameSessionContext.getWebSessionContext().getSession().getUser();

        if (user.getType() == WebUser.UserType.GUEST) {
            initializeGuestMessage();
        } else {
            initializeProfile();
        }
    }

    /**
     * Initializes the guest message when the user is playing as a guest.
     */
    private void initializeGuestMessage() {
    	guestErrorMessageLabel = new Label("You are playing as a guest. Create an account or login to view or edit your profile!");
    	guestErrorMessageLabel.setWrapText(true);
    	guestErrorMessageLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
        getChildren().add(guestErrorMessageLabel);
        setAlignment(Pos.CENTER);
    }

    /**
     * Initializes the profile information and editing components.
     */
    private void initializeProfile() {
        usernameLabel = new Label(user.getUserName());
        usernameLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
        
        emailLabel = new Label(user.getEmail());
        emailLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
        
        passwordLabel = new Label("••••••••");
        passwordLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
        
        bioLabel = new Label(user.getBio());
        bioLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));

        usernameTextField = new TextField(user.getUserName());
        usernameTextField.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
        
        emailTextField = new TextField(user.getEmail());
        emailTextField.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));

        passwordField = new PasswordField();
        passwordField.setText(user.getPassWord());
        passwordTextField = new TextField(user.getPassWord());
        passwordTextField.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));

        bioTextField = new TextField(user.getBio());
        bioTextField.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));

        editUsernameButton = createEditButton(usernameLabel, usernameTextField);
        editEmailButton = createEditButton(emailLabel, emailTextField);
        editPasswordButton = createEditButton(passwordLabel, passwordField);
        editBioButton = createEditButton(bioLabel, bioTextField);

        saveUsernameButton = createSaveButton(usernameLabel, usernameTextField);
        cancelUsernameButton = createCancelButton(usernameLabel, usernameTextField);
        saveEmailButton = createSaveButton(emailLabel, emailTextField);
        cancelEmailButton = createCancelButton(emailLabel, emailTextField);
        savePasswordButton = createSaveButton(passwordLabel, passwordField);
        cancelPasswordButton = createCancelButton(passwordLabel, passwordField);
        saveBioButton = createSaveButton(bioLabel, bioTextField);
        cancelBioButton = createCancelButton(bioLabel, bioTextField);

        showPasswordButton = new CircularButton("Show", 16, gameSessionContext, false);
        hidePasswordButton = new CircularButton("Hide", 16, gameSessionContext, false);

        showPasswordButton.setOnAction(e -> {
            passwordLabel.setText(user.getPassWord());
            showPasswordButton.setVisible(false);
            hidePasswordButton.setVisible(true);
        });
        showPasswordButton.setOnMouseEntered(e -> {
        	showPasswordButton.setFillStyle(true);
        });
        showPasswordButton.setOnMouseExited(e -> {
        	showPasswordButton.setFillStyle(false);
        });

        hidePasswordButton.setOnAction(e -> {
            passwordLabel.setText("••••••••");
            hidePasswordButton.setVisible(false);
            showPasswordButton.setVisible(true);
        });
        hidePasswordButton.setOnMouseEntered(e -> {
        	hidePasswordButton.setFillStyle(true);
        });
        hidePasswordButton.setOnMouseExited(e -> {
        	hidePasswordButton.setFillStyle(false);
        });
        
        hidePasswordButton.setVisible(false);

        usernameFieldLabel = new Label("Username:");
        emailFieldLabel = new Label("Email:");
        passwordFieldLabel = new Label("Password:");
        bioFieldLabel = new Label("Bio:");

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
     
        gridPane.add(usernameFieldLabel, 0, 0);
        gridPane.add(usernameLabel, 1, 0);
        gridPane.add(editUsernameButton, 2, 0);

        gridPane.add(emailFieldLabel, 0, 1);
        gridPane.add(emailLabel, 1, 1);
        gridPane.add(editEmailButton, 2, 1);

        gridPane.add(passwordFieldLabel, 0, 2);
        gridPane.add(passwordLabel, 1, 2);
        gridPane.add(showPasswordButton, 2, 2);
        gridPane.add(hidePasswordButton, 2, 2);
        gridPane.add(editPasswordButton, 3, 2);

        gridPane.add(bioFieldLabel, 0, 3);
        gridPane.add(bioLabel, 1, 3);
        gridPane.add(editBioButton, 2, 3);

        usernameFieldLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
        emailFieldLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
        passwordFieldLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
        bioFieldLabel.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 16));
        usernameTextField.setStyle("-fx-pref-width: 200px;");
        emailTextField.setStyle("-fx-pref-width: 200px;");
        passwordField.setStyle("-fx-pref-width: 200px;");
        bioTextField.setStyle("-fx-pref-width: 200px;");
        
        invalidUsernameMessage = new WarningMessage("...");
        invalidEmailMessage = new WarningMessage("Please enter a valid email address.");
        invalidPasswordMessage = new WarningMessage("...");

        HBox hbox = new HBox(gridPane);
        hbox.setAlignment(Pos.CENTER);

        getChildren().add(hbox);

        // Center the ProfilePane vertically
        setAlignment(Pos.CENTER);
    }

    /**
     * Creates an edit button for the specified label and text field.
     *
     * @param label     the label associated with the field
     * @param textField the text field associated with the field
     * @return the created edit button
     */
    private CircularButton createEditButton(Label label, TextField textField) {
        CircularButton editButton = new CircularButton("Edit", 16, gameSessionContext, false);
        editButton.setOnAction(e -> {
            editField(label, textField, editButton, getCorrespondingSaveButton(label), getCorrespondingCancelButton(label));
        });
        editButton.setOnMouseEntered(e -> {
            editButton.setFillStyle(true);
        });
        editButton.setOnMouseExited(e -> {
            editButton.setFillStyle(false);
        });
        return editButton;
    }

    /**
     * Creates a save button for the specified label and text field.
     *
     * @param label     the label associated with the field
     * @param textField the text field associated with the field
     * @return the created save button
     */
    private CircularButton createSaveButton(Label label, TextField textField) {
        CircularButton saveButton = new CircularButton("Save", 16, gameSessionContext, false);
        saveButton.setOnAction(e -> saveField(label, textField));
        saveButton.setOnMouseEntered(e -> {
            saveButton.setFillStyle(true);
        });
        saveButton.setOnMouseExited(e -> {
            saveButton.setFillStyle(false);
        });
        return saveButton;
    }

    /**
     * Creates a cancel button for the specified label and text field.
     *
     * @param label     the label associated with the field
     * @param textField the text field associated with the field
     * @return the created cancel button
     */
    private CircularButton createCancelButton(Label label, TextField textField) {
        CircularButton cancelButton = new CircularButton("Cancel", 16, gameSessionContext, false);
        cancelButton.setOnAction(e -> cancelField(label, textField));
        cancelButton.setOnMouseEntered(e -> {
            cancelButton.setFillStyle(true);
        });
        cancelButton.setOnMouseExited(e -> {
            cancelButton.setFillStyle(false);
        });
        return cancelButton;
    }

    /**
     * Edits the specified field by replacing the label with a text field and showing the save and cancel buttons.
     *
     * @param label       the label associated with the field
     * @param textField   the text field associated with the field
     * @param editButton  the edit button associated with the field
     * @param saveButton  the save button associated with the field
     * @param cancelButton the cancel button associated with the field
     */
    private void editField(Label label, TextField textField, CircularButton editButton, CircularButton saveButton, CircularButton cancelButton) {
        if (label.getParent() instanceof GridPane) {
            int row = GridPane.getRowIndex(label);
            GridPane gridPane = (GridPane) label.getParent();
            gridPane.getChildren().remove(label);
            gridPane.add(textField, 1, row);

            if (label == passwordLabel) {
                showPasswordButton.setVisible(false);
                hidePasswordButton.setVisible(false);
                textField = passwordField; // Use the PasswordField instead of TextField
            }

            if (!gridPane.getChildren().contains(saveButton)) {
                gridPane.add(saveButton, 2, row);
            }
            if (!gridPane.getChildren().contains(cancelButton)) {
                gridPane.add(cancelButton, 3, row);
            }

            editButton.setVisible(false);
            saveButton.setVisible(true);
            cancelButton.setVisible(true);
        }
    }

    /**
     * Saves the edited field value if it is valid and updates the user's profile information.
     *
     * @param label     the label associated with the field
     * @param textField the text field associated with the field
     */
    private void saveField(Label label, TextField textField) {
        boolean valid = true;

        if (label == usernameLabel) {
            if (!isValidUsername(textField.getText()) || !isDatabaseValidUsername(textField.getText())) {
                showWarningMessage(invalidUsernameMessage, textField);
                valid = false;
            } else {
                hideWarningMessage(invalidUsernameMessage, textField);
            }
        } else if (label == emailLabel) {
            if (!isValidEmail(textField.getText())) {
                showWarningMessage(invalidEmailMessage, textField);
                valid = false;
            } else {
                hideWarningMessage(invalidEmailMessage, textField);
            }
        } else if (label == passwordLabel) {
            if (!isValidPassword(passwordField.getText())) {
                showWarningMessage(invalidPasswordMessage, passwordField);
                valid = false;
            } else {
                hideWarningMessage(invalidPasswordMessage, passwordField);
            }
        }

        if (valid) {
        	user.readFromDatabase();
            if (label == usernameLabel) {
                user.setUserName(textField.getText());
                usernameLabel.setText(textField.getText());
            } else if (label == emailLabel) {
                user.setEmail(textField.getText());
                emailLabel.setText(textField.getText());
            } else if (label == passwordLabel) {
                user.setPassWord(passwordField.getText());
                passwordLabel.setText("••••••••");
                showPasswordButton.setVisible(true);
                hidePasswordButton.setVisible(false);
            } else if (label == bioLabel) {
                user.setBio(textField.getText());
                bioLabel.setText(textField.getText());
            }
            user.writeToDatabase();
            replaceTextFieldWithLabel(textField, label);
            hideEditControls(label);

            GridPane gridPane = (GridPane) label.getParent();
            gridPane.getChildren().remove(getCorrespondingSaveButton(label));
            gridPane.getChildren().remove(getCorrespondingCancelButton(label));
        }
    }

    /**
     * Shows the warning message for the specified text field.
     *
     * @param message   the warning message to be shown
     * @param textField the text field associated with the warning message
     */
    private void showWarningMessage(WarningMessage message, TextField textField) {
        message.setVisible(true);
        textField.setStyle("-fx-border-color: red; -fx-border-width: 1;");
        GridPane gridPane = (GridPane) textField.getParent();
        if (gridPane != null) {
            GridPane.setRowIndex(message, GridPane.getRowIndex(textField));
            GridPane.setColumnIndex(message, 1);
            GridPane.setMargin(message, new Insets(60, 0, 0, 0)); // Add top margin
            if (!gridPane.getChildren().contains(message)) {
                gridPane.getChildren().add(message);
            }
        }
    }

    /**
     * Hides the warning message for the specified text field.
     *
     * @param message   the warning message to be hidden
     * @param textField the text field associated with the warning message
     */
    private void hideWarningMessage(WarningMessage message, TextField textField) {
        message.setVisible(false);
        textField.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        GridPane gridPane = (GridPane) textField.getParent();
        if (gridPane != null) {
            gridPane.getChildren().remove(message);
        }
    }

    /**
     * Checks if the username is valid.
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
     * Checks if the username is valid and available in the database.
     *
     * @param username the username to be validated
     * @return true if the username is valid and available, false otherwise
     */
    private boolean isDatabaseValidUsername(String username) {
        if (WebUserAccount.checkAccountExistsByUserName(webContext, username)) {
            invalidUsernameMessage.setMessage("Username has been taken!");
            return false;
        }
        return true;
    }

    /**
     * Checks if the password is valid.
     *
     * @param password the password to be validated
     * @return true if the password is valid, false otherwise
     */
    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            invalidPasswordMessage.setMessage("Password must be at least 8 characters long.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the email is valid.
     *
     * @param email the email to be validated
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    /**
     * Cancels the editing of the specified field and reverts to the original value.
     *
     * @param label     the label associated with the field
     * @param textField the text field associated with the field
     */
    private void cancelField(Label label, TextField textField) {
        replaceTextFieldWithLabel(textField, label);
        hideEditControls(label);

        GridPane gridPane = (GridPane) label.getParent();
        if (gridPane != null) {
            gridPane.getChildren().remove(getCorrespondingSaveButton(label));
            gridPane.getChildren().remove(getCorrespondingCancelButton(label));

            // Reset the specific field to its default state
            if (label == usernameLabel) {
                gridPane.getChildren().remove(invalidUsernameMessage);
                GridPane.setRowIndex(editUsernameButton, GridPane.getRowIndex(label));
                GridPane.setColumnIndex(editUsernameButton, 2);
            } else if (label == emailLabel) {
                gridPane.getChildren().remove(invalidEmailMessage);
                GridPane.setRowIndex(editEmailButton, GridPane.getRowIndex(label));
                GridPane.setColumnIndex(editEmailButton, 2);
            } else if (label == passwordLabel) {
                gridPane.getChildren().remove(invalidPasswordMessage);
                GridPane.setRowIndex(editPasswordButton, GridPane.getRowIndex(label));
                GridPane.setColumnIndex(editPasswordButton, 3);
                GridPane.setColumnIndex(showPasswordButton, 2);
                GridPane.setColumnIndex(hidePasswordButton, 2);
                passwordLabel.setText("••••••••");
                showPasswordButton.setVisible(true);
                hidePasswordButton.setVisible(false);
            } else if (label == bioLabel) {
                GridPane.setRowIndex(editBioButton, GridPane.getRowIndex(label));
                GridPane.setColumnIndex(editBioButton, 2);
            }
        }
    }

    /**
     * Replaces the text field with the corresponding label.
     *
     * @param textField the text field to be replaced
     * @param label     the label to replace the text field
     */
    private void replaceTextFieldWithLabel(TextField textField, Label label) {
        if (textField.getParent() instanceof GridPane) {
            int row = GridPane.getRowIndex(textField);
            GridPane gridPane = (GridPane) textField.getParent();
            gridPane.getChildren().remove(textField);
            gridPane.add(label, 1, row);
        }
    }

    /**
     * Hides the edit controls (save and cancel buttons) for the specified label.
     *
     * @param label the label associated with the field
     */
    private void hideEditControls(Label label) {
        getCorrespondingSaveButton(label).setVisible(false);
        getCorrespondingCancelButton(label).setVisible(false);
        getCorrespondingEditButton(label).setVisible(true);
    }

    /**
     * Returns the corresponding edit button for the specified label.
     *
     * @param label the label associated with the field
     * @return the corresponding edit button
     */
    private CircularButton getCorrespondingEditButton(Label label) {
        if (label == usernameLabel) {
            return editUsernameButton;
        } else if (label == emailLabel) {
            return editEmailButton;
        } else if (label == passwordLabel) {
            return editPasswordButton;
        } else if (label == bioLabel) {
            return editBioButton;
        }
        return null;
    }

    /**
     * Returns the corresponding save button for the specified label.
     *
     * @param label the label associated with the field
     * @return the corresponding save button
     */
    private CircularButton getCorrespondingSaveButton(Label label) {
        if (label == usernameLabel) {
            return saveUsernameButton;
        } else if (label == emailLabel) {
            return saveEmailButton;
        } else if (label == passwordLabel) {
            return savePasswordButton;
        } else if (label == bioLabel) {
            return saveBioButton;
        }
        return null;
    }

    /**
     * Returns the corresponding cancel button for the specified label.
     *
     * @param label the label associated with the field
     * @return the corresponding cancel button
     */
    private CircularButton getCorrespondingCancelButton(Label label) {
        if (label == usernameLabel) {
            return cancelUsernameButton;
        } else if (label == emailLabel) {
            return cancelEmailButton;
        } else if (label == passwordLabel) {
            return cancelPasswordButton;
        } else if (label == bioLabel) {
            return cancelBioButton;
        }
        return null;
    }

    /**
     * Refreshes the style of the profile pane based on the current style manager.
     */
    @Override
    public void refreshStyle() {
    	StyleManager styleManager = gameSessionContext.getStyleManager();
    	
    	if(user.getType() == WebUser.UserType.GUEST) {
    		guestErrorMessageLabel.setTextFill(styleManager.colorText());
    	} else {
    		usernameLabel.setTextFill(styleManager.colorText());
            emailLabel.setTextFill(styleManager.colorText());
            passwordLabel.setTextFill(styleManager.colorText());
            bioLabel.setTextFill(styleManager.colorText());
            usernameFieldLabel.setTextFill(styleManager.colorText());
            emailFieldLabel.setTextFill(styleManager.colorText());
            passwordFieldLabel.setTextFill(styleManager.colorText());
            bioFieldLabel.setTextFill(styleManager.colorText());
            editUsernameButton.refreshStyle();
            editEmailButton.refreshStyle();
            editPasswordButton.refreshStyle();
            editBioButton.refreshStyle();
            saveUsernameButton.refreshStyle();
            cancelUsernameButton.refreshStyle();
            saveEmailButton.refreshStyle();
            cancelEmailButton.refreshStyle();
            savePasswordButton.refreshStyle();
            cancelPasswordButton.refreshStyle();
            saveBioButton.refreshStyle();
            cancelBioButton.refreshStyle();
            showPasswordButton.refreshStyle();
            hidePasswordButton.refreshStyle();
    	}
    }

    /**
     * Returns the GameSessionContext associated with the profile pane.
     *
     * @return the GameSessionContext associated with the profile pane
     */
    @Override
    public GameSessionContext getGameSessionContext() {
        return gameSessionContext;
    }
}