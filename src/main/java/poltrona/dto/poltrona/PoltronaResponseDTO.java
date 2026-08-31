package poltrona.dto.poltrona;

import poltrona.enums.poltrona.TipoPoltrona;

public record PoltronaResponseDTO(

        Long id,
        char fileira,
        Integer coluna,
        TipoPoltrona tipo,
        Long idSala

) {
}
