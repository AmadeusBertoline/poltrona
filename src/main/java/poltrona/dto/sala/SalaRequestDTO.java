package poltrona.dto.sala;

import poltrona.dto.poltrona.PoltronaRequestDTO;

public record SalaRequestDTO (

    Long idCinema,
    Integer numero,
    PoltronaRequestDTO poltronas
    
){}
