package com.jacinto.model;

public class Cliente {
	
	public Integer id;
	public Integer limite;
	public Integer saldo;
	
	public Cliente(Integer id, Integer limite, Integer saldo) {
		super();
		this.id = id;
		this.limite = limite;
		this.saldo = saldo;
	}
	
	public Cliente(Integer limite, Integer saldo) {
		this(null, limite, saldo);
	}

	public Cliente() {
		this(null, null, null);
	}
}
