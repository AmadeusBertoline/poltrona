package poltrona.dto.proprietario;

import jakarta.validation.Valid;
import poltrona.dto.usuario.AtualizaUsuarioRequestDTO;

public record AtualizaProprietarioRequestDTO(

        @Valid 
        AtualizaUsuarioRequestDTO usuario

) {}
