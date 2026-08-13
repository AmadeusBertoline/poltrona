package poltrona.dto.cinema;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import poltrona.dto.endereco.EnderecoRequestDTO;
import poltrona.validation.cnpjValido.CnpjValido;
import poltrona.validation.nomeValido.NomeValido;
import poltrona.validation.telefoneValido.TelefoneValido;

public record CinemaRequestDTO(

    @NomeValido 
    String nomeFantasia,

    @NomeValido 
    String razaoSocial,

    @CnpjValido
    String cnpj,

    @TelefoneValido
    String telefone,

    @NotNull(message = "O endereço do cinema é obrigatório.")
    @Valid 
    EnderecoRequestDTO endereco

) {}
