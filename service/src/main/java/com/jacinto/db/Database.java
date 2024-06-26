package com.jacinto.db;

import java.sql.Connection;
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
	
	public static boolean conexaoEValida() {
        try (Connection conn = DataSource.getConnection()) {
            if (conn != null && !conn.isClosed()) {
            	conn.setReadOnly(true);
                conn.prepareStatement("SELECT 1");
                return true;
            }
        } catch (SQLException sqlException) {
            return false;
        }
        return false;

    }
	
	public static boolean existeCliente(Integer clienteId) throws SQLException, ClienteNaoEncontradoException {
		try(Connection conn = DataSource.getConnection()) {
			conn.setReadOnly(true);
			var existeClienteSql = "SELECT true FROM cliente WHERE id = ?;";
            var prepareExisteCliente = conn.prepareStatement(existeClienteSql);
            prepareExisteCliente.setInt(1, clienteId);

            try {
                var resultSetExisteCliente = prepareExisteCliente.executeQuery();
                conn.commit();
                if (!resultSetExisteCliente.isBeforeFirst()) {
                    throw new ClienteNaoEncontradoException();
                }
                return true;
            } catch (SQLException sqlException) {
	            conn.rollback();
	            throw sqlException;
	        }
		}
	}

    public static RespostaTransacaoSucedida criarTransacao(Integer clienteId, Integer valor, TipoTransacao tipo,
            String descricao) throws SaldoMenorQueLimiteException, ClienteNaoEncontradoException, SQLException {

        Long limite = 0L, saldo = 0L;

        try (Connection conn = DataSource.getConnection()) {
            var selectLimitacaoDoClienteSql = "SELECT limite, saldo FROM cliente WHERE id = ?;";
            var prepareSelectLimitacao = conn.prepareStatement(selectLimitacaoDoClienteSql);
            prepareSelectLimitacao.setInt(1, clienteId);

            try {
            	conn.setReadOnly(false);
                var resultSetLimitacao = prepareSelectLimitacao.executeQuery();
                resultSetLimitacao.next();
                var cliente = new Cliente(
            		clienteId, 
            		resultSetLimitacao.getLong("limite"),
                    resultSetLimitacao.getLong("saldo")
                );
                limite = cliente.limite;

                validarLimitesDaTransacao(valor, tipo, cliente);
                saldo = atualizarSaldoDoCliente(conn, valor, tipo, cliente);
                salvarTransacao(conn, cliente.id, valor, tipo, descricao);
                conn.commit();
            
            } catch (SQLException sqlException) {
                conn.rollback();
                throw sqlException;
            }
            return new RespostaTransacaoSucedida(limite, saldo);
        }

    }

    private static void salvarTransacao(Connection connection, Integer clienteId, Integer valor,
            TipoTransacao tipoTransacao, String descricao) throws SQLException {
        var insertTransacaoSql = "INSERT INTO transacao (cliente_id, valor, tipo, descricao) VALUES (?,?,tipo_transacao(?),?)";
        var prepareInsertTransacao = connection.prepareStatement(insertTransacaoSql);
        prepareInsertTransacao.setInt(1, clienteId);
        prepareInsertTransacao.setInt(2, valor);
        prepareInsertTransacao.setString(3, tipoTransacao.name().toLowerCase());
        prepareInsertTransacao.setString(4, descricao);
        prepareInsertTransacao.executeUpdate();
    }

    private static Long atualizarSaldoDoCliente(Connection sqlConnection, Integer valor, TipoTransacao tipoTransacao,
            Cliente cliente) throws SQLException {

        Long novoSaldo = tipoTransacao.equals(TipoTransacao.D) ? cliente.saldo - valor : cliente.saldo + valor;

        var updateAtualizarSaldoClienteSql = "UPDATE cliente SET saldo = ? WHERE id = ?;";
        var prepareAtualizarSaldoCliente = sqlConnection.prepareStatement(updateAtualizarSaldoClienteSql);
        prepareAtualizarSaldoCliente.setLong(1, novoSaldo);
        prepareAtualizarSaldoCliente.setInt(2, cliente.id);
        prepareAtualizarSaldoCliente.executeUpdate();
        
        return novoSaldo;
    }

    private static void validarLimitesDaTransacao(Integer valor, TipoTransacao tipo, Cliente cliente)
            throws SaldoMenorQueLimiteException {
        if (tipo.equals(TipoTransacao.D) && cliente.saldo - valor < -cliente.limite) {
            throw new SaldoMenorQueLimiteException();
        }
    }

    public static RespostaExtrato gerarExtrato(Integer clienteId) throws SQLException, ClienteNaoEncontradoException {
		try (Connection conn = DataSource.getConnection()) {
			conn.setReadOnly(true);
			var saldoDoExtrato = consultarSaldoELimite(clienteId, conn);
			var ultimasTransacoes = consultarUltimasTransacoes(clienteId, conn);
			conn.commit();

			return new RespostaExtrato(saldoDoExtrato, ultimasTransacoes);

		} catch (SQLException sqlException) {
			throw sqlException;
		}
    }

    private static List<RespostaExtrato.TransacaoExtrato> consultarUltimasTransacoes(Integer clienteId, Connection conn)
            throws SQLException {
        var selectUltimasTransacoesSql = "SELECT valor, tipo, descricao, realizada_em FROM transacao WHERE cliente_id = ? ORDER BY realizada_em DESC LIMIT 10;";
        var prepareSelectTransacoes = conn.prepareStatement(selectUltimasTransacoesSql);
        prepareSelectTransacoes.setInt(1, clienteId);
        prepareSelectTransacoes.executeQuery();

        var resultUltimasTransacoes = prepareSelectTransacoes.getResultSet();
        List<RespostaExtrato.TransacaoExtrato> ultimasTransacoes = new ArrayList<>();
        while (resultUltimasTransacoes.next()) {

            var transacao = new RespostaExtrato.TransacaoExtrato(resultUltimasTransacoes.getLong("valor"),
                    TipoTransacao.valueOf(resultUltimasTransacoes.getString("tipo").toUpperCase()),
                    resultUltimasTransacoes.getString("descricao"),
                    resultUltimasTransacoes.getObject("realizada_em", LocalDateTime.class));

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
        resultSelectClienteInfo.next();
        return new RespostaExtrato.Saldo(
            resultSelectClienteInfo.getLong("saldo"),
            resultSelectClienteInfo.getLong("limite"),
            LocalDateTime.now(ZoneId.of("GMT+3"))
        );
    }

}
