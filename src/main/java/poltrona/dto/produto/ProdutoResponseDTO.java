package poltrona.dto.produto;

import java.math.BigDecimal;

public record ProdutoResponseDTO(

        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        Boolean ativo

) {
}
