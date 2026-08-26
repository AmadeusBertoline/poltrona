package poltrona.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.proprietario.AtualizaProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
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

        usuarioService.validarCredenciaisDisponiveis(dto.usuario().email(), dto.usuario().cpf());

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

        Usuario usuario = usuarioService.usuarioLogado();

        if (!(usuario instanceof Proprietario proprietario)) {
            throw new RegraNegocioException("Apenas proprietários podem realizar esta operação.");
        }

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
    public void encerrar() {
        
        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        if (ingressoRepository.existsBySessaoSalaCinemaProprietarioIdAndSessaoDataHoraFimAfter(
                proprietario.getId(), LocalDateTime.now())) {
            throw new RegraNegocioException(
                    "Não é possível inativar a conta com sessões futuras que possuem ingressos vendidos.");
        }

        proprietario.encerrar();

        SecurityContextHolder.clearContext();

        cinemaRepository.inativarPorProprietario(proprietario.getId());
    }

    @Transactional
    public void atualizarSenha(AtualizaSenhaRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        if (!passwordEncoder.matches(dto.senhaAtual(), proprietario.getSenha())) {
            throw new BadCredentialsException("Senha atual incorreta");
        }

        if (!dto.novaSenha().equals(dto.confirmarSenha())) {
            throw new RegraNegocioException("A senha nova deve ser igual a confirmação de senha");
        }

        String senha = passwordEncoder.encode(dto.confirmarSenha());

        proprietario.atualizarSenha(senha);

        proprietarioRepository.save(proprietario);

    }

}
