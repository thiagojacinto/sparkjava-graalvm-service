package com.jacinto.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.jacinto.dto.RespostaExtrato;
import com.jacinto.dto.RespostaTransacaoBemSucedida;
import com.jacinto.dto.TipoTransacao;
import com.jacinto.model.Cliente;
import com.jacinto.model.exceptions.ClienteNaoEncontradoException;
import com.jacinto.model.exceptions.SaldoMenorQueLimiteException;

public class Database {
	static final private String SELECT_CLIENTE_SQL 
		= "SELECT saldo, limite FROM cliente WHERE id = ?;";
	static final private String INSERT_TRANSACAO_SQL 
		= "INSERT INTO transacao (cliente_id, valor, tipo, descricao) VALUES (?,?,tipo_transacao(?),?);";
	static final private String UPDATE_ATUALIZAR_SALDO_CLIENTE_SQL 
		= "UPDATE cliente SET saldo = ? WHERE id = ?;";
	static final private String SELECT_ULTIMAS_TRANSACOES_SQL 
		= "SELECT valor, tipo, descricao, realizada_em FROM transacao WHERE cliente_id = ? ORDER BY realizada_em DESC LIMIT 10;";
	
	public static boolean conexaoEValida() {
        try (Connection conn = DataSource.getConnection()) {
            if (conn != null && !conn.isClosed()) {
            	conn.setReadOnly(true);
                return conn.isValid(500);
            }
        } catch (Exception e) {
            return false;
        }
        return false;

    }
	
    public static RespostaTransacaoBemSucedida criarTransacao(Integer clienteId, Integer valor, TipoTransacao tipo,
            String descricao) throws SaldoMenorQueLimiteException, ClienteNaoEncontradoException, SQLException {

        Long limite = 0L, saldo = 0L;

        try (Connection conn = DataSource.getConnection()) {
            var prepareSelectLimitacao = conn.prepareStatement(SELECT_CLIENTE_SQL);
            prepareSelectLimitacao.setInt(1, clienteId);

            try {
            	conn.setReadOnly(false);
                var resultSetLimitacao = prepareSelectLimitacao.executeQuery();
                boolean clienteInexistente = !resultSetLimitacao.next();
                if (clienteInexistente) {
                	conn.commit();
                	throw new ClienteNaoEncontradoException();
                }
                
                var cliente = new Cliente(
            		clienteId, 
            		resultSetLimitacao.getLong("limite"),
                    resultSetLimitacao.getLong("saldo")
                );
                limite = cliente.limite;

                var isLimitesValidos = validarLimites(valor, tipo, cliente);
                if (!isLimitesValidos) {
                	conn.commit();
                	return null;
                }
                saldo = atualizarSaldoDoCliente(conn, valor, tipo, cliente);
                salvarTransacao(conn, cliente.id, valor, tipo, descricao);
                conn.commit();
            
            } catch (SQLException sqlException) {
                conn.rollback();
                throw sqlException;
            }
            return new RespostaTransacaoBemSucedida(limite, saldo);
        }

    }

    private static void salvarTransacao(Connection connection, Integer clienteId, Integer valor,
            TipoTransacao tipoTransacao, String descricao) throws SQLException {
        var prepareInsertTransacao = connection.prepareStatement(INSERT_TRANSACAO_SQL);
        prepareInsertTransacao.setInt(1, clienteId);
        prepareInsertTransacao.setInt(2, valor);
        prepareInsertTransacao.setString(3, tipoTransacao.name().toLowerCase());
        prepareInsertTransacao.setString(4, descricao);
        prepareInsertTransacao.executeUpdate();
    }

    private static Long atualizarSaldoDoCliente(Connection sqlConnection, Integer valor, TipoTransacao tipoTransacao,
            Cliente cliente) throws SQLException {

        Long novoSaldo = tipoTransacao.equals(TipoTransacao.D) ? cliente.saldo - valor : cliente.saldo + valor;

        var prepareAtualizarSaldoCliente = sqlConnection.prepareStatement(UPDATE_ATUALIZAR_SALDO_CLIENTE_SQL);
        prepareAtualizarSaldoCliente.setLong(1, novoSaldo);
        prepareAtualizarSaldoCliente.setInt(2, cliente.id);
        prepareAtualizarSaldoCliente.executeUpdate();
        
        return novoSaldo;
    }

    private static boolean validarLimites(Integer valor, TipoTransacao tipo, Cliente cliente) {
        if (tipo.equals(TipoTransacao.D) && cliente.saldo - valor < -cliente.limite) {
        	return false;
        }
        return true;
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
        var prepareSelectTransacoes = conn.prepareStatement(SELECT_ULTIMAS_TRANSACOES_SQL);
        prepareSelectTransacoes.setInt(1, clienteId);
        prepareSelectTransacoes.executeQuery();

        var resultUltimasTransacoes = prepareSelectTransacoes.getResultSet();
        List<RespostaExtrato.TransacaoExtrato> ultimasTransacoes = new ArrayList<>();
        while (resultUltimasTransacoes.next()) {

            var transacao = new RespostaExtrato.TransacaoExtrato(
            		resultUltimasTransacoes.getLong("valor"),
                    TipoTransacao.valueOf(resultUltimasTransacoes.getString("tipo").toUpperCase()),
                    resultUltimasTransacoes.getString("descricao"),
                    resultUltimasTransacoes.getObject("realizada_em", LocalDateTime.class)
                );

            ultimasTransacoes.add(transacao);
        }
        return ultimasTransacoes;
    }

    private static RespostaExtrato.Saldo consultarSaldoELimite(Integer clienteId, Connection conn)
            throws SQLException, ClienteNaoEncontradoException {

        var prepareSelectClienteInfo = conn.prepareStatement(SELECT_CLIENTE_SQL);
        prepareSelectClienteInfo.setInt(1, clienteId);

        var resultSelectClienteInfo = prepareSelectClienteInfo.executeQuery();
        
        boolean clienteInexistente = !resultSelectClienteInfo.next();;
        if (clienteInexistente) {
        	throw new ClienteNaoEncontradoException();
        }
        return new RespostaExtrato.Saldo(
            resultSelectClienteInfo.getLong("saldo"),
            resultSelectClienteInfo.getLong("limite"),
            LocalDateTime.now(ZoneId.of("GMT+3"))
        );
    }

}
