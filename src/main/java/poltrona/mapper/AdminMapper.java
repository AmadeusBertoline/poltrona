package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Admin;

@Component
public class AdminMapper {

    public Admin toEntity(AdminRequestDTO dto, String senha) {
        if (dto == null || dto.usuario() == null) {
            return null;
        }

        return new Admin(
                dto.usuario().nome(),
                dto.usuario().email(),
                senha,
                dto.usuario().cpf(),
                dto.usuario().dataNascimento());
    }

    public AdminResponseDTO toDTO(Admin admin) {
        if (admin == null) {
            return null;
        }

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                admin.getId(),
                admin.getNome(),
                admin.getEmail(),
                admin.getCpf(),
                admin.getDataNascimento(),
                admin.getStatus(),
                admin.getDataCriacao());

        return new AdminResponseDTO(usuarioDTO);
    }
}