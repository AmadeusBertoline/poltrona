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
import poltrona.enums.filme.FormatoFilme;

@Entity
@Table(name = "precos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cinema_formato", columnNames = { "cinema_id", "formato" }) })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Preco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FormatoFilme formato;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    public Preco(FormatoFilme formato, BigDecimal valor, Cinema cinema) {
        this.formato = formato;
        this.valor = valor;
        this.ativo = (ativo != null) ? ativo : true;
        this.cinema = cinema;
        this.dataCriacao = LocalDateTime.now();
    }

    public void atualizarPrecoBase(BigDecimal novoPreco) {
        if (novoPreco != null) {
            this.valor = novoPreco;
        }
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}