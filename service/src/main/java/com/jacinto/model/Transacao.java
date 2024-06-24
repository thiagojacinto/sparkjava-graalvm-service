package com.jacinto.model;

import java.sql.Date;

import com.jacinto.dto.TipoTransacao;

public class Transacao {

	public Integer id;
	public Integer clienteId;
	public Long valor;
	public TipoTransacao tipoTransacao;
	public String descricao;
	public Date realizadaEm;

	public Transacao(Integer id, Integer clienteId, Long valor, TipoTransacao tipoTransacao, String descricao,
		Date realizadaEm) {
		super();
		this.id = id;
		this.clienteId = clienteId;
		this.valor = valor;
		this.tipoTransacao = tipoTransacao;
		this.descricao = descricao;
		this.realizadaEm = realizadaEm;
	}

}
