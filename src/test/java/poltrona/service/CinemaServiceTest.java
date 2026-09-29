package poltrona.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import poltrona.dto.cinema.AtualizaCinemaRequestDTO;
import poltrona.dto.cinema.CinemaFiltroDTO;
import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Proprietario;
import poltrona.enums.cinema.StatusCinema;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.CinemaMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.IngressoRepository;

@ExtendWith(MockitoExtension.class)
class CinemaServiceTest {

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private CinemaMapper cinemaMapper;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private IngressoRepository ingressoRepository;

    @InjectMocks
    private CinemaService cinemaService;

    private Proprietario proprietarioAtivo;
    private Proprietario proprietarioBloqueado;
    private Cinema cinemaAtivo;
    private CinemaResponseDTO cinemaResponseDTO;
    private CinemaRequestDTO cinemaRequestDTO;

    @BeforeEach
    void setUp() {
        proprietarioAtivo = new Proprietario(
                "Carlos Silva",
                "carlos@poltrona.com",
                "Senha@123",
                "12345678900",
                LocalDate.of(1985, 5, 20));
        ReflectionTestUtils.setField(proprietarioAtivo, "id", 1L);
        ReflectionTestUtils.setField(proprietarioAtivo, "status", StatusConta.ATIVA);

        proprietarioBloqueado = new Proprietario(
                "Roberto Souza",
                "roberto@poltrona.com",
                "Senha@123",
                "98765432100",
                LocalDate.of(1990, 10, 15));
        ReflectionTestUtils.setField(proprietarioBloqueado, "id", 2L);
        ReflectionTestUtils.setField(proprietarioBloqueado, "status", StatusConta.BLOQUEADA);

        cinemaAtivo = new Cinema(
                "Cine Central",
                "Cine Central LTDA",
                "12345678000199",
                "11999999999",
                null,
                proprietarioAtivo,
                null);
        ReflectionTestUtils.setField(cinemaAtivo, "id", 10L);

        // CinemaResponseDTO com 7 campos
        cinemaResponseDTO = new CinemaResponseDTO(
                10L,
                "Cine Central",
                "Cine Central LTDA",
                "12345678000199",
                "11999999999",
                null,
                null);

        // CinemaRequestDTO com 6 campos
        cinemaRequestDTO = new CinemaRequestDTO(
                "Cine Central",
                "Cine Central LTDA",
                "12345678000199",
                "11999999999",
                null,
                null);
    }

    // =========================================================================
    // cadastrar
    // =========================================================================

    @Test
    @DisplayName("Deve cadastrar cinema com sucesso quando os dados forem válidos")
    void deveCadastrarCinemaComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietarioAtivo);
        when(cinemaRepository.existsByCnpj(cinemaRequestDTO.cnpj())).thenReturn(false);
        when(cinemaRepository.existsByNomeFantasiaAndProprietarioId(cinemaRequestDTO.nomeFantasia(),
                proprietarioAtivo.getId())).thenReturn(false);
        when(cinemaMapper.toEntity(cinemaRequestDTO, proprietarioAtivo)).thenReturn(cinemaAtivo);
        when(cinemaRepository.save(cinemaAtivo)).thenReturn(cinemaAtivo);
        when(cinemaMapper.toDTO(cinemaAtivo)).thenReturn(cinemaResponseDTO);

        // ACT
        CinemaResponseDTO resposta = cinemaService.cadastrar(cinemaRequestDTO);

        // ASSERT
        assertThat(resposta).isNotNull();
        assertThat(resposta.id()).isEqualTo(10L);
        assertThat(resposta.nomeFantasia()).isEqualTo("Cine Central");
        verify(cinemaRepository).save(cinemaAtivo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar com proprietário bloqueado")
    void deveLancarExcecaoAoCadastrarComProprietarioBloqueado() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietarioBloqueado);

        // ACT & ASSERT
        assertThatThrownBy(() -> cinemaService.cadastrar(cinemaRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Proprietários encerrados ou bloqueados não podem realizar operações.");

        verify(cinemaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar cinema com CNPJ já existente")
    void deveLancarExcecaoQuandoCnpjJaExiste() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietarioAtivo);
        when(cinemaRepository.existsByCnpj(cinemaRequestDTO.cnpj())).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> cinemaService.cadastrar(cinemaRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Já existe um cinema cadastrado com este CNPJ.");

        verify(cinemaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o proprietário já possuir cinema com o mesmo nome fantasia")
    void deveLancarExcecaoQuandoNomeFantasiaJaExisteParaProprietario() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietarioAtivo);
        when(cinemaRepository.existsByCnpj(cinemaRequestDTO.cnpj())).thenReturn(false);
        when(cinemaRepository.existsByNomeFantasiaAndProprietarioId(cinemaRequestDTO.nomeFantasia(),
                proprietarioAtivo.getId())).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> cinemaService.cadastrar(cinemaRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Você já possui um cinema cadastrado com este nome.");

        verify(cinemaRepository, never()).save(any());
    }

    // =========================================================================
    // listarTodos, me
    // =========================================================================

    @Test
    @DisplayName("Deve listar todos os cinemas paginados conforme o filtro fornecido")
    void deveListarTodosOsCinemasPaginados() {
        // ARRANGE
        CinemaFiltroDTO filtro = new CinemaFiltroDTO("Cine", null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cinema> paginaCinemas = new PageImpl<>(List.of(cinemaAtivo));

        when(cinemaRepository.findAllByFiltro(filtro, pageable)).thenReturn(paginaCinemas);
        when(cinemaMapper.toDTO(cinemaAtivo)).thenReturn(cinemaResponseDTO);

        // ACT
        Page<CinemaResponseDTO> resultado = cinemaService.listarTodos(filtro, pageable);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar apenas os cinemas pertencentes ao proprietário logado")
    void deveListarCinemasDoProprietarioLogado() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cinema> paginaCinemas = new PageImpl<>(List.of(cinemaAtivo));

        when(usuarioService.usuarioLogado()).thenReturn(proprietarioAtivo);
        when(cinemaRepository.findAllByProprietario(pageable, proprietarioAtivo)).thenReturn(paginaCinemas);
        when(cinemaMapper.toDTO(cinemaAtivo)).thenReturn(cinemaResponseDTO);

        // ACT
        Page<CinemaResponseDTO> resultado = cinemaService.me(pageable);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar e retornar um cinema pelo ID")
    void deveBuscarCinemaPorIdComSucesso() {
        // ARRANGE
        when(cinemaRepository.findById(10L)).thenReturn(Optional.of(cinemaAtivo));
        when(cinemaMapper.toDTO(cinemaAtivo)).thenReturn(cinemaResponseDTO);

        // ACT
        CinemaResponseDTO resposta = cinemaService.buscarPorId(10L);

        // ASSERT
        assertThat(resposta).isNotNull();
        assertThat(resposta.id()).isEqualTo(10L);
    }

    // =========================================================================
    // atualizar
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar os dados do cinema com sucesso")
    void deveAtualizarCinemaComSucesso() {
        // ARRANGE
        AtualizaCinemaRequestDTO dto = new AtualizaCinemaRequestDTO("Novo Nome", "11888888888", null, null);

        when(usuarioService.usuarioLogado()).thenReturn(proprietarioAtivo);
        when(cinemaRepository.findByIdAndProprietarioId(10L, proprietarioAtivo.getId()))
                .thenReturn(Optional.of(cinemaAtivo));
        when(cinemaRepository.existsByNomeFantasiaAndProprietarioIdAndIdNot("Novo Nome", proprietarioAtivo.getId(),
                10L))
                .thenReturn(false);
        when(cinemaMapper.toDTO(cinemaAtivo)).thenReturn(cinemaResponseDTO);

        // ACT
        CinemaResponseDTO resposta = cinemaService.atualizar(10L, dto);

        // ASSERT
        assertThat(resposta).isNotNull();
        assertThat(cinemaAtivo.getNomeFantasia()).isEqualTo("Novo Nome");
        assertThat(cinemaAtivo.getTelefone()).isEqualTo("11888888888");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cinema com proprietário bloqueado")
    void deveLancarExcecaoAoAtualizarComProprietarioBloqueado() {
        // ARRANGE
        AtualizaCinemaRequestDTO dto = new AtualizaCinemaRequestDTO("Novo Nome", "11888888888", null, null);

        when(usuarioService.usuarioLogado()).thenReturn(proprietarioBloqueado);

        // ACT & ASSERT
        assertThatThrownBy(() -> cinemaService.atualizar(10L, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Proprietários inativos ou bloqueados não podem alterar dados de cinemas.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar um cinema inexistente")
    void deveLancarExcecaoAoAtualizarCinemaNaoEncontrado() {
        // ARRANGE
        AtualizaCinemaRequestDTO dto = new AtualizaCinemaRequestDTO("Novo Nome", "11888888888", null, null);

        when(usuarioService.usuarioLogado()).thenReturn(proprietarioAtivo);
        when(cinemaRepository.findByIdAndProprietarioId(10L, proprietarioAtivo.getId()))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> cinemaService.atualizar(10L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cinema não encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um cinema inativo")
    void deveLancarExcecaoAoAtualizarCinemaInativo() {
        // ARRANGE
        AtualizaCinemaRequestDTO dto = new AtualizaCinemaRequestDTO("Novo Nome", "11888888888", null, null);

        Cinema cinemaInativo = new Cinema("Cine Central", "Cine Central LTDA", "12345678000199", "11999999999", null,
                proprietarioAtivo, null);
        ReflectionTestUtils.setField(cinemaInativo, "id", 10L);
        cinemaInativo.encerrar();

        when(usuarioService.usuarioLogado()).thenReturn(proprietarioAtivo);
        when(cinemaRepository.findByIdAndProprietarioId(10L, proprietarioAtivo.getId()))
                .thenReturn(Optional.of(cinemaInativo));

        // ACT & ASSERT
        assertThatThrownBy(() -> cinemaService.atualizar(10L, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Não é possível alterar um cinema que está inativo.");
    }

    // =========================================================================
    // vibe code
    // =========================================================================

    @Test
    @DisplayName("Deve encerrar o cinema alterando seu status para INATIVO")
    void deveEncerrarCinemaComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietarioAtivo);
        when(cinemaRepository.findById(10L)).thenReturn(Optional.of(cinemaAtivo));
        when(ingressoRepository.existsBySessaoSalaCinemaIdAndSessaoDataHoraFimAfter(eq(10L), any(LocalDateTime.class)))
                .thenReturn(false);

        // ACT
        cinemaService.encerrar(10L);

        // ASSERT
        assertThat(cinemaAtivo.getStatus()).isEqualTo(StatusCinema.INATIVO);
        verify(cinemaRepository).save(cinemaAtivo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar encerrar cinema de outro proprietário")
    void deveLancarExcecaoAoEncerrarCinemaDeOutroProprietario() {
        // ARRANGE
        Proprietario outroProprietario = new Proprietario("Outro Carlos", "outro@poltrona.com", "Senha@123",
                "11122233344", LocalDate.of(1992, 3, 10));
        ReflectionTestUtils.setField(outroProprietario, "id", 99L);

        when(usuarioService.usuarioLogado()).thenReturn(outroProprietario);
        when(cinemaRepository.findById(10L)).thenReturn(Optional.of(cinemaAtivo));

        // ACT & ASSERT
        assertThatThrownBy(() -> cinemaService.encerrar(10L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Você não tem permissão para encerrar este cinema.");
    }

    // =========================================================================
    // deletar
    // =========================================================================

    @Test
    @DisplayName("Deve deletar o cinema do banco de dados quando não houver ingressos associados")
    void deveDeletarCinemaComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietarioAtivo);
        when(cinemaRepository.findById(10L)).thenReturn(Optional.of(cinemaAtivo));
        when(ingressoRepository.existsBySessaoSalaCinemaId(10L)).thenReturn(false);

        // ACT
        cinemaService.deletar(10L);

        // ASSERT
        verify(cinemaRepository).delete(cinemaAtivo);
    }
}