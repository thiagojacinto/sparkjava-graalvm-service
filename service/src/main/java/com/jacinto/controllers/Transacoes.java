package com.jacinto.controllers;

import static spark.Spark.post;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.jacinto.db.Database;
import com.jacinto.dto.JsonResponseTransformer;
import com.jacinto.dto.TipoTransacao;

public class Transacoes {

	public static void postRegistrarTransacao() {

		var acceptedType = "application/json";
		var json = JsonMapper.builder()
		  .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
		  .build();;

		post("/clientes/:id/transacoes", acceptedType, (req, res) -> {
			var clientId = Integer.parseInt(req.params(":id"));
			res.type(acceptedType);
			var reqBody = json.readValue(req.body(), Transacoes.Requisicao.class);

			return Database.criarTransacao(
				clientId, 
				reqBody.valor, 
				reqBody.tipo, 
				reqBody.descricao
			);
		}, new JsonResponseTransformer());
	}
	
	public record Requisicao(Integer valor, TipoTransacao tipo, String descricao) {
	}
}
