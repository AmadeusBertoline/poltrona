package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;
import poltrona.entity.Filme;

@Component
public class FilmeMapper {

    public Filme toEntity(FilmeRequestDTO dto) {
        if (dto == null) return null;

        Filme entidade = new Filme();
        entidade.setTitulo(dto.titulo());
        entidade.setSinopse(dto.sinopse());
        entidade.setGeneros(dto.generos());
        entidade.setDuracao(dto.duracao());
        entidade.setDiretor(dto.diretor());
        entidade.setDistribuidora(dto.distribuidora());
        entidade.setDataLancamento(dto.dataLancamento());
        entidade.setImagePath(dto.imagePath());

        return entidade;
    }

    public FilmeResponseDTO toDTO(Filme entidade) {
        if (entidade == null) return null;

        return new FilmeResponseDTO(
            entidade.getId(),
            entidade.getTitulo(),
            entidade.getSinopse(),
            entidade.getGeneros(),
            entidade.getDuracao(),
            entidade.getDiretor(),
            entidade.getDistribuidora(),
            entidade.getDataLancamento(),
            entidade.getImagePath(),
            entidade.getStatus()
        );
    }
}