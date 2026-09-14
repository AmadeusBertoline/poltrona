package poltrona.dto.admin;

import jakarta.validation.Valid;
import poltrona.dto.usuario.UsuarioRequestDTO;

public record AdminRequestDTO(

        @Valid 
        UsuarioRequestDTO usuario

) {}
