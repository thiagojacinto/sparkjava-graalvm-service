package com.jacinto.controllers;

import static spark.Spark.exception;
import static spark.Spark.get;

import java.sql.SQLException;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.jacinto.db.Database;
import com.jacinto.model.exceptions.ClienteNaoEncontradoException;

import spark.ResponseTransformer;

public class Extrato {
	
	public static void gerar(ResponseTransformer jsonTransformer, ObjectMapper json, String contentType, Logger logger) {
		
		get("clientes/:id/extrato", (req, res) -> {
			var clientId = Integer.parseInt(req.params(":id"));
			res.type(contentType);
			return Database.gerarExtrato(clientId);
		}, jsonTransformer);
		
		exception(SQLException.class, (exception, req, res)-> {
			res.status(HttpStatus.UNPROCESSABLE_ENTITY_422);
			try {
				if (logger != null) logger.error("[ 	error	] " + req.requestMethod() + " - " + req.uri() + " 		-	 Exception: " + exception.getMessage());
				res.body(json.writeValueAsString(Map.of("code", HttpStatus.UNPROCESSABLE_ENTITY_422, "message", exception.getMessage())));
			} catch (JsonProcessingException e) {}
		});
		exception(InvalidDefinitionException.class, (exception, req, res) -> {
			res.status(HttpStatus.SERVICE_UNAVAILABLE_503);
			try {
				if (logger != null) logger.error("[ 	error	] " + req.requestMethod() + " - " + req.uri() + " 		-	 Exception: " + exception.getMessage());
				res.body(json.writeValueAsString(Map.of("code", HttpStatus.SERVICE_UNAVAILABLE_503, "message", "Erro no servidor. Por favor entre em contato com suporte")));
			} catch (JsonProcessingException e) {}
		});
		
		exception(ClienteNaoEncontradoException.class, (exception, req, res) -> {
			res.status(HttpStatus.UNPROCESSABLE_ENTITY_422);
			try {
				if (logger != null) logger.error("[ 	error	] " + req.requestMethod() + " - " + req.uri() + " 		-	 Exception: " + exception.getMessage());
				res.body(json.writeValueAsString(Map.of("code", HttpStatus.UNPROCESSABLE_ENTITY_422, "message", exception.getMessage())));
			} catch (JsonProcessingException e) {}
		});
		
	}
}
