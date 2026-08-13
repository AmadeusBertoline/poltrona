package poltrona.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import poltrona.enums.GeneroFilme;
import poltrona.enums.StatusFilme;

@Entity
@Getter
@Setter
@Table(name = "filmes")
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String titulo;

    @Column(nullable = false)
    private String sinopse;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "generos", joinColumns = @JoinColumn(name = "filme_id"))
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<GeneroFilme> generos = new HashSet<>();

    @Column(nullable = false)
    private Integer duracao;

    @Column(nullable = false)
    private String diretor;

    @Column(nullable = false)
    private String distribuidora;

    @Column(nullable = false)
    private LocalDate dataLancamento;

    @Column(nullable = false, unique = true)
    private String imagePath;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusFilme status;

    @PrePersist
    void prePersist() {
        this.status = StatusFilme.EM_BREVE;
    }

    public void adicionarGenero(GeneroFilme genero) {
        this.generos.add(genero);
    }

}