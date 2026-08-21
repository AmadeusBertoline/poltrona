package poltrona.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Sala;

@Component
@RequiredArgsConstructor
public class SalaMapper {

    private final PoltronaMapper poltronaMapper;

    public Sala toEntity(SalaRequestDTO dto, Cinema cinema) {
        if (dto == null) {
            return null;
        }

        return new Sala(
                dto.numero(),
                dto.fileiras(),
                dto.poltronasPorFileira(),
                cinema);
    }

    public Sala toEntity(SalaRequestDTO dto) {
        return toEntity(dto, null);
    }

    public SalaResponseDTO toDTO(Sala entidade) {
        if (entidade == null) {
            return null;
        }

        List<PoltronaResponseDTO> poltronasDTO = entidade.getPoltronas() != null
                ? entidade.getPoltronas().stream().map(poltronaMapper::toDTO).toList()
                : Collections.emptyList();

        return new SalaResponseDTO(
                entidade.getId(),
                entidade.getNumero(),
                entidade.getFileiras(),
                entidade.getPoltronasPorFileira(),
                poltronasDTO);
    }
}