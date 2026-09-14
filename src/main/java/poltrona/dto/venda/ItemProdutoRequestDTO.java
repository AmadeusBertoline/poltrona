package poltrona.dto.venda;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemProdutoRequestDTO(

        @NotNull(message = "O ID do produto é obrigatório.")
        @Positive(message = "O ID do produto deve ser um número positivo.")
        Long produtoId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade
) {}