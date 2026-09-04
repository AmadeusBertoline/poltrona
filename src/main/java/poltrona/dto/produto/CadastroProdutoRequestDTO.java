package poltrona.dto.produto;

import java.math.BigDecimal;

import poltrona.enums.produto.TipoProduto;

public record CadastroProdutoRequestDTO(

        Long cinemaId,
        String nome,
        String descricao,
        TipoProduto tipo,
        BigDecimal preco,
        Integer quantidadeEstoque

) {}
