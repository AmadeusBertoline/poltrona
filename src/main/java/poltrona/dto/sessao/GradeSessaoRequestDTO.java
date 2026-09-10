package poltrona.dto.sessao;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import poltrona.enums.filme.FormatoFilme;

public record GradeSessaoRequestDTO(

        @NotNull Long filmeId,

        @NotNull Long salaId,

        @NotNull FormatoFilme formato,

        @NotNull LocalDate dataInicio,

        @NotNull LocalDate dataFim,

        @NotEmpty List<LocalTime> horarios
){}