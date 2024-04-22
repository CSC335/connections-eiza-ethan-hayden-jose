package com.connections.web;

import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;
import com.mongodb.client.MongoDatabase;

public class WebContext {
	private MongoDatabase mongoDatabase;
	private WebAPI webAPI;
	private JProApplication jproApplication;
	
	public WebContext(MongoDatabase mongoDatabase, WebAPI webAPI, JProApplication jproApplication) {
		this.jproApplication = jproApplication;
		this.mongoDatabase = mongoDatabase;
		this.webAPI = webAPI;
	}
	
	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}
	
	public WebAPI getWebAPI() {
		return webAPI;
	}
	
	public JProApplication getJProApplication() {
		return jproApplication;
	}
}
