package poltrona.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "operadores")
@Getter
@Setter
public class Operador extends Usuario {

    @Column(unique = true, length = 50)
    private String matricula;

    private String cargo;

    private String departamento;

    private LocalDate dataAdmissao;
}
