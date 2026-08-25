package poltrona.dto.usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UsuarioResponseDTO(

                Long id,
                String nome,
                String email,
                String cpf,
                LocalDate dataNascimento,
                Boolean ativo,
                LocalDateTime dataCriacao

) {
}
