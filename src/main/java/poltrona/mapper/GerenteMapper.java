package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.gerente.GerenteRequestDTO;
import poltrona.dto.gerente.GerenteResponseDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Gerente;
import poltrona.entity.Cinema;;

@Component
public class GerenteMapper {

    public Gerente toEntity(GerenteRequestDTO dto, String senha, Cinema cinema) {

        if (dto == null) {
            return null;
        }

        return new Gerente(dto.usuario().nome(), dto.usuario().email(), senha, dto.usuario().cpf(),
                dto.usuario().dataNascimento(), cinema);

    }

    public GerenteResponseDTO toDTO(Gerente gerente) {
        if (gerente == null) {
            return null;
        }

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                gerente.getId(),
                gerente.getNome(),
                gerente.getEmail(),
                gerente.getCpf(),
                gerente.getDataNascimento(),
                gerente.getStatus(),
                gerente.getDataCriacao());

        String cinema = gerente.getCinema().getNomeFantasia();

        return new GerenteResponseDTO(usuarioDTO, cinema);
    }

}
