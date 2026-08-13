package poltrona.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import poltrona.enums.TipoPoltrona;

@Entity
@Getter
@Setter
@Table(name = "poltronas", uniqueConstraints = @UniqueConstraint(
    name = "uk_poltronas_salas",
    columnNames = {"sala_id", "fileira", "coluna"}
))
public class Poltrona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileira;

    @Column(nullable = false)
    private String coluna;

    @Column(nullable = false)
    private TipoPoltrona tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

}
