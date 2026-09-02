package poltrona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Sala;
import poltrona.entity.Usuario;
import poltrona.enums.cinema.StatusCinema;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.SalaMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.SalaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;
    private final PoltronaService poltronaService;
    private final CinemaRepository cinemaRepository;
    private final UsuarioService usuarioService;

    public SalaService(SalaRepository salaRepository, SalaMapper salaMapper, PoltronaService poltronaService,
            CinemaRepository cinemaRepository, UsuarioService usuarioService) {
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
        this.poltronaService = poltronaService;
        this.cinemaRepository = cinemaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public SalaResponseDTO cadastrar(SalaRequestDTO dto) {

        Usuario usuarioLogado = usuarioService.usuarioLogado();

        Cinema cinema = cinemaRepository.findById(dto.idCinema())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema selecionado não existe"));

        if (!cinema.getProprietario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Você só pode cadastrar salas nos seus próprios cinemas.");
        }

        if (cinema.getStatus() != StatusCinema.ATIVO) {
            throw new RegraNegocioException("Não é possível cadastrar salas para um cinema inativo.");
        }

        if (salaRepository.existsByCinemaIdAndNumero(dto.idCinema(), dto.numero())) {
            throw new RegraNegocioException("Esse cinema já possui uma sala com o número " + dto.numero());
        }

        Sala sala = salaMapper.toEntity(dto, cinema);

        Sala salaSalva = salaRepository.save(sala);

        poltronaService.cadastrar(dto.poltronas(), salaSalva);

        return salaMapper.toDTO(salaSalva);
    }

    public List<SalaResponseDTO> listarTodas() {

        return salaRepository.findAll()
                .stream()
                .map(salaMapper::toDTO)
                .collect(Collectors.toList());

    }

}
