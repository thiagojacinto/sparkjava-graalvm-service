package com.jacinto.controllers;

import static spark.Spark.get;

import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;

import com.jacinto.config.JsonResponseTransformer;
import com.jacinto.db.Database;

public class HealthCheck {

	public static void getStatus(String contentType, Logger logger) {

		get("/status", (req, res) -> {
			res.type(contentType);
			String dbStatus = "UP";
			
			if (!Database.conexaoEValida()) {
				  dbStatus = "DOWN";
				  res.status(HttpStatus.SERVICE_UNAVAILABLE_503);
					logger.error("[ 	error	] GET - /status		-	 Error: Failed to connect to database");

			}
			return Map.of("server", "UP", "database", dbStatus);
		}, new JsonResponseTransformer());

	}

}
