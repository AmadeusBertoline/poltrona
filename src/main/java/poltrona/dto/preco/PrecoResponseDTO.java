package poltrona.dto.preco;

import java.math.BigDecimal;

public record PrecoResponseDTO(

        Long id,
        String nome,
        BigDecimal preco,
        Boolean status

) {
}
