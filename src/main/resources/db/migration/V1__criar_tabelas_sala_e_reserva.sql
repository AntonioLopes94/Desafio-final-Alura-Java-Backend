CREATE TABLE sala (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255)
);

CREATE TABLE reserva (
    id BIGSERIAL PRIMARY KEY,
    nome_pessoa VARCHAR(255),
    sala_id BIGINT,
    data_inicio TIMESTAMP,
    data_fim TIMESTAMP,
    status VARCHAR(30),
    CONSTRAINT fk_reserva_sala
        FOREIGN KEY (sala_id) REFERENCES sala (id)
);

CREATE INDEX idx_reserva_sala_periodo
    ON reserva (sala_id, data_inicio, data_fim);
