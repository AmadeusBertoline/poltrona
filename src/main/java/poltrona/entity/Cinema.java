package poltrona.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "cinemas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomeFantasia;

    @Column(nullable = false, length = 150)
    private String razaoSocial;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false, unique = true)
    private String telefone;

    @Embedded
    private Endereco endereco;

    @OneToMany(mappedBy = "cinema", orphanRemoval = true)
    private List<Sala> salas = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "proprietario_id", nullable = false)
    private Proprietario proprietario;

    @PrePersist
    private void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

    public Cinema(String nomeFantasia, String razaoSocial, String cnpj, String telefone, Endereco endereco) {
        this.nomeFantasia = nomeFantasia;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.endereco = endereco;
    }
}