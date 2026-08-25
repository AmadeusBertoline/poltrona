package poltrona.dto.operador;

import java.time.LocalDate;
import poltrona.dto.usuario.UsuarioRequestDTO;
import poltrona.enums.TipoUsuario;

public record OperadorRequestDTO(

        UsuarioRequestDTO usuario,
        String matricula,
        TipoUsuario cargo,
        String telefone,
        String departamento,
        TipoUsuario tipo,
        LocalDate dataAdmissao

) {
}
