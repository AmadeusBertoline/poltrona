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

    public Operador toEntity(OperadorRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Operador.builder()
                .nome(dto.usuario().nome())
                .email(dto.usuario().email())
                .cpf(dto.usuario().cpf())
                .matricula(dto.matricula())
                .cargo(dto.cargo())
                .departamento(dto.departamento())
                .dataAdmissao(dto.dataAdmissao())
                .build();
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
                operador.getDataAdmissao()
        );
    }
}