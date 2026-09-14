package poltrona.dto.admin;

import jakarta.validation.Valid;
import poltrona.dto.usuario.AtualizaUsuarioRequestDTO;

public record AtualizaAdminRequestDTO(

        @Valid 
        AtualizaUsuarioRequestDTO usuario

) {}
