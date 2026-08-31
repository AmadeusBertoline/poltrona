package poltrona.dto.usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

import poltrona.enums.usuario.StatusConta;

public record UsuarioResponseDTO(

                Long id,
                String nome,
                String email,
                String cpf,
                LocalDate dataNascimento,
                StatusConta status,
                LocalDateTime dataCriacao

) {
}
