package poltrona.dto.filme;

import java.time.LocalDate;
import java.util.Set;

import poltrona.enums.filme.ClassificacaoIndicativa;
import poltrona.enums.filme.GeneroFilme;

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

        ClassificacaoIndicativa classificacaoIndicativa

) {
}
