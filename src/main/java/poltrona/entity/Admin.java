package poltrona.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends Usuario {

    public Admin(String nome, String email, String senha, String cpf) {
        super(nome, email, senha, cpf);
    }

    public void atualizar(String nome, String email) {

        super.atualizar(nome, email);

    }
}