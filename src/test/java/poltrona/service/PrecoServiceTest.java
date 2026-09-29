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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.preco.AtualizaPrecoRequestDTO;
import poltrona.dto.preco.PrecoRequestDTO;
import poltrona.dto.preco.PrecoResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Preco;
import poltrona.entity.Proprietario;
import poltrona.enums.cinema.StatusCinema;
import poltrona.enums.filme.FormatoFilme;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.PrecoMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.PrecoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrecoService - Testes Unitários")
class PrecoServiceTest {

    @Mock
    private PrecoRepository precoRepository;

    @Mock
    private PrecoMapper precoMapper;

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private PrecoService precoService;

    private Proprietario proprietario;
    private Cinema cinemaAtivo;
    private Cinema cinemaInativo;
    private Preco precoAtivo;
    private Preco precoInativo;

    private PrecoRequestDTO precoRequestDTO;
    private AtualizaPrecoRequestDTO atualizaPrecoRequestDTO;
    private PrecoResponseDTO precoResponseDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        proprietario = new Proprietario("Proprietario Teste", "proprietario@email.com", "senha123", "11111111111",
                LocalDate.of(1980, 1, 1));
        ReflectionTestUtils.setField(proprietario, "id", 10L);

        cinemaAtivo = new Cinema("Cine Top", "Cine Top LTDA", "12345678000199", "11999999999", null, proprietario,
                null);
        ReflectionTestUtils.setField(cinemaAtivo, "id", 100L);
        ReflectionTestUtils.setField(cinemaAtivo, "status", StatusCinema.ATIVO);

        cinemaInativo = new Cinema("Cine Inativo", "Cine Inativo LTDA", "98765432000188", "11888888888", null,
                proprietario, null);
        ReflectionTestUtils.setField(cinemaInativo, "id", 101L);
        ReflectionTestUtils.setField(cinemaInativo, "status", StatusCinema.INATIVO);

        precoAtivo = new Preco(FormatoFilme.DUAS_D, BigDecimal.valueOf(30.00), cinemaAtivo);
        ReflectionTestUtils.setField(precoAtivo, "id", 1L);
        ReflectionTestUtils.setField(precoAtivo, "ativo", true);

        precoInativo = new Preco(FormatoFilme.TRES_D, BigDecimal.valueOf(40.00), cinemaAtivo);
        ReflectionTestUtils.setField(precoInativo, "id", 2L);
        ReflectionTestUtils.setField(precoInativo, "ativo", false);

        precoRequestDTO = new PrecoRequestDTO(100L, FormatoFilme.DUAS_D, BigDecimal.valueOf(30.00));
        atualizaPrecoRequestDTO = new AtualizaPrecoRequestDTO(BigDecimal.valueOf(35.00), true);
        precoResponseDTO = new PrecoResponseDTO(1L, FormatoFilme.DUAS_D, BigDecimal.valueOf(30.00), true);
        pageable = PageRequest.of(0, 10);
    }

    // ==============================================
    // CADASTRAR PREÇO
    // ==============================================

    @Test
    @DisplayName("Deve cadastrar preço com sucesso")
    void deveCadastrarPrecoComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(cinemaRepository.findByIdAndProprietarioId(100L, 10L)).thenReturn(Optional.of(cinemaAtivo));
        when(precoRepository.existsByFormatoAndCinemaId(precoRequestDTO.formato(), 100L)).thenReturn(false);
        when(precoMapper.toEntity(precoRequestDTO, cinemaAtivo)).thenReturn(precoAtivo);
        when(precoRepository.save(precoAtivo)).thenReturn(precoAtivo);
        when(precoMapper.toDTO(precoAtivo)).thenReturn(precoResponseDTO);

        // ACT
        PrecoResponseDTO resultado = precoService.cadastrar(precoRequestDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        verify(precoRepository).save(precoAtivo);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando cinema não for encontrado ou não pertencer ao proprietário")
    void deveLancarExcecaoQuandoCinemaNaoPertencerAoProprietarioOuNaoExistir() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(cinemaRepository.findByIdAndProprietarioId(100L, 10L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> precoService.cadastrar(precoRequestDTO))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Cinema não encontrado ou não pertence ao proprietário logado.");

        verify(precoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar cadastrar preço para cinema inativo")
    void deveLancarExcecaoQuandoCinemaEstiverInativo() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(cinemaRepository.findByIdAndProprietarioId(100L, 10L)).thenReturn(Optional.of(cinemaInativo));

        // ACT + ASSERT
        assertThatThrownBy(() -> precoService.cadastrar(precoRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Não é possível cadastrar tabela de preços para um cinema inativo.");

        verify(precoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceAlreadyExistsException quando já existir preço para o mesmo formato no cinema")
    void deveLancarExcecaoQuandoPrecoJaExistirParaOFormatoECinema() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(cinemaRepository.findByIdAndProprietarioId(100L, 10L)).thenReturn(Optional.of(cinemaAtivo));
        when(precoRepository.existsByFormatoAndCinemaId(precoRequestDTO.formato(), 100L)).thenReturn(true);

        // ACT + ASSERT
        assertThatThrownBy(() -> precoService.cadastrar(precoRequestDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Já existe um preço cadastrado com este nome para este cinema.");

        verify(precoRepository, never()).save(any());
    }

    // ==============================================
    // LISTAR TODOS
    // ==============================================

    @Test
    @DisplayName("Deve listar todos os preços com sucesso de forma paginada")
    void deveListarTodosOsPrecosComSucesso() {
        // ARRANGE
        Page<Preco> pagePrecos = new PageImpl<>(List.of(precoAtivo));
        when(precoRepository.findAll(pageable)).thenReturn(pagePrecos);
        when(precoMapper.toDTO(precoAtivo)).thenReturn(precoResponseDTO);

        // ACT
        Page<PrecoResponseDTO> resultado = precoService.listarTodos(pageable);

        // ASSERT
        assertThat(resultado).isNotNull().hasSize(1);
        verify(precoRepository).findAll(pageable);
    }

    // ==============================================
    // ATUALIZAR PREÇO
    // ==============================================

    @Test
    @DisplayName("Deve atualizar valor base do preço com sucesso")
    void deveAtualizarPrecoComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(precoRepository.findByIdAndCinemaProprietarioId(1L, 10L)).thenReturn(Optional.of(precoAtivo));
        when(precoRepository.save(precoAtivo)).thenReturn(precoAtivo);
        when(precoMapper.toDTO(precoAtivo)).thenReturn(precoResponseDTO);

        // ACT
        PrecoResponseDTO resultado = precoService.atualizar(1L, atualizaPrecoRequestDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        verify(precoRepository).save(precoAtivo);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao atualizar quando preço não for encontrado ou não pertencer ao proprietário")
    void deveLancarExcecaoAoAtualizarPrecoInexistenteOuSemPermissao() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(precoRepository.findByIdAndCinemaProprietarioId(99L, 10L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> precoService.atualizar(99L, atualizaPrecoRequestDTO))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Preço não encontrado ou não pertence a nenhum cinema seu.");

        verify(precoRepository, never()).save(any());
    }

    // ==============================================
    // DESATIVAR PREÇO
    // ==============================================

    @Test
    @DisplayName("Deve desativar preço com sucesso quando estiver ativo")
    void deveDesativarPrecoComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(precoRepository.findByIdAndCinemaProprietarioId(1L, 10L)).thenReturn(Optional.of(precoAtivo));

        // ACT
        precoService.desativar(1L);

        // ASSERT
        assertThat(precoAtivo.getAtivo()).isFalse();
        verify(precoRepository).findByIdAndCinemaProprietarioId(1L, 10L);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao desativar quando preço não for encontrado ou não pertencer ao proprietário")
    void deveLancarExcecaoAoDesativarPrecoInexistenteOuSemPermissao() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(precoRepository.findByIdAndCinemaProprietarioId(99L, 10L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> precoService.desativar(99L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Preço não encontrado ou não pertence a nenhum cinema seu.");
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar desativar preço já inativo")
    void deveLancarExcecaoAoDesativarPrecoJaInativo() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(precoRepository.findByIdAndCinemaProprietarioId(2L, 10L)).thenReturn(Optional.of(precoInativo));

        // ACT + ASSERT
        assertThatThrownBy(() -> precoService.desativar(2L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Este preço já se encontra inativo.");
    }

    // ==============================================
    // BUSCAR POR CINEMA
    // ==============================================

    @Test
    @DisplayName("Deve buscar preços por cinema com sucesso")
    void deveBuscarPrecosPorCinemaComSucesso() {
        // ARRANGE
        when(cinemaRepository.existsById(100L)).thenReturn(true);
        Page<Preco> pagePrecos = new PageImpl<>(List.of(precoAtivo));
        when(precoRepository.findAllByCinemaId(100L, pageable)).thenReturn(pagePrecos);
        when(precoMapper.toDTO(precoAtivo)).thenReturn(precoResponseDTO);

        // ACT
        Page<PrecoResponseDTO> resultado = precoService.buscarPorCinema(100L, pageable);

        // ASSERT
        assertThat(resultado).isNotNull().hasSize(1);
        verify(cinemaRepository).existsById(100L);
        verify(precoRepository).findAllByCinemaId(100L, pageable);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar preços para cinema inexistente")
    void deveLancarExcecaoAoBuscarPrecosPorCinemaInexistente() {
        // ARRANGE
        when(cinemaRepository.existsById(999L)).thenReturn(false);

        // ACT + ASSERT
        assertThatThrownBy(() -> precoService.buscarPorCinema(999L, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cinema não encontrado com o id 999");

        verify(precoRepository, never()).findAllByCinemaId(any(), any());
    }

    // ==============================================
    // BUSCAR POR ID
    // ==============================================

    @Test
    @DisplayName("Deve buscar preço por ID com sucesso")
    void deveBuscarPrecoPorIdComSucesso() {
        // ARRANGE
        when(precoRepository.findById(1L)).thenReturn(Optional.of(precoAtivo));
        when(precoMapper.toDTO(precoAtivo)).thenReturn(precoResponseDTO);

        // ACT
        PrecoResponseDTO resultado = precoService.buscarPorId(1L);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        verify(precoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar preço por ID inexistente")
    void deveLancarExcecaoAoBuscarPrecoPorIdInexistente() {
        // ARRANGE
        when(precoRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> precoService.buscarPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Preco não encontrado de id 999");
    }
}