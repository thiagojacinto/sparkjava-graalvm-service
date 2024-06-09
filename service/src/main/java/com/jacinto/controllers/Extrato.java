package com.jacinto.controllers;

import static spark.Spark.exception;
import static spark.Spark.get;

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.jacinto.config.JsonResponseTransformer;
import com.jacinto.db.Database;

public class Extrato {
	
	public static void gerar(ObjectMapper json, String contentType, Logger logger) {
		
		get("clientes/:id/extrato", (req, res) -> {
			var clientId = Integer.parseInt(req.params(":id"));
			res.type(contentType);
			
			return Database.gerarExtrato(clientId);
		}, new JsonResponseTransformer());
		
		exception(InvalidDefinitionException.class, (exception, req, res) -> {
			res.status(503);
			try {
				logger.error("[ 	error	] " + req.requestMethod() + " - " + req.uri() + " 		-	 Exception: " + exception.getMessage());
				res.body(json.writeValueAsString(Map.of("code", 503, "message", "Erro no servidor. Por favor entre em contato com suporte")));
			} catch (JsonProcessingException e) {}
		});
		
		exception(SQLException.class, (exception, req, res) -> {
			res.status(503);
			try {
				logger.error("[ 	error	] " + req.requestMethod() + " - " + req.uri() + " 		-	 Exception: " + exception.getMessage());
				res.body(json.writeValueAsString(Map.of("code", 503, "message", "Erro no servidor. Por favor entre em contato com suporte")));
			} catch (JsonProcessingException e) {}
		});
		
	}
}
