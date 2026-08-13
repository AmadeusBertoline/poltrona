package poltrona.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.entity.Sala;

@Component
public class SalaMapper {

    private final PoltronaMapper poltronaMapper;

    public SalaMapper(PoltronaMapper poltronaMapper) {
        this.poltronaMapper = poltronaMapper;
    }

    public Sala toEntity(SalaRequestDTO dto) {
        if (dto == null)
            return null;

        Sala entidade = new Sala();
        entidade.setNumero(dto.numero());
        entidade.setFileiras(dto.fileiras());
        entidade.setPoltronasPorFileira(dto.poltronasPorFileira());

        return entidade;
    }

    public SalaResponseDTO toDTO(Sala entidade) {
        if (entidade == null)
            return null;

        Long cinemaId = entidade.getCinema() != null ? entidade.getCinema().getId() : null;

        List<PoltronaResponseDTO> poltronasDTO = entidade.getPoltronas() != null
                ? entidade.getPoltronas().stream().map(poltronaMapper::toDTO).toList()
                : Collections.emptyList();

        return new SalaResponseDTO(
                entidade.getId(),
                entidade.getNumero(),
                entidade.getFileiras(),
                entidade.getPoltronasPorFileira(),
                poltronasDTO,
                cinemaId);
    }
}