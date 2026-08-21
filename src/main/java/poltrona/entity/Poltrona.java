package poltrona.entity;

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
import poltrona.enums.TipoPoltrona;

@Entity
@Getter
@Table(name = "poltronas", uniqueConstraints = @UniqueConstraint(name = "uk_poltronas_salas", columnNames = { "sala_id",
        "fileira", "coluna" }))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Poltrona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private char fileira;

    @Column(nullable = false)
    private Integer coluna;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoltrona tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    public Poltrona(char fileira, Integer coluna, Sala sala) {
        this.fileira = fileira;
        this.coluna = coluna;
        this.tipo = (tipo != null) ? tipo : TipoPoltrona.COMUM;
        this.sala = sala;
    }

    public void atualizarTipo(TipoPoltrona tipo) {
        if (tipo != null) {
            this.tipo = tipo;
        }
    }
}