package com.connections.view_controller;

import com.connections.model.GameData;
import com.connections.web.WebContext;
import com.connections.web.WebSessionContext;

public class GameSessionContext {
	private StyleManager styleManager;
	private GameData gameData;
	private WebContext webContext;
	private WebSessionContext webSessionContext;

	public GameSessionContext(StyleManager styleManager, GameData gameData, WebContext webContext,
			WebSessionContext webSessionContext) {
		this.styleManager = styleManager;
		this.gameData = gameData;
		this.webContext = webContext;
		this.webSessionContext = webSessionContext;
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

	public WebSessionContext getWebSessionContext() {
		return webSessionContext;
	}
}
