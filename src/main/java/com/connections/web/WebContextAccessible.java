package com.connections.web;

/**
 * An interface representing an object that can access and set the WebContext.
 * The WebContext contains information for accessing the database, as well as
 * the browser settings and cookies.
 */
public interface WebContextAccessible {
	/**
	 * Sets the WbeContext, which has information for accessing the database as well
	 * as the browser settings and cookies.
	 * 
	 * @param webContext the WebContext object
	 */
	void setWebContext(WebContext webContext);

	/**
	 * Gets the WbeContext, which has information for accessing the database as well
	 * as the browser settings and cookies.
	 * 
	 * @param webContext the WebContext object
	 */
	WebContext getWebContext();
}
