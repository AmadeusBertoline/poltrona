package poltrona.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    public Usuario(String nome, String email, String senha, String cpf, LocalDate dataNascimento) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
    }

    public void alterarSenha(String novaSenha) {
        if (novaSenha != null && !novaSenha.isBlank()) {
            this.senha = novaSenha;
        }
    }

    public void encerrarConta() {
        this.ativo = false;
    }

    public void ativarConta() {
        this.ativo = true;
    }

    public void atualizar(String nome, String email) {

        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }

        if (email != null && !email.isBlank()) {
            this.email = email;
        }
    }
}