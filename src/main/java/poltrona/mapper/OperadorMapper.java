package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.operador.OperadorRequestDTO;
import poltrona.dto.operador.OperadorResponseDTO;
import poltrona.entity.Operador;

@Component
public class OperadorMapper {

    private final UsuarioMapper usuarioMapper;

    public OperadorMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public Operador toEntity(OperadorRequestDTO dto) {

        if (dto == null) {
            return null;
        }

        Operador operador = new Operador();
        operador.setNome(dto.usuario().nome());
        operador.setEmail(dto.usuario().email());
        operador.setCpf(dto.usuario().cpf());
        operador.setMatricula(dto.matricula());
        operador.setCargo(dto.departamento());
        operador.setDepartamento(dto.departamento());
        operador.setDataAdmissao(dto.dataAdmissao());

        return operador;

    }

    public OperadorResponseDTO toDTO(Operador operador) {

        if (operador == null) {
            return null;
        }

        return new OperadorResponseDTO(
                usuarioMapper.toDTO(operador), operador.getMatricula(), operador.getCargo(), operador.getDepartamento(),
                operador.getDataAdmissao());

    }

}
