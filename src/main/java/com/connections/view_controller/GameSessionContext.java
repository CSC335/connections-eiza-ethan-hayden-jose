package com.connections.view_controller;

import com.connections.model.*;
import com.connections.web.WebContext;

public class GameSessionContext {
	private StyleManager styleManager;
	private GameData gameData;
	private WebContext webContext;
	
	public GameSessionContext(StyleManager styleManager, GameData gameData, WebContext webContext) {
		this.styleManager = styleManager;
		this.gameData = gameData;
		this.webContext = webContext;
	}
	
	public StyleManager getStyleManager() {
		return styleManager;
	}
	
	public GameData getGameData() {
		return gameData;
	}

	public WebContext getWebContext() {
		return webContext;
	}
}
