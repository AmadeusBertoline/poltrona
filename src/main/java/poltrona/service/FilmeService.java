package poltrona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;
import poltrona.entity.Filme;
import poltrona.exception.RegraNegocioException;
import poltrona.repository.FilmeRepository;

@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;

    public FilmeService(FilmeRepository filmeRepository) {
        this.filmeRepository = filmeRepository;
    }

    public FilmeResponseDTO cadastrar(FilmeRequestDTO dto) {

        if (filmeRepository.existsByTitulo(dto.titulo())) {
            throw new RegraNegocioException("Esse filme já está cadastrado");
        }

        Filme filme = new Filme();
        filme.setTitulo(dto.titulo());
        filme.setSinopse(dto.sinopse());
        filme.setDiretor(dto.diretor());
        filme.setDistribuidora(dto.distribuidora());
        filme.setDataLancamento(dto.dataLancamento());
        filme.setDuracao(dto.duracao());
        filme.setImagePath(dto.imagePath());

        Filme cadastrado = filmeRepository.save(filme);

        return toDTO(cadastrado);

    }

    public List<FilmeResponseDTO> listarTodos(){

        return filmeRepository.findAll()
        .stream()
        .map(this::toDTO)
        .collect(Collectors.toList());

    }

    private FilmeResponseDTO toDTO(Filme filme) {

        return new FilmeResponseDTO(
                filme.getId(), filme.getTitulo(), filme.getSinopse(), filme.getGeneros(), filme.getDuracao(), filme.getDiretor(),
                filme.getDistribuidora(), filme.getDataLancamento(), filme.getImagePath(), filme.getStatus());

    }

}
