package poltrona.dto.sala;

import poltrona.dto.poltrona.PoltronaRequestDTO;

public record AtualizaSalaRequestDTO(

        Integer numero,
        PoltronaRequestDTO poltronas

) {}
