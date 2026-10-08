package poltrona.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.page.RespostaPaginadaDTO;
import poltrona.dto.produto.AtualizaProdutoRequestDTO;
import poltrona.dto.produto.CadastroProdutoRequestDTO;
import poltrona.dto.produto.ProdutoResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Cliente;
import poltrona.entity.Gerente;
import poltrona.entity.Produto;
import poltrona.entity.Proprietario;
import poltrona.entity.Usuario;
import poltrona.enums.produto.TipoProduto;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.ProdutoMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoService - Testes Unitários")
class ProdutoServiceTest {

        @Mock
        private ProdutoRepository produtoRepository;

        @Mock
        private ProdutoMapper produtoMapper;

        @Mock
        private UsuarioService usuarioService;

        @Mock
        private CinemaRepository cinemaRepository;

        @InjectMocks
        private ProdutoService produtoService;

        private Proprietario proprietario;
        private Gerente gerente;
        private Gerente gerenteOutroCinema;
        private Usuario usuarioComum;

        private Cinema cinema1;
        private Cinema cinema2;

        private Produto produto;

        private CadastroProdutoRequestDTO cadastroDTO;
        private CadastroProdutoRequestDTO cadastroCinemaOutroDTO;

        private AtualizaProdutoRequestDTO atualizaDtoAumento;
        private AtualizaProdutoRequestDTO atualizaDtoReducao;
        private AtualizaProdutoRequestDTO atualizaDtoSemEstoque;
        private AtualizaProdutoRequestDTO atualizaDtoNomeConflitante;

        private ProdutoResponseDTO produtoResponseDTO;

        private Pageable pageable;
        private Page<Produto> paginaProdutos;

        @BeforeEach
        void setUp() {

                proprietario = new Proprietario(
                                "Carlos Silva",
                                "proprietario@email.com",
                                "senha123",
                                "12345678901",
                                LocalDate.of(1980, 1, 1));
                ReflectionTestUtils.setField(proprietario, "id", 10L);

                cinema1 = new Cinema(
                                "Cinema Central",
                                "Cinema Central Ltda",
                                "12.345.678/0001-90",
                                "11999999999",
                                null,
                                proprietario,
                                null);
                ReflectionTestUtils.setField(cinema1, "id", 100L);

                cinema2 = new Cinema(
                                "Cinema Shopping",
                                "Cinema Shopping Ltda",
                                "98.765.432/0001-10",
                                "11888888888",
                                null,
                                proprietario,
                                null);
                ReflectionTestUtils.setField(cinema2, "id", 200L);

                gerente = new Gerente(
                                "João Gerente",
                                "gerente@email.com",
                                "senha123",
                                "10987654321",
                                LocalDate.of(1990, 5, 15),
                                cinema1);
                ReflectionTestUtils.setField(gerente, "id", 20L);

                gerenteOutroCinema = new Gerente(
                                "Maria Gerente",
                                "maria@email.com",
                                "senha123",
                                "98765432100",
                                LocalDate.of(1992, 8, 20),
                                cinema2);
                ReflectionTestUtils.setField(gerenteOutroCinema, "id", 30L);

                usuarioComum = new Cliente(
                                "Cliente Comum",
                                "cliente@email.com",
                                "senha123",
                                "11122233344",
                                LocalDate.of(2000, 10, 10));
                ReflectionTestUtils.setField(usuarioComum, "id", 40L);

                produto = new Produto(
                                cinema1,
                                "Pipoca Grande",
                                "Pipoca Grande Salgada",
                                TipoProduto.PIPOCAS,
                                BigDecimal.valueOf(25.00),
                                100);
                ReflectionTestUtils.setField(produto, "id", 1L);

                cadastroDTO = new CadastroProdutoRequestDTO(
                                100L,
                                "Pipoca Grande",
                                "Descrição",
                                TipoProduto.PIPOCAS,
                                BigDecimal.valueOf(25.00),
                                100);

                cadastroCinemaOutroDTO = new CadastroProdutoRequestDTO(
                                200L,
                                "Pipoca Grande",
                                "Descrição",
                                TipoProduto.PIPOCAS,
                                BigDecimal.valueOf(25.00),
                                100);

                atualizaDtoAumento = new AtualizaProdutoRequestDTO(
                                "Pipoca Mega",
                                "Descrição",
                                BigDecimal.valueOf(35.00),
                                150);

                atualizaDtoReducao = new AtualizaProdutoRequestDTO(
                                "Pipoca Mega",
                                "Descrição",
                                BigDecimal.valueOf(35.00),
                                40);

                atualizaDtoSemEstoque = new AtualizaProdutoRequestDTO(
                                "Pipoca Mega",
                                "Descrição",
                                BigDecimal.valueOf(35.00),
                                null);

                atualizaDtoNomeConflitante = new AtualizaProdutoRequestDTO(
                                "Refrigerante",
                                "Descrição",
                                BigDecimal.valueOf(10.00),
                                50);

                produtoResponseDTO = new ProdutoResponseDTO(
                                "Pipoca Grande",
                                "Descrição",
                                BigDecimal.valueOf(25.00),
                                100,
                                true);

                pageable = PageRequest.of(0, 10);
                paginaProdutos = new PageImpl<>(List.of(produto));
        }

        // ==============================================
        // CADASTRAR PRODUTO
        // ==============================================

        @Test
        @DisplayName("Deve cadastrar produto com sucesso quando usuário for Proprietário")
        void deveCadastrarProdutoComSucessoQuandoProprietario() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(cinemaRepository.findByIdAndProprietarioId(100L, 10L))
                                .thenReturn(Optional.of(cinema1));

                when(produtoRepository.existsByNomeIgnoreCaseAndCinemaId(
                                "Pipoca Grande",
                                100L)).thenReturn(false);

                when(produtoMapper.toEntity(cadastroDTO, cinema1))
                                .thenReturn(produto);

                when(produtoRepository.save(produto))
                                .thenReturn(produto);

                when(produtoMapper.toDTO(produto))
                                .thenReturn(produtoResponseDTO);

                // ACT
                ProdutoResponseDTO resultado = produtoService.cadastrar(cadastroDTO);

                // ASSERT
                assertThat(resultado)
                                .isNotNull();

                assertThat(resultado.ativo())
                                .isTrue();

                verify(produtoRepository)
                                .save(produto);
        }

        @Test
        @DisplayName("Deve cadastrar produto com sucesso quando usuário for Gerente do cinema")
        void deveCadastrarProdutoComSucessoQuandoGerente() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(gerente);

                when(produtoRepository.existsByNomeIgnoreCaseAndCinemaId(
                                "Pipoca Grande",
                                100L)).thenReturn(false);

                when(produtoMapper.toEntity(cadastroDTO, cinema1))
                                .thenReturn(produto);

                when(produtoRepository.save(produto))
                                .thenReturn(produto);

                when(produtoMapper.toDTO(produto))
                                .thenReturn(produtoResponseDTO);

                // ACT
                ProdutoResponseDTO resultado = produtoService.cadastrar(cadastroDTO);

                // ASSERT
                assertThat(resultado)
                                .isNotNull();

                verify(produtoRepository)
                                .save(produto);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando cinema não pertencer ao proprietário no cadastro")
        void deveLancarExcecaoQuandoCinemaNaoEncontradoParaProprietario() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(cinemaRepository.findByIdAndProprietarioId(100L, 10L))
                                .thenReturn(Optional.empty());

                // ACT + ASSERT
                assertThatThrownBy(() -> produtoService.cadastrar(cadastroDTO))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining(
                                                "Cinema não encontrado ou não pertence a este proprietário: 100");

                verify(produtoRepository, never())
                                .save(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando Gerente tentar cadastrar produto em outro cinema")
        void deveLancarExcecaoQuandoGerenteTentarCadastrarEmOutroCinema() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(gerente);

                // ACT + ASSERT
                assertThatThrownBy(() -> produtoService.cadastrar(cadastroCinemaOutroDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Você não pode cadastrar um produto em um cinema que não opera");

                verify(produtoRepository, never())
                                .save(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando tipo de usuário for inválido no cadastro")
        void deveLancarExcecaoQuandoUsuarioNaoTiverPermissaoParaCadastrar() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(usuarioComum);

                // ACT + ASSERT
                assertThatThrownBy(() -> produtoService.cadastrar(cadastroDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Usuário sem permissão para realizar esta operação.");

                verify(produtoRepository, never())
                                .save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando produto com mesmo nome já existir no cinema")
        void deveLancarExcecaoQuandoProdutoJaExistirNoCinema() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(cinemaRepository.findByIdAndProprietarioId(100L, 10L))
                                .thenReturn(Optional.of(cinema1));

                when(produtoRepository.existsByNomeIgnoreCaseAndCinemaId(
                                "Pipoca Grande",
                                100L)).thenReturn(true);

                // ACT + ASSERT
                assertThatThrownBy(() -> produtoService.cadastrar(cadastroDTO))
                                .isInstanceOf(ResourceAlreadyExistsException.class)
                                .hasMessage(
                                                "Produto já existente com o nome: Pipoca Grande");

                verify(produtoRepository, never())
                                .save(any());
        }

        // ==============================================
        // LISTAR TODOS
        // ==============================================

        @Test
        @DisplayName("Deve listar todos os produtos filtrados com sucesso")
        void deveListarTodosOsProdutosComSucesso() {

                // ARRANGE
                when(produtoRepository.findAllByFiltro(
                                true,
                                "Pipoca",
                                TipoProduto.PIPOCAS,
                                pageable)).thenReturn(paginaProdutos);

                when(produtoMapper.toDTO(produto))
                                .thenReturn(produtoResponseDTO);

                // ACT
                RespostaPaginadaDTO<ProdutoResponseDTO> resultado = produtoService.listarTodos(
                                true,
                                "Pipoca",
                                TipoProduto.PIPOCAS,
                                pageable);

                // ASSERT
                assertThat(resultado)
                                .isNotNull();

                verify(produtoRepository)
                                .findAllByFiltro(
                                                true,
                                                "Pipoca",
                                                TipoProduto.PIPOCAS,
                                                pageable);
        }

        // ==============================================
        // BUSCAR POR ID
        // ==============================================

        @Test
        @DisplayName("Deve buscar produto por ID com sucesso quando usuário for Proprietário")
        void deveBuscarPorIdComSucessoQuandoProprietario() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(produtoRepository.findByIdAndCinemaProprietarioId(1L, 10L))
                                .thenReturn(Optional.of(produto));

                when(produtoMapper.toDTO(produto))
                                .thenReturn(produtoResponseDTO);

                // ACT
                ProdutoResponseDTO resultado = produtoService.buscarPorId(1L);

                // ASSERT
                assertThat(resultado)
                                .isNotNull();

                verify(produtoRepository)
                                .findByIdAndCinemaProprietarioId(1L, 10L);
        }

        @Test
        @DisplayName("Deve buscar produto por ID com sucesso quando usuário for Gerente do mesmo cinema")
        void deveBuscarPorIdComSucessoQuandoGerente() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(gerente);

                when(produtoRepository.findById(1L))
                                .thenReturn(Optional.of(produto));

                when(produtoMapper.toDTO(produto))
                                .thenReturn(produtoResponseDTO);

                // ACT
                ProdutoResponseDTO resultado = produtoService.buscarPorId(1L);

                // ASSERT
                assertThat(resultado)
                                .isNotNull();

                verify(produtoRepository)
                                .findById(1L);
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando Gerente tentar buscar produto de outro cinema")
        void deveLancarExcecaoQuandoGerenteAcessarProdutoDeOutroCinema() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(gerenteOutroCinema);

                when(produtoRepository.findById(1L))
                                .thenReturn(Optional.of(produto));

                // ACT + ASSERT
                assertThatThrownBy(() -> produtoService.buscarPorId(1L))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Você não tem permissão para acessar ou alterar produtos de outro cinema.");
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando produto não for encontrado por ID")
        void deveLancarExcecaoQuandoProdutoNaoEncontradoPorId() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(produtoRepository.findByIdAndCinemaProprietarioId(99L, 10L))
                                .thenReturn(Optional.empty());

                // ACT + ASSERT
                assertThatThrownBy(() -> produtoService.buscarPorId(99L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage(
                                                "Produto não encontrado de id: 99");
        }

        // ==============================================
        // ATUALIZAR PRODUTO E ESTOQUE
        // ==============================================

        @Test
        @DisplayName("Deve atualizar produto e adicionar estoque quando a nova quantidade for maior")
        void deveAtualizarProdutoEAdicionarEstoque() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(produtoRepository.findByIdAndCinemaProprietarioId(1L, 10L))
                                .thenReturn(Optional.of(produto));

                when(produtoRepository.existsByNomeIgnoreCaseAndCinemaId(
                                "Pipoca Mega",
                                100L)).thenReturn(false);

                when(produtoRepository.save(produto))
                                .thenReturn(produto);

                when(produtoMapper.toDTO(produto))
                                .thenReturn(produtoResponseDTO);

                // ACT
                ProdutoResponseDTO resultado = produtoService.atualizar(
                                1L,
                                atualizaDtoAumento);

                // ASSERT
                assertThat(resultado)
                                .isNotNull();

                assertThat(produto.getQuantidadeEstoque())
                                .isEqualTo(150);

                verify(produtoRepository)
                                .save(produto);
        }

        @Test
        @DisplayName("Deve atualizar produto e debitar estoque quando a nova quantidade for menor")
        void deveAtualizarProdutoEDebitarEstoque() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(produtoRepository.findByIdAndCinemaProprietarioId(1L, 10L))
                                .thenReturn(Optional.of(produto));

                when(produtoRepository.existsByNomeIgnoreCaseAndCinemaId(
                                "Pipoca Mega",
                                100L)).thenReturn(false);

                when(produtoRepository.save(produto))
                                .thenReturn(produto);

                when(produtoMapper.toDTO(produto))
                                .thenReturn(produtoResponseDTO);

                // ACT
                ProdutoResponseDTO resultado = produtoService.atualizar(
                                1L,
                                atualizaDtoReducao);

                // ASSERT
                assertThat(resultado)
                                .isNotNull();

                assertThat(produto.getQuantidadeEstoque())
                                .isEqualTo(40);

                verify(produtoRepository)
                                .save(produto);
        }

        @Test
        @DisplayName("Deve atualizar produto sem alterar estoque se quantidadeEstoque for nulo")
        void deveAtualizarProdutoSemAlterarEstoque() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(produtoRepository.findByIdAndCinemaProprietarioId(1L, 10L))
                                .thenReturn(Optional.of(produto));

                when(produtoRepository.existsByNomeIgnoreCaseAndCinemaId(
                                "Pipoca Mega",
                                100L)).thenReturn(false);

                when(produtoRepository.save(produto))
                                .thenReturn(produto);

                when(produtoMapper.toDTO(produto))
                                .thenReturn(produtoResponseDTO);

                // ACT
                ProdutoResponseDTO resultado = produtoService.atualizar(
                                1L,
                                atualizaDtoSemEstoque);

                // ASSERT
                assertThat(resultado)
                                .isNotNull();

                assertThat(produto.getQuantidadeEstoque())
                                .isEqualTo(100);

                verify(produtoRepository)
                                .save(produto);
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException ao tentar atualizar para um nome de produto já existente")
        void deveLancarExcecaoAoAtualizarParaNomeJaExistente() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(produtoRepository.findByIdAndCinemaProprietarioId(1L, 10L))
                                .thenReturn(Optional.of(produto));

                when(produtoRepository.existsByNomeIgnoreCaseAndCinemaId(
                                "Refrigerante",
                                100L)).thenReturn(true);

                // ACT + ASSERT
                assertThatThrownBy(() -> produtoService.atualizar(
                                1L,
                                atualizaDtoNomeConflitante))
                                .isInstanceOf(ResourceAlreadyExistsException.class)
                                .hasMessage(
                                                "Produto já existente com o nome: Refrigerante");

                verify(produtoRepository, never())
                                .save(any());
        }

        // ==============================================
        // ALTERAR STATUS E DELETAR
        // ==============================================

        @Test
        @DisplayName("Deve alterar status do produto com sucesso")
        void deveAlterarStatusComSucesso() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(produtoRepository.findByIdAndCinemaProprietarioId(1L, 10L))
                                .thenReturn(Optional.of(produto));

                when(produtoRepository.save(produto))
                                .thenReturn(produto);

                when(produtoMapper.toDTO(produto))
                                .thenReturn(produtoResponseDTO);

                // ACT
                ProdutoResponseDTO resultado = produtoService.alterarStatus(1L, false);

                // ASSERT
                assertThat(resultado)
                                .isNotNull();

                assertThat(produto.getAtivo())
                                .isFalse();

                verify(produtoRepository)
                                .save(produto);
        }

        @Test
        @DisplayName("Deve deletar produto com sucesso")
        void deveDeletarProdutoComSucesso() {

                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(produtoRepository.findByIdAndCinemaProprietarioId(1L, 10L))
                                .thenReturn(Optional.of(produto));

                // ACT
                produtoService.deletar(1L);

                // ASSERT
                verify(produtoRepository)
                                .delete(produto);
        }
}