package poltrona.entity;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "salas", uniqueConstraints = @UniqueConstraint(name = "uk_sala_numero_cinema", columnNames = { "numero",
        "cinema_id" }))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private Integer fileiras;

    @Column(nullable = false)
    private Integer poltronasPorFileira;

    @OneToMany(mappedBy = "sala", fetch = FetchType.LAZY)
    private List<Poltrona> poltronas;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "cinema_id", nullable = true)
    private Cinema cinema;

}
