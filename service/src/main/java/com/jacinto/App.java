package com.jacinto;

import com.jacinto.config.Config;
import com.jacinto.controllers.Extrato;
import com.jacinto.controllers.HealthCheck;
import com.jacinto.controllers.Transacoes;

public class App {

	public static void main(String[] args) {

		HealthCheck.getAppHealthCheck();

		Transacoes.registrarTransacao(Config.JSON, Config.CONTENT_TYPE);

		Extrato.gerar(Config.JSON, Config.CONTENT_TYPE);

	}

}
