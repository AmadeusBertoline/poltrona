package poltrona.dto.cliente;

import poltrona.dto.usuario.UsuarioRequestDTO;

public record ClienteRequestDTO(

        UsuarioRequestDTO usuario,
        String telefone

) {
}
