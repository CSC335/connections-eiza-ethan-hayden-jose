import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GameTileWord extends StackPane {
	private boolean selected;
	private boolean incorrect;
	private Rectangle rectangle;
	private Text text;
	private Font font;
	private Word word;
	private StyleManager styleManager;
	private GameBoard gameBoard;

	public GameTileWord(GameTileWord other) {
		selected = other.selected;
		incorrect = other.incorrect;
		styleManager = other.styleManager;
		gameBoard = other.gameBoard;
		word = other.word;
		font = other.font;
		
		initAssets();
		enable();
		refreshStyle();
	}
	
	public GameTileWord(Font font, GameBoard gameBoard) {
		this.word = null;
		this.font = font;
		this.gameBoard = gameBoard;
		this.styleManager = gameBoard.getStyleManager();
		initAssets();
		enable();
	}

	public GameTileWord(Word word, Font font, GameBoard gameBoard) {
		this(font, gameBoard);
		setWord(word);
	}

	public void setWord(Word word) {
		if(word != null) {
			this.word = word;
			text.setText(word.getText().toUpperCase());	
		}
	}

	public void setSelectedStatus(boolean selected) {
		this.selected = selected;

		if (selected) {
			setStyleSelected();
		} else {
			setStyleDefault();
		}
	}

	public boolean getSelectedStatus() {
		return selected;
	}
	
	public void setIncorrectStatus(boolean incorrect) {
		this.incorrect = incorrect;
		
		if(incorrect) {
			setStyleIncorrect();
		} else {
			setStyleDefault();
		}
	}
	
	public boolean getIncorrectStatus() {
		return incorrect;
	}

	public Word getWord() {
		return word;
	}

	private void initAssets() {
		rectangle = new Rectangle(GameBoard.RECTANGLE_WIDTH, GameBoard.RECTANGLE_HEIGHT);
		rectangle.setArcWidth(GameBoard.CORNER_RADIUS);
		rectangle.setArcHeight(GameBoard.CORNER_RADIUS);
		rectangle.setFill(styleManager.colorDefaultRectangle());

		text = new Text();
		text.setFont(font);
		text.setFill(styleManager.colorText());
		setWord(word);
		
		this.getChildren().addAll(rectangle, text);
	}

	private void setStyleDefault() {
		rectangle.setFill(styleManager.colorDefaultRectangle());
		text.setFill(styleManager.colorText());
	}

	private void setStyleSelected() {
		rectangle.setFill(styleManager.colorSelectedRectangle());
		text.setFill(styleManager.colorTextInverted());
	}

	private void setStyleIncorrect() {
		rectangle.setFill(styleManager.colorIncorrectRectangle());
		text.setFill(styleManager.colorTextInverted());
	}

	public void disable() {
		this.setDisable(true);
		this.setOnMouseClicked(null);
		this.setOnMouseEntered(null);
		this.setOnMouseExited(null);
	}

	public void enable() {
		this.setDisable(false);
		this.setOnMouseClicked(event -> {
			if(!selected && gameBoard.getSelectedCount() < GameBoard.MAX_SELECTED) {
				setSelectedStatus(true);
				gameBoard.incrementSelectedCount();
			} else if(selected) {
				setSelectedStatus(false);
				gameBoard.decrementSelectedCount();
			}
			
			Button deselectButton = gameBoard.getDeselectButton();
			Button submitButton = gameBoard.getSubmitButton();

			deselectButton.setDisable(gameBoard.getSelectedCount() == 0);
			submitButton.setDisable(gameBoard.getSelectedCount() != GameBoard.MAX_SELECTED);

			if (gameBoard.getSelectedCount() != 0) {
				deselectButton.setStyle(
						"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50;");
			}

			if (gameBoard.getSelectedCount() == GameBoard.MAX_SELECTED) {
				submitButton.setStyle(
						"-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50;");
			} else {
				submitButton.setStyle(
						"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px; -fx-background-radius: 50; -fx-border-radius: 50;");
			}
		});

		this.setOnMouseEntered(event -> {
			this.setCursor(Cursor.HAND);
		});

		this.setOnMouseExited(event -> {
			this.setCursor(Cursor.DEFAULT);
		});
	}
	
	public void refreshStyle() {
		if(selected && incorrect) {
			setStyleDefault();
		} else if(selected) {
			setStyleSelected();
		} else if(incorrect) {
			setStyleIncorrect();
		} else {
			setStyleDefault();
		}
	}
}
