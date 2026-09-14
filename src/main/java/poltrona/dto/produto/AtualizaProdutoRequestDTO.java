package poltrona.dto.produto;

import jakarta.validation.constraints.Min;
import poltrona.validation.nomeValido.NomeValido;
import poltrona.validation.precoValido.PrecoValido;
import poltrona.validation.sinopseValida.SinopseValida;
import java.math.BigDecimal;

public record AtualizaProdutoRequestDTO(

        @NomeValido
        String nome,

        @SinopseValida
        String descricao,

        @PrecoValido
        BigDecimal preco,

        @Min(value = 0, message = "A quantidade em estoque não pode ser negativa.")
        Integer quantidadeEstoque
) {}