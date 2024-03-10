package com.jacinto;

import com.jacinto.controllers.HealthCheck;
import com.jacinto.controllers.Transacoes;

public class App {

	public static void main(String[] args) {

		HealthCheck.getAppHealthCheck();

		Transacoes.postRegistrarTransacao();
	}

}
