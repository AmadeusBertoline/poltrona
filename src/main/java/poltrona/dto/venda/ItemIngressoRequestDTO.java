package poltrona.dto.venda;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import poltrona.enums.ingresso.TipoIngresso;
import poltrona.validation.tipoIngresso.TipoIngressoValido;

public record ItemIngressoRequestDTO(

        @NotNull(message = "O ID da sessão é obrigatório.")
        @Positive(message = "O ID da sessão deve ser um número positivo.")
        Long sessaoId,

        @NotNull(message = "O ID da poltrona é obrigatório.")
        @Positive(message = "O ID da poltrona deve ser um número positivo.")
        Long poltronaId,

        @NotNull(message = "O tipo de ingresso é obrigatório.")
        @TipoIngressoValido
        TipoIngresso tipoIngresso
) {}