package poltrona.dto.venda;

import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.produto.ProdutoRequestDTO;
import poltrona.enums.venda.FormaPagamento;
import java.util.List;

public record VendaRequestDTO(

        FormaPagamento formaPagamento,

        List<IngressoRequestDTO> ingressos,

        List<ProdutoRequestDTO> produtos
) {}