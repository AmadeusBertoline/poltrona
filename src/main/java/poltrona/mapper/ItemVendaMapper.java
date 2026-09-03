package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.venda.ItemVendaResponseDTO;
import poltrona.entity.Ingresso;
import poltrona.entity.ItemVenda;
import poltrona.entity.Produto;
import poltrona.enums.venda.TipoItemVenda;
import java.math.BigDecimal;

@Component
public class ItemVendaMapper {

    public ItemVenda toEntityIngresso(Ingresso ingresso, BigDecimal precoCalculado, String descricao) {
        if (ingresso == null) {
            return null;
        }

        return new ItemVenda(
                descricao,
                precoCalculado,
                1,
                TipoItemVenda.INGRESSO,
                ingresso,
                null);
    }

    public ItemVenda toEntityProduto(Produto produto, Integer quantidade) {
        if (produto == null) {
            return null;
        }

        return new ItemVenda(
                produto.getNome(),
                produto.getPreco(),
                quantidade,
                TipoItemVenda.PRODUTO_CONVENIENCIA,
                null,
                produto);
    }

    public ItemVendaResponseDTO toDTO(ItemVenda itemVenda) {
        if (itemVenda == null) {
            return null;
        }

        Long ingressoId = (itemVenda.getIngresso() != null) ? itemVenda.getIngresso().getId() : null;
        Long produtoId = (itemVenda.getProduto() != null) ? itemVenda.getProduto().getId() : null;

        return new ItemVendaResponseDTO(
                itemVenda.getId(),
                itemVenda.getDescricao(),
                itemVenda.getPrecoUnitario(),
                itemVenda.getQuantidade(),
                itemVenda.getPrecoSubtotal(),
                itemVenda.getTipoItem(),
                ingressoId,
                produtoId);
    }
}