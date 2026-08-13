package poltrona.dto.cinema;

import java.util.List;
import poltrona.dto.endereco.EnderecoResponseDTO;
import poltrona.dto.sala.SalaResponseDTO;

public record CinemaResponseDTO(

        Long id,
        String nomeFantasia,
        String razaoSocial,
        String cnpj,
        String telefone,
        EnderecoResponseDTO endereco,
        List<SalaResponseDTO> salas

) {
}
