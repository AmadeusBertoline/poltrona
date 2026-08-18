CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao DATETIME(6)
);

CREATE TABLE administradores (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_administradores_usuarios FOREIGN KEY (id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE clientes (
    id BIGINT PRIMARY KEY,
    telefone VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT fk_clientes_usuarios FOREIGN KEY (id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE operadores (
    id BIGINT PRIMARY KEY,
    matricula VARCHAR(50) UNIQUE,
    cargo VARCHAR(255),
    departamento VARCHAR(255),
    data_admissao DATE,
    CONSTRAINT fk_operadores_usuarios FOREIGN KEY (id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE cinemas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_fantasia VARCHAR(100) NOT NULL,
    razao_social VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    telefone VARCHAR(255),
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    uf VARCHAR(255),
    cep VARCHAR(255)
);

CREATE TABLE salas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL,
    fileiras INT NOT NULL,
    poltronas_por_fileira INT NOT NULL,
    cinema_id BIGINT,
    CONSTRAINT uk_sala_numero_cinema UNIQUE (numero, cinema_id),
    CONSTRAINT fk_salas_cinemas FOREIGN KEY (cinema_id) REFERENCES cinemas (id)
);

CREATE TABLE poltronas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fileira CHAR(1) NOT NULL,
    coluna INT NOT NULL,
    tipo INT NOT NULL,
    sala_id BIGINT NOT NULL,
    CONSTRAINT uk_poltronas_salas UNIQUE (sala_id, fileira, coluna),
    CONSTRAINT fk_poltronas_salas FOREIGN KEY (sala_id) REFERENCES salas (id)
);

CREATE TABLE filmes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL UNIQUE,
    sinopse VARCHAR(255) NOT NULL,
    duracao INT NOT NULL,
    diretor VARCHAR(255) NOT NULL,
    distribuidora VARCHAR(255) NOT NULL,
    data_lancamento DATE NOT NULL,
    image_path VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE generos (
    filme_id BIGINT NOT NULL,
    generos VARCHAR(255) NOT NULL,
    CONSTRAINT fk_generos_filmes FOREIGN KEY (filme_id) REFERENCES filmes (id)
);