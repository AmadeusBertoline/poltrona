package poltrona.dto.operador;

import java.time.LocalDate;
import poltrona.dto.usuario.UsuarioRequestDTO;

public record OperadorRequestDTO (

    UsuarioRequestDTO usuario,
    String matricula,
    String cargo,
    String departamento,
    LocalDate dataAdmissao

){}
