package com.jacinto;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

public class App {

	public static void main(String[] args) {

		getAppHealthCheck();
	}

	private static void getAppHealthCheck() {
		Map<String, String> healthStatusMap = new HashMap<>();
		healthStatusMap.put("status", "UP");

		getAsJSON("/health", healthStatusMap);
	}

	private static void getAsJSON(String routePath, Map<String, String> responseBody) {
		get(routePath, (req, res) -> {
			res.type("application/json");
			return responseBody;
		}, new JsonResponseTransformer());
	}

}
