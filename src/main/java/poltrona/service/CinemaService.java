package poltrona.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.cinema.AtualizaCinemaRequestDTO;
import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Proprietario;
import poltrona.enums.cinema.StatusCinema;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.CinemaMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.IngressoRepository;

@Service
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;
    private final UsuarioService usuarioService;
    private final IngressoRepository ingressoRepository;

    public CinemaService(CinemaRepository cinemaRepository,
            CinemaMapper cinemaMapper, UsuarioService usuarioService, IngressoRepository ingressoRepository) {
        this.cinemaRepository = cinemaRepository;
        this.cinemaMapper = cinemaMapper;
        this.usuarioService = usuarioService;
        this.ingressoRepository = ingressoRepository;
    }

    @Transactional
    public CinemaResponseDTO cadastrar(CinemaRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        if (proprietario.getStatus().equals(StatusConta.BLOQUEADA)
                || proprietario.getStatus().equals(StatusConta.ENCERRADA)) {
            throw new RegraNegocioException("Proprietários encerrados ou bloqueados não podem realizar operações.");
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

    @Transactional
    public CinemaResponseDTO atualizar(Long id, AtualizaCinemaRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        if (proprietario.getStatus() != StatusConta.ATIVA) {
            throw new RegraNegocioException("Proprietários inativos ou bloqueados não podem alterar dados de cinemas.");
        }

        Cinema cinema = cinemaRepository.findByIdAndProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema não encontrado"));

        if (cinema.getStatus() != StatusCinema.ATIVO) {
            throw new RegraNegocioException("Não é possível alterar um cinema que está inativo.");
        }

        if (dto.nomeFantasia() != null && !dto.nomeFantasia().equalsIgnoreCase(cinema.getNomeFantasia())) {
            if (cinemaRepository.existsByNomeFantasiaAndProprietarioIdAndIdNot(dto.nomeFantasia(), proprietario.getId(),
                    id)) {
                throw new RegraNegocioException("Você já possui outro cinema cadastrado com este nome.");
            }
        }

        if (dto.endereco() != null) {
            cinema.getEndereco().atualizar(
                    dto.endereco().logradouro(),
                    dto.endereco().numero(),
                    dto.endereco().complemento(),
                    dto.endereco().bairro(),
                    dto.endereco().cidade(),
                    dto.endereco().uf(),
                    dto.endereco().cep());
        }

        cinema.atualizar(dto.nomeFantasia(), dto.telefone());

        return cinemaMapper.toDTO(cinema);
    }

    @Transactional
    public void deletar(Long id) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema não encontrado com ID: " + id));

        if (!cinema.getProprietario().getId().equals(proprietario.getId())) {
            throw new AccessDeniedException("Você não tem permissão para excluir este cinema.");
        }

        if (ingressoRepository.existsBySessaoSalaCinemaIdAndSessaoDataHoraFimAfter(cinema.getId(),
                LocalDateTime.now())) {
            throw new RegraNegocioException(
                    "Não é possível encerrar um cinema com sessões futuras que possuem ingressos vendidos.");
        }

        cinema.encerrar();

        cinemaRepository.save(cinema);
    }

    @Transactional(readOnly = true)
    public CinemaResponseDTO buscarPorId(Long id) {

        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema não encontrado de id " + id));

        return cinemaMapper.toDTO(cinema);

    }

}
