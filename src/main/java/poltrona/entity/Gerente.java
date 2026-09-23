package poltrona.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gerentes")
@Getter 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gerente extends Usuario {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Cinema cinema;

    public Gerente(String nome, String email, String senha, String cpf, LocalDate dataNascimento) {
        super(nome, email, senha, cpf, dataNascimento);
    }

}
