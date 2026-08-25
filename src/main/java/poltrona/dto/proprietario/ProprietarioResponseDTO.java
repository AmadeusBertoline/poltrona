package poltrona.dto.proprietario;

import java.util.List;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;

public record ProprietarioResponseDTO(

        UsuarioResponseDTO usuarioResponseDTO,
        List<CinemaResponseDTO> cinemas

) {
}
