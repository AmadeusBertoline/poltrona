package poltrona.service;

import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.dto.admin.AtualizaAdminRequestDTO;
import poltrona.entity.Admin;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.mapper.AdminMapper;
import poltrona.repository.AdminRepository;
import poltrona.repository.UsuarioRepository;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;

    public AdminService(AdminRepository adminRepository,
            UsuarioRepository usuarioRepository,
            AdminMapper adminMapper,
            PasswordEncoder passwordEncoder,
            UsuarioService usuarioService) {
        this.adminRepository = adminRepository;
        this.usuarioRepository = usuarioRepository;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public AdminResponseDTO cadastrar(AdminRequestDTO dto) {

        if (usuarioRepository.existsByEmail(dto.usuario().email())) {
            throw new ResourceAlreadyExistsException("Email já cadastrado");
        }

        if (usuarioRepository.existsByCpf(dto.usuario().cpf())) {
            throw new ResourceAlreadyExistsException("CPF já cadastrado");
        }

        if (!Objects.equals(dto.usuario().senha(), dto.usuario().confirmarSenha())) {
            throw new RegraNegocioException("As senhas devem ser iguais");
        }

        String senha = passwordEncoder.encode(dto.usuario().senha());

        Admin admin = adminMapper.toEntity(dto, senha);
        Admin salvo = adminRepository.save(admin);

        return adminMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<AdminResponseDTO> listarTodos(Pageable pageable) {
        return adminRepository.findAll(pageable).map(adminMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public AdminResponseDTO me() {
        Admin usuario = (Admin) usuarioService.usuarioLogado();
        return adminMapper.toDTO(usuario);
    }

    @Transactional
    public AdminResponseDTO atualizar(AtualizaAdminRequestDTO dto) {

        Admin admin = (Admin) usuarioService.usuarioLogado();

        if (!admin.getEmail().equalsIgnoreCase(dto.usuario().email())
                && usuarioRepository.existsByEmail(dto.usuario().email())) {
            throw new ResourceAlreadyExistsException("Email já cadastrado por outro usuário");
        }

        admin.atualizar(dto.usuario().nome(), dto.usuario().email());

        return adminMapper.toDTO(admin);
    }

    @Transactional
    public void encerrarConta() {

        Admin admin = (Admin) usuarioService.usuarioLogado();

        if (!admin.getAtivo()) {
            throw new RegraNegocioException("Esta conta já foi encerrada");
        }

        if (adminRepository.eUltimoAdmin()) {
            throw new RegraNegocioException("Você é o último admin do sistema, portanto não pode encerrar sua conta");
        }

        admin.encerrarConta();

        adminRepository.save(admin);
    }
}