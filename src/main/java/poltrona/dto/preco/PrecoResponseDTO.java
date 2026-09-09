package poltrona.dto.preco;

import java.math.BigDecimal;

import poltrona.enums.filme.FormatoFilme;

public record PrecoResponseDTO(

        Long id,
        FormatoFilme formato,
        BigDecimal valor,
        Boolean status

) {
}
