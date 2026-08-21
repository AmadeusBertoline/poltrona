package poltrona.dto.ingresso;

import jakarta.validation.constraints.NotNull;
import poltrona.enums.TipoIngresso;

public record IngressoRequestDTO(

        @NotNull
        TipoIngresso tipo,

        @NotNull
        Long idSessao,
        
        @NotNull
        Long idPoltrona

) {
}
