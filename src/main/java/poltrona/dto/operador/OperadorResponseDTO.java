package poltrona.dto.operador;

import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.enums.TipoUsuario;

public record OperadorResponseDTO(

        UsuarioResponseDTO usuario,
        Long idCinema,
        TipoUsuario cargo,
        String telefone
     

) {
}
