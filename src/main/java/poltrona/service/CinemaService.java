package poltrona.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.entity.Cinema;
import poltrona.mapper.CinemaMapper;
import poltrona.repository.CinemaRepository;

@Service
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;

    public CinemaService(CinemaRepository cinemaRepository,
            CinemaMapper cinemaMapper) {
        this.cinemaRepository = cinemaRepository;
        this.cinemaMapper = cinemaMapper;
    }

    public CinemaResponseDTO cadastrar(CinemaRequestDTO dto) {

        Cinema cinema = cinemaMapper.toEntity(dto);
        Cinema salvo = cinemaRepository.save(cinema);

        return cinemaMapper.toDTO(salvo);

    }

    public List<CinemaResponseDTO> listarTodos() {
        return cinemaRepository.findAll()
                .stream()
                .map(cinemaMapper::toDTO)
                .collect(Collectors.toList());
    }

}
