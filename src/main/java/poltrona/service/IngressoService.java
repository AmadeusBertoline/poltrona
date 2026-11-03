package poltrona.service;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.entity.Cliente;
import poltrona.entity.Ingresso;
import poltrona.entity.PoliticaOperacional;
import poltrona.entity.Poltrona;
import poltrona.entity.Sessao;
import poltrona.entity.Usuario;
import poltrona.entity.Admin;
import poltrona.entity.Gerente;
import poltrona.entity.Proprietario;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.enums.usuario.StatusConta;
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
        private final UsuarioService usuarioService;
        private final AcessoCinemaService acessoCinemaService;
        private final PdfService pdfService;

        public IngressoService(IngressoRepository ingressoRepository, SessaoRepository sessaoRepository,
                        PoltronaRepository poltronaRepository, IngressoMapper ingressoMapper,
                        UsuarioService usuarioService, AcessoCinemaService acessoCinemaService,
                        PdfService pdfService) {
                this.ingressoRepository = ingressoRepository;
                this.sessaoRepository = sessaoRepository;
                this.poltronaRepository = poltronaRepository;
                this.ingressoMapper = ingressoMapper;
                this.usuarioService = usuarioService;
                this.acessoCinemaService = acessoCinemaService;
                this.pdfService = pdfService;
        }

        @Transactional(readOnly = true)
        public byte[] gerarPdfIngresso(Long id) {

                Usuario usuario = usuarioService.usuarioLogado();

                Ingresso ingresso = ingressoRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Ingresso não encontrado de id " + id));

                acessoCinemaService.validarAcessoIngresso(usuario, ingresso);

                IngressoResponseDTO dto = ingressoMapper.toDTO(ingresso);
                return pdfService.gerarIngresso(dto);
        }

        @Transactional
        public Ingresso cadastrar(IngressoRequestDTO dto) {

                Usuario usuario = usuarioService.usuarioLogado();

                if (usuario.getStatus() != StatusConta.ATIVA) {
                        throw new RegraNegocioException("Usuário inativo não pode realizar compras.");
                }

                Sessao sessao = sessaoRepository.findByIdWithLockRead(dto.idSessao())
                                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada"));

                Poltrona poltrona = poltronaRepository.findByIdWithLock(dto.idPoltrona())
                                .orElseThrow(() -> new ResourceNotFoundException("Poltrona não encontrada"));

                boolean permiteVenda = sessao.getSala().getCinema().getPoliticaOperacional()
                                .isVendaPermitida(sessao.getDataHoraInicio());

                if (!permiteVenda) {
                        throw new RegraNegocioException(
                                        "Você não pode comprar ingressos para essa sessão pois já passou do horário permitido");
                }

                if (!poltrona.getAtiva()) {
                        throw new RegraNegocioException("A poltrona selecionada está inativa: " + poltrona.getNumero());
                }

                if (!Objects.equals(poltrona.getSala().getId(), sessao.getSala().getId())) {
                        throw new RegraNegocioException(
                                        "A poltrona deve estar na mesma sala em que a sessão irá ocorrer.");
                }

                if (ingressoRepository.existsBySessaoIdAndPoltronaIdAndStatus(
                                dto.idSessao(), dto.idPoltrona(), StatusIngresso.ATIVO)) {
                        throw new RegraNegocioException("Esta poltrona já está ocupada nesta sessão.");
                }

                sessao.validarPermiteVenda(LocalDateTime.now());

                Ingresso ingresso = ingressoMapper.toEntity(dto, sessao, poltrona, usuario);
                return ingressoRepository.save(ingresso);
        }

        @Transactional(readOnly = true)
        public Page<IngressoResponseDTO> listarTodos(Pageable pageable) {
                Usuario usuario = usuarioService.usuarioLogado();
                if (usuario instanceof Admin) {
                        return ingressoRepository.findAll(pageable).map(ingressoMapper::toDTO);
                }
                if (usuario instanceof Gerente gerente) {
                        return ingressoRepository.findAllBySessaoSalaCinemaId(
                                        gerente.getCinema().getId(), pageable).map(ingressoMapper::toDTO);
                }
                if (usuario instanceof Proprietario proprietario) {
                        return ingressoRepository.findAllBySessaoSalaCinemaProprietarioId(
                                        proprietario.getId(), pageable).map(ingressoMapper::toDTO);
                }
                throw new AccessDeniedException("Você não tem permissão para listar ingressos.");
        }

        @Transactional(readOnly = true)
        public Page<IngressoResponseDTO> meusIngressos(Pageable pageable) {
                Usuario usuario = usuarioService.usuarioLogado();
                return ingressoRepository.findAllByUsuarioIdOrderByDataCriacaoDesc(usuario.getId(), pageable)
                                .map(ingressoMapper::toDTO);
        }

        @Transactional
        public void cancelar(Long id) {

                Usuario usuario = usuarioService.usuarioLogado();

                Ingresso ingresso = ingressoRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

                if (!(usuario instanceof Cliente) || !ingresso.getUsuario().getId().equals(usuario.getId())) {
                        throw new AccessDeniedException("Você só pode cancelar seus próprios ingressos.");
                }

                if (ingresso.getStatus() == StatusIngresso.CANCELADO) {
                        throw new RegraNegocioException("Este ingresso já se encontra cancelado.");
                }

                cancelarIngresso(ingresso);

                ingressoRepository.save(ingresso);
        }

        @Transactional
        public void cancelarIngresso(Ingresso ingresso) {
                if (ingresso.getStatus() == StatusIngresso.CANCELADO) {
                        return;
                }

                Sessao sessao = ingresso.getSessao();
                PoliticaOperacional politica = sessao.getSala().getCinema().getPoliticaOperacional();
                if (!politica.isCancelamentoPermitido(sessao.getDataHoraInicio())) {
                        throw new RegraNegocioException(
                                        "O cancelamento só é permitido com até "
                                                        + politica.getAntecedenciaMinutosCancelamento()
                                                        + " minutos de antecedência do início da sessão.");
                }

                ingresso.cancelar();
        }

        @Transactional
        public IngressoResponseDTO buscarPorId(Long id) {

                Ingresso ingresso = ingressoRepository.findById(id)
                                .orElseThrow((() -> new ResourceNotFoundException(
                                                "Ingresso não encontrado de id " + id)));

                acessoCinemaService.validarAcessoIngresso(usuarioService.usuarioLogado(), ingresso);
                return ingressoMapper.toDTO(ingresso);

        }
}