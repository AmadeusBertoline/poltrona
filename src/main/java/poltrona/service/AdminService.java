package poltrona.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.entity.Admin;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.mapper.AdminMapper;
import poltrona.repository.AdminRepository;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, AdminMapper adminMapper, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminResponseDTO cadastrar(AdminRequestDTO dto) {

        if (adminRepository.existsByEmail(dto.usuario().email())) {
            throw new ResourceAlreadyExistsException("Email já cadastrado");
        }

        if (adminRepository.existsByCpf(dto.usuario().cpf())) {
            throw new ResourceAlreadyExistsException("CPF já cadastrado");
        }

        if (!dto.usuario().senha().equals(dto.usuario().confirmarSenha())) {
            throw new RegraNegocioException("As senhas devem ser iguais");
        }

        Admin admin = adminMapper.toEntity(dto);
        admin.setSenha(passwordEncoder.encode(dto.usuario().senha()));
        Admin salvo = adminRepository.save(admin);

        return adminMapper.toDTO(salvo);

    }

    public List<AdminResponseDTO> listarTodos() {

        return adminRepository.findAll()
                .stream()
                .map(adminMapper::toDTO)
                .collect(Collectors.toList());

    }

}
