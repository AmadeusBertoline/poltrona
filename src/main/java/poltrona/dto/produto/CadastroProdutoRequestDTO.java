package poltrona.dto.produto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import poltrona.enums.produto.TipoProduto;
import poltrona.validation.nomeValido.NomeValido;
import poltrona.validation.precoValido.PrecoValido;
import poltrona.validation.sinopseValida.SinopseValida;

public record CadastroProdutoRequestDTO(

        @NotNull(message = "O ID do cinema é obrigatório.")
        @Positive(message = "O ID do cinema deve ser um número positivo.")
        Long cinemaId,

        @NotNull(message = "O nome do produto é obrigatório.")
        @NomeValido
        String nome,

        @NotNull(message = "A descrição do produto é obrigatória.")
        @SinopseValida
        String descricao,

        @NotNull(message = "O tipo de produto é obrigatório.")
        TipoProduto tipo,

        @NotNull(message = "O preço é obrigatório.")
        @PrecoValido
        BigDecimal preco,

        @NotNull(message = "A quantidade em estoque é obrigatória.")
        @Min(value = 0, message = "A quantidade em estoque não pode ser negativa.")
        Integer quantidadeEstoque

) {}