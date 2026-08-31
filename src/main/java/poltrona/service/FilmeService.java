package poltrona.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;
import poltrona.entity.Filme;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.FilmeMapper;
import poltrona.repository.FilmeRepository;
import poltrona.repository.SessaoRepository;

@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;
    private final FilmeMapper filmeMapper;
    private final SessaoRepository sessaoRepository;

    public FilmeService(FilmeRepository filmeRepository, FilmeMapper filmeMapper,
            SessaoRepository sessaoRepository) {
        this.filmeRepository = filmeRepository;
        this.filmeMapper = filmeMapper;
        this.sessaoRepository = sessaoRepository;
    }

    @Transactional
    public FilmeResponseDTO cadastrar(FilmeRequestDTO dto) {

        if (filmeRepository.existsByTituloIgnoreCaseAndDataLancamento(dto.titulo(), dto.dataLancamento())) {
            throw new RegraNegocioException("Filme já cadastrado no catálogo com este título e ano.");
        }

        Filme filme = filmeMapper.toEntity(dto);
        Filme cadastrado = filmeRepository.save(filme);

        return filmeMapper.toDTO(cadastrado);
    }

    public Page<FilmeResponseDTO> listarTodos(Pageable pageable) {

        return filmeRepository.findAll(pageable).map(filmeMapper::toDTO);

    }

    @Transactional
    public FilmeResponseDTO atualizar(Long id, FilmeRequestDTO dto) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado com o ID: " + id));

        if (filmeRepository.existsByTituloIgnoreCaseAndDataLancamentoAndIdNot(dto.titulo(), dto.dataLancamento(), id)) {
            throw new RegraNegocioException("Já existe outro filme cadastrado com este título e data de lançamento.");
        }

        boolean possuiSessoesFuturas = sessaoRepository.existsByFilmeIdAndDataHoraFimAfterAndAtivoTrue(id,
                LocalDateTime.now());

        if (possuiSessoesFuturas && !filme.getDuracao().equals(dto.duracao())) {
            throw new RegraNegocioException(
                    "Não é possível alterar a duração de um filme que possui sessões futuras agendadas.");
        }

        filme.atualizarDados(
                dto.titulo(),
                dto.sinopse(),
                dto.duracao(),
                dto.diretor(),
                dto.distribuidora(),
                dto.dataLancamento(),
                dto.imagePath(),
                dto.formatos(),
                dto.generos(),
                dto.ativo());

        return filmeMapper.toDTO(filme);
    }

    @Transactional
    public void inativar(Long id) {

        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado com o ID: " + id));

        if (!filme.getAtivo()) {
            throw new RegraNegocioException("Este filme já está inativo no catálogo.");
        }

        boolean possuiSessoesFuturas = sessaoRepository
                .existsByFilmeIdAndDataHoraFimAfterAndAtivoTrue(id, LocalDateTime.now());

        if (possuiSessoesFuturas) {
            throw new RegraNegocioException(
                    "Não é possível inativar o filme pois existem sessões futuras agendadas em cinemas da rede.");
        }

        filme.inativar();
    }

}
