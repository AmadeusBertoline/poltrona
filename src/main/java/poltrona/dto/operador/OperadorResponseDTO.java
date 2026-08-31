package poltrona.dto.operador;

import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.enums.usuario.TipoUsuario;

public record OperadorResponseDTO(

        UsuarioResponseDTO usuario,
        Long idCinema,
        TipoUsuario cargo,
        String telefone
     

) {
}
