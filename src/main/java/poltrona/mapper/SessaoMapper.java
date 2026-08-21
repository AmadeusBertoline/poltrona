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

        return new Sessao(
                dto.dataHoraInicio(),
                filme,
                sala,
                preco,
                null
        );
    }

    public SessaoResponseDTO toDTO(Sessao entidade) {
        if (entidade == null) {
            return null;
        }

        return new SessaoResponseDTO(
                entidade.getId(),
                entidade.getDataHoraInicio(),
                entidade.getDataHoraFim(),
                entidade.getFilme() != null ? entidade.getFilme().getTitulo() : null,
                entidade.getSala() != null ? entidade.getSala().getNumero() : null,
                entidade.getPreco() != null ? entidade.getPreco().getPrecoBase() : null);
    }
}