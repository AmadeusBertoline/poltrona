package poltrona.dto.proprietario;

import jakarta.validation.Valid;
import poltrona.dto.usuario.UsuarioRequestDTO;

public record ProprietarioRequestDTO(

        @Valid 
        UsuarioRequestDTO usuario

) {}
