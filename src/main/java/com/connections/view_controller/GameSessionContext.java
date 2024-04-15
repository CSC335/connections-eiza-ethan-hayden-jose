package com.connections.view_controller;

import com.connections.model.*;

public class GameSessionContext {
	private StyleManager styleManager;
	private GameData gameData;
	
	public GameSessionContext(StyleManager styleManager, GameData gameData) {
		this.styleManager = styleManager;
		this.gameData = gameData;
	}
	
	public StyleManager getStyleManager() {
		return styleManager;
	}
	
	public GameData getGameData() {
		return gameData;
	}
}
