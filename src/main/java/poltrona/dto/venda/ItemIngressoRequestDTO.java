package poltrona.dto.venda;

import poltrona.enums.ingresso.TipoIngresso;

public record ItemIngressoRequestDTO(

        Long sessaoId,

        Long poltronaId,

        TipoIngresso tipoIngresso
) {}