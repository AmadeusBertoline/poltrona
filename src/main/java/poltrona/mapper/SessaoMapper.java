package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.sessao.SessaoRequestDTO;
import poltrona.dto.sessao.SessaoResponseDTO;
import poltrona.entity.Filme;
import poltrona.entity.Preco;
import poltrona.entity.Sala;
import poltrona.entity.Sessao;

@Component
public class SessaoMapper {

    public Sessao toEntity(SessaoRequestDTO dto, Filme filme, Sala sala, Preco preco) {
        if (dto == null) {
            return null;
        }

        return Sessao.builder()
                .filme(filme)
                .sala(sala)
                .dataHoraInicio(dto.dataHoraInicio())
                .preco(preco)
                .build();
    }

    public SessaoResponseDTO toDTO(Sessao entidade) {
        if (entidade == null) {
            return null;
        }

        return new SessaoResponseDTO(
                entidade.getId(),
                entidade.getDataHoraInicio(),
                entidade.getDataHoraFim(),
                entidade.getFilme().getTitulo(),
                entidade.getSala().getNumero(),
                entidade.getPreco().getPrecoBase());
    }
}