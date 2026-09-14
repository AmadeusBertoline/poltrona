package poltrona.dto.cinema;

import poltrona.dto.endereco.EnderecoRequestDTO;
import poltrona.dto.politicaOperacional.PoliticaOperacionalRequestDTO;

public record AtualizaCinemaRequestDTO(

        String nomeFantasia,
        String telefone,
        EnderecoRequestDTO endereco,
        PoliticaOperacionalRequestDTO politica

) {
}