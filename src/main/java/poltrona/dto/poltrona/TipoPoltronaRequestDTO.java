package poltrona.dto.poltrona;

import poltrona.enums.poltrona.TipoPoltrona;
import poltrona.validation.tipoPoltronaValida.TipoPoltronaValida;

public record TipoPoltronaRequestDTO(

        @TipoPoltronaValida 
        TipoPoltrona tipo

) {
}
