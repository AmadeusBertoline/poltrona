package poltrona.dto.sessao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Positive;
import poltrona.enums.filme.FormatoFilme;

public record AtualizaSessaoRequestDTO(
        LocalDateTime dataHoraInicio,
        Long filmeId,
        Long salaId,
        @Positive(message = "O preço deve ser maior que zero") BigDecimal preco,
        FormatoFilme formato,
        Integer toleranciaMinutosCompra) {
}