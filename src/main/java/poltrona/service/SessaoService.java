package poltrona.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.poltrona.MapaPoltronasResponseDTO;
import poltrona.dto.poltrona.PoltronaStatusDTO;
import poltrona.dto.sessao.SessaoRequestDTO;
import poltrona.dto.sessao.SessaoResponseDTO;
import poltrona.entity.Filme;
import poltrona.entity.Poltrona;
import poltrona.entity.Preco;
import poltrona.entity.Sala;
import poltrona.entity.Sessao;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.SessaoMapper;
import poltrona.repository.FilmeRepository;
import poltrona.repository.IngressoRepository;
import poltrona.repository.PoltronaRepository;
import poltrona.repository.PrecoRepository;
import poltrona.repository.SalaRepository;
import poltrona.repository.SessaoRepository;

@Service
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final FilmeRepository filmeRepository;
    private final SalaRepository salaRepository;
    private final SessaoMapper sessaoMapper;
    private final PrecoRepository precoRepository;
    private final PoltronaRepository poltronaRepository;
    private final IngressoRepository ingressoRepository;

    public SessaoService(SessaoRepository sessaoRepository, FilmeRepository filmeRepository,
            SalaRepository salaRepository, SessaoMapper sessaoMapper, PrecoRepository precoRepository,
            PoltronaRepository poltronaRepository, IngressoRepository ingressoRepository) {
        this.sessaoRepository = sessaoRepository;
        this.filmeRepository = filmeRepository;
        this.salaRepository = salaRepository;
        this.sessaoMapper = sessaoMapper;
        this.precoRepository = precoRepository;
        this.poltronaRepository = poltronaRepository;
        this.ingressoRepository = ingressoRepository;
    }

    @Transactional
    public SessaoResponseDTO cadastrar(SessaoRequestDTO dto) {

        Filme filme = filmeRepository.findById(dto.idFilme())
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado"));

        Sala sala = salaRepository.findById(dto.idSala())
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada"));

        Preco preco = precoRepository.findById(dto.idPreco())
                .orElseThrow(() -> new ResourceNotFoundException("Preço não encontrado"));

        if (dto.dataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data da sessão deve ser futura");
        }

        Sessao sessao = sessaoMapper.toEntity(dto, filme, sala, preco);

        if (sessaoRepository.existeConflitoDeHorario(sala.getId(), dto.dataHoraInicio(), sessao.getDataHoraFim())) {
            throw new RegraNegocioException("O horário da sessão cadastrada está em conflito com outra sessão");
        }

        Sessao cadastrada = sessaoRepository.save(sessao);

        return sessaoMapper.toDTO(cadastrada);

    }

    @Transactional(readOnly = true)
    public Page<SessaoResponseDTO> listarTodas(Pageable pageable) {

        return sessaoRepository.findAll(pageable)
                .map(sessaoMapper::toDTO);

    }

    @Transactional(readOnly = true)
    public SessaoResponseDTO buscarPorId(Long id) {

        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada de id " + id));

        return sessaoMapper.toDTO(sessao);

    }

    @Transactional
    public void deletar(Long id) {

        if (!sessaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sessão não encontrada de id " + id);
        }

        sessaoRepository.deleteById(id);

    }

    @Transactional(readOnly = true)
    public MapaPoltronasResponseDTO obterMapaPoltronas(Long sessaoId) {
        Sessao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada"));

        List<Poltrona> poltronasDaSala = poltronaRepository.findBySalaId(sessao.getSala().getId());

        Set<Long> poltronasOcupadasIds = ingressoRepository.findPoltronaIdsBySessaoId(sessaoId);

        List<PoltronaStatusDTO> poltronasStatus = poltronasDaSala.stream()
                .map(p -> new PoltronaStatusDTO(
                        p.getId(),
                        p.getNumero(),
                        poltronasOcupadasIds.contains(p.getId())))
                .toList();

        return new MapaPoltronasResponseDTO(sessao.getId(), sessao.getSala().getId(), poltronasStatus);
    }

}
