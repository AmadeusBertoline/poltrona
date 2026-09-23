package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.gerente.AtualizaGerenteRequestDTO;
import poltrona.dto.gerente.GerenteRequestDTO;
import poltrona.dto.gerente.GerenteResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.entity.Gerente;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.GerenteMapper;
import poltrona.repository.GerenteRepository;

@Service
public class GerenteService {

    private final GerenteRepository gerenteRepository;
    private final UsuarioService usuarioService;
    private final GerenteMapper gerenteMapper;
    private final PasswordEncoder passwordEncoder;

    public GerenteService(GerenteRepository gerenteRepository, UsuarioService usuarioService,
            GerenteMapper gerenteMapper, PasswordEncoder passwordEncoder) {
        this.gerenteRepository = gerenteRepository;
        this.usuarioService = usuarioService;
        this.gerenteMapper = gerenteMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public GerenteResponseDTO cadastrar(GerenteRequestDTO dto) {

        if (gerenteRepository.existsByCpf(dto.usuario().cpf())) {
            throw new ResourceAlreadyExistsException("Já existe uma conta com este CPF");
        }

        if (gerenteRepository.existsByEmail(dto.usuario().email())) {
            throw new ResourceAlreadyExistsException("Já existe uma conta com este e-mail");
        }

        if (!dto.usuario().senha().equals(dto.usuario().confirmarSenha())) {
            throw new RegraNegocioException("A senha e a confirmação de senha não coincidem");
        }

        String senha = passwordEncoder.encode(dto.usuario().senha());

        Gerente gerente = gerenteMapper.toEntity(dto, senha);

        Gerente salvo = gerenteRepository.save(gerente);

        return gerenteMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<GerenteResponseDTO> listarTodos(Pageable pageable) {
        return gerenteRepository.findAll(pageable).map(gerenteMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public GerenteResponseDTO me() {
        Gerente gerente = (Gerente) usuarioService.usuarioLogado();
        return gerenteMapper.toDTO(gerente);
    }

    @Transactional
    public GerenteResponseDTO atualizar(AtualizaGerenteRequestDTO dto) {

        Gerente gerente = (Gerente) usuarioService.usuarioLogado();

        if (gerente.getStatus() != StatusConta.ATIVA) {
            throw new RegraNegocioException("Uma conta bloqueada ou encerrada não pode atualizar dados");
        }

        if (dto.email() != null && !dto.email().isBlank() && !dto.email().equalsIgnoreCase(gerente.getEmail())) {
            if (gerenteRepository.existsByEmailAndIdNot(dto.email(), gerente.getId())) {
                throw new ResourceAlreadyExistsException("Já existe uma conta para este e-mail");
            }
        }

        gerente.atualizar(dto.nome(), dto.email(), dto.dataNascimento());

        return gerenteMapper.toDTO(gerente);
    }

    @Transactional
    public void encerrar() {

        Gerente gerente = (Gerente) usuarioService.usuarioLogado();

        SecurityContextHolder.clearContext();

        gerente.encerrar();

        gerenteRepository.save(gerente);
    }

    @Transactional
    public void atualizarSenha(AtualizaSenhaRequestDTO dto) {

        Gerente gerente = (Gerente) usuarioService.usuarioLogado();

        if (!passwordEncoder.matches(dto.senhaAtual(), gerente.getSenha())) {
            throw new BadCredentialsException("Senha atual incorreta");
        }

        if (!dto.novaSenha().equals(dto.confirmarSenha())) {
            throw new RegraNegocioException("A senha nova deve ser igual a confirmação de senha");
        }

        String senha = passwordEncoder.encode(dto.confirmarSenha());

        gerente.atualizarSenha(senha);

        gerenteRepository.save(gerente);
    }

    @Transactional(readOnly = true)
    public GerenteResponseDTO buscarPorId(Long id) {

        Gerente gerente = gerenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gerente não encontrado com id " + id));

        return gerenteMapper.toDTO(gerente);
    }
}