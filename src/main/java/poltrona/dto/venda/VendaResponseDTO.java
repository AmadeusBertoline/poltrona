package poltrona.dto.venda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import poltrona.enums.venda.FormaPagamento;
import poltrona.enums.venda.StatusVenda;

public record VendaResponseDTO(
        Long id,
        UUID codigoComprovante,
        LocalDateTime dataVenda,
        BigDecimal valorTotal,
        StatusVenda status,
        FormaPagamento formaPagamento,
        List<ItemVendaResponseDTO> itens
) {}