package com.jacinto.controllers;

import static spark.Spark.get;

import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;

import com.jacinto.db.Database;

import spark.ResponseTransformer;

public class HealthCheck {

	public static void getStatus(ResponseTransformer jsonTransformer, String contentType, Logger logger) {

		get("/status", (req, res) -> {
			res.type(contentType);
			String dbStatus = "UP";

			if (!Database.conexaoEValida()) {
				dbStatus = "DOWN";
				res.status(HttpStatus.SERVICE_UNAVAILABLE_503);
				if (logger != null) logger.error("[ 	error	] GET - /status		-	 Error: Failed to connect to database");
			}
			return Map.of("server", "UP", "database", dbStatus);
		}, jsonTransformer);
	}
}
