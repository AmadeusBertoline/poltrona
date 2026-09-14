package poltrona.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.br.CPF;
import poltrona.validation.nomeValido.NomeValido;
import poltrona.validation.senhaValida.SenhaValida;
import java.time.LocalDate;

public record UsuarioRequestDTO(

        @NotNull(message = "O nome é obrigatório.")
        @NomeValido
        String nome,

        @NotNull(message = "O e-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        String email,

        @NotNull(message = "O CPF é obrigatório.")
        @CPF(message = "CPF inválido.")
        String cpf,

        @NotNull(message = "A senha é obrigatória.")
        @SenhaValida
        String senha,

        @NotNull(message = "A confirmação de senha é obrigatória.")
        String confirmarSenha,

        @NotNull(message = "A data de nascimento é obrigatória.")
        @Past(message = "A data de nascimento deve ser no passado.")
        LocalDate dataNascimento

) {
}