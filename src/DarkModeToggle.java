import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class DarkModeToggle extends StackPane {
    private Label label;
    private Circle circle;
    private boolean isDarkMode;
    private GameBoard gameBoard;

    public DarkModeToggle(GameBoard gameBoard) {
    	this.gameBoard = gameBoard;
        label = new Label();
        label.setPrefSize(92.5, 37);
        label.setStyle("-fx-background-color: #ebebeb; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 0, 5), dropshadow(gaussian, rgba(255, 255, 255, 0.4), 5, 0, 0, -5);");

        circle = new Circle(16.65);
        circle.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #ffcc89, #d8860b); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);");
        circle.setTranslateX(-27.5);

        getChildren().addAll(label, circle);
        
        label.setOnMouseClicked(event -> toggle());
        circle.setOnMouseClicked(event -> toggle());
    }

    public void toggle() {
        isDarkMode = !isDarkMode;
        gameBoard.getStyleManager().setDarkMode(isDarkMode);
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), circle);
        if (isDarkMode) {
            label.setStyle("-fx-background-color: #242424; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 0, 5), dropshadow(gaussian, rgba(255, 255, 255, 0.4), 5, 0, 0, -5);");
            circle.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #777, #3a3a3a); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);");
            transition.setToX(27.5);
            
        } else {
        	label.setStyle("-fx-background-color: #ebebeb; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 0, 5), dropshadow(gaussian, rgba(255, 255, 255, 0.4), 5, 0, 0, -5);");
            circle.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #ffcc89, #d8860b); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);");
        	transition.setToX(-27.5);
        }
        transition.play();
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }
}