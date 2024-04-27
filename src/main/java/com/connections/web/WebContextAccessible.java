package com.connections.web;

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
