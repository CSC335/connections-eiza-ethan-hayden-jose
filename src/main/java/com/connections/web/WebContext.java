package com.connections.web;

import com.jpro.webapi.WebAPI;
import com.mongodb.client.MongoDatabase;

import javafx.collections.ObservableMap;

public class WebContext {
	private MongoDatabase mongoDatabase;
	private WebAPI webAPI;
	
	public WebContext(MongoDatabase mongoDatabase, WebAPI webAPI) {
		this.mongoDatabase = mongoDatabase;
		this.webAPI = webAPI;
	}
	
	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}
	
	public WebAPI getWebAPI() {
		return webAPI;
	}
}
