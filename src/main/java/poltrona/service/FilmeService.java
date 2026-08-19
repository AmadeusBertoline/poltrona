package poltrona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;
import poltrona.entity.Filme;
import poltrona.exception.RegraNegocioException;
import poltrona.mapper.FilmeMapper;
import poltrona.repository.FilmeRepository;

@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;
    private final FilmeMapper filmeMapper;

    public FilmeService(FilmeRepository filmeRepository, FilmeMapper filmeMapper) {
        this.filmeRepository = filmeRepository;
        this.filmeMapper = filmeMapper;
    }

    @Transactional
    public FilmeResponseDTO cadastrar(FilmeRequestDTO dto) {

        if (filmeRepository.existsByTituloIgnoreCase(dto.titulo())) {
            throw new RegraNegocioException("Esse filme já está cadastrado");
        }

        Filme filme = filmeMapper.toEntity(dto);
        Filme cadastrado = filmeRepository.save(filme);

        return filmeMapper.toDTO(cadastrado);

    }

    public Page<FilmeResponseDTO> listarTodos(Pageable pageable) {

        return filmeRepository.findAll(pageable).map(filmeMapper::toDTO);

    }

    @Transactional(readOnly = true)
    public FilmeResponseDTO buscarPorId(Long id) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Filme não encontrado com o ID: " + id));
        return filmeMapper.toDTO(filme);
    }

    @Transactional
    public FilmeResponseDTO atualizar(Long id, FilmeRequestDTO dto) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Filme não encontrado com o ID: " + id));

        filme.atualizarDados(dto);

        return filmeMapper.toDTO(filme);
    }

    @Transactional
    public void deletar(Long id) {
        if (!filmeRepository.existsById(id)) {
            throw new EntityNotFoundException("Filme não encontrado com o ID: " + id);
        }
        filmeRepository.deleteById(id);
    }

}
