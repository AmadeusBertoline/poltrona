package poltrona.dto.sala;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import poltrona.dto.poltrona.PoltronaRequestDTO;

public record AtualizaSalaRequestDTO(

        @Positive(message = "O número da sala deve ser um número positivo.")
        Integer numero,
        
        @Valid 
        PoltronaRequestDTO poltronas

) {}