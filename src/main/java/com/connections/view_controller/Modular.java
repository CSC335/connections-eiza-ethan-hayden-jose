package com.connections.view_controller;

/**
 * The Modular interface defines methods that should be implemented by classes
 * that need to refresh their style and provide access to the GameSessionContext.
 */
public interface Modular {
	
    /**
     * Refreshes the style of the implementing class based on the current style manager.
     */
	void refreshStyle();

    /**
     * Returns the GameSessionContext associated with the implementing class.
     *
     * @return the GameSessionContext associated with the implementing class
     */
	GameSessionContext getGameSessionContext();
}
