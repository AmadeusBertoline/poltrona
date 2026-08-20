package poltrona.dto.sessao;

import java.time.LocalDateTime;

public record SessaoRequestDTO(

                LocalDateTime dataHoraInicio,
                Long idFilme,
                Long idSala,
                Long idPreco

) {
}
