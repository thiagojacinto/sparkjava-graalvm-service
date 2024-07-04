package com.jacinto;

import com.jacinto.config.Config;
import com.jacinto.config.JsonResponseTransformer;
import com.jacinto.controllers.Extrato;
import com.jacinto.controllers.HealthCheck;
import com.jacinto.controllers.Transacoes;

public class App {

	public static void main(String[] args) {

		var jsonTransformer = new JsonResponseTransformer();

		HealthCheck.getStatus(
				jsonTransformer, 
				Config.CONTENT_TYPE, 
				Config.LOGGER
			);

		Transacoes.registrarTransacao(
				jsonTransformer, 
				Config.JSON, 
				Config.CONTENT_TYPE, 
				Config.LOGGER
			);

		Extrato.gerar(
				jsonTransformer, 
				Config.JSON, 
				Config.CONTENT_TYPE, 
				Config.LOGGER
			);

	}

}
