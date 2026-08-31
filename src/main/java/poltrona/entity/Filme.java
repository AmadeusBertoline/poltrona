package poltrona.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poltrona.enums.filme.ClassificacaoIndicativa;
import poltrona.enums.filme.FormatoFilme;
import poltrona.enums.filme.GeneroFilme;

@Entity
@Getter
@Table(name = "filmes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private ClassificacaoIndicativa classificacaoIndicativa;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "generos", joinColumns = @JoinColumn(name = "filme_id"))
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<FormatoFilme> formatoFilme;

    @Column(nullable = false)
    private Boolean ativo;

    public Filme(String titulo, String sinopse, Set<GeneroFilme> generos, Integer duracao,
            String diretor, String distribuidora, LocalDate dataLancamento, String imagePath,
            ClassificacaoIndicativa classificacaoIndicativa) {
        this.titulo = titulo;
        this.sinopse = sinopse;

        if (generos != null) {
            this.generos.addAll(generos);
        }

        this.duracao = duracao;
        this.diretor = diretor;
        this.distribuidora = distribuidora;
        this.dataLancamento = dataLancamento;
        this.imagePath = imagePath;
        this.classificacaoIndicativa = classificacaoIndicativa;
        this.ativo = true;
    }

    public void atualizarDados(String titulo, String sinopse, Integer duracao, String diretor,
            String distribuidora, LocalDate dataLancamento, String imagePath, Set<FormatoFilme> formatoFilme,
            Set<GeneroFilme> genero, Boolean ativo) {
        if (titulo != null && !titulo.isBlank())
            this.titulo = titulo;
        if (sinopse != null && !sinopse.isBlank())
            this.sinopse = sinopse;
        if (duracao != null)
            this.duracao = duracao;
        if (diretor != null && !diretor.isBlank())
            this.diretor = diretor;
        if (distribuidora != null && !distribuidora.isBlank())
            this.distribuidora = distribuidora;
        if (dataLancamento != null)
            this.dataLancamento = dataLancamento;
        if (imagePath != null && !imagePath.isBlank())
            this.imagePath = imagePath;
        if (formatoFilme != null) {
            this.formatoFilme = formatoFilme;
        }
        if (genero != null) {
            this.generos = genero;
        }
        if (ativo != null) {
            this.ativo = ativo;
        }
    }

    public void adicionarGenero(GeneroFilme genero) {
        this.generos.add(genero);
    }

    public void removerGenero(GeneroFilme genero) {
        this.generos.remove(genero);
    }

    public void inativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }
}