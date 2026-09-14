package poltrona.dto.cinema;

import jakarta.validation.Valid;
import poltrona.dto.endereco.EnderecoRequestDTO;
import poltrona.dto.politicaOperacional.PoliticaOperacionalRequestDTO;
import poltrona.validation.nomeValido.NomeValido;
import poltrona.validation.telefoneValido.TelefoneValido;

public record AtualizaCinemaRequestDTO(

        @NomeValido 
        String nomeFantasia,

        @TelefoneValido 
        String telefone,

        @Valid 
        EnderecoRequestDTO endereco,
        
        @Valid 
        PoliticaOperacionalRequestDTO politica

) {
}