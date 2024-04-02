import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GameTileAnswer extends StackPane {
	private StyleManager styleManager;
	
	public GameTileAnswer(GameAnswerColor answer, GameBoard gameBoard) {
		this.styleManager = gameBoard.getStyleManager();
		
		Text categoryNameText = new Text(answer.getDescription().toUpperCase());
		categoryNameText.setFont(Font.font("Arial", FontWeight.BOLD, 18));

		Text wordListText = new Text(answer.getWordListString());
		wordListText.setFont(Font.font(18));

		VBox textVBox = new VBox(categoryNameText, wordListText);
		textVBox.setAlignment(Pos.CENTER);
		Rectangle rectBackground = new Rectangle(GameBoard.RECTANGLE_WIDTH * 4 + GameBoard.GAP * 3, GameBoard.RECTANGLE_HEIGHT);
		rectBackground.setFill(styleManager.colorDifficulty(answer.getColor()));
		rectBackground.setArcWidth(GameBoard.CORNER_RADIUS);
		rectBackground.setArcHeight(GameBoard.CORNER_RADIUS);

		this.getChildren().addAll(rectBackground, textVBox);
	}
}
