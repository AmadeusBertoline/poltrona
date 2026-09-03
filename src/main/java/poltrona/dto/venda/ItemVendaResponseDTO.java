package poltrona.dto.venda;

import java.math.BigDecimal;
import poltrona.enums.venda.TipoItemVenda;

public record ItemVendaResponseDTO(
        Long id,
        String descricao,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal precoSubtotal,
        TipoItemVenda tipoItem,
        Long ingressoId,
        Long produtoId
) {}