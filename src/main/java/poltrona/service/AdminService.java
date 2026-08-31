package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.dto.admin.AtualizaAdminRequestDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.entity.Admin;
import poltrona.entity.Usuario;
import poltrona.exception.RegraNegocioException;
import poltrona.mapper.AdminMapper;
import poltrona.repository.AdminRepository;
import poltrona.repository.UsuarioRepository;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
            AdminMapper adminMapper, UsuarioService usuarioService,
            UsuarioRepository usuarioRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminMapper = adminMapper;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public AdminResponseDTO cadastrar(AdminRequestDTO dto) {

        usuarioService.validarCredenciaisDisponiveis(dto.usuario().email(), dto.usuario().cpf());

        String senhaCriptografada = passwordEncoder.encode(dto.usuario().senha());

        Admin admin = adminMapper.toEntity(dto, senhaCriptografada);

        Admin salvo = adminRepository.save(admin);

        return adminMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<AdminResponseDTO> listarTodos(Pageable pageable) {

        return adminRepository.findAll(pageable).map(adminMapper::toDTO);

    }

    @Transactional(readOnly = true)
    public AdminResponseDTO me() {

        Usuario usuario = usuarioService.usuarioLogado();

        if (!(usuario instanceof Admin admin)) {
            throw new RegraNegocioException("Apenas administradores podem realizar esta operação.");
        }

        return adminMapper.toDTO(admin);
    }

    @Transactional
    public AdminResponseDTO atualizar(AtualizaAdminRequestDTO dto) {

        Admin admin = (Admin) usuarioService.usuarioLogado();

        if (dto.usuario().email() != null && !dto.usuario().email().equalsIgnoreCase(admin.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.usuario().email())) {
                throw new RegraNegocioException("O e-mail informado já está em uso por outro usuário.");
            }
        }

        admin.atualizar(dto.usuario().nome(), dto.usuario().email(), dto.usuario().dataNascimento());

        return adminMapper.toDTO(admin);

    }

    @Transactional
    public void encerrar() {

        Admin admin = (Admin) usuarioService.usuarioLogado();

        admin.encerrar();

        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void atualizarSenha(AtualizaSenhaRequestDTO dto) {

        Admin admin = (Admin) usuarioService.usuarioLogado();

        if (!passwordEncoder.matches(dto.senhaAtual(), admin.getSenha())) {
            throw new BadCredentialsException("Senha atual incorreta");
        }

        if (!dto.novaSenha().equals(dto.confirmarSenha())) {
            throw new RegraNegocioException("A senha nova deve ser igual a confirmação de senha");
        }

        String senha = passwordEncoder.encode(dto.confirmarSenha());

        admin.atualizarSenha(senha);

        adminRepository.save(admin);

    }

}