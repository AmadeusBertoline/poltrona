package poltrona.dto.venda;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.produto.ProdutoRequestDTO;
import poltrona.enums.venda.FormaPagamento;
import poltrona.validation.formaPagamentoValida.FormaPagamentoValida;

import java.util.List;

public record VendaRequestDTO(

        @NotNull(message = "A forma de pagamento é obrigatória.")
        @FormaPagamentoValida
        FormaPagamento formaPagamento,

        @NotNull(message = "A lista de ingressos é obrigatória.")
        @NotEmpty(message = "A venda deve conter pelo menos um ingresso.")
        @Valid
        List<IngressoRequestDTO> ingressos,

        @Valid
        List<ProdutoRequestDTO> produtos
) {}