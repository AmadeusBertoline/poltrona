package poltrona.dto.cliente;

import java.time.LocalDate;

import poltrona.dto.usuario.UsuarioResponseDTO;

public record ClienteResponseDTO(

                UsuarioResponseDTO usuario,
                String telefone,
                LocalDate dataNascimento

) {
}
