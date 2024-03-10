package com.jacinto.model;

import java.sql.Date;

import com.jacinto.dto.TipoTransacao;

public class Transacao {

	public Integer id;
	public Integer clienteId;
	public Integer valor;
	public TipoTransacao tipoTranscao;
	public String descricao;
	public Date realizadaEm;

	public Transacao(Integer id, Integer clienteId, Integer valor, TipoTransacao tipoTranscao, String descricao,
		Date realizadaEm) {
		super();
		this.id = id;
		this.clienteId = clienteId;
		this.valor = valor;
		this.tipoTranscao = tipoTranscao;
		this.descricao = descricao;
		this.realizadaEm = realizadaEm;
	}

}
