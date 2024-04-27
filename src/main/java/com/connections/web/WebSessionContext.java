package com.connections.web;

public class WebSessionContext {
	private WebSession session;

	 /**
     * Creates a new WebSessionContext instance.
     *
     * @param session The WebSession associated with this context.
     */
	public WebSessionContext(WebSession session) {
		this.session = session;
	}

	/**
     * Gets the WebSession associated with this context.
     *
     * @return The WebSession associated with this context.
     */
	public WebSession getSession() {
		return session;
	}
}
