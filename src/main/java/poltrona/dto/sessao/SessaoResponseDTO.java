package poltrona.dto.sessao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SessaoResponseDTO(

        Long id,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String nomeFilme,
        Integer sala,
        BigDecimal preco

) {
}
