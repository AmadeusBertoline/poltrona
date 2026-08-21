package poltrona.mapper;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import poltrona.dto.operador.OperadorRequestDTO;
import poltrona.dto.operador.OperadorResponseDTO;
import poltrona.entity.Operador;

@Component
@RequiredArgsConstructor
public class OperadorMapper {

    private final UsuarioMapper usuarioMapper;

    public Operador toEntity(OperadorRequestDTO dto, String senha) {
        if (dto == null) {
            return null;
        }

        return new Operador(
                dto.usuario().nome(),
                dto.usuario().email(),
                senha,
                dto.usuario().cpf(),
                dto.matricula(),
                dto.cargo(),
                dto.departamento(),
                dto.dataAdmissao());
    }

    public OperadorResponseDTO toDTO(Operador operador) {
        if (operador == null) {
            return null;
        }

        return new OperadorResponseDTO(
                usuarioMapper.toDTO(operador),
                operador.getMatricula(),
                operador.getCargo(),
                operador.getDepartamento(),
                operador.getDataAdmissao());
    }
}