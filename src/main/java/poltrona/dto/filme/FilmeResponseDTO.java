package poltrona.dto.filme;

import java.time.LocalDate;
import poltrona.enums.StatusFilme;

public record FilmeResponseDTO(

        Long id,

        String titulo,

        String sinopse,

        Integer duracao,

        String diretor,

        String distribuidora,

        LocalDate dataLancamento,

        String imagePath,

        StatusFilme status

) {
}
