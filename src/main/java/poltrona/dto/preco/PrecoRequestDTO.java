package poltrona.dto.preco;

import java.math.BigDecimal;

import poltrona.validation.nomeValido.NomeValido;
import poltrona.validation.precoValido.PrecoValido;

public record PrecoRequestDTO(

        @NomeValido
        String nome,

        @PrecoValido
        BigDecimal precoBase

) {
}
