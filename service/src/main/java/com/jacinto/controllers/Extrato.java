package com.jacinto.controllers;

import static spark.Spark.exception;
import static spark.Spark.get;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.jacinto.db.Database;
import com.jacinto.dto.JsonResponseTransformer;
import com.jacinto.model.exceptions.ClienteNaoEncontradoException;

public class Extrato {
	
	public static void gerar(ObjectMapper json, String contentType) {
		
		get("clientes/:id/extrato", (req, res) -> {
			var clientId = Integer.parseInt(req.params(":id"));
			res.type(contentType);
			
			return Database.gerarExtrato(clientId);
		}, new JsonResponseTransformer());
		
		exception(ClienteNaoEncontradoException.class, (exception, req, res) -> {
			res.status(422);
			try {
				res.body(json.writeValueAsString(Map.of("code", 422, "message", exception.getMessage())));
			} catch (JsonProcessingException e) {}
		});
		
		exception(InvalidDefinitionException.class, (exception, req, res) -> {
			res.status(503);
			try {
				res.body(json.writeValueAsString(Map.of("code", 503, "message", "Erro no servidor. Por favor entre em contato com suporte")));
			} catch (JsonProcessingException e) {}
		});
		
	}

}
