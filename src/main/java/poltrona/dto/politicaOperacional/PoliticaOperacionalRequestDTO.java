package poltrona.dto.politicaOperacional;

public record PoliticaOperacionalRequestDTO(

                Integer toleranciaMinutosCompra,

                Integer antecedenciaMinutosCancelamento,

                Integer intervaloLimpezaMinutos

) {
}
