package poltrona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.entity.Sala;
import poltrona.mapper.SalaMapper;
import poltrona.repository.SalaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;
    private final PoltronaService poltronaService;

    public SalaService(SalaRepository salaRepository, SalaMapper salaMapper, PoltronaService poltronaService) {
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
        this.poltronaService = poltronaService;
    }

    public SalaResponseDTO cadastrar(SalaRequestDTO dto) {

        Sala sala = salaMapper.toEntity(dto);
        Sala salaSalva = salaRepository.save(sala);

        poltronaService.cadastrar(dto.fileiras(), dto.poltronasPorFileira(), salaSalva);

        return salaMapper.toDTO(salaSalva);

    }

    public List<SalaResponseDTO> listarTodas() {

        return salaRepository.findAll()
                .stream()
                .map(salaMapper::toDTO)
                .collect(Collectors.toList());

    }

}
