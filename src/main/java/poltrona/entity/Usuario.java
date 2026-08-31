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
import poltrona.enums.usuario.StatusConta;

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

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private StatusConta status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    public Usuario(String nome, String email, String senha, String cpf, LocalDate dataNascimento) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.status = StatusConta.ATIVA;
        this.dataCriacao = LocalDateTime.now();
    }

    public void encerrar() {
        this.status = StatusConta.ENCERRADA;
    }

    public void bloquear() {
        this.status = StatusConta.BLOQUEADA;
    }

    public void atualizar(String nome, String email, LocalDate dataNascimento) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }

        if (email != null && !email.isBlank()) {
            this.email = email;
        }

        if (dataNascimento != null) {
            this.dataNascimento = dataNascimento;
        }
    }

    public void atualizarSenha(String novaSenha) {
        if (novaSenha != null && !novaSenha.isBlank()) {
            this.senha = novaSenha;
        }
    }
}