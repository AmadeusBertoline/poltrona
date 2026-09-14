package poltrona.dto.produto;

import java.math.BigDecimal;

public record AtualizaProdutoRequestDTO(

        String nome,

        String descricao,

        BigDecimal preco,

        Integer quantidadeEstoque
) {}