package poltrona.dto.usuario;

import java.time.LocalDate;

public record AtualizaUsuarioRequestDTO(

                String nome,
                String email,
                String cpf,
                LocalDate dataNascimento

) {
}
