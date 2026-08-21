package poltrona.entity;

import java.util.ArrayList;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "salas", uniqueConstraints = @UniqueConstraint(name = "uk_sala_numero_cinema", columnNames = { "numero",
        "cinema_id" }))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private List<Poltrona> poltronas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    public Sala(Integer numero, Integer fileiras, Integer poltronasPorFileira, Cinema cinema) {
        this.numero = numero;
        this.fileiras = fileiras;
        this.poltronasPorFileira = poltronasPorFileira;
        this.cinema = cinema;
    }

    public void atualizarDados(Integer numero, Integer fileiras, Integer poltronasPorFileira) {
        if (numero != null)
            this.numero = numero;
        if (fileiras != null)
            this.fileiras = fileiras;
        if (poltronasPorFileira != null)
            this.poltronasPorFileira = poltronasPorFileira;
    }
}