package poltrona.dto.sessao;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import poltrona.enums.filme.FormatoFilme;
import poltrona.validation.dataHoraFuturaValida.DataHoraFuturaValida;
import poltrona.validation.formatoFilme.FormatoFilmeValido;

public record SessaoRequestDTO(

        @NotNull(message = "A data e hora de início são obrigatórias.")
        @DataHoraFuturaValida 
        LocalDateTime dataHoraInicio,

        @NotNull(message = "O ID do filme é obrigatório.")
        @Positive(message = "O ID do filme deve ser um número positivo.")
        Long idFilme,

        @NotNull(message = "O ID da sala é obrigatório.")
        @Positive(message = "O ID da sala deve ser um número positivo.")
        Long idSala,

        @NotNull(message = "O formato do filme é obrigatório.")
        @FormatoFilmeValido 
        FormatoFilme formato

) {}