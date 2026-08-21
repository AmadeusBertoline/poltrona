package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.poltrona.PoltronaRequestDTO;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.entity.Poltrona;
import poltrona.entity.Sala;

@Component
public class PoltronaMapper {

    public Poltrona toEntity(PoltronaRequestDTO dto, Sala sala) {
        if (dto == null) {
            return null;
        }

        return new Poltrona(
                dto.fileira(),
                dto.coluna(),
                sala);
    }

    public Poltrona toEntity(PoltronaRequestDTO dto) {
        return toEntity(dto, null);
    }

    public PoltronaResponseDTO toDTO(Poltrona entidade) {
        if (entidade == null) {
            return null;
        }

        Long idSala = entidade.getSala() != null ? entidade.getSala().getId() : null;

        return new PoltronaResponseDTO(
                entidade.getId(),
                entidade.getFileira(),
                entidade.getColuna(),
                entidade.getTipo(),
                idSala);
    }
}