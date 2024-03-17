package com.jacinto.model.exceptions;

public class SaldoMenorQueLimiteException extends Exception {

	private static final long serialVersionUID = -3624053757460570405L;

	public SaldoMenorQueLimiteException() {
		super("Saldo do cliente menor que seu limite disponivel.");
	}
}
