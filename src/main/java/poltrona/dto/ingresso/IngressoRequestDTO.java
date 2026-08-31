package poltrona.dto.ingresso;

import jakarta.validation.constraints.NotNull;
import poltrona.enums.ingresso.TipoIngresso;

public record IngressoRequestDTO(

        @NotNull
        TipoIngresso tipo,

        @NotNull
        Long idSessao,
        
        @NotNull
        Long idPoltrona

) {
}
