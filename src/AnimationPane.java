import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class AnimationPane extends Pane {
	private static final int SWAP_TRANS_MS = 1000;
	private static final int BUFFER_MS = 50;
	private static final int PLACEHOLDER_MS = 5;
	private static final int SHOW_CORRECT_MS = 750;
	private Set<GameTileWord> usedOriginalPieces = new HashSet<>();
	private Set<GameTileWord> usedGhostPieces = new HashSet<>();
	private List<Integer> swapUnselectedCol = new ArrayList<>();
	private List<Integer> swapSelectedRow = new ArrayList<>();
	private List<Integer> swapSelectedCol = new ArrayList<>();
	private GridPane watchGridPane;
	private GameBoard gameBoard;

	public AnimationPane(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
		this.watchGridPane = gameBoard.getWordGridPane();
	}

	public SequentialTransition getSwapTransitions() {
		SequentialTransition sequence = new SequentialTransition();

		PauseTransition preparePause = new PauseTransition(Duration.millis(PLACEHOLDER_MS));
		preparePause.setOnFinished(event -> {
			this.setVisible(true);
			for (GameTileWord piece : usedOriginalPieces) {
				piece.setVisible(false);
			}
			for (GameTileWord piece : usedGhostPieces) {
				piece.setVisible(true);
			}
		});

		PauseTransition pauseForSwapping = new PauseTransition(Duration.millis(SWAP_TRANS_MS + BUFFER_MS));
		pauseForSwapping.setOnFinished(event -> {
			transitionFinished();
		});

		PauseTransition pauseDisplayCorrect = new PauseTransition(Duration.millis(SHOW_CORRECT_MS));
		pauseDisplayCorrect.setOnFinished(event -> {
			System.out.println("end of initial swap animation");
		});

		sequence.getChildren().add(preparePause);

		swapUnselectedCol = new ArrayList<>();
		swapSelectedRow = new ArrayList<>();
		swapSelectedCol = new ArrayList<>();
		usedOriginalPieces = new HashSet<>();
		usedGhostPieces = new HashSet<>();
		ParallelTransition swapPieceParallel = new ParallelTransition();

		for (int c = 0; c < GameBoard.COLS; c++) {
			GameTileWord tileWord = (GameTileWord) getGridNode(gameBoard.getCurrentRow(), c);
			if(!tileWord.getSelectedStatus()) {
				swapUnselectedCol.add(c);
				usedOriginalPieces.add(tileWord);
			}
		}

		for (int r = gameBoard.getCurrentRow() + 1; r < GameBoard.ROWS; r++) {
			for (int c = 0; c < GameBoard.COLS; c++) {
				GameTileWord tileWord = (GameTileWord) getGridNode(r, c);
				if(tileWord.getSelectedStatus()) {
					swapSelectedRow.add(r);
					swapSelectedCol.add(c);
					usedOriginalPieces.add(tileWord);
				}
			}
		}

		for (int i = 0; i < swapUnselectedCol.size(); i++) {
			int destRow = swapSelectedRow.get(i);
			int destCol = swapSelectedCol.get(i);
			int sourceRow = gameBoard.getCurrentRow();
			int sourceCol = swapUnselectedCol.get(i);

			GameTileWord sourcePiece = createGhostPiece(sourceRow, sourceCol);
			GameTileWord destPiece = createGhostPiece(destRow, destCol);

			sourcePiece.setTranslateX(0);
			sourcePiece.setTranslateY(0);

			destPiece.setTranslateX(0);
			destPiece.setTranslateY(0);

			sourcePiece.setVisible(false);
			destPiece.setVisible(false);

			TranslateTransition sourceTrans = new TranslateTransition(Duration.millis(SWAP_TRANS_MS), sourcePiece);
			sourceTrans.setToX(destPiece.getLayoutX() - sourcePiece.getLayoutX());
			sourceTrans.setToY(destPiece.getLayoutY() - sourcePiece.getLayoutY());

			TranslateTransition destTrans = new TranslateTransition(Duration.millis(SWAP_TRANS_MS), destPiece);
			destTrans.setToX(sourcePiece.getLayoutX() - destPiece.getLayoutX());
			destTrans.setToY(sourcePiece.getLayoutY() - destPiece.getLayoutY());
			
			usedGhostPieces.add(destPiece);
			usedGhostPieces.add(sourcePiece);
			swapPieceParallel.getChildren().addAll(sourceTrans, destTrans);
		}

		sequence.getChildren().addAll(swapPieceParallel, pauseForSwapping, pauseDisplayCorrect);

		return sequence;
	}

	private void transitionFinished() {
		this.getChildren().removeAll(usedGhostPieces);

		for (int i = 0; i < swapUnselectedCol.size(); i++) {
			swapGridNode(gameBoard.getCurrentRow(), swapUnselectedCol.get(i), swapSelectedRow.get(i), swapSelectedCol.get(i));
		}

		for (Node node : watchGridPane.getChildren()) {
			if (GridPane.getRowIndex(node) >= gameBoard.getCurrentRow()) {
				node.setVisible(true);
			}
		}

		swapUnselectedCol = new ArrayList<>();
		swapSelectedRow = new ArrayList<>();
		swapSelectedCol = new ArrayList<>();
		usedOriginalPieces = new HashSet<>();

		this.setVisible(false);
		gameBoard.gameDeselect();
		gameBoard.advanceRow();
	}
//	
//	private void playCategoryAnimation() {
//		Set<GameTileWord> displayRowPieces = new HashSet<>();
//		Set<String> displayRowWordsLower = new HashSet<>();
//		
//		for (Node node : watchGridPane.getChildren()) {
//			if (GridPane.getRowIndex(node) == gameBoard.getCurrentRow() - 1) {
//				GameTileWord tileWord = (GameTileWord) node; 
//				
//				displayRowPieces.add(tileWord);
//				displayRowWordsLower.add(tileWord.getWord().getText().toLowerCase());
//			}
//		}
//		
//		GameAnswerColor matchedAnswer = null;
//		for(DifficultyColor color : DifficultyColor.getAllColors()) {
//			GameAnswerColor colorAnswer = gameBoard.getCurrentGame().getAnswerForColor(color);
//			if(colorAnswer.wordMatchesSet(displayRowWordsLower)) {
//				matchedAnswer = colorAnswer;
//			}
//		}
//		
//		if(matchedAnswer == null) {
//			System.out.println("ERROR: could not find color difficulty");
//			return;
//			
//		}
//
//		int hardCodeX = 88;
//		int hardCodeY = 88;
//		
////		Node leftmostNode = getGridNode(currentRow - 1, 0);
////		System.out.println("first element " + ((Text)((StackPane) leftmostNode ).getChildren().get(1)).getText());
////		System.out.println(leftmostNode.getLayoutX());
////		System.out.println(leftmostNode.getLayoutY());
//		
//		SequentialTransition sequence = new SequentialTransition();
//		GameTileAnswer tileAnswer = new GameTileAnswer(matchedAnswer, gameBoard);  
//		
//		this.getChildren().add(displayCategoryStack);
////		displayCategoryStack.setLayoutX(leftmostNode.getLayoutX());
////		displayCategoryStack.setLayoutY(leftmostNode.getLayoutY() * (currentRow - 1));
//		displayCategoryStack.setLayoutX(hardCodeX);
//		displayCategoryStack.setLayoutY((GAP + RECTANGLE_HEIGHT) * (currentRow - 1));
//		displayCategoryStack.setVisible(false);
//		
//		ScaleTransition scaleTrans = new ScaleTransition(Duration.millis(CATEGORY_SCALE_MS), displayCategoryStack); 
//		
//		scaleTrans.setFromX(1); // Initial scaleX
//		scaleTrans.setFromY(1); // Initial scaleY
//		scaleTrans.setToX(1.75); // Initial scaleX
//		scaleTrans.setToY(1.75); // Initial scaleY
//        scaleTrans.setAutoReverse(true);
//        scaleTrans.setCycleCount(2);
//        
//        System.out.println(displayRowPieces);
//        
//		PauseTransition preparePause = new PauseTransition(Duration.millis(PLACEHOLDER_MS));
//		preparePause.setOnFinished(event -> {
//			for(Node node : displayRowPieces) {
//				node.setVisible(false);
//			}
//			displayCategoryStack.setVisible(true);
//			this.setVisible(true);
//		});
//		
//		scaleTrans.setOnFinished(event -> {
//			this.getChildren().remove(displayCategoryStack);
//			this.setVisible(false);
//			watchGridPane.getChildren().removeAll(displayRowPieces);
//			watchGridPane.add(displayCategoryStack, 0, currentRow - 1);
//			GridPane.setColumnSpan(displayCategoryStack, COLS);
//		});
//        
//        sequence.getChildren().addAll(preparePause, scaleTrans);
//        sequence.play();
//	}

	private GameTileWord createGhostPiece(int row, int col) {
		GameTileWord original = (GameTileWord) getGridNode(row, col);
		GameTileWord copy = new GameTileWord(original);
		copy.disable();

		this.getChildren().add(copy);
		copy.setLayoutX(original.getLayoutX());
		copy.setLayoutY(original.getLayoutY());

		return copy;
	}

	private void swapGridNode(int sourceRow, int sourceCol, int destRow, int destCol) {
		Node node1 = getGridNode(sourceRow, sourceCol);
		Node node2 = getGridNode(destRow, destCol);

		watchGridPane.getChildren().removeAll(node1, node2);

		watchGridPane.add(node1, destCol, destRow);
		watchGridPane.add(node2, sourceCol, sourceRow);
	}

	private Node getGridNode(int row, int column) {
		for (Node node : watchGridPane.getChildren()) {
			if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
				return node;
			}
		}
		return null;
	}
}