package poltrona.dto.cinema;

import poltrona.enums.cinema.StatusCinema;

public record CinemaFiltroDTO(
        String nome,
        String cnpj,
        String cidade,
        String uf,
        StatusCinema status,
        Long proprietarioId) {
}