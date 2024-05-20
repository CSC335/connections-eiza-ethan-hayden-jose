package com.connections.web;

import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;
import com.mongodb.client.MongoDatabase;

/**
 * The WebContext class represents the context for a web application, containing
 * references to the MongoDB database, the web API, and the JProApplication
 * instance. It provides methods to access these components and facilitates
 * their interaction.
 */
public class WebContext {
	private MongoDatabase mongoDatabase;
	private WebAPI webAPI;
	private JProApplication jproApplication;

	/**
	 * Constructs a WebContext with the specified MongoDatabase, WebAPI, and
	 * JProApplication.
	 *
	 * @param mongoDatabase   The MongoDatabase object representing the connected
	 *                        database.
	 * @param webAPI          The WebAPI object used to interact with the web
	 *                        application.
	 * @param jproApplication The JProApplication object representing the web
	 *                        application.
	 */
	public WebContext(MongoDatabase mongoDatabase, WebAPI webAPI, JProApplication jproApplication) {
		this.jproApplication = jproApplication;
		this.mongoDatabase = mongoDatabase;
		this.webAPI = webAPI;
	}

	/**
	 * Retrieves the MongoDatabase object associated with this WebContext.
	 *
	 * @return The MongoDatabase object representing the connected database.
	 */
	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}

	/**
	 * Retrieves the WebAPI object associated with this WebContext.
	 *
	 * @return The WebAPI object used to interact with the web application.
	 */
	public WebAPI getWebAPI() {
		return webAPI;
	}

	/**
	 * Retrieves the JProApplication object associated with this WebContext.
	 *
	 * @return The JProApplication object representing the web application.
	 */
	public JProApplication getJProApplication() {
		return jproApplication;
	}
}
