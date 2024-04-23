package com.connections.view_controller;

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
import javafx.scene.text.Text;

public class ProfilePane extends VBox implements Modular {
    private GameSessionContext gameSessionContext;
    private WebUser user;

    private Label usernameLabel;
    private Label emailLabel;
    private Label passwordLabel;
    private Label bioLabel;

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

    public ProfilePane(GameSessionContext gameSessionContext) {
        this.gameSessionContext = gameSessionContext;
        user = gameSessionContext.getWebSessionContext().getSession().getUser();

        if (user.getType() == WebUser.UserType.GUEST) {
            initializeGuestMessage();
        } else {
            initializeProfile();
        }
    }

    private void initializeGuestMessage() {
        Text message = new Text("You are playing as a guest. Create an account or login to view or edit your profile!");
        message.setFont(gameSessionContext.getStyleManager().getFont("franklin-normal", 600, 32));
        getChildren().add(message);
        setAlignment(Pos.CENTER);
    }

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

        Label usernameFieldLabel = new Label("Username:");
        Label emailFieldLabel = new Label("Email:");
        Label passwordFieldLabel = new Label("Password:");
        Label bioFieldLabel = new Label("Bio:");

        GridPane gridPane = new GridPane();
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

        HBox hbox = new HBox(gridPane);
        hbox.setAlignment(Pos.CENTER);

        getChildren().add(hbox);

        // Center the ProfilePane vertically
        setAlignment(Pos.CENTER);
    }

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

    private void saveField(Label label, TextField textField) {
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

    private void cancelField(Label label, TextField textField) {
        replaceTextFieldWithLabel(textField, label);
        hideEditControls(label);

        GridPane gridPane = (GridPane) label.getParent();
        gridPane.getChildren().remove(getCorrespondingSaveButton(label));
        gridPane.getChildren().remove(getCorrespondingCancelButton(label));

        if (label == passwordLabel) {
        	passwordLabel.setText("••••••••");
            showPasswordButton.setVisible(true);
            hidePasswordButton.setVisible(false);
        }
    }

    private void replaceTextFieldWithLabel(TextField textField, Label label) {
        if (textField.getParent() instanceof GridPane) {
            int row = GridPane.getRowIndex(textField);
            GridPane gridPane = (GridPane) textField.getParent();
            gridPane.getChildren().remove(textField);
            gridPane.add(label, 1, row);
        }
    }

    private void hideEditControls(Label label) {
        getCorrespondingSaveButton(label).setVisible(false);
        getCorrespondingCancelButton(label).setVisible(false);
        getCorrespondingEditButton(label).setVisible(true);
    }

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

    @Override
    public void refreshStyle() {
        // Implement any style refreshing logic here
    }

    @Override
    public GameSessionContext getGameSessionContext() {
        return gameSessionContext;
    }
}