package com.jacinto.controllers;

import static spark.Spark.exception;
import static spark.Spark.post;

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacinto.db.Database;
import com.jacinto.dto.TipoTransacao;
import com.jacinto.model.exceptions.ClienteNaoEncontradoException;
import com.jacinto.model.exceptions.SaldoMenorQueLimiteException;
import com.jacinto.model.exceptions.TransacaoComFormatoInvalidoException;

import spark.ResponseTransformer;

public class Transacoes {

	public static void registrarTransacao(ResponseTransformer jsonTransformer, ObjectMapper json, String contentType, Logger logger) {

		post("/clientes/:id/transacoes", contentType, (req, res) -> {
			var clientId = Integer.parseInt(req.params(":id"));
			res.type(contentType);
            try {
                var reqBody = json.readValue(req.body(), Transacoes.Requisicao.class);
                if (reqBody.descricao == "" || reqBody.descricao == null || reqBody.descricao.length() > 10) {
                	throw new TransacaoComFormatoInvalidoException("Verificar campo `descricao`.");
                }
                return Database.criarTransacao(clientId, reqBody.valor, reqBody.tipo, reqBody.descricao);
            } catch (StreamReadException | DatabindException e) {
                throw new TransacaoComFormatoInvalidoException();
            } 
		}, jsonTransformer);
		
		exception(SQLException.class, (exception, req, res)-> {
			res.status(422);
			try {
				if (logger != null) logger.error("[ 	error	] " + req.requestMethod() + " - " + req.uri() + " 		-	 Exception: " + exception.getMessage());
				res.body(json.writeValueAsString(Map.of("code", 422, "message", exception.getMessage())));
			} catch (JsonProcessingException e) {}
		});
		exception(SaldoMenorQueLimiteException.class, (exception, req, res)-> {
			res.status(422);
			try {
				if (logger != null) logger.error("[ 	error	] " + req.requestMethod() + " - " + req.uri() + " 		-	 Exception: " + exception.getMessage());
				res.body(json.writeValueAsString(Map.of("code", 422, "message", exception.getMessage())));
			} catch (JsonProcessingException e) {}
		});
		exception(ClienteNaoEncontradoException.class, (exception, req, res) -> {
			res.status(422);
			try {
				if (logger != null) logger.error("[ 	error	] " + req.requestMethod() + " - " + req.uri() + " 		-	 Exception: " + exception.getMessage());
				res.body(json.writeValueAsString(Map.of("code", 422, "message", exception.getMessage())));
			} catch (JsonProcessingException e) {}
		});
		exception(TransacaoComFormatoInvalidoException.class, (exception, req, res) -> {
			res.status(400);
			try {
				if (logger != null) logger.error("[ 	error	] " + req.requestMethod() + " - " + req.uri() + " 		-	 Exception: " + exception.getMessage());
				res.body(json.writeValueAsString(Map.of("code", 422, "message", exception.getMessage())));
			} catch (JsonProcessingException e) {}
		});
	}
	
	public record Requisicao(Integer valor, TipoTransacao tipo, String descricao) {
	}
}
