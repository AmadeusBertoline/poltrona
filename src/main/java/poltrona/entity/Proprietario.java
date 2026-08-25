package poltrona.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proprietarios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Proprietario extends Usuario {

    @OneToMany(mappedBy = "proprietario")
    List<Cinema> cinemas;

    public Proprietario(String nome, String email, String senha, String cpf, LocalDate dataNascimento) {
        super(nome, email, senha, cpf, dataNascimento);
    }

}
