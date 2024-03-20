package com.jacinto.config;

import com.fasterxml.jackson.core.JsonProcessingException;

import spark.ResponseTransformer;

public class JsonResponseTransformer implements ResponseTransformer {

	@Override
	public String render(final Object object) throws JsonProcessingException {
		return Config.JSON.writeValueAsString(object);
	}
}