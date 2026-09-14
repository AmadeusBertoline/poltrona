package poltrona.dto.sala;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import poltrona.dto.poltrona.PoltronaRequestDTO;

public record SalaRequestDTO (

    @Positive 
    @NotNull 
    Long idCinema,

    @Positive 
    @NotNull 
    Integer numero,
    
    @Valid 
    PoltronaRequestDTO poltronas
    
){}
