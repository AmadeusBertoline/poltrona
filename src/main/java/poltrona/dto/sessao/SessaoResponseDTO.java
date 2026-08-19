package poltrona.dto.sessao;

import java.time.LocalDateTime;

public record SessaoResponseDTO(

    Long id,
    LocalDateTime dataHoraInicio,
    LocalDateTime dataHoraFim, 
    String nomeFilme,
    Integer sala

) {}
