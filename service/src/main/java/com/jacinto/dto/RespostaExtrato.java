package com.jacinto.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RespostaExtrato(Saldo saldo, List<TransacaoExtrato> ultimasTransacoes) {

	public record Saldo(Integer total, Integer limite, LocalDateTime dataExtrato) {}

	public record TransacaoExtrato(Integer valor, TipoTransacao tipoTransacao, String descricao, LocalDateTime realizadaEm) {}

}
