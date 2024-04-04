import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GameTileAnswer extends StackPane {
	private GameBoard gameBoard;
	private GameAnswerColor answer;
	private StyleManager styleManager;
	private Text categoryNameText;
	private Text wordListText;
	private VBox textVBox;
	private Rectangle rectBackground;
	
	public GameTileAnswer(GameAnswerColor answer, GameBoard gameBoard) {
		this.styleManager = gameBoard.getStyleManager();
		this.gameBoard = gameBoard;
		this.answer = answer;
		
		categoryNameText = new Text(answer.getDescription().toUpperCase());
		categoryNameText.setFont(styleManager.getFont("franklin-normal",700, 20));

		wordListText = new Text(answer.getWordListString());
		wordListText.setFont(styleManager.getFont("franklin-normal",500, 20));
		
		textVBox = new VBox(categoryNameText, wordListText);
		textVBox.setAlignment(Pos.CENTER);
		rectBackground = new Rectangle(GameBoard.RECTANGLE_WIDTH * 4 + GameBoard.GAP * 3, GameBoard.RECTANGLE_HEIGHT);
		rectBackground.setArcWidth(GameBoard.CORNER_RADIUS);
		rectBackground.setArcHeight(GameBoard.CORNER_RADIUS);
		
		refreshStyle();

		this.getChildren().addAll(rectBackground, textVBox);
	}
	
	public void refreshStyle() {
		wordListText.setFill(styleManager.colorTextNeutral());
		categoryNameText.setFill(styleManager.colorTextNeutral());
		rectBackground.setFill(styleManager.colorDifficulty(answer.getColor()));
	}
}
