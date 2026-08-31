package poltrona.service;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.entity.Cliente;
import poltrona.entity.Ingresso;
import poltrona.entity.Poltrona;
import poltrona.entity.Sessao;
import poltrona.entity.Usuario;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.IngressoMapper;
import poltrona.repository.IngressoRepository;
import poltrona.repository.PoltronaRepository;
import poltrona.repository.SessaoRepository;

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final SessaoRepository sessaoRepository;
    private final PoltronaRepository poltronaRepository;
    private final IngressoMapper ingressoMapper;
    private final UsuarioService usuarioService;

    public IngressoService(IngressoRepository ingressoRepository, SessaoRepository sessaoRepository,
            PoltronaRepository poltronaRepository, IngressoMapper ingressoMapper, UsuarioService usuarioService) {
        this.ingressoRepository = ingressoRepository;
        this.sessaoRepository = sessaoRepository;
        this.poltronaRepository = poltronaRepository;
        this.ingressoMapper = ingressoMapper;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public IngressoResponseDTO cadastrar(IngressoRequestDTO dto) {

        Usuario usuario = usuarioService.usuarioLogado();

        if (usuario.getStatus() != StatusConta.ATIVA) {
            throw new RegraNegocioException("Usuário inativo não pode realizar compras.");
        }

        Sessao sessao = sessaoRepository.findById(dto.idSessao())
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada"));

        Poltrona poltrona = poltronaRepository.findById(dto.idPoltrona())
                .orElseThrow(() -> new ResourceNotFoundException("Poltrona não encontrada"));

        if (!poltrona.getAtiva()) {
            throw new RegraNegocioException("A poltrona selecionada está inativa: " + poltrona.getNumero());
        }

        if (!Objects.equals(poltrona.getSala().getId(), sessao.getSala().getId())) {
            throw new RegraNegocioException("A poltrona deve estar na mesma sala em que a sessão irá ocorrer.");
        }

        if (ingressoRepository.existsBySessaoIdAndPoltronaIdAndStatus(
                dto.idSessao(), dto.idPoltrona(), StatusIngresso.ATIVO)) {
            throw new RegraNegocioException("Esta poltrona já está ocupada nesta sessão.");
        }

        sessao.validarPermiteVenda(LocalDateTime.now());

        Ingresso ingresso = ingressoMapper.toEntity(dto, sessao, poltrona, usuario);
        Ingresso salvo = ingressoRepository.save(ingresso);

        return ingressoMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<IngressoResponseDTO> listarTodos(Pageable pageable) {

        return ingressoRepository.findAll(pageable).map(ingressoMapper::toDTO);

    }

    @Transactional
    public void cancelar(Long id) {

        Cliente cliente = (Cliente) usuarioService.usuarioLogado();

        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

        if (!ingressoRepository.existsByIdAndUsuarioId(id, cliente.getId())) {
            throw new AccessDeniedException("Você só pode cancelar seus ingressos");
        }

        if (ingresso.getSessao().getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException(
                    "Não é possível cancelar o ingresso de uma sessão que já iniciou ou ocorreu.");
        }

        ingresso.cancelar();

        ingressoRepository.save(ingresso);
    }

}