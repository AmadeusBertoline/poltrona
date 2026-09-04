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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.enums.ingresso.TipoIngresso;
import poltrona.exception.RegraNegocioException;

@Entity
@Getter
@Table(name = "ingressos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoIngresso tipo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusIngresso status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poltrona_id", nullable = false)
    private Poltrona poltrona;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    public Ingresso(TipoIngresso tipo, Sessao sessao, Poltrona poltrona, Usuario usuario) {
        this.preco = tipo.calcularPrecoFinal(sessao.getPreco().getPrecoBase());
        this.sessao = sessao;
        this.poltrona = poltrona;
        this.tipo = tipo;
        this.usuario = usuario;
        this.status = StatusIngresso.ATIVO;
        this.dataCriacao = LocalDateTime.now();
    }

    public void cancelar() {
        if (this.status == StatusIngresso.CANCELADO) {
            throw new RegraNegocioException("Este ingresso já está cancelado.");
        }
        this.status = StatusIngresso.CANCELADO;
    }
}