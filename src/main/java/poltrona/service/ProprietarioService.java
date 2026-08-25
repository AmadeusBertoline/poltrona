package poltrona.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.proprietario.AtualizaProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioResponseDTO;
import poltrona.entity.Proprietario;
import poltrona.entity.Usuario;
import poltrona.exception.RegraNegocioException;
import poltrona.mapper.ProprietarioMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.IngressoRepository;
import poltrona.repository.ProprietarioRepository;
import poltrona.repository.UsuarioRepository;

@Service
public class ProprietarioService {

    private final ProprietarioRepository proprietarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProprietarioMapper proprietarioMapper;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final IngressoRepository ingressoRepository;
    private final CinemaRepository cinemaRepository;

    public ProprietarioService(ProprietarioRepository proprietarioRepository, PasswordEncoder passwordEncoder,
            ProprietarioMapper proprietarioMapper, UsuarioService usuarioService, UsuarioRepository usuarioRepository,
            IngressoRepository ingressoRepository, CinemaRepository cinemaRepository) {
        this.proprietarioRepository = proprietarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.proprietarioMapper = proprietarioMapper;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.ingressoRepository = ingressoRepository;
        this.cinemaRepository = cinemaRepository;
    }

    @Transactional
    public ProprietarioResponseDTO cadastrar(ProprietarioRequestDTO dto) {

        if (usuarioRepository.existsByEmail(dto.usuario().email())) {
            throw new RegraNegocioException("E-mail já cadastrado no sistema.");
        }

        if (usuarioRepository.existsByCpf(dto.usuario().cpf())) {
            throw new RegraNegocioException("CPF já cadastrado no sistema.");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.usuario().senha());

        Proprietario proprietario = proprietarioMapper.toEntity(dto, senhaCriptografada);

        Proprietario salvo = proprietarioRepository.save(proprietario);

        return proprietarioMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<ProprietarioResponseDTO> listarTodos(Pageable pageable) {

        return proprietarioRepository.findAll(pageable).map(proprietarioMapper::toDTO);

    }

    @Transactional(readOnly = true)
    public ProprietarioResponseDTO me() {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        return proprietarioMapper.toDTO(proprietario);
    }

    @Transactional
    public ProprietarioResponseDTO atualizar(AtualizaProprietarioRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        if (dto.email() != null && !dto.email().equalsIgnoreCase(proprietario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.email())) {
                throw new RegraNegocioException("O e-mail informado já está em uso por outro usuário.");
            }
        }

        proprietario.atualizar(dto.nome(), dto.email(), dto.dataNascimento());

        return proprietarioMapper.toDTO(proprietario);

    }

    @Transactional
    public void inativar() {
        Usuario usuario = usuarioService.usuarioLogado();

        if (!(usuario instanceof Proprietario proprietario)) {
            throw new RegraNegocioException("Apenas proprietários podem realizar esta operação.");
        }

        if (ingressoRepository.existsBySessaoSalaCinemaProprietarioIdAndSessaoDataHoraFimAfter(
                proprietario.getId(), LocalDateTime.now())) {
            throw new RegraNegocioException(
                    "Não é possível inativar a conta com sessões futuras que possuem ingressos vendidos.");
        }

        proprietario.inativar();
        cinemaRepository.inativarPorProprietario(proprietario.getId());
    }

}
