package poltrona.dto.usuario;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(

        Long id,
        String nome,
        String email,
        String cpf,
        Boolean ativo,
        LocalDateTime dataCriacao

) {
}
