package poltrona.entity;

import java.math.BigDecimal;
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
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poltrona.enums.TipoIngresso;

@Entity
@Getter
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poltrona_id", nullable = false)
    private Poltrona poltrona;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Ingresso(TipoIngresso tipo, Sessao sessao, Poltrona poltrona) {
        this.preco = tipo.calcularPrecoFinal(sessao.getPreco().getPrecoBase());
        this.sessao = sessao;
        this.poltrona = poltrona;
        this.tipo = tipo;
    }

}
