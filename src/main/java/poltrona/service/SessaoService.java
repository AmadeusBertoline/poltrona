package poltrona.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poltrona.dto.sessao.SessaoRequestDTO;
import poltrona.dto.sessao.SessaoResponseDTO;
import poltrona.entity.Filme;
import poltrona.entity.Sala;
import poltrona.entity.Sessao;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.SessaoMapper;
import poltrona.repository.FilmeRepository;
import poltrona.repository.SalaRepository;
import poltrona.repository.SessaoRepository;

@Service
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final FilmeRepository filmeRepository;
    private final SalaRepository salaRepository;
    private final SessaoMapper sessaoMapper;

    public SessaoService(SessaoRepository sessaoRepository, FilmeRepository filmeRepository,
            SalaRepository salaRepository, SessaoMapper sessaoMapper) {
        this.sessaoRepository = sessaoRepository;
        this.filmeRepository = filmeRepository;
        this.salaRepository = salaRepository;
        this.sessaoMapper = sessaoMapper;
    }

    public SessaoResponseDTO cadastrar(SessaoRequestDTO dto) {

        Filme filme = filmeRepository.findById(dto.idFilme())
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado"));

        Sala sala = salaRepository.findById(dto.idSala())
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada"));

        if (dto.dataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data da sessão deve ser futura");
        }

        Sessao sessao = sessaoMapper.toEntity(dto, filme, sala);

        if (sessaoRepository.existeConflitoDeHorario(sala.getId(), dto.dataHoraInicio(), sessao.getDataHoraFim())) {
            throw new RegraNegocioException("O horário da sessão cadastrada está em conflito com outra sessão");
        }

        Sessao cadastrada = sessaoRepository.save(sessao);

        return sessaoMapper.toDTO(cadastrada);

    }

    public Page<SessaoResponseDTO> listarTodas(Pageable pageable) {

        return sessaoRepository.findAll(pageable)
                .map(sessaoMapper::toDTO);

    }

    public SessaoResponseDTO buscarPorId(Long id) {

        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada de id " + id));

        return sessaoMapper.toDTO(sessao);

    }

    public void deletar(Long id) {

        if (!sessaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sessão não encontrada de id " + id);
        }

        sessaoRepository.deleteById(id);

    }

}
