CREATE UNIQUE INDEX uk_sessao_poltrona_ativa
ON ingressos (
    (CASE WHEN status = 'ATIVO' THEN sessao_id ELSE NULL END),
    (CASE WHEN status = 'ATIVO' THEN poltrona_id ELSE NULL END)
);