package poltrona.dto.sessao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Positive;
import poltrona.enums.filme.FormatoFilme;
import poltrona.validation.dataHoraFuturaValida.DataHoraFuturaValida;
import poltrona.validation.formatoFilme.FormatoFilmeValido;
import poltrona.validation.precoValido.PrecoValido;

public record AtualizaSessaoRequestDTO(

        @DataHoraFuturaValida 
        LocalDateTime dataHoraInicio,

        @Positive(message = "O ID do filme deve ser um número positivo.")
        Long filmeId,

        @Positive(message = "O ID da sala deve ser um número positivo.")
        Long salaId,

        @PrecoValido 
        BigDecimal preco,
        
        @FormatoFilmeValido 
        FormatoFilme formato,

        @Positive(message = "A tolerância de minutos deve ser um número positivo.")
        Integer toleranciaMinutosCompra

) {}