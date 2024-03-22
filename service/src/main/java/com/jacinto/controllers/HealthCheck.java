package com.jacinto.controllers;

import static spark.Spark.get;

import java.util.Map;

import com.jacinto.config.JsonResponseTransformer;
import com.jacinto.db.Database;

public class HealthCheck {

	public static void getStatus(String contentType) {

		get("/status", (req, res) -> {
			res.type(contentType);

			return Map.of("server", "UP", "database", Database.conexaoEValida() ? "UP" : "DOWN");
		}, new JsonResponseTransformer());

	}

}
