package com.jacinto.controllers;

import static spark.Spark.exception;
import static spark.Spark.post;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacinto.db.Database;
import com.jacinto.dto.JsonResponseTransformer;
import com.jacinto.dto.TipoTransacao;
import com.jacinto.model.exceptions.ClienteNaoEncontradoException;
import com.jacinto.model.exceptions.SaldoMenorQueLimiteException;

public class Transacoes {

	public static void registrarTransacao(ObjectMapper json, String contentType) {

		post("/clientes/:id/transacoes", contentType, (req, res) -> {
			var clientId = Integer.parseInt(req.params(":id"));
			res.type(contentType);
			var reqBody = json.readValue(req.body(), Transacoes.Requisicao.class);

			return Database.criarTransacao(clientId, reqBody.valor, reqBody.tipo, reqBody.descricao);
		}, new JsonResponseTransformer());
		
		exception(SaldoMenorQueLimiteException.class, (exception, req, res)-> {
			res.status(422);
			try {
				res.body(json.writeValueAsString(Map.of("code", 422, "message", exception.getMessage())));
			} catch (JsonProcessingException e) {}
		});
		exception(ClienteNaoEncontradoException.class, (exception, req, res) -> {
			res.status(422);
			try {
				res.body(json.writeValueAsString(Map.of("code", 422, "message", exception.getMessage())));
			} catch (JsonProcessingException e) {}
		});
		
	}
	
	public record Requisicao(Integer valor, TipoTransacao tipo, String descricao) {
	}
}
