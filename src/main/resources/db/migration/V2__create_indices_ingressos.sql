CREATE UNIQUE INDEX uk_ingresso_sessao_poltrona_ativo
ON ingressos (sessao_id, poltrona_id)
WHERE status = 'ATIVO'

