package poltrona.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poltrona.exception.RegraNegocioException;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque;

    @Column(nullable = false)
    private Boolean ativo;

    public Produto(String nome, String descricao, BigDecimal preco, Integer quantidadeEstoque) {
        this.nome = nome.trim();
        this.descricao = descricao != null ? descricao.trim() : null;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.ativo = true;
    }

    public void debitarEstoque(Integer quantidade) {
        if (quantidade > this.quantidadeEstoque) {
            throw new RegraNegocioException("Estoque insuficiente para o produto: " + this.nome);
        }
        this.quantidadeEstoque -= quantidade;
    }

    public void adicionarEstoque(Integer quantidade) {
        this.quantidadeEstoque += quantidade;
    }

    public boolean temEstoqueSuficiente(Integer quantidade) {
        return this.quantidadeEstoque >= quantidade;
    }

    public void atualizar(String novoNome, String novaDescricao, BigDecimal novoPreco) {
        this.nome = novoNome.trim();
        this.descricao = novaDescricao != null ? novaDescricao.trim() : null;
        this.preco = novoPreco;
    }

    public void atualizarPreco(BigDecimal novoPreco) {
        this.preco = novoPreco;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}