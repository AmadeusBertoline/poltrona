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

        return Filme.builder()
                .titulo(dto.titulo())
                .sinopse(dto.sinopse())
                .generos(dto.generos())
                .duracao(dto.duracao())
                .diretor(dto.diretor())
                .distribuidora(dto.distribuidora())
                .dataLancamento(dto.dataLancamento())
                .imagePath(dto.imagePath())
                .build();
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