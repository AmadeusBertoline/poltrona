package poltrona.dto.cliente;

import jakarta.validation.Valid;
import poltrona.dto.usuario.UsuarioRequestDTO;

public record ClienteRequestDTO(

        @Valid 
        UsuarioRequestDTO usuario

) {
}
