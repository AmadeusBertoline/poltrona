package poltrona.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Cliente;
import poltrona.entity.Endereco;
import poltrona.entity.Filme;
import poltrona.entity.Ingresso;
import poltrona.entity.PoliticaOperacional;
import poltrona.entity.Poltrona;
import poltrona.entity.Preco;
import poltrona.entity.Proprietario;
import poltrona.entity.Sala;
import poltrona.entity.Sessao;
import poltrona.enums.filme.ClassificacaoIndicativa;
import poltrona.enums.filme.FormatoFilme;
import poltrona.enums.filme.GeneroFilme;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.enums.ingresso.TipoIngresso;
import poltrona.enums.poltrona.TipoPoltrona;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.IngressoMapper;
import poltrona.repository.IngressoRepository;
import poltrona.repository.PoltronaRepository;
import poltrona.repository.SessaoRepository;

@ExtendWith(MockitoExtension.class)
class IngressoServiceTest {

    @Mock
    private IngressoRepository ingressoRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PoltronaRepository poltronaRepository;

    @Mock
    private IngressoMapper ingressoMapper;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private IngressoService ingressoService;

    private Cliente clienteLogado;
    private Cliente outroCliente;
    private PoliticaOperacional politicaOperacional;
    private Cinema cinema;
    private Sala sala;
    private Sala outraSala;
    private Sessao sessao;
    private Poltrona poltrona;
    private Ingresso ingresso;
    private IngressoRequestDTO ingressoRequestDTO;
    private IngressoResponseDTO ingressoResponseDTO;
    private Filme filme;
    private Preco preco;

    @BeforeEach
    void setUp() {

        politicaOperacional = new PoliticaOperacional(
                30,
                30,
                15);

        Proprietario proprietario = mock(Proprietario.class);

        Endereco endereco = mock(Endereco.class);

        cinema = new Cinema(
                "Cine Poltrona Centro",
                "Cine Poltrona Centro LTDA",
                "12345678000199",
                "11999999999",
                endereco,
                proprietario,
                politicaOperacional);

        ReflectionTestUtils.setField(cinema, "id", 1L);

        preco = new Preco(
                FormatoFilme.DUAS_D,
                new BigDecimal("35.00"),
                cinema);

        filme = new Filme(
                "Matrix",
                "Um hacker descobre a verdadeira natureza da realidade.",
                Set.of(GeneroFilme.FICCAO_CIENTIFICA),
                120,
                "Lana Wachowski e Lilly Wachowski",
                "Warner Bros.",
                LocalDate.of(1999, 3, 31),
                "matrix.jpg",
                ClassificacaoIndicativa.QUATORZE_ANOS,
                Set.of(FormatoFilme.DUAS_D));

        ReflectionTestUtils.setField(filme, "id", 1L);

        sala = new Sala(
                1,
                Map.of('A', 10),
                cinema);

        ReflectionTestUtils.setField(sala, "id", 10L);

        outraSala = new Sala(
                99,
                Map.of('A', 10),
                cinema);

        ReflectionTestUtils.setField(outraSala, "id", 99L);

        sessao = new Sessao(
                LocalDateTime.now().plusHours(2),
                filme,
                sala,
                null,
                preco,
                politicaOperacional);

        ReflectionTestUtils.setField(sessao, "id", 1L);

        poltrona = new Poltrona(
                'A',
                1,
                sala);

        ReflectionTestUtils.setField(poltrona, "id", 100L);

        clienteLogado = new Cliente(
                "Carlos Silva",
                "carlos@email.com",
                "123456",
                "12345678901",
                LocalDate.of(2000, 1, 10));

        ReflectionTestUtils.setField(clienteLogado, "id", 1L);
        ReflectionTestUtils.setField(
                clienteLogado,
                "status",
                StatusConta.ATIVA);

        outroCliente = new Cliente(
                "João Silva",
                "joao@email.com",
                "123456",
                "98765432100",
                LocalDate.of(2000, 5, 20));

        ReflectionTestUtils.setField(outroCliente, "id", 2L);
        ReflectionTestUtils.setField(
                outroCliente,
                "status",
                StatusConta.ATIVA);

        ingresso = new Ingresso(
                TipoIngresso.INTEIRA,
                sessao,
                poltrona,
                clienteLogado);

        ReflectionTestUtils.setField(ingresso, "id", 500L);

        ingressoRequestDTO = new IngressoRequestDTO(
                TipoIngresso.INTEIRA,
                1L,
                100L);

        ingressoResponseDTO = new IngressoResponseDTO(
                500L,
                "Cine Poltrona Centro",
                new BigDecimal("35.00"),
                TipoIngresso.INTEIRA,
                "Matrix",
                1,
                sessao.getDataHoraInicio(),
                'A',
                1,
                TipoPoltrona.VIP,
                "Carlos Silva",
                "Rua Principal, 123");
    }

    // =========================================================================
    // cadastrar
    // =========================================================================

    @Test
    @DisplayName("Deve cadastrar ingresso com sucesso quando todas as regras forem satisfeitas")
    void deveCadastrarIngressoComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(poltronaRepository.findById(100L)).thenReturn(Optional.of(poltrona));
        when(ingressoRepository.existsBySessaoIdAndPoltronaIdAndStatus(1L, 100L, StatusIngresso.ATIVO))
                .thenReturn(false);
        when(ingressoMapper.toEntity(ingressoRequestDTO, sessao, poltrona, clienteLogado)).thenReturn(ingresso);
        when(ingressoRepository.save(ingresso)).thenReturn(ingresso);

        // ACT
        Ingresso resultado = ingressoService.cadastrar(ingressoRequestDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(500L);
        verify(ingressoRepository, times(1)).save(ingresso);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar comprar ingresso com usuário inativo")
    void deveLancarExcecaoQuandoUsuarioEstiverInativo() {
        // ARRANGE
        ReflectionTestUtils.setField(clienteLogado, "status", StatusConta.BLOQUEADA);
        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);

        // ACT & ASSERT
        assertThatThrownBy(() -> ingressoService.cadastrar(ingressoRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Usuário inativo não pode realizar compras.");

        verify(ingressoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a sessão informada não existir")
    void deveLancarExcecaoQuandoSessaoNaoEncontrada() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> ingressoService.cadastrar(ingressoRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Sessão não encontrada");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a poltrona selecionada estiver inativa")
    void deveLancarExcecaoQuandoPoltronaEstiverInativa() {
        // ARRANGE
        poltrona.setAtiva(false);

        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(poltronaRepository.findById(100L)).thenReturn(Optional.of(poltrona));

        // ACT & ASSERT
        assertThatThrownBy(() -> ingressoService.cadastrar(ingressoRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("A poltrona selecionada está inativa: A1");
    }

    @Test
    @DisplayName("Deve lançar exceção se a poltrona pertencer a uma sala diferente da sessão")
    void deveLancarExcecaoQuandoPoltronaForDeOutraSala() {
        // ARRANGE
        ReflectionTestUtils.setField(poltrona, "sala", outraSala);

        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(poltronaRepository.findById(100L)).thenReturn(Optional.of(poltrona));

        // ACT & ASSERT
        assertThatThrownBy(() -> ingressoService.cadastrar(ingressoRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("A poltrona deve estar na mesma sala em que a sessão irá ocorrer.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a poltrona já estiver ocupada na sessão")
    void deveLancarExcecaoQuandoPoltronaJaEstiverOcupada() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(poltronaRepository.findById(100L)).thenReturn(Optional.of(poltrona));
        when(ingressoRepository.existsBySessaoIdAndPoltronaIdAndStatus(1L, 100L, StatusIngresso.ATIVO))
                .thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> ingressoService.cadastrar(ingressoRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Esta poltrona já está ocupada nesta sessão.");
    }

    // =========================================================================
    // gerarPdfIngresso
    // =========================================================================

    @Test
    @DisplayName("Deve gerar array de bytes do PDF do ingresso com sucesso")
    void deveGerarPdfIngressoComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(ingressoRepository.findById(500L)).thenReturn(Optional.of(ingresso));
        when(ingressoMapper.toDTO(ingresso)).thenReturn(ingressoResponseDTO);

        // ACT
        byte[] pdfBytes = ingressoService.gerarPdfIngresso(500L);

        // ASSERT
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar baixar PDF de ingresso pertencente a outro cliente")
    void deveLancarExcecaoAoBaixarPdfDeOutroUsuario() {
        // ARRANGE
        ReflectionTestUtils.setField(ingresso, "usuario", outroCliente);

        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(ingressoRepository.findById(500L)).thenReturn(Optional.of(ingresso));

        // ACT & ASSERT
        assertThatThrownBy(() -> ingressoService.gerarPdfIngresso(500L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Você não pode baixar um ingresso que não te pertence");
    }

    // =========================================================================
    // cancelar
    // =========================================================================

    @Test
    @DisplayName("Deve cancelar ingresso com sucesso quando respeitar a antecedência mínima")
    void deveCancelarIngressoComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(ingressoRepository.findById(500L)).thenReturn(Optional.of(ingresso));

        // ACT
        ingressoService.cancelar(500L);

        // ASSERT
        assertThat(ingresso.getStatus()).isEqualTo(StatusIngresso.CANCELADO);
        verify(ingressoRepository, times(1)).save(ingresso);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar ingresso pertencente a outro usuário")
    void deveLancarExcecaoAoCancelarIngressoDeOutroUsuario() {
        // ARRANGE
        ReflectionTestUtils.setField(ingresso, "usuario", outroCliente);

        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(ingressoRepository.findById(500L)).thenReturn(Optional.of(ingresso));

        // ACT & ASSERT
        assertThatThrownBy(() -> ingressoService.cancelar(500L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Você só pode cancelar seus próprios ingressos.");

        verify(ingressoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar um ingresso já cancelado")
    void deveLancarExcecaoAoCancelarIngressoJaCancelado() {
        // ARRANGE
        ReflectionTestUtils.setField(ingresso, "status", StatusIngresso.CANCELADO);

        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(ingressoRepository.findById(500L)).thenReturn(Optional.of(ingresso));

        // ACT & ASSERT
        assertThatThrownBy(() -> ingressoService.cancelar(500L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Este ingresso já se encontra cancelado.");
    }

    // =========================================================================
    // listarTodos
    // =========================================================================

    @Test
    @DisplayName("Deve listar todos os ingressos de forma paginada")
    void deveListarTodosOsIngressosPaginado() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ingresso> pageIngresso = new PageImpl<>(List.of(ingresso), pageable, 1);

        when(ingressoRepository.findAll(pageable)).thenReturn(pageIngresso);
        when(ingressoMapper.toDTO(ingresso)).thenReturn(ingressoResponseDTO);

        // ACT
        Page<IngressoResponseDTO> resultado = ingressoService.listarTodos(pageable);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    // =========================================================================
    // meusIngressos
    // =========================================================================

    @Test
    @DisplayName("Deve buscar os ingressos do usuário logado ordenados por data")
    void deveBuscarMeusIngressosComSucesso() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ingresso> pageIngresso = new PageImpl<>(List.of(ingresso), pageable, 1);

        when(usuarioService.usuarioLogado()).thenReturn(clienteLogado);
        when(ingressoRepository.findAllByUsuarioIdOrderByDataCriacaoDesc(1L, pageable)).thenReturn(pageIngresso);
        when(ingressoMapper.toDTO(ingresso)).thenReturn(ingressoResponseDTO);

        // ACT
        Page<IngressoResponseDTO> resultado = ingressoService.meusIngressos(pageable);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    // =========================================================================
    // buscarPorId
    // =========================================================================

    @Test
    @DisplayName("Deve buscar ingresso por ID com sucesso")
    void deveBuscarIngressoPorIdComSucesso() {
        // ARRANGE
        when(ingressoRepository.findById(500L)).thenReturn(Optional.of(ingresso));
        when(ingressoMapper.toDTO(ingresso)).thenReturn(ingressoResponseDTO);

        // ACT
        IngressoResponseDTO resultado = ingressoService.buscarPorId(500L);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(500L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ingresso não for encontrado por ID")
    void deveLancarExcecaoQuandoIngressoNaoEncontradoPorId() {
        // ARRANGE
        when(ingressoRepository.findById(500L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> ingressoService.buscarPorId(500L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ingresso não encontrado de id 500");
    }
}