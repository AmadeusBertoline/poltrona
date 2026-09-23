package poltrona.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gerentes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gerente extends Usuario {

    public Gerente(String nome, String email, String senha, String cpf, LocalDate dataNascimento) {
        super(nome, email, senha, cpf, dataNascimento);
    }

}
