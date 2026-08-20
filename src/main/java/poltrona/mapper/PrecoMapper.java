package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.preco.PrecoRequestDTO;
import poltrona.dto.preco.PrecoResponseDTO;
import poltrona.entity.Preco;

@Component
public class PrecoMapper {

    public Preco toEntity(PrecoRequestDTO dto) {

        return Preco.builder()
                .nome(dto.nome())
                .precoBase(dto.precoBase())
                .build();

    }

    public PrecoResponseDTO toDTO(Preco preco) {

        return new PrecoResponseDTO(

                preco.getId(),
                preco.getNome(),
                preco.getPrecoBase(),
                preco.getAtivo()

        );

    }

}
