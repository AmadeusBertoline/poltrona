package poltrona.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import poltrona.enums.usuario.StatusConta;

@Entity
@Table(name = "gerentes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gerente extends Usuario {

    public Gerente(String nome, String email, String senha, String cpf, LocalDate dataNascimento, StatusConta status) {
        super(nome, email, senha, cpf, dataNascimento);
    }

}
