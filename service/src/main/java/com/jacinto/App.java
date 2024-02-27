package com.jacinto;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;

public class App {

	public static void main(String[] args) {
		
		Map<String, String> healthStatusMap = new HashMap<>();
		healthStatusMap.put("status", "UP");

		get("/health", (req, res) -> {
			res.type("application/json");
			
			return healthStatusMap;
		}, new JsonTransformer());
	}
}
