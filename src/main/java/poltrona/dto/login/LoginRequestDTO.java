package poltrona.dto.login;

import jakarta.validation.constraints.NotNull;
import poltrona.validation.emailOrCpfValido.EmailOrCpfValido;
import poltrona.validation.senhaValida.SenhaValida;

public record LoginRequestDTO(

    @NotNull(message = "O e-mail ou CPF é obrigatório.")
    @EmailOrCpfValido
    String emailOrCpf,

    @NotNull(message = "A senha é obrigatória.")
    @SenhaValida
    String senha

) {}