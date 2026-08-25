package poltrona.dto.usuario;

import java.time.LocalDate;

public record UsuarioRequestDTO(

        String nome,
        String email,
        String cpf,
        String senha,
        String confirmarSenha,
        LocalDate dataNascimento

) {
}
