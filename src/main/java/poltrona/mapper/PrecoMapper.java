package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.preco.PrecoRequestDTO;
import poltrona.dto.preco.PrecoResponseDTO;
import poltrona.entity.Preco;

@Component
public class PrecoMapper {

    public Preco toEntity(PrecoRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Preco(
                dto.nome(),
                dto.precoBase()
        );
    }

    public PrecoResponseDTO toDTO(Preco preco) {
        if (preco == null) {
            return null;
        }

        return new PrecoResponseDTO(
                preco.getId(),
                preco.getNome(),
                preco.getPrecoBase(),
                preco.getAtivo());
    }
}