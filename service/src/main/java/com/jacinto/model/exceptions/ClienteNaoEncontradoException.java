package com.jacinto.model.exceptions;

public class ClienteNaoEncontradoException extends Exception {

	private static final long serialVersionUID = -6758117921970902843L;
	
	public ClienteNaoEncontradoException() {
		super("Cliente nao registrado");
	}
}
