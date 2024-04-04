import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class DarkModeToggle extends StackPane {
	private boolean isDarkMode;
	private Label label;
	private Circle circle;
	private GameBoard gameBoard;
	private Pane svgPane;
	private SVGPath sunIconSVG;
	private SVGPath moonIconSVG;

	public DarkModeToggle(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
		label = new Label();
		label.setPrefSize(92.5, 37);
		label.setStyle(
				"-fx-background-color: #ebebeb; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 0, 5), dropshadow(gaussian, rgba(255, 255, 255, 0.4), 5, 0, 0, -5);");

		circle = new Circle(16.65);
		circle.setStyle(
				"-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #ffcc89, #d8860b); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);");
		circle.setTranslateX(-27.5);
		
		moonIconSVG = new SVGPath();
		moonIconSVG.setContent(
				"M349.852,343.15c-49.875,49.916-131.083,49.916-181,0c-49.916-49.918-49.916-131.125,0-181.021  c13.209-13.187,29.312-23.25,47.832-29.812c5.834-2.042,12.293-0.562,16.625,3.792c4.376,4.375,5.855,10.833,3.793,16.625  c-12.542,35.375-4,73.666,22.25,99.917c26.209,26.228,64.5,34.75,99.916,22.25c5.792-2.062,12.271-0.582,16.625,3.793  c4.376,4.332,5.834,10.812,3.771,16.625C373.143,313.838,363.06,329.941,349.852,343.15z M191.477,184.754  c-37.438,37.438-37.438,98.354,0,135.771c40,40.021,108.125,36.416,143-8.168c-35.959,2.25-71.375-10.729-97.75-37.084  c-26.375-26.354-39.333-61.771-37.084-97.729C196.769,179.796,194.039,182.192,191.477,184.754z");
		moonIconSVG.setScaleX(0.05);
		moonIconSVG.setScaleY(0.05);
		moonIconSVG.setTranslateX(-241);
		moonIconSVG.setTranslateY(-241);
		moonIconSVG.setFill(Color.WHITE);
		moonIconSVG.setVisible(false);
		
		sunIconSVG = new SVGPath();
		sunIconSVG.setContent("M256,144c-61.75,0-112,50.25-112,112s50.25,112,112,112s112-50.25,112-112S317.75,144,256,144z M256,336    c-44.188,0-80-35.812-80-80c0-44.188,35.812-80,80-80c44.188,0,80,35.812,80,80C336,300.188,300.188,336,256,336z M256,112    c8.833,0,16-7.167,16-16V64c0-8.833-7.167-16-16-16s-16,7.167-16,16v32C240,104.833,247.167,112,256,112z M256,400    c-8.833,0-16,7.167-16,16v32c0,8.833,7.167,16,16,16s16-7.167,16-16v-32C272,407.167,264.833,400,256,400z M380.438,154.167    l22.625-22.625c6.25-6.25,6.25-16.375,0-22.625s-16.375-6.25-22.625,0l-22.625,22.625c-6.25,6.25-6.25,16.375,0,22.625    S374.188,160.417,380.438,154.167z M131.562,357.834l-22.625,22.625c-6.25,6.249-6.25,16.374,0,22.624s16.375,6.25,22.625,0    l22.625-22.624c6.25-6.271,6.25-16.376,0-22.625C147.938,351.583,137.812,351.562,131.562,357.834z M112,256    c0-8.833-7.167-16-16-16H64c-8.833,0-16,7.167-16,16s7.167,16,16,16h32C104.833,272,112,264.833,112,256z M448,240h-32    c-8.833,0-16,7.167-16,16s7.167,16,16,16h32c8.833,0,16-7.167,16-16S456.833,240,448,240z M131.541,154.167    c6.251,6.25,16.376,6.25,22.625,0c6.251-6.25,6.251-16.375,0-22.625l-22.625-22.625c-6.25-6.25-16.374-6.25-22.625,0    c-6.25,6.25-6.25,16.375,0,22.625L131.541,154.167z M380.459,357.812c-6.271-6.25-16.376-6.25-22.625,0    c-6.251,6.25-6.271,16.375,0,22.625l22.625,22.625c6.249,6.25,16.374,6.25,22.624,0s6.25-16.375,0-22.625L380.459,357.812z");
		sunIconSVG.setScaleX(0.04);
		sunIconSVG.setScaleY(0.04);
		sunIconSVG.setTranslateX(-241);
		sunIconSVG.setTranslateY(-241);
		sunIconSVG.setFill(Color.WHITE);
		
		svgPane = new Pane(sunIconSVG, moonIconSVG);
		svgPane.setMaxWidth(30);
		svgPane.setMaxHeight(30);
		svgPane.setTranslateX(-28);
		
		StackPane knobPane = new StackPane(circle, svgPane);
		getChildren().addAll(label, knobPane);

		label.setOnMouseClicked(event -> toggle());
		knobPane.setOnMouseClicked(event -> toggle());
	}

	public void toggle() {
		isDarkMode = !isDarkMode;
		gameBoard.getStyleManager().setDarkMode(isDarkMode);
		TranslateTransition transition = new TranslateTransition(Duration.millis(300), circle);
		TranslateTransition transitionSVG = new TranslateTransition(Duration.millis(300), svgPane);
		if (isDarkMode) {
			label.setStyle(
					"-fx-background-color: #242424; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 0, 5), dropshadow(gaussian, rgba(255, 255, 255, 0.4), 5, 0, 0, -5);");
			circle.setStyle(
					"-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #777, #3a3a3a); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);");
			transition.setToX(27.5);
			transitionSVG.setToX(27.5);
			moonIconSVG.setVisible(true);
			sunIconSVG.setVisible(false);
		} else {
			label.setStyle(
					"-fx-background-color: #ebebeb; -fx-background-radius: 200px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0, 0, 5), dropshadow(gaussian, rgba(255, 255, 255, 0.4), 5, 0, 0, -5);");
			circle.setStyle(
					"-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #ffcc89, #d8860b); -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 5);");
			transition.setToX(-27.5);
			transitionSVG.setToX(-27.5);
			moonIconSVG.setVisible(false);
			sunIconSVG.setVisible(true);
		}
		transition.play();
		transitionSVG.play();
	}

	public boolean isDarkMode() {
		return isDarkMode;
	}
}