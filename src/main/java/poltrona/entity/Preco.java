package poltrona.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "precos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Preco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private BigDecimal precoBase;

    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    public Preco(String nome, BigDecimal precoBase, Cinema cinema) {
        this.nome = nome;
        this.precoBase = precoBase;
        this.ativo = (ativo != null) ? ativo : true;
        this.cinema = cinema;
        this.dataCriacao = LocalDateTime.now();
    }

    public void atualizarPrecoBase(BigDecimal novoPreco) {
        if (novoPreco != null) {
            this.precoBase = novoPreco;
        }
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}