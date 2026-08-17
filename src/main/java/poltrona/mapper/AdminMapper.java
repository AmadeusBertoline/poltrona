package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.entity.Admin;

@Component
public class AdminMapper {

    private final UsuarioMapper usuarioMapper;

    public AdminMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public Admin toEntity(AdminRequestDTO dto) {

        if (dto == null) {
            return null;
        }

        Admin admin = new Admin();
        admin.setNome(dto.usuario().nome());
        admin.setEmail(dto.usuario().email());
        admin.setCpf(dto.usuario().cpf());

        return admin;

    }

    public AdminResponseDTO toDTO(Admin admin) {

        return new AdminResponseDTO(usuarioMapper.toDTO(admin));

    }

}
