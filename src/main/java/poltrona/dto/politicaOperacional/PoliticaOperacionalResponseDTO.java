package poltrona.dto.politicaOperacional;

public record PoliticaOperacionalResponseDTO(

        Integer toleranciaMinutosCompra,

        Integer antecedenciaMinutosCancelamento,

        Integer intervaloLimpezaMinutos

) {
}
