package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Proprietario;
import poltrona.exception.RegraNegocioException;
import poltrona.mapper.CinemaMapper;
import poltrona.repository.CinemaRepository;

@Service
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;
    private final UsuarioService usuarioService;

    public CinemaService(CinemaRepository cinemaRepository,
            CinemaMapper cinemaMapper, UsuarioService usuarioService) {
        this.cinemaRepository = cinemaRepository;
        this.cinemaMapper = cinemaMapper;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public CinemaResponseDTO cadastrar(CinemaRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        if (!proprietario.getAtivo()) {
            throw new RegraNegocioException("Proprietários inativos não podem cadastrar novos cinemas.");
        }

        if (dto.cnpj() != null && cinemaRepository.existsByCnpj(dto.cnpj())) {
            throw new RegraNegocioException("Já existe um cinema cadastrado com este CNPJ.");
        }

        if (cinemaRepository.existsByNomeFantasiaAndProprietarioId(dto.nomeFantasia(), proprietario.getId())) {
            throw new RegraNegocioException("Você já possui um cinema cadastrado com este nome.");
        }

        Cinema cinema = cinemaMapper.toEntity(dto, proprietario);

        Cinema salvo = cinemaRepository.save(cinema);

        return cinemaMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<CinemaResponseDTO> listarTodos(Pageable pageable) {
        return cinemaRepository.findAll(pageable).map(cinemaMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CinemaResponseDTO> me(Pageable pageable) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        return cinemaRepository.findAllByProprietario(pageable, proprietario).map(cinemaMapper::toDTO);

    }

}
