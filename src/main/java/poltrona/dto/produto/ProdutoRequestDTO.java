package poltrona.dto.produto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProdutoRequestDTO(

        @NotNull(message = "O ID do produto é obrigatório.")
        @Positive(message = "O ID do produto deve ser um número positivo.")
        Long id,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser um número positivo.")
        Integer quantidade

) {}