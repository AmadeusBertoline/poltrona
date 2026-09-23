package poltrona.dto.gerente;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import poltrona.validation.nomeValido.NomeValido;

public record AtualizaGerenteRequestDTO(

        @NomeValido String nome,

        @Email(message = "E-mail inválido.") String email,

        @Past(message = "A data de nascimento deve ser no passado.") LocalDate dataNascimento

) {
}
