package com.jacinto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String render(final Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}
}