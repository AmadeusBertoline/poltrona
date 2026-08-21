package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.entity.Ingresso;
import poltrona.entity.Poltrona;
import poltrona.entity.Sessao;

@Component
public class IngressoMapper {

    public Ingresso toEntity(IngressoRequestDTO dto, Sessao sessao, Poltrona poltrona) {

        return new Ingresso(dto.tipo(), sessao, poltrona);

    }

    public IngressoResponseDTO toDTO(Ingresso ingresso) {

        return new IngressoResponseDTO(
                ingresso.getId(),
                ingresso.getPreco(),
                ingresso.getTipo(),
                ingresso.getSessao().getFilme().getTitulo(),
                ingresso.getSessao().getSala().getNumero(),
                ingresso.getSessao().getDataHoraInicio(),
                ingresso.getPoltrona().getFileira(),
                ingresso.getPoltrona().getColuna(),
                ingresso.getPoltrona().getTipo());

    }

}
