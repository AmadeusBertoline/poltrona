package poltrona.dto.sessao;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import poltrona.enums.filme.FormatoFilme;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

public record GradeSessaoRequestDTO(

                @NotNull Long filmeId,

                @NotNull Long salaId,

                @NotNull FormatoFilme formato,

                @NotNull LocalDate dataInicio,

                @NotNull LocalDate dataFim,

                @NotEmpty @ArraySchema(schema = @Schema(type = "string", format = "time", example = "19:30:00")) List<LocalTime> horarios) {
}