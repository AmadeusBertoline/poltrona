CREATE DATABASE IF NOT EXISTS poltrona_db CHARACTER
SET
    utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE poltrona_db;

-- ============================================================
-- USUÁRIOS
-- ============================================================
CREATE TABLE
    usuarios (
        id BIGINT NOT NULL AUTO_INCREMENT,
        cpf VARCHAR(255) NOT NULL,
        data_criacao DATETIME NOT NULL,
        data_nascimento DATE NOT NULL,
        email VARCHAR(255) NOT NULL,
        nome VARCHAR(255) NOT NULL,
        senha VARCHAR(255) NOT NULL,
        status TINYINT NOT NULL,
        CONSTRAINT pk_usuarios PRIMARY KEY (id)
    ) ENGINE = InnoDB;

CREATE TABLE
    admins (
        id BIGINT NOT NULL,
        CONSTRAINT pk_admins PRIMARY KEY (id),
        CONSTRAINT fk_admins_usuario FOREIGN KEY (id) REFERENCES usuarios (id)
    ) ENGINE = InnoDB;

CREATE TABLE
    clientes (
        id BIGINT NOT NULL,
        CONSTRAINT pk_clientes PRIMARY KEY (id),
        CONSTRAINT fk_clientes_usuario FOREIGN KEY (id) REFERENCES usuarios (id)
    ) ENGINE = InnoDB;

CREATE TABLE
    proprietarios (
        id BIGINT NOT NULL,
        CONSTRAINT pk_proprietarios PRIMARY KEY (id),
        CONSTRAINT fk_proprietarios_usuario FOREIGN KEY (id) REFERENCES usuarios (id)
    ) ENGINE = InnoDB;

CREATE TABLE
    gerentes (
        id BIGINT NOT NULL,
        cinema_id BIGINT NOT NULL,
        CONSTRAINT pk_gerentes PRIMARY KEY (id),
        CONSTRAINT fk_gerentes_usuario FOREIGN KEY (id) REFERENCES usuarios (id)
    ) ENGINE = InnoDB;

-- ============================================================
-- FILMES
-- ============================================================
CREATE TABLE
    filmes (
        id BIGINT NOT NULL AUTO_INCREMENT,
        ativo BIT NOT NULL,
        classificacao_indicativa TINYINT NOT NULL,
        data_lancamento DATE NOT NULL,
        diretor VARCHAR(255) NOT NULL,
        distribuidora VARCHAR(255) NOT NULL,
        duracao_minutos INT NOT NULL,
        image_path VARCHAR(255) NOT NULL,
        sinopse VARCHAR(255) NOT NULL,
        titulo VARCHAR(255) NOT NULL,
        CONSTRAINT pk_filmes PRIMARY KEY (id),
        CONSTRAINT uk_filmes_image_path UNIQUE (image_path),
        CONSTRAINT uk_filmes_titulo UNIQUE (titulo)
    ) ENGINE = InnoDB;

CREATE TABLE
    formatos (
        filme_id BIGINT NOT NULL,
        formato_filme VARCHAR(50) NOT NULL,
        CONSTRAINT pk_formatos PRIMARY KEY (filme_id, formato_filme),
        CONSTRAINT fk_formatos_filme FOREIGN KEY (filme_id) REFERENCES filmes (id)
    ) ENGINE = InnoDB;

CREATE TABLE
    generos (
        filme_id BIGINT NOT NULL,
        generos VARCHAR(50) NOT NULL,
        CONSTRAINT pk_generos PRIMARY KEY (filme_id, generos),
        CONSTRAINT fk_generos_filme FOREIGN KEY (filme_id) REFERENCES filmes (id)
    ) ENGINE = InnoDB;

-- ============================================================
-- CINEMAS / SALAS / POLTRONAS
-- ============================================================
CREATE TABLE
    cinemas (
        id BIGINT NOT NULL AUTO_INCREMENT,
        cnpj VARCHAR(255) NOT NULL,
        data_criacao DATETIME NOT NULL,
        bairro VARCHAR(255) NOT NULL,
        cep VARCHAR(255) NOT NULL,
        cidade VARCHAR(255) NOT NULL,
        complemento VARCHAR(255) NULL,
        logradouro VARCHAR(255) NOT NULL,
        numero VARCHAR(255) NOT NULL,
        uf VARCHAR(255) NOT NULL,
        nome_fantasia VARCHAR(255) NOT NULL,
        antecedencia_minutos_cancelamento INT NULL,
        intervalo_limpeza_minutos INT NULL,
        tolerancia_minutos_compra INT NULL,
        quantidade_salas INT NOT NULL,
        razao_social VARCHAR(255) NOT NULL,
        status TINYINT NOT NULL,
        telefone VARCHAR(255) NOT NULL,
        proprietario_id BIGINT NOT NULL,
        CONSTRAINT pk_cinemas PRIMARY KEY (id),
        CONSTRAINT uk_cinemas_cnpj UNIQUE (cnpj),
        CONSTRAINT uk_cinemas_telefone UNIQUE (telefone),
        CONSTRAINT fk_cinemas_proprietario FOREIGN KEY (proprietario_id) REFERENCES proprietarios (id)
    ) ENGINE = InnoDB;

ALTER TABLE gerentes ADD CONSTRAINT fk_gerentes_cinema FOREIGN KEY (cinema_id) REFERENCES cinemas (id);

CREATE TABLE
    salas (
        id BIGINT NOT NULL AUTO_INCREMENT,
        ativa BIT NOT NULL,
        capacidade INT NOT NULL,
        data_criacao DATETIME NOT NULL,
        numero INT NOT NULL,
        cinema_id BIGINT NOT NULL,
        CONSTRAINT pk_salas PRIMARY KEY (id),
        CONSTRAINT fk_salas_cinema FOREIGN KEY (cinema_id) REFERENCES cinemas (id)
    ) ENGINE = InnoDB;

CREATE TABLE
    poltronas (
        id BIGINT NOT NULL AUTO_INCREMENT,
        ativa BIT NOT NULL,
        coluna INT NOT NULL,
        fileira CHAR(1) NOT NULL,
        tipo VARCHAR(50) NOT NULL,
        sala_id BIGINT NOT NULL,
        CONSTRAINT pk_poltronas PRIMARY KEY (id),
        CONSTRAINT fk_poltronas_sala FOREIGN KEY (sala_id) REFERENCES salas (id)
    ) ENGINE = InnoDB;

-- ============================================================
-- SESSÕES / INGRESSOS
-- ============================================================
CREATE TABLE
    sessoes (
        id BIGINT NOT NULL AUTO_INCREMENT,
        ativo BIT NOT NULL,
        data_hora_fim DATETIME NOT NULL,
        data_hora_inicio DATETIME NOT NULL,
        formato VARCHAR(50) NOT NULL,
        preco DECIMAL(10, 2) NOT NULL,
        version BIGINT NULL,
        filme_id BIGINT NOT NULL,
        sala_id BIGINT NOT NULL,
        CONSTRAINT pk_sessoes PRIMARY KEY (id),
        CONSTRAINT uk_sessoes_sala UNIQUE (sala_id),
        CONSTRAINT fk_sessoes_filme FOREIGN KEY (filme_id) REFERENCES filmes (id),
        CONSTRAINT fk_sessoes_sala FOREIGN KEY (sala_id) REFERENCES salas (id)
    ) ENGINE = InnoDB;

CREATE TABLE
    ingressos (
        id BIGINT NOT NULL AUTO_INCREMENT,
        data_criacao DATETIME NOT NULL,
        preco DECIMAL(10, 2) NOT NULL,
        status VARCHAR(50) NOT NULL,
        tipo VARCHAR(50) NOT NULL,
        poltrona_id BIGINT NOT NULL,
        sessao_id BIGINT NOT NULL,
        usuario_id BIGINT NOT NULL,
        CONSTRAINT pk_ingressos PRIMARY KEY (id),
        CONSTRAINT fk_ingressos_poltrona FOREIGN KEY (poltrona_id) REFERENCES poltronas (id),
        CONSTRAINT fk_ingressos_sessao FOREIGN KEY (sessao_id) REFERENCES sessoes (id),
        CONSTRAINT fk_ingressos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
    ) ENGINE = InnoDB;

-- ============================================================
-- PRODUTOS / PREÇOS
-- ============================================================
CREATE TABLE
    produtos (
        id BIGINT NOT NULL AUTO_INCREMENT,
        ativo BIT NOT NULL,
        data_criacao DATETIME NOT NULL,
        descricao VARCHAR(255) NULL,
        nome VARCHAR(255) NOT NULL,
        preco DECIMAL(10, 2) NOT NULL,
        quantidade_estoque INT NOT NULL,
        tipo VARCHAR(50) NOT NULL,
        version BIGINT NULL,
        cinema_id BIGINT NOT NULL,
        CONSTRAINT pk_produtos PRIMARY KEY (id),
        CONSTRAINT fk_produtos_cinema FOREIGN KEY (cinema_id) REFERENCES cinemas (id)
    ) ENGINE = InnoDB;

CREATE TABLE
    precos (
        id BIGINT NOT NULL AUTO_INCREMENT,
        ativo BIT NOT NULL,
        data_criacao DATETIME NOT NULL,
        formato VARCHAR(50) NOT NULL,
        valor DECIMAL(10, 2) NOT NULL,
        cinema_id BIGINT NOT NULL,
        CONSTRAINT pk_precos PRIMARY KEY (id),
        CONSTRAINT fk_precos_cinema FOREIGN KEY (cinema_id) REFERENCES cinemas (id)
    ) ENGINE = InnoDB;

-- ============================================================
-- VENDAS / ITENS DA VENDA
-- ============================================================
CREATE TABLE
    vendas (
        id BIGINT NOT NULL AUTO_INCREMENT,
        codigo_comprovante VARCHAR(255) NOT NULL,
        data_hora DATETIME NOT NULL,
        forma_pagamento VARCHAR(50) NOT NULL,
        status VARCHAR(50) NOT NULL,
        valor_total DECIMAL(10, 2) NOT NULL,
        cliente_id BIGINT NOT NULL,
        CONSTRAINT pk_vendas PRIMARY KEY (id),
        CONSTRAINT uk_vendas_codigo_comprovante UNIQUE (codigo_comprovante),
        CONSTRAINT fk_vendas_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
    ) ENGINE = InnoDB;

CREATE TABLE
    itens_venda (
        id BIGINT NOT NULL AUTO_INCREMENT,
        descricao VARCHAR(255) NOT NULL,
        preco_subtotal DECIMAL(10, 2) NOT NULL,
        preco_unitario DECIMAL(10, 2) NOT NULL,
        quantidade INT NOT NULL,
        tipo_item VARCHAR(50) NOT NULL,
        ingresso_id BIGINT NULL,
        produto_id BIGINT NULL,
        venda_id BIGINT NOT NULL,
        CONSTRAINT pk_itens_venda PRIMARY KEY (id),
        CONSTRAINT uk_itens_venda_ingresso UNIQUE (ingresso_id),
        CONSTRAINT fk_itens_venda_ingresso FOREIGN KEY (ingresso_id) REFERENCES ingressos (id),
        CONSTRAINT fk_itens_venda_produto FOREIGN KEY (produto_id) REFERENCES produtos (id),
        CONSTRAINT fk_itens_venda_venda FOREIGN KEY (venda_id) REFERENCES vendas (id)
    ) ENGINE = InnoDB;