package com.jacinto.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.jacinto.dto.RespostaExtrato;
import com.jacinto.dto.RespostaTransacaoSucedida;
import com.jacinto.dto.TipoTransacao;
import com.jacinto.model.Cliente;
import com.jacinto.model.exceptions.ClienteNaoEncontradoException;
import com.jacinto.model.exceptions.SaldoMenorQueLimiteException;

public class Database {

	public static final String CONNECTION_URL = "jdbc:postgresql://localhost:5432/rinha";
	public static final String USER = "admin";
	public static final String PASSWORD = "1313";

	public static RespostaTransacaoSucedida criarTransacao(Integer clienteId, Integer valor, TipoTransacao tipo,
		String descricao) throws SaldoMenorQueLimiteException, ClienteNaoEncontradoException, SQLException {

		Integer limite = 0, saldo = 0;

		try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
			var selectLimitacaoDoClienteSql = "SELECT limite, saldo FROM cliente WHERE id = ?;";
			var prepareSelectLimitacao = conn.prepareStatement(selectLimitacaoDoClienteSql);
			prepareSelectLimitacao.setInt(1, clienteId);

			boolean autoCommit = conn.getAutoCommit();
			try {
				conn.setAutoCommit(false);

				var resultSetLimitacao = prepareSelectLimitacao.executeQuery();
				if (!resultSetLimitacao.isBeforeFirst()) {
					throw new ClienteNaoEncontradoException();
				}
				resultSetLimitacao.next();
				var cliente = new Cliente(clienteId, resultSetLimitacao.getInt("limite"), resultSetLimitacao.getInt("saldo"));
				limite = cliente.limite;

				validarLimitesDaTransacao(valor, tipo, cliente);
				saldo = atualizarSaldoDoCliente(conn, valor, tipo, cliente);
				salvarTransacao(conn, cliente.id, valor, tipo, descricao);

				conn.commit();

			} catch (SQLException sqlException) {
				conn.rollback();
				throw sqlException;
			} finally {
				conn.setAutoCommit(autoCommit);
			}
			return new RespostaTransacaoSucedida(limite, saldo);
		}

	}

	private static Integer salvarTransacao(Connection connection, Integer clienteId, Integer valor,
		TipoTransacao tipoTransacao, String descricao) throws SQLException {
		var insertTransacaoSql = "INSERT INTO transacao (cliente_id, valor, tipo, descricao) VALUES (?,?,tipo_transacao(?),?) RETURNING id;";
		var prepareInsertTransacao = connection.prepareStatement(insertTransacaoSql);
		prepareInsertTransacao.setInt(1, clienteId);
		prepareInsertTransacao.setInt(2, valor);
		prepareInsertTransacao.setString(3, tipoTransacao.name().toLowerCase());
		prepareInsertTransacao.setString(4, descricao);
		prepareInsertTransacao.execute();

		var resultado = prepareInsertTransacao.getResultSet();
		return resultado.next() ? resultado.getInt(1) : 0;

	}

	private static Integer atualizarSaldoDoCliente(Connection sqlConnection, Integer valor, TipoTransacao tipoTransacao,
		Cliente cliente) throws SQLException {

		Integer novoSaldo = cliente.saldo;
		if (tipoTransacao.equals(TipoTransacao.D)) {
			novoSaldo = novoSaldo - valor;
		} else {
			novoSaldo = novoSaldo + valor;
		}

		var updateAtualizarSaldoClienteSql = "UPDATE cliente SET saldo = ? WHERE id = ?;";
		var prepareAtualizarSaldoCliente = sqlConnection.prepareStatement(updateAtualizarSaldoClienteSql);
		prepareAtualizarSaldoCliente.setInt(1, novoSaldo);
		prepareAtualizarSaldoCliente.setInt(2, cliente.id);
		prepareAtualizarSaldoCliente.execute();

		return novoSaldo;
	}

	private static void validarLimitesDaTransacao(Integer valor, TipoTransacao tipo, Cliente cliente)
		throws SaldoMenorQueLimiteException {
		if (tipo.equals(TipoTransacao.D) && cliente.saldo - valor < -cliente.limite) {
			throw new SaldoMenorQueLimiteException();
		}
	}

	public static RespostaExtrato gerarExtrato(Integer clienteId) throws SQLException, ClienteNaoEncontradoException {

		try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {

			boolean autoCommit = conn.getAutoCommit();
			try {
				conn.setAutoCommit(false);

				var saldoDoExtrato = consultarSaldoELimite(clienteId, conn);
				var ultimasTransacoes = consultarUltimasTransacoes(clienteId, conn);

				return new RespostaExtrato(saldoDoExtrato, ultimasTransacoes);

			} catch (SQLException sqlException) {
				conn.rollback();
				throw sqlException;
			} finally {
				conn.setAutoCommit(autoCommit);
			}
		}
	}

	private static List<RespostaExtrato.TransacaoExtrato> consultarUltimasTransacoes(Integer clienteId, Connection conn)
		throws SQLException {
		var selectUltimasTransacoesSql = "SELECT valor, tipo, descricao, realizada_em FROM transacao WHERE cliente_id = ? ORDER BY realizada_em DESC LIMIT 10;";
		var prepareSelectTransacoes = conn.prepareStatement(selectUltimasTransacoesSql);
		prepareSelectTransacoes.setInt(1, clienteId);
		prepareSelectTransacoes.execute();

		var resultUltimasTransacoes = prepareSelectTransacoes.getResultSet();
		List<RespostaExtrato.TransacaoExtrato> ultimasTransacoes = new ArrayList<>();
		while (resultUltimasTransacoes.next()) {
			
			var transacao = new RespostaExtrato.TransacaoExtrato(resultUltimasTransacoes.getInt("valor"),
				TipoTransacao.valueOf(resultUltimasTransacoes.getString("tipo").toUpperCase()),
				resultUltimasTransacoes.getString("descricao"), resultUltimasTransacoes.getObject("realizada_em", LocalDateTime.class));

			ultimasTransacoes.add(transacao);
		}
		return ultimasTransacoes;
	}

	private static RespostaExtrato.Saldo consultarSaldoELimite(Integer clienteId, Connection conn)
		throws SQLException, ClienteNaoEncontradoException {

		var selectClienteSql = "SELECT saldo, limite FROM cliente WHERE id = ?;";
		var prepareSelectClienteInfo = conn.prepareStatement(selectClienteSql);
		prepareSelectClienteInfo.setInt(1, clienteId);

		var resultSelectClienteInfo = prepareSelectClienteInfo.executeQuery();
		if (!resultSelectClienteInfo.isBeforeFirst()) {
			throw new ClienteNaoEncontradoException();
		}
		resultSelectClienteInfo.next();
		return new RespostaExtrato.Saldo(resultSelectClienteInfo.getInt("saldo"), resultSelectClienteInfo.getInt("limite"),
			LocalDateTime.now(ZoneId.of("GMT+3")));
	}

}
