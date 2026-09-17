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
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.AdminMapper;
import poltrona.repository.AdminRepository;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;

    public AdminService(AdminRepository adminRepository, AdminMapper adminMapper,
            PasswordEncoder passwordEncoder, UsuarioService usuarioService) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public AdminResponseDTO cadastrar(AdminRequestDTO dto) {

        if (adminRepository.existsByCpf(dto.usuario().cpf())) {
            throw new ResourceAlreadyExistsException("Já existe uma conta com este CPF");
        }

        if (adminRepository.existsByEmail(dto.usuario().email())) {
            throw new ResourceAlreadyExistsException("Já existe uma conta com este e-mail");
        }

        if (!dto.usuario().senha().equals(dto.usuario().confirmarSenha())) {
            throw new RegraNegocioException("A senha e a confirmação de senha não coincidem");
        }

        String senha = passwordEncoder.encode(dto.usuario().confirmarSenha());

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

        Admin admin = (Admin) usuarioService.usuarioLogado();

        return adminMapper.toDTO(admin);

    }

    @Transactional
    public AdminResponseDTO atualizar(AtualizaAdminRequestDTO dto) {

        Admin admin = (Admin) usuarioService.usuarioLogado();

        if (admin.getStatus() != StatusConta.ATIVA) {
            throw new RegraNegocioException("Uma conta bloqueada ou encerrada não pode atualizar dados");
        }

        if (dto.usuario().email() != null && !dto.usuario().email().isBlank()
                && !dto.usuario().email().equalsIgnoreCase(admin.getEmail())) {
            if (adminRepository.existsByEmailAndIdNot(dto.usuario().email(), admin.getId())) {
                throw new ResourceAlreadyExistsException("Já existe uma conta para este e-mail");
            }
        }

        admin.atualizar(dto.usuario().nome(), dto.usuario().email(), dto.usuario().dataNascimento());

        return adminMapper.toDTO(admin);
    }

    @Transactional
    public void encerrar() {

        Admin admin = (Admin) usuarioService.usuarioLogado();

        SecurityContextHolder.clearContext();

        admin.encerrar();

        adminRepository.save(admin);

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

    @Transactional(readOnly = true)
    public AdminResponseDTO buscarPorId(Long id) {

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin não encontrado de id " + id));

        return adminMapper.toDTO(admin);

    }

}