package poltrona.dto.cliente;

import poltrona.dto.usuario.UsuarioResponseDTO;

public record ClienteResponseDTO(

                UsuarioResponseDTO usuario,
                String telefone

) {
}
