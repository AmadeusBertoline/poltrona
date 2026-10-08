package poltrona.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.produto.ProdutoRequestDTO;
import poltrona.dto.venda.ItemVendaResponseDTO;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.entity.Admin;
import poltrona.entity.Cliente;
import poltrona.entity.Cinema;
import poltrona.entity.Ingresso;
import poltrona.entity.ItemVenda;
import poltrona.entity.Poltrona;
import poltrona.entity.Produto;
import poltrona.entity.Sessao;
import poltrona.entity.Venda;
import poltrona.enums.ingresso.TipoIngresso;
import poltrona.enums.produto.TipoProduto;
import poltrona.enums.venda.FormaPagamento;
import poltrona.enums.venda.StatusVenda;
import poltrona.enums.venda.TipoItemVenda;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.ItemVendaMapper;
import poltrona.mapper.VendaMapper;
import poltrona.repository.ProdutoRepository;
import poltrona.repository.VendaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("VendaService - Testes Unitários")
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private VendaMapper vendaMapper;

    @Mock
    private ItemVendaMapper itemVendaMapper;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private IngressoService ingressoService;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private Cinema cinema;

    @Mock
    private Sessao sessao;

    @Mock
    private Poltrona poltrona;

    @Mock
    private VendaResponseDTO vendaResponseDTO;

    @Mock
    private ItemVendaResponseDTO itemVendaResponseDTO;

    @Mock
    private VendaRequestDTO vendaRequestDTO;

    @Mock
    private VendaRequestDTO vendaRequestSemIngressoDTO;

    @Mock
    private VendaRequestDTO vendaRequestApenasIngressoDTO;

    @Mock
    private IngressoRequestDTO ingressoRequestDTO;

    @Mock
    private ProdutoRequestDTO produtoRequestDTO;

    @Mock
    private Admin admin;

    @InjectMocks
    private VendaService vendaService;

    private Cliente cliente;
    private Cliente outroCliente;

    private Venda venda;
    private Venda vendaCancelada;

    private Produto produto;
    private Ingresso ingresso;

    private ItemVenda itemVendaIngresso;
    private ItemVenda itemVendaProduto;

    private Pageable pageable;
    private Page<Venda> paginaVenda;

    @BeforeEach
    void setUp() {

        // =========================================================
        // CLIENTES
        // =========================================================

        cliente = new Cliente(
                "Cliente Teste",
                "cliente@email.com",
                "123456",
                "12345678901",
                LocalDate.of(1995, 1, 1));
        ReflectionTestUtils.setField(cliente, "id", 1L);

        outroCliente = new Cliente(
                "Outro Cliente",
                "outro@email.com",
                "123456",
                "98765432100",
                LocalDate.of(1998, 2, 2));
        ReflectionTestUtils.setField(outroCliente, "id", 2L);

        // =========================================================
        // SESSÃO
        // =========================================================

        when(sessao.getPreco())
                .thenReturn(BigDecimal.valueOf(25.00));

        // =========================================================
        // PRODUTO
        // =========================================================

        produto = new Produto(
                cinema,
                "Pipoca Grande",
                "Pipoca Salgada Grande",
                TipoProduto.PIPOCAS,
                BigDecimal.valueOf(15.00),
                10);

        ReflectionTestUtils.setField(
                produto,
                "id",
                5L);

        // =========================================================
        // INGRESSO
        // =========================================================

        ingresso = new Ingresso(
                TipoIngresso.INTEIRA,
                sessao,
                poltrona,
                cliente);

        // =========================================================
        // ITENS DA VENDA
        // =========================================================

        itemVendaIngresso = new ItemVenda(
                "Ingresso - INTEIRA",
                BigDecimal.valueOf(25.00),
                1,
                TipoItemVenda.INGRESSO,
                ingresso,
                null);

        itemVendaProduto = new ItemVenda(
                "Pipoca Grande",
                BigDecimal.valueOf(15.00),
                2,
                TipoItemVenda.PRODUTO_CONVENIENCIA,
                null,
                produto);

        // =========================================================
        // VENDA PAGA
        // =========================================================

        venda = new Venda(
                cliente,
                FormaPagamento.PIX);

        ReflectionTestUtils.setField(
                venda,
                "id",
                100L);

        ReflectionTestUtils.setField(
                venda,
                "status",
                StatusVenda.PAGA);

        ReflectionTestUtils.setField(
                venda,
                "itens",
                new ArrayList<>(
                        List.of(itemVendaIngresso)));

        // =========================================================
        // VENDA CANCELADA
        // =========================================================

        vendaCancelada = new Venda(
                cliente,
                FormaPagamento.PIX);

        ReflectionTestUtils.setField(
                vendaCancelada,
                "id",
                101L);

        ReflectionTestUtils.setField(
                vendaCancelada,
                "status",
                StatusVenda.CANCELADA);

        ReflectionTestUtils.setField(
                vendaCancelada,
                "itens",
                new ArrayList<>(
                        List.of(itemVendaIngresso)));

        // =========================================================
        // PAGINAÇÃO
        // =========================================================

        pageable = PageRequest.of(
                0,
                10);

        paginaVenda = new PageImpl<>(
                List.of(venda));
    }

    // =========================================================================
    // gerarPdfComprovanteVenda
    // =========================================================================

    @Test
    @DisplayName("Deve gerar PDF de comprovante de venda com sucesso para Cliente")
    void deveGerarPdfComprovanteVendaComSucessoParaCliente() {

        when(usuarioService.usuarioLogado())
                .thenReturn(cliente);

        when(vendaRepository.findById(100L))
                .thenReturn(Optional.of(venda));

        when(vendaMapper.toDTO(venda))
                .thenReturn(vendaResponseDTO);

        when(vendaResponseDTO.id())
                .thenReturn(100L);

        when(vendaResponseDTO.codigoComprovante())
                .thenReturn("COMP-12345");

        when(vendaResponseDTO.cinema())
                .thenReturn("Cine Poltrona");

        when(vendaResponseDTO.cliente())
                .thenReturn("Cliente Teste");

        when(vendaResponseDTO.dataVenda())
                .thenReturn(LocalDateTime.now());

        when(vendaResponseDTO.formaPagamento())
                .thenReturn(FormaPagamento.PIX);

        when(vendaResponseDTO.status())
                .thenReturn(StatusVenda.PAGA);

        when(vendaResponseDTO.itens())
                .thenReturn(List.of(itemVendaResponseDTO));

        when(vendaResponseDTO.valorTotal())
                .thenReturn(BigDecimal.valueOf(50.00));

        when(itemVendaResponseDTO.quantidade())
                .thenReturn(2);

        when(itemVendaResponseDTO.descricao())
                .thenReturn("Pipoca Grande");

        when(itemVendaResponseDTO.precoUnitario())
                .thenReturn(BigDecimal.valueOf(25.00));

        byte[] pdf = vendaService.gerarPdfComprovanteVenda(100L);

        assertThat(pdf)
                .isNotNull()
                .isNotEmpty();

        verify(vendaRepository, times(1))
                .findById(100L);

        verify(vendaMapper, times(1))
                .toDTO(venda);
    }

    @Test
    @DisplayName("Deve gerar PDF de comprovante de venda para Admin")
    void deveGerarPdfComprovanteVendaParaAdmin() {

        when(usuarioService.usuarioLogado())
                .thenReturn(admin);

        when(vendaRepository.findById(100L))
                .thenReturn(Optional.of(venda));

        when(vendaMapper.toDTO(venda))
                .thenReturn(vendaResponseDTO);

        when(vendaResponseDTO.id())
                .thenReturn(100L);

        when(vendaResponseDTO.codigoComprovante())
                .thenReturn("COMP-12345");

        when(vendaResponseDTO.cinema())
                .thenReturn("Cine Poltrona");

        when(vendaResponseDTO.cliente())
                .thenReturn("Cliente Teste");

        when(vendaResponseDTO.dataVenda())
                .thenReturn(LocalDateTime.now());

        when(vendaResponseDTO.formaPagamento())
                .thenReturn(FormaPagamento.PIX);

        when(vendaResponseDTO.status())
                .thenReturn(StatusVenda.PAGA);

        when(vendaResponseDTO.itens())
                .thenReturn(List.of(itemVendaResponseDTO));

        when(vendaResponseDTO.valorTotal())
                .thenReturn(BigDecimal.valueOf(50.00));

        when(itemVendaResponseDTO.quantidade())
                .thenReturn(1);

        when(itemVendaResponseDTO.descricao())
                .thenReturn("Ingresso");

        when(itemVendaResponseDTO.precoUnitario())
                .thenReturn(BigDecimal.valueOf(50.00));

        byte[] pdf = vendaService.gerarPdfComprovanteVenda(100L);

        assertThat(pdf)
                .isNotNull()
                .isNotEmpty();

        verify(vendaRepository)
                .findById(100L);

        verify(vendaMapper)
                .toDTO(venda);
    }

    @Test
    @DisplayName("Deve gerar PDF de comprovante de venda com sucesso quando não houver itens")
    void deveGerarPdfComprovanteVendaQuandoNaoHouverItens() {

        when(usuarioService.usuarioLogado())
                .thenReturn(cliente);

        when(vendaRepository.findById(100L))
                .thenReturn(Optional.of(venda));

        when(vendaMapper.toDTO(venda))
                .thenReturn(vendaResponseDTO);

        when(vendaResponseDTO.id())
                .thenReturn(100L);

        when(vendaResponseDTO.codigoComprovante())
                .thenReturn("COMP-12345");

        when(vendaResponseDTO.cinema())
                .thenReturn("Cine Poltrona");

        when(vendaResponseDTO.cliente())
                .thenReturn("Cliente Teste");

        when(vendaResponseDTO.dataVenda())
                .thenReturn(null);

        when(vendaResponseDTO.formaPagamento())
                .thenReturn(FormaPagamento.CARTAO_CREDITO);

        when(vendaResponseDTO.status())
                .thenReturn(StatusVenda.PAGA);

        when(vendaResponseDTO.itens())
                .thenReturn(null);

        when(vendaResponseDTO.valorTotal())
                .thenReturn(BigDecimal.ZERO);

        byte[] pdf = vendaService.gerarPdfComprovanteVenda(100L);

        assertThat(pdf)
                .isNotNull()
                .isNotEmpty();

        verify(vendaRepository, times(1))
                .findById(100L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar gerar PDF de venda inexistente")
    void deveLancarExcecaoQuandoVendaNaoEncontradaAoGerarPdf() {

        when(usuarioService.usuarioLogado())
                .thenReturn(cliente);

        when(vendaRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.gerarPdfComprovanteVenda(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Venda não encontrada de id 999");
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar gerar PDF de venda pertencente a outro cliente")
    void deveLancarExcecaoQuandoVendaNaoPertencerAoClienteLogado() {

        when(usuarioService.usuarioLogado())
                .thenReturn(outroCliente);

        when(vendaRepository.findById(100L))
                .thenReturn(Optional.of(venda));

        assertThatThrownBy(() -> vendaService.gerarPdfComprovanteVenda(100L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Você não pode baixar uma comprovante de uma venda que não te pertence");
    }

    // =========================================================================
    // cadastrar
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando a lista de ingressos for nula ou vazia")
    void deveLancarExcecaoQuandoNenhumIngressoForSelecionado() {

        when(vendaRequestSemIngressoDTO.ingressos())
                .thenReturn(null);

        assertThatThrownBy(() -> vendaService.cadastrar(
                vendaRequestSemIngressoDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Nenhum ingresso selecionado");

        verify(vendaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Deve cadastrar venda com sucesso incluindo ingressos e produtos")
    void deveCadastrarVendaComSucessoComIngressosEProdutos() {

        when(vendaRequestDTO.ingressos())
                .thenReturn(List.of(ingressoRequestDTO));

        when(vendaRequestDTO.produtos())
                .thenReturn(List.of(produtoRequestDTO));

        when(usuarioService.usuarioLogado())
                .thenReturn(cliente);

        when(vendaMapper.toEntity(
                vendaRequestDTO,
                cliente)).thenReturn(venda);

        when(ingressoService.cadastrar(
                ingressoRequestDTO)).thenReturn(ingresso);

        when(itemVendaMapper.toEntityIngresso(
                any(),
                anyString())).thenReturn(itemVendaIngresso);

        when(produtoRequestDTO.id())
                .thenReturn(5L);

        when(produtoRequestDTO.quantidade())
                .thenReturn(2);

        when(produtoRepository.findById(5L))
                .thenReturn(Optional.of(produto));

        when(produtoRepository.reduzirEstoque(
                5L,
                2)).thenReturn(1);

        when(itemVendaMapper.toEntityProduto(
                produto,
                2)).thenReturn(itemVendaProduto);

        when(vendaRepository.save(venda))
                .thenReturn(venda);

        when(vendaMapper.toDTO(venda))
                .thenReturn(vendaResponseDTO);

        VendaResponseDTO resposta = vendaService.cadastrar(vendaRequestDTO);

        assertThat(resposta)
                .isNotNull()
                .isEqualTo(vendaResponseDTO);

        verify(ingressoService, times(1))
                .cadastrar(ingressoRequestDTO);

        verify(produtoRepository, times(1))
                .reduzirEstoque(5L, 2);

        verify(vendaRepository, times(1))
                .save(venda);
    }

    @Test
    @DisplayName("Deve cadastrar venda com sucesso contendo apenas ingressos")
    void deveCadastrarVendaComSucessoApenasComIngressos() {

        when(vendaRequestApenasIngressoDTO.ingressos())
                .thenReturn(List.of(ingressoRequestDTO));

        when(vendaRequestApenasIngressoDTO.produtos())
                .thenReturn(null);

        when(usuarioService.usuarioLogado())
                .thenReturn(cliente);

        when(vendaMapper.toEntity(
                vendaRequestApenasIngressoDTO,
                cliente)).thenReturn(venda);

        when(ingressoService.cadastrar(
                ingressoRequestDTO)).thenReturn(ingresso);

        when(itemVendaMapper.toEntityIngresso(
                any(),
                anyString())).thenReturn(itemVendaIngresso);

        when(vendaRepository.save(venda))
                .thenReturn(venda);

        when(vendaMapper.toDTO(venda))
                .thenReturn(vendaResponseDTO);

        VendaResponseDTO resposta = vendaService.cadastrar(
                vendaRequestApenasIngressoDTO);

        assertThat(resposta)
                .isNotNull()
                .isEqualTo(vendaResponseDTO);

        verify(ingressoService, times(1))
                .cadastrar(ingressoRequestDTO);

        verify(produtoRepository, never())
                .findById(any());

        verify(vendaRepository, times(1))
                .save(venda);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao cadastrar venda se produto não for encontrado")
    void deveLancarExcecaoQuandoProdutoNaoEncontradoAoCadastrarVenda() {

        when(vendaRequestDTO.ingressos())
                .thenReturn(List.of(ingressoRequestDTO));

        when(vendaRequestDTO.produtos())
                .thenReturn(List.of(produtoRequestDTO));

        when(usuarioService.usuarioLogado())
                .thenReturn(cliente);

        when(vendaMapper.toEntity(
                vendaRequestDTO,
                cliente)).thenReturn(venda);

        when(ingressoService.cadastrar(
                ingressoRequestDTO)).thenReturn(ingresso);

        when(itemVendaMapper.toEntityIngresso(
                any(),
                anyString())).thenReturn(itemVendaIngresso);

        when(produtoRequestDTO.id())
                .thenReturn(5L);

        when(produtoRepository.findById(5L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.cadastrar(vendaRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Produto não encontrado ID: 5");

        verify(vendaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao cadastrar venda se o estoque do produto for insuficiente")
    void deveLancarExcecaoQuandoEstoqueInsuficienteAoCadastrarVenda() {

        when(vendaRequestDTO.ingressos())
                .thenReturn(List.of(ingressoRequestDTO));

        when(vendaRequestDTO.produtos())
                .thenReturn(List.of(produtoRequestDTO));

        when(usuarioService.usuarioLogado())
                .thenReturn(cliente);

        when(vendaMapper.toEntity(
                vendaRequestDTO,
                cliente)).thenReturn(venda);

        when(ingressoService.cadastrar(
                ingressoRequestDTO)).thenReturn(ingresso);

        when(itemVendaMapper.toEntityIngresso(
                any(),
                anyString())).thenReturn(itemVendaIngresso);

        when(produtoRequestDTO.id())
                .thenReturn(5L);

        when(produtoRequestDTO.quantidade())
                .thenReturn(2);

        when(produtoRepository.findById(5L))
                .thenReturn(Optional.of(produto));

        when(produtoRepository.reduzirEstoque(
                5L,
                2)).thenReturn(0);

        assertThatThrownBy(() -> vendaService.cadastrar(vendaRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Estoque do produto Pipoca Grande insuficiente");

        verify(vendaRepository, never())
                .save(any());
    }

    // =========================================================================
    // buscarPorId
    // =========================================================================

    @Test
    @DisplayName("Deve buscar venda por id com sucesso")
    void deveBuscarVendaPorIdComSucesso() {

        when(vendaRepository.findById(100L))
                .thenReturn(Optional.of(venda));

        when(vendaMapper.toDTO(venda))
                .thenReturn(vendaResponseDTO);

        VendaResponseDTO resposta = vendaService.buscarPorId(100L);

        assertThat(resposta)
                .isNotNull()
                .isEqualTo(vendaResponseDTO);

        verify(vendaRepository, times(1))
                .findById(100L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar venda inexistente por id")
    void deveLancarExcecaoQuandoVendaNaoEncontradaPorId() {

        when(vendaRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.buscarPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Venda não encontrada de id 999");
    }

    // =========================================================================
    // listarTodas
    // =========================================================================

    @Test
    @DisplayName("Deve listar todas as vendas paginadas por id do cliente")
    void deveListarTodasAsVendasPaginadas() {

        when(vendaRepository.buscarVendas(
                1L,
                pageable)).thenReturn(paginaVenda);

        when(vendaMapper.toDTO(venda))
                .thenReturn(vendaResponseDTO);

        Page<VendaResponseDTO> resposta = vendaService.listarTodas(
                1L,
                pageable);

        assertThat(resposta)
                .isNotNull();

        assertThat(resposta.getContent())
                .hasSize(1);

        assertThat(resposta.getContent().get(0))
                .isEqualTo(vendaResponseDTO);

        verify(vendaRepository, times(1))
                .buscarVendas(
                        1L,
                        pageable);
    }

    // =========================================================================
    // cancelar
    // =========================================================================

    @Test
    @DisplayName("Deve cancelar venda com sucesso e cancelar seus ingressos")
    void deveCancelarVendaComSucesso() {

        when(vendaRepository.findById(100L))
                .thenReturn(Optional.of(venda));

        when(vendaMapper.toDTO(venda))
                .thenReturn(vendaResponseDTO);

        VendaResponseDTO resposta = vendaService.cancelar(100L);

        assertThat(resposta)
                .isNotNull()
                .isEqualTo(vendaResponseDTO);

        assertThat(venda.getStatus())
                .isEqualTo(StatusVenda.CANCELADA);

        verify(vendaRepository, times(1))
                .findById(100L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar cancelar venda inexistente")
    void deveLancarExcecaoQuandoVendaNaoEncontradaAoCancelar() {

        when(vendaRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.cancelar(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Venda não encontrada de id 999");
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar cancelar uma venda que já está cancelada")
    void deveLancarExcecaoQuandoVendaJaEstiverCancelada() {

        when(vendaRepository.findById(101L))
                .thenReturn(Optional.of(vendaCancelada));

        assertThatThrownBy(() -> vendaService.cancelar(101L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Esta venda já se encontra cancelada.");
    }

    // =========================================================================
    // me
    // =========================================================================

    @Test
    @DisplayName("Deve listar as vendas paginadas pertencentes ao cliente logado")
    void deveListarVendasDoClienteLogado() {

        when(usuarioService.usuarioLogado())
                .thenReturn(cliente);

        when(vendaRepository.findByCliente(
                cliente,
                pageable)).thenReturn(paginaVenda);

        when(vendaMapper.toDTO(venda))
                .thenReturn(vendaResponseDTO);

        Page<VendaResponseDTO> resposta = vendaService.me(pageable);

        assertThat(resposta)
                .isNotNull();

        assertThat(resposta.getContent())
                .hasSize(1);

        assertThat(resposta.getContent().get(0))
                .isEqualTo(vendaResponseDTO);

        verify(usuarioService, times(1))
                .usuarioLogado();

        verify(vendaRepository, times(1))
                .findByCliente(
                        cliente,
                        pageable);
    }
}
