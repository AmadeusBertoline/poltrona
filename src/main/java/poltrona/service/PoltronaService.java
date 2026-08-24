package poltrona.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.dto.poltrona.TipoPoltronaRequestDTO;
import poltrona.entity.Poltrona;
import poltrona.entity.Sala;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.PoltronaMapper;
import poltrona.repository.PoltronaRepository;

@Service
public class PoltronaService {

    private final PoltronaRepository poltronaRepository;
    private final PoltronaMapper poltronaMapper;

    public PoltronaService(PoltronaRepository poltronaRepository, PoltronaMapper poltronaMapper) {
        this.poltronaRepository = poltronaRepository;
        this.poltronaMapper = poltronaMapper;
    }

    public List<PoltronaResponseDTO> cadastrar(Integer fileiras, Integer poltronasPorFileira, Sala sala) {

        List<Poltrona> poltronas = new ArrayList<>();

        for (int i = 0; i <= fileiras; i++) {

            for (int j = 1; j <= poltronasPorFileira; j++) {

                char letra = (char) ('A' + i);

                Poltrona poltrona = new Poltrona(letra, j, sala);

                Poltrona poltronaSalva = poltronaRepository.save(poltrona);

                poltronas.add(poltronaSalva);

            }

        }

        return poltronas.stream().map(poltronaMapper::toDTO).collect(Collectors.toList());

    }

    public PoltronaResponseDTO buscarPorId(Long id) {

        Poltrona poltrona = poltronaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poltrona não encontrada de id " + id));

        return poltronaMapper.toDTO(poltrona);

    }

    public PoltronaResponseDTO atualizarTipo(Long id, TipoPoltronaRequestDTO tipo) {

        Poltrona poltrona = poltronaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poltrona não encontrada"));

        poltrona.atualizarTipo(tipo.tipo());

        Poltrona salva = poltronaRepository.save(poltrona);

        return poltronaMapper.toDTO(salva);

    }

}
