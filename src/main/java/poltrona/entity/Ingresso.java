package poltrona.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Setter;
import poltrona.enums.TipoIngresso;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(nullable = false)
    private TipoIngresso tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poltrona_id", nullable = false)
    private Poltrona poltrona;

    public Ingresso(Sessao sessao, Poltrona poltrona, TipoIngresso tipo) {
        this.sessao = sessao;
        this.poltrona = poltrona;
        this.tipo = tipo;
    }

}
