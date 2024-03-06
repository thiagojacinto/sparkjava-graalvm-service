package com.jacinto.controllers;

import java.util.HashMap;
import java.util.Map;

import com.jacinto.dto.JsonResponse;

public class HealthCheck {

	public static void getAppHealthCheck() {
		Map<String, String> healthStatusMap = new HashMap<>();
		healthStatusMap.put("status", "UP");

		JsonResponse.getAsJSON("/health", healthStatusMap);
	}

}
