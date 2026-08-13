package poltrona.dto.poltrona;

import poltrona.enums.TipoPoltrona;

public record PoltronaResponseDTO(

        Long id,
        String fileira,
        String coluna,
        TipoPoltrona tipo,
        Long idSala

) {
}
