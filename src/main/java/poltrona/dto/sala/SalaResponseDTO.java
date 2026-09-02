package poltrona.dto.sala;

import java.util.List;
import poltrona.dto.poltrona.PoltronaResponseDTO;

public record SalaResponseDTO(

        Long id,
        Integer numero,
        Integer capacidade,
        List<PoltronaResponseDTO> poltronas

) {
}
