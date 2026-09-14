package poltrona.dto.politicaOperacional;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PoliticaOperacionalRequestDTO(

                @NotNull(message = "A tolerancia do tempo de compra deve ser preenchida")
                @Positive(message = "O valor deve ser positivo")
                Integer toleranciaMinutosCompra,

                @NotNull(message = "A tolerancia do tempo de antecedencia de cancelamento deve ser preenchida")
                @Positive(message = "O valor deve ser positivo")
                Integer antecedenciaMinutosCancelamento,

                @NotNull(message = "O tempo de limpeza entre as sessões deve ser preenchido")
                @Positive(message = "O valor deve ser positivo")
                Integer intervaloLimpezaMinutos

) {
}
