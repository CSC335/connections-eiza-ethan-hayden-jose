package com.connections.web;

public class WebSessionContext {
	private WebSession session;

	public WebSessionContext(WebSession session) {
		this.session = session;
	}

	public WebSession getSession() {
		return session;
	}
}
