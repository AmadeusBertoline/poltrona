package poltrona.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poltrona.enums.produto.TipoProduto;
import poltrona.exception.RegraNegocioException;

@Entity
@Table(name = "produtos", uniqueConstraints = @UniqueConstraint(name = "uk_produtos_cinema_nome", columnNames = {
        "cinema_id", "nome" }))
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoProduto tipo;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    public Produto(Cinema cinema, String nome, String descricao, TipoProduto tipo, BigDecimal preco,
            Integer quantidadeEstoque) {
        if (cinema == null) {
            throw new IllegalArgumentException("O cinema é obrigatório para cadastrar um produto.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto não pode ser nulo ou vazio.");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço do produto deve ser um valor válido.");
        }

        this.cinema = cinema;
        this.nome = nome.trim();
        this.descricao = (descricao != null) ? descricao.trim() : null;
        this.tipo = tipo;
        this.preco = preco;
        this.quantidadeEstoque = (quantidadeEstoque != null && quantidadeEstoque >= 0) ? quantidadeEstoque : 0;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
    }

    public void debitarEstoque(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("A quantidade a debitar deve ser maior que zero.");
        }
        if (quantidade > this.quantidadeEstoque) {
            throw new RegraNegocioException("Estoque insuficiente para o produto: " + this.nome);
        }
        this.quantidadeEstoque -= quantidade;
    }

    public void adicionarEstoque(Integer quantidade) {
        if (quantidade != null && quantidade > 0) {
            this.quantidadeEstoque += quantidade;
        }
    }

    public boolean temEstoqueSuficiente(Integer quantidade) {
        return quantidade != null && quantidade > 0 && this.quantidadeEstoque >= quantidade;
    }

    public void atualizar(String novoNome, String novaDescricao, BigDecimal novoPreco) {
        if (novoNome != null && !novoNome.isBlank()) {
            this.nome = novoNome.trim();
        }
        if (novaDescricao != null) {
            this.descricao = novaDescricao.trim();
        }
        if (novoPreco != null && novoPreco.compareTo(BigDecimal.ZERO) >= 0) {
            this.preco = novoPreco;
        }
    }

    public void atualizarPreco(BigDecimal novoPreco) {
        if (novoPreco != null && novoPreco.compareTo(BigDecimal.ZERO) >= 0) {
            this.preco = novoPreco;
        }
    }

    public void alterarStatus(Boolean status) {
        if (status != null) {

            this.ativo = status;

        }
    }
}