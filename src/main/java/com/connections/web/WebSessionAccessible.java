package com.connections.web;

/**
 * An interface representing an object that can access and set the WebContext.
 * The WebContext contains information for accessing the database, as well as
 * the browser settings and cookies.
 */
public interface WebSessionAccessible {
	/**
	 * Sets the WebSessionContext for this object.
	 *
	 * @param webSessionContext The WebSessionContext to be set.
	 */
	void setWebSessionContext(WebSessionContext webSessionContext);

	/**
	 * Gets the WebSessionContext associated with this object.
	 *
	 * @return The WebSessionContext associated with this object.
	 */
	WebSessionContext getWebSessionContext();
}
