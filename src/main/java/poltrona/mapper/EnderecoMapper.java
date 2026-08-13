package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.endereco.EnderecoRequestDTO;
import poltrona.dto.endereco.EnderecoResponseDTO;
import poltrona.entity.Endereco;

@Component
public class EnderecoMapper {

    public Endereco toEntity(EnderecoRequestDTO endereco) {

        Endereco entidade = new Endereco();
        entidade.setBairro(endereco.bairro());
        entidade.setCep(endereco.cep());
        entidade.setCidade(endereco.cidade());
        entidade.setComplemento(endereco.complemento());
        entidade.setLogradouro(endereco.logradouro());
        entidade.setNumero(endereco.numero());
        entidade.setUf(endereco.uf());

        return entidade;

    }

    public EnderecoResponseDTO toDTO(Endereco entidade) {
        if (entidade == null) {
            return null;
        }

        return new EnderecoResponseDTO(
            entidade.getLogradouro(),
            entidade.getNumero(),
            entidade.getComplemento(),
            entidade.getBairro(),
            entidade.getCidade(),
            entidade.getUf(),
            entidade.getCep()
        );
    }

}
