package poltrona.service;

import java.math.BigDecimal;

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

@Service
public class PrecoService {

    private final PrecoRepository precoRepository;
    private final PrecoMapper precoMapper;
    private final CinemaRepository cinemaRepository;
    private final UsuarioService usuarioService;

    public PrecoService(PrecoRepository precoRepository, PrecoMapper precoMapper, CinemaRepository cinemaRepository,
            UsuarioService usuarioService) {
        this.precoRepository = precoRepository;
        this.precoMapper = precoMapper;
        this.cinemaRepository = cinemaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public PrecoResponseDTO cadastrar(PrecoRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        if (!cinemaRepository.existsByIdAndProprietarioId(dto.idCinema(), proprietario.getId())) {
            throw new AccessDeniedException("Você só pode cadastrar preços para o seu cinema");
        }

        Cinema cinema = cinemaRepository.findById(dto.idCinema())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema selecionado não existe"));

        if (cinema.getStatus() != StatusCinema.ATIVO) {
            throw new RegraNegocioException("Não é possível cadastrar tabela de preços para um cinema inativo.");
        }

        if (precoRepository.existsByNomeIgnoreCaseAndCinemaId(dto.nome(), dto.idCinema())) {
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

        Preco preco = precoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preço não encontrado de id " + id));

        if (dto.precoBase() != null) {
            if (dto.precoBase().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraNegocioException("O preço deve ser maior que zero");
            }
            preco.atualizarPrecoBase(dto.precoBase());
        }

        if (dto.status() != null) {
            if (Boolean.TRUE.equals(dto.status())) {
                preco.ativar();
            } else {
                preco.desativar();
            }
        }

        Preco salvo = precoRepository.save(preco);

        return precoMapper.toDTO(salvo);

    }

    @Transactional
    public void deletar(Long id) {

        Preco preco = precoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preço não encontrado de id " + id));

        precoRepository.delete(preco);

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
