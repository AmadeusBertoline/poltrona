package poltrona.dto.poltrona;

import java.util.List;

public record MapaPoltronasResponseDTO(
        Long sessaoId,
        Long salaId,
        List<PoltronaStatusDTO> poltronas) {
}