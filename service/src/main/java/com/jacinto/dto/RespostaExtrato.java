package com.jacinto.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RespostaExtrato(Saldo saldo, List<TransacaoExtrato> ultimasTransacoes) {

	public record Saldo(Long total, Long limite, LocalDateTime dataExtrato) {}

	public record TransacaoExtrato(Long valor, TipoTransacao tipoTransacao, String descricao, LocalDateTime realizadaEm) {}

}
