package poltrona.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

import poltrona.dto.poltrona.PoltronaRequestDTO;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.entity.Poltrona;
import poltrona.entity.Sala;

@Component
public class PoltronaMapper {

    public Poltrona toEntity(PoltronaRequestDTO dto, Sala sala) {

        if (dto == null || dto.fileiras() == null || dto.fileiras().isEmpty()) {
            return null;
        }

        Map.Entry<String, Integer> entry = dto.fileiras().entrySet().iterator().next();

        char letra = entry.getKey().charAt(0);
        Integer numero = entry.getValue();

        return new Poltrona(
                letra,
                numero,
                sala);
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