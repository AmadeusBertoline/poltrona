package poltrona.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends Usuario {

    public Admin(String nome, String email, String senha, String cpf, LocalDate dataNascimento) {
        super(nome, email, senha, cpf, dataNascimento);
    }

}
