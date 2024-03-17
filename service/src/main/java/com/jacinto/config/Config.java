package com.jacinto.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class Config {

	public static final String CONTENT_TYPE = "application/json";

	public static final ObjectMapper JSON = JsonMapper.builder()
		.addModule(new ParameterNamesModule())
		.addModule(new Jdk8Module())
		.addModule(new JavaTimeModule())
		.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		.build();

}
