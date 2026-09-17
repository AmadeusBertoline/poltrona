package poltrona.dto.sessao;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record SessaoFiltroDTO(
        Long cinemaId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
        Long filmeId,
        Boolean apenasDisponiveis) {
    public SessaoFiltroDTO {
        if (apenasDisponiveis == null) {
            apenasDisponiveis = true;
        }
    }
}