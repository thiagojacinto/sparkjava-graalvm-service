package com.jacinto.controllers;

import static spark.Spark.get;

import java.util.Map;

import com.jacinto.config.JsonResponseTransformer;

public class HealthCheck {

	public static void getStatus(String contentType) {

		get("/status", (req, res) -> {
			res.type(contentType);

			return Map.of("status", "UP");
		}, new JsonResponseTransformer());

	}

}
