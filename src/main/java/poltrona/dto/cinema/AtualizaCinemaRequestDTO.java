package poltrona.dto.cinema;

import poltrona.dto.endereco.EnderecoRequestDTO;

public record AtualizaCinemaRequestDTO(

        String nomeFantasia,
        String telefone,
        EnderecoRequestDTO endereco

) {
}