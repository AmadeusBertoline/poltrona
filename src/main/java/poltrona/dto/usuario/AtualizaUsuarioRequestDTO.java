package poltrona.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import poltrona.validation.nomeValido.NomeValido;
import java.time.LocalDate;

public record AtualizaUsuarioRequestDTO(

        @NomeValido
        String nome,

        @Email(message = "E-mail inválido.")
        String email,

        @Past(message = "A data de nascimento deve ser no passado.")
        LocalDate dataNascimento

) {
}