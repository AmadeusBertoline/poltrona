package poltrona.dto.endereco;

import poltrona.validation.cepValido.CepValido;
import poltrona.validation.logradouroValido.LogradouroValido;
import poltrona.validation.nomeValido.NomeValido;
import poltrona.validation.numeroEnderecoValido.NumeroEnderecoValido;
import poltrona.validation.ufValida.UfValida;

public record EnderecoRequestDTO(
                @LogradouroValido String logradouro,
                @NumeroEnderecoValido String numero,
                String complemento,
                @NomeValido String bairro,
                @NomeValido String cidade,
                @UfValida String uf,
                @CepValido String cep) {
}