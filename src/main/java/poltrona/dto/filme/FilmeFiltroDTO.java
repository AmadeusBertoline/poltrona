package poltrona.dto.filme;

import java.time.LocalDate;
import poltrona.enums.filme.ClassificacaoIndicativa;
import poltrona.enums.filme.FormatoFilme;
import poltrona.enums.filme.GeneroFilme;

public record FilmeFiltroDTO(
        String titulo,
        GeneroFilme generoFilme,
        String diretor,
        String distribuidora,
        LocalDate dataLancamento,
        ClassificacaoIndicativa classificacaoIndicativa,
        FormatoFilme formatoFilme) {
}