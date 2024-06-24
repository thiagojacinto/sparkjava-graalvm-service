package com.jacinto.model;

public class Cliente {
	
	public Integer id;
	public Long limite;
	public Long saldo;
	
	public Cliente(Integer id, Long limite, Long saldo) {
		super();
		this.id = id;
		this.limite = limite;
		this.saldo = saldo;
	}
	
	public Cliente(Long limite, Long saldo) {
		this(null, limite, saldo);
	}

	public Cliente() {
		this(null, null, null);
	}
}
