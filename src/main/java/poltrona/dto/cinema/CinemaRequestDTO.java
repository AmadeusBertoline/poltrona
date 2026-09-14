package poltrona.dto.cinema;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import poltrona.dto.endereco.EnderecoRequestDTO;
import poltrona.dto.politicaOperacional.PoliticaOperacionalRequestDTO;
import poltrona.validation.cnpjValido.CnpjValido;
import poltrona.validation.nomeValido.NomeValido;
import poltrona.validation.telefoneValido.TelefoneValido;

public record CinemaRequestDTO(

    @NotNull(message = "O nome fantasia é obrigatório.")
    @NomeValido 
    String nomeFantasia,

    @NotNull(message = "A razão social é obrigatória.")
    @NomeValido 
    String razaoSocial,

    @NotNull(message = "O CNPJ é obrigatório.")
    @CnpjValido
    String cnpj,

    @NotNull(message = "O telefone é obrigatório.")
    @TelefoneValido
    String telefone,

    @NotNull(message = "O endereço do cinema é obrigatório.")
    @Valid 
    EnderecoRequestDTO endereco,

    @Valid
    PoliticaOperacionalRequestDTO politicaOperacional

) {}