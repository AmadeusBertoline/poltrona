package poltrona.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cliente extends Usuario {

    @Column(nullable = false, unique = true)
    private String telefone;

    public Cliente(String nome, String email, String cpf, String senha, String telefone) {
        super(nome, email, senha, cpf);
        this.telefone = telefone;
    }

    public void atualizarTelefone(String novoTelefone) {
        if (novoTelefone != null && !novoTelefone.isBlank()) {
            this.telefone = novoTelefone;
        }
    }
}