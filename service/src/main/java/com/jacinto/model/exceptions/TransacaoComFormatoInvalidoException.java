package com.jacinto.model.exceptions;

public class TransacaoComFormatoInvalidoException extends Exception {

	private static final long serialVersionUID = -4702415894768923166L;

	public TransacaoComFormatoInvalidoException(String extraMessage) {
		super(String.format("Transação com formato inválido: %s", extraMessage));
	}

	public TransacaoComFormatoInvalidoException() {
		this("Transação com formato inválido.");
	}

}
