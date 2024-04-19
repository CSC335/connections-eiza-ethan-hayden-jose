package com.connections.web;

public interface WebSessionAccessible {
	void setWebSessionContext(WebSessionContext webSessionContext);
	WebSessionContext getWebSessionContext();
}
