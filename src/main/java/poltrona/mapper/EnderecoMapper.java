package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.endereco.EnderecoRequestDTO;
import poltrona.dto.endereco.EnderecoResponseDTO;
import poltrona.entity.Endereco;

@Component
public class EnderecoMapper {

    public Endereco toEntity(EnderecoRequestDTO dto) {
        if (dto == null)
            return null;

        return new Endereco(
                dto.logradouro(),
                dto.numero(),
                dto.complemento(),
                dto.bairro(),
                dto.cidade(),
                dto.uf(),
                dto.cep());
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
                entidade.getCep());
    }
}