package com.connections.view_controller;

import com.connections.model.GameData;
import com.connections.web.WebContext;
import com.connections.web.WebSessionContext;

/**
 * The GameSessionContext class represents the context of a game session. It
 * holds references to various objects and data required for the game session.
 */
public class GameSessionContext {
	private StyleManager styleManager;
	private GameData gameData;
	private WebContext webContext;
	private WebSessionContext webSessionContext;

	/**
	 * Constructs a new GameSessionContext with the specified StyleManager,
	 * GameData, WebContext, and WebSessionContext.
	 *
	 * @param styleManager      the StyleManager used by the game session
	 * @param gameData          the GameData used by the game session
	 * @param webContext        the WebContext used by the game session
	 * @param webSessionContext the WebSessionContext used by the game session
	 */
	public GameSessionContext(StyleManager styleManager, GameData gameData, WebContext webContext,
			WebSessionContext webSessionContext) {
		this.styleManager = styleManager;
		this.gameData = gameData;
		this.webContext = webContext;
		this.webSessionContext = webSessionContext;
	}

	/**
	 * Returns the StyleManager used by the game session.
	 *
	 * @return the StyleManager used by the game session
	 */
	public StyleManager getStyleManager() {
		return styleManager;
	}

	/**
	 * Returns the GameData used by the game session.
	 *
	 * @return the GameData used by the game session
	 */
	public GameData getGameData() {
		return gameData;
	}

	/**
	 * Returns the WebContext used by the game session.
	 *
	 * @return the WebContext used by the game session
	 */
	public WebContext getWebContext() {
		return webContext;
	}

	/**
	 * Returns the WebSessionContext used by the game session.
	 *
	 * @return the WebSessionContext used by the game session
	 */
	public WebSessionContext getWebSessionContext() {
		return webSessionContext;
	}
}
