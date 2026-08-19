package poltrona.mapper;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.entity.Admin;

@Component
@RequiredArgsConstructor
public class AdminMapper {

    private final UsuarioMapper usuarioMapper;

    public Admin toEntity(AdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Admin.builder()
                .nome(dto.usuario().nome())
                .email(dto.usuario().email())
                .cpf(dto.usuario().cpf())
                .build();
    }

    public AdminResponseDTO toDTO(Admin admin) {
        if (admin == null) {
            return null;
        }

        return new AdminResponseDTO(usuarioMapper.toDTO(admin));
    }
}