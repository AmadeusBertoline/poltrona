ALTER TABLE usuarios MODIFY COLUMN status VARCHAR(20) NOT NULL;

UPDATE usuarios
SET
    status = CASE status
        WHEN '0' THEN 'ATIVA'
        WHEN '1' THEN 'ENCERRADA'
        WHEN '2' THEN 'BLOQUEADA'
        ELSE status
    END;