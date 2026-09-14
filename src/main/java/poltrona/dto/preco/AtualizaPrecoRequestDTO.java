package poltrona.dto.preco;

import java.math.BigDecimal;

import poltrona.validation.precoValido.PrecoValido;

public record AtualizaPrecoRequestDTO(

        @PrecoValido 
        BigDecimal precoBase,
    
        Boolean status

) {
}
