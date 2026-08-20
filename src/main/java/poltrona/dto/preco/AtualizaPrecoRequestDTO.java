package poltrona.dto.preco;

import java.math.BigDecimal;

public record AtualizaPrecoRequestDTO(

        BigDecimal precoBase,
        Boolean status

) {
}
