package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.cliente.ClienteRequestDTO;
import poltrona.dto.cliente.ClienteResponseDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Cliente;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO dto, String senha) {
        if (dto == null || dto.usuario() == null) {
            return null;
        }

        return new Cliente(
                dto.usuario().nome(),
                dto.usuario().email(),
                senha,
                dto.usuario().cpf(),
                dto.usuario().dataNascimento());
    }

    public ClienteResponseDTO toDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf(),
                cliente.getDataNascimento(),
                cliente.getStatus(),
                cliente.getDataCriacao());

        return new ClienteResponseDTO(usuarioDTO);
    }

}
