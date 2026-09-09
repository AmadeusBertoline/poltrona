package poltrona.dto.preco;

import java.math.BigDecimal;
import poltrona.enums.filme.FormatoFilme;
import poltrona.validation.precoValido.PrecoValido;

public record PrecoRequestDTO(

        Long idCinema,

        FormatoFilme formato,

        @PrecoValido BigDecimal precoBase

) {
}
