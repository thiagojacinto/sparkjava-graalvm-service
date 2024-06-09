CREATE TYPE tipo_transacao AS ENUM ('c', 'd');

--- TABELAS
CREATE UNLOGGED TABLE IF NOT EXISTS cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    limite INTEGER NOT NULL,
    saldo INTEGER NOT NULL DEFAULT 0
);

CREATE UNLOGGED TABLE IF NOT EXISTS transacao (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    valor INTEGER NOT NULL,
    tipo tipo_transacao NOT NULL,
    descricao VARCHAR(10) NOT NULL,
    realizada_em TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cliente_transacao_id
            FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

--- INDICES

CREATE INDEX IF NOT EXISTS "idx_transacao_cliente_id" ON transacao (cliente_id);

CREATE INDEX IF NOT EXISTS "idx_last_client_transactions" ON transacao (cliente_id, realizada_em DESC);

--- SEED
DO $$
BEGIN
  INSERT INTO cliente (nome, limite)
  VALUES
    ('o barato sai caro', 1000 * 100),
    ('zan corp ltda', 800 * 100),
    ('les cruders', 10000 * 100),
    ('padaria joia de cocaia', 100000 * 100),
    ('kid mais', 5000 * 100);
END; $$
