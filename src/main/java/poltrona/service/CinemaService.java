package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public CinemaResponseDTO cadastrar(CinemaRequestDTO dto) {

        Cinema cinema = cinemaMapper.toEntity(dto);
        Cinema salvo = cinemaRepository.save(cinema);

        return cinemaMapper.toDTO(salvo);

    }

    @Transactional(readOnly = true)
    public Page<CinemaResponseDTO> listarTodos(Pageable pageable) {
        return cinemaRepository.findAll(pageable).map(cinemaMapper::toDTO);
    }

    // @Transactional(readOnly = true)
    // public CinemaResponseDTO me(){

    // }

}
