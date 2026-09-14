package poltrona.dto.preco;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import poltrona.enums.filme.FormatoFilme;
import poltrona.validation.formatoFilme.FormatoFilmeValido;
import poltrona.validation.precoValido.PrecoValido;

public record PrecoRequestDTO(

        @NotNull(message = "O ID do cinema é obrigatório.")
        @Positive(message = "O ID do cinema deve ser um número positivo.")
        Long idCinema,

        @NotNull(message = "O formato do filme é obrigatório.")
        @FormatoFilmeValido 
        FormatoFilme formato,

        @NotNull(message = "O preço base é obrigatório.")
        @PrecoValido 
        BigDecimal precoBase

) {
}