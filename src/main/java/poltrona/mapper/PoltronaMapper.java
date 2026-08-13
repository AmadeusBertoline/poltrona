package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.poltrona.PoltronaRequestDTO;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.entity.Poltrona;

@Component
public class PoltronaMapper {

    public Poltrona toEntity(PoltronaRequestDTO dto) {
        if (dto == null)
            return null;

        Poltrona entidade = new Poltrona();
        entidade.setFileira(dto.fileira());
        entidade.setColuna(dto.coluna());
        return entidade;
    }

    public PoltronaResponseDTO toDTO(Poltrona entidade) {
        if (entidade == null)
            return null;

        Long idSala = entidade.getSala() != null ? entidade.getSala().getId() : null;

        return new PoltronaResponseDTO(
                entidade.getId(),
                entidade.getFileira(),
                entidade.getColuna(),
                entidade.getTipo(),
                idSala);
    }
}