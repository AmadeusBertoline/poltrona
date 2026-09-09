package poltrona.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.persistence.CascadeType;
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
    private Integer capacidade;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Poltrona> poltronas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    public Sala(Integer numero, Map<Character, Integer> capacidade, Cinema cinema) {
        this.numero = numero;
        this.cinema = cinema;
        this.dataCriacao = LocalDateTime.now();
        this.capacidade = (capacidade == null) ? 0
                : capacidade.values().stream()
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .sum();
    }

    public void atualizar(Integer numero, Map<String, Integer> capacidade) {
        if (numero != null)
            this.numero = numero;

        this.capacidade = (capacidade == null) ? 0
                : capacidade.values().stream()
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .sum();
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }
}