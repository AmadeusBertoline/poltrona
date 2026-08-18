package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.cliente.ClienteRequestDTO;
import poltrona.dto.cliente.ClienteResponseDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Cliente;

@Component
public class ClienteMapper {

    private final UsuarioMapper usuarioMapper;

    public ClienteMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public Cliente toEntity(ClienteRequestDTO dto) {

        if (dto == null) {
            return null;
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.usuario().nome());
        cliente.setEmail(dto.usuario().email());
        cliente.setCpf(dto.usuario().cpf());
        cliente.setTelefone(dto.telefone());

        return cliente;

    }

    public ClienteResponseDTO toDTO(Cliente cliente) {

        if (cliente == null) {
            return null;
        }

        UsuarioResponseDTO usuario = usuarioMapper.toDTO(cliente);

        return new ClienteResponseDTO(
                usuario, cliente.getTelefone());

    }

}
