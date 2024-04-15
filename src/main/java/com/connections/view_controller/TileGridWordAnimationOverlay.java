package com.connections.view_controller;

import com.connections.model.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
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

public class TileGridWordAnimationOverlay extends Pane implements Modular {
	private static final int SWAP_TRANS_MS = 350;
	private static final int BUFFER_MS = 5;
	private static final int PLACEHOLDER_MS = 5;

	private TileGridWord tileGridWord;

	private boolean allowChangeVisibility = true;
	private boolean paneShouldBeVisible = false;

	public TileGridWordAnimationOverlay(TileGridWord tileGridWord) {
		this.tileGridWord = tileGridWord;
		setMaxWidth(TileGridWord.PANE_WIDTH);
		setVisible(false);
	}

	private class EaseOutInterpolator extends Interpolator {
		@Override
		protected double curve(double t) {
			return 1 - Math.pow(1 - t, 3);
		}
	}

	private void getWordSwap(Set<GameTileWord> ghostPieceSet, ParallelTransition parallel, int destRow, int destCol,
			int sourceRow, int sourceCol) {
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
		sourceTrans.setInterpolator(new EaseOutInterpolator());

		TranslateTransition destTrans = new TranslateTransition(Duration.millis(SWAP_TRANS_MS), destPiece);
		destTrans.setToX(sourcePiece.getLayoutX() - destPiece.getLayoutX());
		destTrans.setToY(sourcePiece.getLayoutY() - destPiece.getLayoutY());
		destTrans.setInterpolator(new EaseOutInterpolator());

		parallel.getChildren().addAll(sourceTrans, destTrans);
		ghostPieceSet.add(sourcePiece);
		ghostPieceSet.add(destPiece);
	}

	private void getSwapRowColIndex(Set<GameTileWord> pieceSet, List<Integer> destRowList, List<Integer> destColList,
			List<Integer> sourceRowList, List<Integer> sourceColList) {
		for (int c = 0; c < TileGridWord.COLS; c++) {
			GameTileWord tileWord = (GameTileWord) tileGridWord.gridGetNode(tileGridWord.getCurrentSolvingRow(), c);
			if (!tileWord.getSelectedStatus()) {
				sourceRowList.add(tileGridWord.getCurrentSolvingRow());
				sourceColList.add(c);
				pieceSet.add(tileWord);
			}
		}

		for (int r = tileGridWord.getCurrentSolvingRow() + 1; r < TileGridWord.ROWS; r++) {
			for (int c = 0; c < TileGridWord.COLS; c++) {
				GameTileWord tileWord = (GameTileWord) tileGridWord.gridGetNode(r, c);
				if (tileWord.getSelectedStatus()) {
					destRowList.add(r);
					destColList.add(c);
					pieceSet.add(tileWord);
				}
			}
		}
	}

	public SequentialTransition getSequenceCorrectAnswer() {
		Set<GameTileWord> originalPieceSet = new HashSet<>();
		Set<GameTileWord> ghostPieceSet = new HashSet<>();
		List<Integer> destRowList = new ArrayList<>();
		List<Integer> destColList = new ArrayList<>();
		List<Integer> sourceRowList = new ArrayList<>();
		List<Integer> sourceColList = new ArrayList<>();

		SequentialTransition sequence = new SequentialTransition();

		PauseTransition pausePrepareSwapping = new PauseTransition(Duration.millis(PLACEHOLDER_MS));
		pausePrepareSwapping.setOnFinished(event -> {
			if (allowChangeVisibility) {
				this.setVisible(true);
			}
			paneShouldBeVisible = true;
			for (GameTileWord piece : originalPieceSet) {
				piece.setVisible(false);
			}
			for (GameTileWord piece : ghostPieceSet) {
				piece.setVisible(true);
			}
		});

		ParallelTransition parallelSwapPieces = new ParallelTransition();
		getSwapRowColIndex(originalPieceSet, destRowList, destColList, sourceRowList, sourceColList);
		Set<GameTileWord> originalSelectedPieceSet = tileGridWord.getSelectedTileWords();

		for (int i = 0; i < destRowList.size(); i++) {
			getWordSwap(ghostPieceSet, parallelSwapPieces, destRowList.get(i), destColList.get(i), sourceRowList.get(i),
					sourceColList.get(i));
		}

		PauseTransition pauseDuringSwapping = new PauseTransition(Duration.millis(SWAP_TRANS_MS + BUFFER_MS));
		pauseDuringSwapping.setOnFinished(event -> {
			this.getChildren().removeAll(ghostPieceSet);
			for (int i = 0; i < destRowList.size(); i++) {
				tileGridWord.gridSwapNode(destRowList.get(i), destColList.get(i), sourceRowList.get(i), sourceColList.get(i));
			}
			tileGridWord.gridSetNonSolvingNodeVisible(false);
			setVisibleWeak(false);
			tileGridWord.incrementCurrentSolvingRow();
		});

		sequence.getChildren().addAll(pausePrepareSwapping, parallelSwapPieces, pauseDuringSwapping);

		Set<String> displayRowWordsLower = new HashSet<>();
		for (GameTileWord tileWord : originalSelectedPieceSet) {
			displayRowWordsLower.add(tileWord.getWord().getText().toLowerCase());
		}

		GameAnswerColor matchedAnswer = null;
		for (DifficultyColor color : DifficultyColor.getAllColors()) {
			GameAnswerColor colorAnswer = tileGridWord.getGameSessionContext().getGameData().getAnswerForColor(color);
			if (colorAnswer.wordMatchesSet(displayRowWordsLower)) {
				matchedAnswer = colorAnswer;
				break;
			}
		}

		if (matchedAnswer != null) {
			GameTileAnswer tileAnswer = new GameTileAnswer(matchedAnswer, tileGridWord);
			tileAnswer.setLayoutX(0);
			tileAnswer.setLayoutY((TileGridWord.GAP + GameTile.RECTANGLE_HEIGHT) * tileGridWord.getCurrentSolvingRow());

			PauseTransition pauseBeforeDisplayAnswer = new PauseTransition(Duration.millis(PLACEHOLDER_MS));
			pauseBeforeDisplayAnswer.setOnFinished(event -> {
				this.getChildren().add(tileAnswer);
				setVisibleWeak(true);
				for (Node node : originalSelectedPieceSet) {
					node.setVisible(false);
				}
			});

			ParallelTransition tileAppear = tileAnswer.getAppearAnimation();

			tileAppear.setOnFinished(event -> {
				this.getChildren().remove(tileAnswer);
				setVisibleWeak(false);
				tileGridWord.gridRemoveNodeSet(originalSelectedPieceSet);
				tileGridWord.gridSetTileAnswer(tileAnswer);
				tileGridWord.deselectTileWords();
			});

			sequence.getChildren().addAll(pauseBeforeDisplayAnswer, tileAppear);
		} else {
			System.out.printf("ERROR: could not match words %s\n", displayRowWordsLower);
		}

		return sequence;
	}

	private GameTileWord createGhostPiece(int row, int col) {
		GameTileWord original = (GameTileWord) tileGridWord.gridGetNode(row, col);
		GameTileWord copy = new GameTileWord(original);
		copy.disable();

		this.getChildren().add(copy);
		copy.setLayoutX(original.getLayoutX());
		copy.setLayoutY(original.getLayoutY());

		return copy;
	}
	
	private void setVisibleWeak(boolean status) {
		if (allowChangeVisibility) {
			this.setVisible(status);
		}
		paneShouldBeVisible = status;
	}

	public void setVisibleDynamic(boolean status) {
		allowChangeVisibility = status;
		this.setVisible(paneShouldBeVisible);
	}
	
	@Override
	public void refreshStyle() {
		for (Node node : getChildren()) {
			if (node instanceof Modular) {
				Modular stylableNode = (Modular) node;
				stylableNode.refreshStyle();
			}
		}
	}

	@Override
	public GameSessionContext getGameSessionContext() {
		return tileGridWord.getGameSessionContext();
	}
}