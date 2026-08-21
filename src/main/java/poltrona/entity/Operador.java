package poltrona.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operadores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Operador extends Usuario {

    @Column(unique = true, length = 50)
    private String matricula;

    @Column(nullable = false)
    private String cargo;

    @Column(nullable = false)
    private String departamento;

    @Column(nullable = false)
    private LocalDate dataAdmissao;

    public Operador(String nome, String email, String senha, String cpf, String matricula,
            String cargo, String departamento, LocalDate dataAdmissao) {
        super(nome, email, senha, cpf);
        this.matricula = matricula;
        this.cargo = cargo;
        this.departamento = departamento;
        this.dataAdmissao = dataAdmissao;
    }

    public void alterarCargoEDepartamento(String novoCargo, String novoDepartamento) {
        if (novoCargo != null && !novoCargo.isBlank()) {
            this.cargo = novoCargo;
        }
        if (novoDepartamento != null && !novoDepartamento.isBlank()) {
            this.departamento = novoDepartamento;
        }
    }
}