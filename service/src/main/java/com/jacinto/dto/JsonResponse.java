package com.jacinto.dto;

import static spark.Spark.get;

import java.util.Map;

public class JsonResponse {

	public JsonResponse() {
		super();
	}

	public static void getAsJSON(String routePath, Map<String, String> responseBody) {
		get(routePath, (req, res) -> {
			res.type("application/json");
			return responseBody;
		}, new JsonResponseTransformer());
	}
}
