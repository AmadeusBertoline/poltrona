package poltrona.dto.venda;

import poltrona.enums.venda.FormaPagamento;
import java.util.List;

public record VendaRequestDTO(

        FormaPagamento formaPagamento,

        List<ItemIngressoRequestDTO> ingressos,

        List<ItemProdutoRequestDTO> produtos
) {}