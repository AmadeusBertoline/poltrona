package poltrona.dto.cliente;

import java.time.LocalDate;
import poltrona.dto.usuario.UsuarioRequestDTO;

public record ClienteRequestDTO(

    UsuarioRequestDTO usuario,
    String telefone,
    LocalDate dataNascimento

) {}
