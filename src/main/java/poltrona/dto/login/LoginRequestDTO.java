package poltrona.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import poltrona.validation.senhaValida.SenhaValida;

public record LoginRequestDTO(

    @NotNull(message = "O e-mail ou CPF é obrigatório.")
    @Email 
    String email,

    @NotNull(message = "A senha é obrigatória.")
    @SenhaValida
    String senha

) {}