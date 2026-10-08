ALTER TABLE sessoes
ADD INDEX idx_sessoes_sala (sala_id);

ALTER TABLE sessoes
DROP INDEX uk_sessoes_sala;