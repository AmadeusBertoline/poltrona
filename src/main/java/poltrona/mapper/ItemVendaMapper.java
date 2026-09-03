package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.venda.ItemVendaResponseDTO;
import poltrona.entity.ItemVenda;

@Component
public class ItemVendaMapper {

    public ItemVendaResponseDTO toDTO(ItemVenda itemVenda) {

        return new ItemVendaResponseDTO(
                itemVenda.getId(),
                itemVenda.getDescricao(),
                itemVenda.getPrecoUnitario(),
                itemVenda.getQuantidade(),
                itemVenda.getPrecoSubtotal(),
                itemVenda.getTipoItem(),
                itemVenda.getIngresso().getId(),
                itemVenda.getProduto().getId()

        );

    }

}
