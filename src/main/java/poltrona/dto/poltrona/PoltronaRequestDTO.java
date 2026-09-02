package poltrona.dto.poltrona;

import java.util.Map;

public record PoltronaRequestDTO(

        Map<String, Integer> fileiras
) {
}
