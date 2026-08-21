package poltrona.dto.ingresso;

import poltrona.enums.TipoIngresso;

public record IngressoRequestDTO(

        TipoIngresso tipo,
        Long idSessao,
        Long idPoltrona

) {
}
