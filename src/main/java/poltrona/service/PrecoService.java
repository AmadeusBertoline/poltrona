package poltrona.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.preco.AtualizaPrecoRequestDTO;
import poltrona.dto.preco.PrecoRequestDTO;
import poltrona.dto.preco.PrecoResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Preco;
import poltrona.entity.Proprietario;
import poltrona.enums.cinema.StatusCinema;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.PrecoMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.PrecoRepository;
import poltrona.repository.SessaoRepository;

@Service
public class PrecoService {

    private final PrecoRepository precoRepository;
    private final PrecoMapper precoMapper;
    private final CinemaRepository cinemaRepository;
    private final UsuarioService usuarioService;
    private final SessaoRepository sessaoRepository;

    public PrecoService(PrecoRepository precoRepository, PrecoMapper precoMapper, CinemaRepository cinemaRepository,
            UsuarioService usuarioService, SessaoRepository sessaoRepository) {
        this.precoRepository = precoRepository;
        this.precoMapper = precoMapper;
        this.cinemaRepository = cinemaRepository;
        this.usuarioService = usuarioService;
        this.sessaoRepository = sessaoRepository;
    }

    @Transactional
    public PrecoResponseDTO cadastrar(PrecoRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Cinema cinema = cinemaRepository.findByIdAndProprietarioId(dto.idCinema(), proprietario.getId())
                .orElseThrow(() -> new AccessDeniedException(
                        "Cinema não encontrado ou não pertence ao proprietário logado."));

        if (cinema.getStatus() != StatusCinema.ATIVO) {
            throw new RegraNegocioException("Não é possível cadastrar tabela de preços para um cinema inativo.");
        }

        String nomeSanitizado = dto.nome().trim();

        if (precoRepository.existsByNomeIgnoreCaseAndCinemaId(nomeSanitizado, dto.idCinema())) {
            throw new ResourceAlreadyExistsException("Já existe um preço cadastrado com este nome para este cinema.");
        }

        Preco preco = precoMapper.toEntity(dto, cinema);
        Preco salvo = precoRepository.save(preco);

        return precoMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<PrecoResponseDTO> listarTodos(Pageable pageable) {

        return precoRepository.findAll(pageable).map(precoMapper::toDTO);

    }

    @Transactional
    public PrecoResponseDTO atualizar(Long id, AtualizaPrecoRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Preco preco = precoRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(
                        () -> new AccessDeniedException("Preço não encontrado ou não pertence a nenhum cinema seu."));

        preco.atualizarPrecoBase(dto.precoBase());

        Preco salvo = precoRepository.save(preco);

        return precoMapper.toDTO(salvo);
    }

    @Transactional
    public void desativar(Long id) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Preco preco = precoRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(
                        () -> new AccessDeniedException("Preço não encontrado ou não pertence a nenhum cinema seu."));

        if (!preco.getAtivo()) {
            throw new RegraNegocioException("Este preço já se encontra inativo.");
        }

        boolean existeSessaoFutura = sessaoRepository.existsByPrecoIdAndDataHoraInicioAfterAndStatus(
                id, LocalDateTime.now(), true);

        if (existeSessaoFutura) {
            throw new RegraNegocioException("Você não pode desativar preços que serão usados em sessões futuras.");
        }

        preco.desativar();

    }

    @Transactional(readOnly = true)
    public Page<PrecoResponseDTO> buscarPorCinema(Long idCinema, Pageable pageable) {

        if (!cinemaRepository.existsById(idCinema)) {
            throw new ResourceNotFoundException("Cinema não encontrado com o id " + idCinema);
        }

        return precoRepository.findAllByCinemaId(idCinema, pageable)
                .map(precoMapper::toDTO);
    }

}
