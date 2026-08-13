package poltrona.dto.poltrona;

import poltrona.enums.TipoPoltrona;

public record PoltronaResponseDTO(

        Long id,
        char fileira,
        Integer coluna,
        TipoPoltrona tipo,
        Long idSala

) {
}
