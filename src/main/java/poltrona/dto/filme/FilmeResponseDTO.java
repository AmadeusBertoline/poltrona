package poltrona.dto.filme;

import java.time.LocalDate;
import java.util.Set;

import poltrona.enums.GeneroFilme;
import poltrona.enums.StatusFilme;

public record FilmeResponseDTO(

        Long id,

        String titulo,

        String sinopse,

        Set<GeneroFilme> genero,

        Integer duracao,

        String diretor,

        String distribuidora,

        LocalDate dataLancamento,

        String imagePath,

        StatusFilme status

) {
}
