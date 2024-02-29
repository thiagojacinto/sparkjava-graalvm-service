CREATE IF NOT EXISTS TYPE tipo_transacao AS ENUM ('c', 'd');

--- TABELAS
CREATE IF NOT EXISTS UNLOGGED TABLE cliente (
    id INT PRIMARY KEY,
    limite INTEGER NOT NULL,
    saldo INTEGER NOT NULL DEFAULT 0
);

CREATE IF NOT EXISTS UNLOGGED TABLE transacao (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    valor INTEGER NOT NULL,
    tipo tipo_transacao NOT NULL,
    descricao VARCHAR(10) NOT NULL,
    realizada_em TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cliente_transacao_id
            FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

--- SEED
DO $$
BEGIN
  INSERT INTO clientes (nome, limite)
  VALUES
    ('o barato sai caro', 1000 * 100),
    ('zan corp ltda', 800 * 100),
    ('les cruders', 10000 * 100),
    ('padaria joia de cocaia', 100000 * 100),
    ('kid mais', 5000 * 100);
END; $$