package com.connections.view_controller;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * The HintMenuButton class represents a button with a hint icon in the menu. It
 * extends the SVGButton class and implements the Modular interface.
 */
public class BackMenuButton extends SVGButton implements Modular {
	
	/**
	 * Constructs a new HintMenuButton with the specified GameSessionContext.
	 *
	 * @param gameSessionContext the GameSessionContext used by the hint menu button
	 */
	public BackMenuButton(GameSessionContext gameSessionContext) {
		super(gameSessionContext);
		SVGPath backSVG = new SVGPath();
		backSVG.setContent("M18 10H2m0 0l7-7m-7 7l7 7");
		backSVG.setStrokeLineCap(StrokeLineCap.ROUND);
		backSVG.setStrokeLineJoin(StrokeLineJoin.ROUND);
		backSVG.setStrokeWidth(2);

		backSVG.setScaleX(1.3875);
		backSVG.setScaleY(1.3875);
		backSVG.setTranslateX(3);
		backSVG.setTranslateY(7);

		setSVG(backSVG);
		refreshStyle();
	}

	public BackMenuButton() {
		this(null);
	}

	/**
	 * Refreshes the style of the hint menu button based on the current style
	 * manager.
	 */
	@Override
	public void refreshStyle() {
		if (gameSessionContext == null) {
			if(isDisabled()) {
				svgPath.setStroke(Color.GRAY);
			} else {
				svgPath.setStroke(Color.BLACK);
			}
		} else {
			StyleManager styleManager = gameSessionContext.getStyleManager();

			if (isDisabled()) {
				svgPath.setStroke(styleManager.colorTextDisabled());
			} else {
				svgPath.setStroke(styleManager.colorText());
			}
		}
	}
}