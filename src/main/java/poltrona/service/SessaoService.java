package poltrona.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.page.RespostaPaginadaDTO;
import poltrona.dto.poltrona.MapaPoltronasResponseDTO;
import poltrona.dto.poltrona.PoltronaStatusDTO;
import poltrona.dto.sessao.AtualizaSessaoRequestDTO;
import poltrona.dto.sessao.GradeSessaoRequestDTO;
import poltrona.dto.sessao.SessaoFiltroDTO;
import poltrona.dto.sessao.SessaoRequestDTO;
import poltrona.dto.sessao.SessaoResponseDTO;
import poltrona.entity.Filme;
import poltrona.entity.Gerente;
import poltrona.entity.Poltrona;
import poltrona.entity.Preco;
import poltrona.entity.Proprietario;
import poltrona.entity.Sala;
import poltrona.entity.Sessao;
import poltrona.entity.Usuario;
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
        private final UsuarioService usuarioService;

        public SessaoService(SessaoRepository sessaoRepository, FilmeRepository filmeRepository,
                        SalaRepository salaRepository, SessaoMapper sessaoMapper, PrecoRepository precoRepository,
                        PoltronaRepository poltronaRepository, IngressoRepository ingressoRepository,
                        UsuarioService usuarioService) {
                this.sessaoRepository = sessaoRepository;
                this.filmeRepository = filmeRepository;
                this.salaRepository = salaRepository;
                this.sessaoMapper = sessaoMapper;
                this.precoRepository = precoRepository;
                this.poltronaRepository = poltronaRepository;
                this.ingressoRepository = ingressoRepository;
                this.usuarioService = usuarioService;
        }

        @CacheEvict(value = "sessoes", allEntries = true)
        @Transactional
        public SessaoResponseDTO cadastrar(SessaoRequestDTO dto) {

                Sala sala = buscarSalaEValidarAcesso(dto.idSala());

                Filme filme = filmeRepository.findById(dto.idFilme())
                                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado"));

                if (!filme.getFormatoFilme().contains(dto.formato())) {
                        throw new RegraNegocioException(
                                        "O filme '" + filme.getTitulo() + "' não está disponível no formato "
                                                        + dto.formato());
                }

                Preco preco = precoRepository.findByCinemaIdAndFormato(sala.getCinema().getId(), dto.formato())
                                .orElseThrow(() -> new RegraNegocioException(
                                                "O cinema não possui um preço cadastrado para o formato "
                                                                + dto.formato()));

                if (dto.dataHoraInicio().isBefore(LocalDateTime.now())) {
                        throw new RegraNegocioException("A data e horário da sessão devem ser no futuro");
                }

                Sessao sessao = sessaoMapper.toEntity(dto, filme, sala, preco);

                int tempoLimpeza = sala.getCinema().getPoliticaOperacional().getIntervaloLimpezaMinutos();

                boolean conflito = sessaoRepository.existeConflitoDeHorario(
                                sala.getId(),
                                null,
                                dto.dataHoraInicio(),
                                sessao.getDataHoraFim().plusMinutes(tempoLimpeza));

                if (conflito) {
                        throw new RegraNegocioException(
                                        "O horário da sessão cadastrada está em conflito com outra sessão nesta sala");
                }

                Sessao cadastrada = sessaoRepository.save(sessao);

                return sessaoMapper.toDTO(cadastrada);
        }

        @Cacheable(value = "sessoes", key = "{ #filtro, #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString() }")
        @Transactional(readOnly = true)
        public RespostaPaginadaDTO<SessaoResponseDTO> listar(SessaoFiltroDTO filtro, Pageable pageable) {
                Page<SessaoResponseDTO> pagina = sessaoRepository
                                .buscarComFiltros(filtro, pageable)
                                .map(sessaoMapper::toDTO);

                return RespostaPaginadaDTO.de(pagina);
        }

        @Cacheable(value = "sessaoPorId", key = "#id")
        @Transactional(readOnly = true)
        public SessaoResponseDTO buscarPorId(Long id) {

                Sessao sessao = buscarSessaoEValidarAcesso(id);

                return sessaoMapper.toDTO(sessao);

        }

        @CacheEvict(value = { "sessoes", "sessaoPorId" }, key = "#id", allEntries = true)
        @Transactional
        public void deletar(Long id) {

                Sessao sessao = buscarSessaoEValidarAcesso(id);

                long ingressosVendidos = ingressoRepository.countBySessaoId(sessao.getId());

                sessao.permiteExclusao(ingressosVendidos);

                sessaoRepository.delete(sessao);

        }

        @Transactional(readOnly = true)
        public MapaPoltronasResponseDTO obterMapaPoltronas(Long sessaoId) {
                Sessao sessao = buscarSessaoEValidarAcesso(sessaoId);

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

        @CacheEvict(value = "sessoes")
        @Transactional
        public List<SessaoResponseDTO> cadastrarGrade(GradeSessaoRequestDTO dto) {
                Filme filme = filmeRepository.findById(dto.filmeId())
                                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado"));

                Sala sala = buscarSalaEValidarAcesso(dto.salaId());

                Preco preco = precoRepository.findByCinemaIdAndFormato(sala.getCinema().getId(), dto.formato())
                                .orElseThrow(
                                                () -> new RegraNegocioException(
                                                                "Cinema sem preço cadastrado para o formato "
                                                                                + dto.formato()));

                List<Sessao> sessoesParaSalvar = new ArrayList<>();
                int tempoLimpezaMinutos = sala.getCinema().getPoliticaOperacional().getIntervaloLimpezaMinutos();

                LocalDate dataAtual = dto.dataInicio();
                while (!dataAtual.isAfter(dto.dataFim())) {

                        for (LocalTime horario : dto.horarios()) {
                                LocalDateTime inicio = LocalDateTime.of(dataAtual, horario);
                                LocalDateTime fim = inicio.plusMinutes(filme.getDuracaoMinutos() + tempoLimpezaMinutos);

                                boolean conflitoNoBanco = sessaoRepository.existeConflitoDeHorario(sala.getId(), null,
                                                inicio, fim);

                                boolean conflitoNaGrade = sessoesParaSalvar.stream()
                                                .anyMatch(s -> inicio.isBefore(s.getDataHoraFim())
                                                                && fim.isAfter(s.getDataHoraInicio()));

                                if (conflitoNoBanco || conflitoNaGrade) {
                                        throw new RegraNegocioException(
                                                        String.format("Conflito de horário na sala %s em %s entre %s e %s",
                                                                        sala.getNumero(), dataAtual, horario,
                                                                        fim.toLocalTime()));
                                }

                                Sessao sessao = new Sessao(inicio, filme, sala, dto.formato(), preco, null);
                                sessoesParaSalvar.add(sessao);
                        }

                        dataAtual = dataAtual.plusDays(1);
                }

                List<Sessao> sessoesSalvas = sessaoRepository.saveAll(sessoesParaSalvar);
                return sessoesSalvas.stream().map(sessaoMapper::toDTO).toList();
        }

        @CacheEvict(value = { "sessoes", "sessoesPorId" }, key = "#id", allEntries = true)
        @Transactional
        public SessaoResponseDTO atualizar(Long id, AtualizaSessaoRequestDTO dto) {
                Sessao sessao = buscarSessaoEValidarAcesso(id);

                long ingressosVendidos = ingressoRepository.countBySessaoId(sessao.getId());

                sessao.validarPermiteAlteracao(ingressosVendidos);

                if (dto.filmeId() != null) {
                        Filme novoFilme = filmeRepository.findById(dto.filmeId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado."));
                        sessao.alterarFilme(novoFilme);
                }

                if (dto.salaId() != null) {
                        Sala novaSala = buscarSalaEValidarAcesso(dto.salaId());
                        sessao.alterarSala(novaSala);
                }

                if (dto.dataHoraInicio() != null) {
                        sessao.alterarHorario(dto.dataHoraInicio());
                }

                sessao.alterarPreco(dto.preco());
                sessao.alterarFormato(dto.formato());

                int tempoLimpeza = sessao.getSala().getCinema().getPoliticaOperacional().getIntervaloLimpezaMinutos();

                LocalDateTime inicioParaValidacao = dto.dataHoraInicio() != null ? dto.dataHoraInicio()
                                : sessao.getDataHoraInicio();

                boolean conflito = sessaoRepository.existeConflitoDeHorario(
                                sessao.getSala().getId(),
                                sessao.getId(),
                                inicioParaValidacao,
                                sessao.getDataHoraFim().plusMinutes(tempoLimpeza));

                if (conflito) {
                        throw new RegraNegocioException(
                                        "O horário da sessão cadastrada está em conflito com outra sessão nesta sala");
                }

                return sessaoMapper.toDTO(sessao);
        }

        /**
         * Métodos auxiliares para garantir que Gerente e Proprietario
         * operem somente sobre salas e sessões de cinemas autorizados.
         */
        private Sala buscarSalaEValidarAcesso(Long salaId) {
                Usuario usuario = usuarioService.usuarioLogado();

                Sala sala = salaRepository.findByIdWithLock(salaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada"));

                if (usuario instanceof Proprietario proprietario) {
                        if (!sala.getCinema().getProprietario().getId().equals(proprietario.getId())) {
                                throw new RegraNegocioException(
                                                "Você não tem permissão para acessar salas de um cinema que não lhe pertence.");
                        }
                } else if (usuario instanceof Gerente gerente) {
                        if (!sala.getCinema().getId().equals(gerente.getCinema().getId())) {
                                throw new RegraNegocioException(
                                                "Você não tem permissão para acessar salas de um cinema que não opera.");
                        }
                } else {
                        throw new RegraNegocioException("Usuário sem permissão para realizar esta operação.");
                }

                return sala;
        }

        private Sessao buscarSessaoEValidarAcesso(Long id) {
                Usuario usuario = usuarioService.usuarioLogado();

                Sessao sessao = sessaoRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada de id " + id));

                if (usuario instanceof Proprietario proprietario) {
                        if (!sessao.getSala().getCinema().getProprietario().getId().equals(proprietario.getId())) {
                                throw new RegraNegocioException(
                                                "Você não tem permissão para acessar ou alterar sessões de outro proprietário.");
                        }
                } else if (usuario instanceof Gerente gerente) {
                        if (!sessao.getSala().getCinema().getId().equals(gerente.getCinema().getId())) {
                                throw new RegraNegocioException(
                                                "Você não tem permissão para acessar ou alterar sessões de outro cinema.");
                        }
                } else {
                        throw new RegraNegocioException("Usuário sem permissão para realizar esta operação.");
                }

                return sessao;
        }
}