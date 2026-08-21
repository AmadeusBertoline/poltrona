package poltrona.mapper;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import poltrona.dto.cliente.ClienteRequestDTO;
import poltrona.dto.cliente.ClienteResponseDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Cliente;

@Component
@RequiredArgsConstructor
public class ClienteMapper {

    private final UsuarioMapper usuarioMapper;

    public Cliente toEntity(ClienteRequestDTO dto, String senha) {
        if (dto == null) {
            return null;
        }

        return new Cliente(
                dto.usuario().nome(),
                dto.usuario().email(),
                dto.usuario().cpf(),
                senha,
                dto.telefone());
    }

    public ClienteResponseDTO toDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        UsuarioResponseDTO usuario = usuarioMapper.toDTO(cliente);

        return new ClienteResponseDTO(usuario, cliente.getTelefone());
    }
}