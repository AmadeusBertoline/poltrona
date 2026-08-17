package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Usuario;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toDTO(Usuario usuario) {

        if (usuario == null) {
            return null;
        }

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getAtivo(),
                usuario.getDataCriacao());
    }
}