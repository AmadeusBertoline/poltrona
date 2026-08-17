package poltrona.dto.admin;

import poltrona.dto.usuario.UsuarioRequestDTO;

public record AdminRequestDTO(

        UsuarioRequestDTO usuario

) {
}
