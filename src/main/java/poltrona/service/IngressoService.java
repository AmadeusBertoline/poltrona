package poltrona.service;

import java.time.LocalDateTime;
import java.util.Objects;

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
            throw new RegraNegocioException("A poltrona deve estar na mesma sala que a sessão irá ocorrer");
        }

        if (ingressoRepository.existsBySessaoIdAndPoltronaId(dto.idSessao(), dto.idPoltrona())) {
            throw new RegraNegocioException("Esta poltrona já está ocupada nessa sessão");
        }

        if (sessao.getDataHoraFim().isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível comprar ingressos para sessões já encerradas");
        }

        if (sessao.getDataHoraInicio().plusMinutes(sessao.getPoliticaVenda().getToleranciaMinutosCompra())
                .isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível comprar ingressos após 15 minutos de início das sessões");
        }

        Ingresso ingresso = ingressoMapper.toEntity(dto, sessao, poltrona);

        Ingresso salvo = ingressoRepository.save(ingresso);

        return ingressoMapper.toDTO(salvo);

    }
}