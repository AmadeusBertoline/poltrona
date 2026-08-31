package poltrona.dto.ingresso;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import poltrona.enums.ingresso.TipoIngresso;
import poltrona.enums.poltrona.TipoPoltrona;

public record IngressoResponseDTO(

        Long id,
        BigDecimal preco,
        TipoIngresso tipo,
        String tituloFilme,
        Integer sala,
        LocalDateTime inicioSessao,
        char fileira,
        Integer coluna,
        TipoPoltrona tipoPoltrona

) {}
