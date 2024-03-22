package com.jacinto;

import com.jacinto.config.Config;
import com.jacinto.controllers.Extrato;
import com.jacinto.controllers.HealthCheck;
import com.jacinto.controllers.Transacoes;

public class App {

	public static void main(String[] args) {

		HealthCheck.getStatus(Config.CONTENT_TYPE);
		
		Transacoes.registrarTransacao(Config.JSON, Config.CONTENT_TYPE, Config.LOGGER);

		Extrato.gerar(Config.JSON, Config.CONTENT_TYPE);

	}

}
