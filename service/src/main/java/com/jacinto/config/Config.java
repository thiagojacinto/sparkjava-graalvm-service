package com.jacinto.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class Config {

	public static final boolean NEEDS_LOGGING = System.getenv("TURN_LOG_ON") != null
			&& !System.getenv("TURN_LOG_ON").isBlank();

	public static final String CONTENT_TYPE = "application/json";

	public static final ObjectMapper JSON = JsonMapper
			.builder()
			.addModule(new ParameterNamesModule())
			.addModule(new Jdk8Module()).addModule(new JavaTimeModule())
			.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
			.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.build();

	public static final Logger LOGGER = NEEDS_LOGGING
			? LoggerFactory.getLogger("com.jacinto.App")
			: null;
	
	public static final JsonResponseTransformer RESPONSE_TRANSFORMER = 
			new JsonResponseTransformer();
}
