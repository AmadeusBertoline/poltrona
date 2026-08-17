package poltrona.dto.operador;

import java.time.LocalDate;

import poltrona.dto.usuario.UsuarioResponseDTO;

public record OperadorResponseDTO(

        UsuarioResponseDTO usuario,
        String matricula,
        String cargo,
        String departamento,
        LocalDate dataAdmissao

) {
}
