package poltrona.mapper;

import org.springframework.stereotype.Component;

import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;
import poltrona.entity.Filme;

@Component
public class FilmeMapper {

    public Filme toEntity(FilmeRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Filme(
                dto.titulo(),
                dto.sinopse(),
                dto.generos(),
                dto.duracao(),
                dto.diretor(),
                dto.distribuidora(),
                dto.dataLancamento(),
                dto.imagePath());
    }

    public FilmeResponseDTO toDTO(Filme entidade) {
        if (entidade == null) {
            return null;
        }

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
                entidade.getStatus());
    }
}