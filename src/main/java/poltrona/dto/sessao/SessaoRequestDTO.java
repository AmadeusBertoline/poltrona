package poltrona.dto.sessao;

import java.time.LocalDateTime;
import poltrona.enums.filme.FormatoFilme;

public record SessaoRequestDTO(

                LocalDateTime dataHoraInicio,
                Long idFilme,
                Long idSala,
                FormatoFilme formato

) {}
