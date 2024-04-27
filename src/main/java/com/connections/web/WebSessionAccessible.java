package com.connections.web;

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
