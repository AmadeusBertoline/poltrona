package poltrona.service;

import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.entity.Ingresso;
import poltrona.entity.Poltrona;
import poltrona.entity.Sessao;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.IngressoMapper;
import poltrona.repository.IngressoRepository;
import poltrona.repository.PoltronaRepository;
import poltrona.repository.SessaoRepository;

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final SessaoRepository sessaoRepository;
    private final PoltronaRepository poltronaRepository;
    private final IngressoMapper ingressoMapper;

    public IngressoService(IngressoRepository ingressoRepository, SessaoRepository sessaoRepository,
            PoltronaRepository poltronaRepository, IngressoMapper ingressoMapper) {
        this.ingressoRepository = ingressoRepository;
        this.sessaoRepository = sessaoRepository;
        this.poltronaRepository = poltronaRepository;
        this.ingressoMapper = ingressoMapper;
    }

    @Transactional
    public IngressoResponseDTO cadastrar(IngressoRequestDTO dto) {

        Sessao sessao = sessaoRepository.findById(dto.idSessao())
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada"));

        Poltrona poltrona = poltronaRepository.findById(dto.idPoltrona())
                .orElseThrow(() -> new ResourceNotFoundException("Poltrona não encontrada"));

        if (!Objects.equals(poltrona.getSala().getId(), sessao.getSala().getId())) {
            throw new RegraNegocioException("A poltrona deve estar na mesma sala em que a sessão irá ocorrer.");
        }

        if (ingressoRepository.existsBySessaoIdAndPoltronaId(dto.idSessao(), dto.idPoltrona())) {
            throw new RegraNegocioException("Esta poltrona já está ocupada nesta sessão.");
        }

        sessao.validarPermiteVenda(LocalDateTime.now());

        Ingresso ingresso = ingressoMapper.toEntity(dto, sessao, poltrona);
        Ingresso salvo = ingressoRepository.save(ingresso);

        return ingressoMapper.toDTO(salvo);

    }

    @Transactional(readOnly = true)
    public Page<IngressoResponseDTO> listarTodos(Pageable pageable) {

        return ingressoRepository.findAll(pageable).map(ingressoMapper::toDTO);

    }

    @Transactional
    public void cancelar(Long id) {
        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

        if (ingresso.getSessao().getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException(
                    "Não é possível cancelar o ingresso de uma sessão que já iniciou ou ocorreu.");
        }

        ingressoRepository.delete(ingresso);
    }

}