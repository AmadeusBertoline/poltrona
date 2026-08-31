package poltrona.dto.sala;

public record SalaRequestDTO (

    Long idCinema,
    Integer numero,
    Integer fileiras,
    Integer poltronasPorFileira
    
){}
