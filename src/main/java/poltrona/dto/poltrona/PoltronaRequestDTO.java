package poltrona.dto.poltrona;

import java.util.Map;

public record PoltronaRequestDTO(

        Map<Character, Integer> fileiras
) {}
