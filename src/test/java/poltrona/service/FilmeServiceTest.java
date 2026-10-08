package poltrona.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.filme.FilmeFiltroDTO;
import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;
import poltrona.entity.Filme;
import poltrona.enums.filme.ClassificacaoIndicativa;
import poltrona.enums.filme.FormatoFilme;
import poltrona.enums.filme.GeneroFilme;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.FilmeMapper;
import poltrona.repository.FilmeRepository;
import poltrona.repository.SessaoRepository;

@ExtendWith(MockitoExtension.class)
public class FilmeServiceTest {

    @Mock
    private FilmeRepository filmeRepository;

    @Mock
    private FilmeMapper filmeMapper;

    @Mock
    private SessaoRepository sessaoRepository;

    @InjectMocks
    private FilmeService filmeService;

    private Filme filmeSemId;
    private Filme filmeSalvo;
    private FilmeRequestDTO filmeRequestDTO;
    private FilmeResponseDTO filmeResponseDTO;

    @BeforeEach
    void setUp() {
        filmeRequestDTO = new FilmeRequestDTO(
                "Matrix",
                "Um hacker descobre a verdadeira natureza da sua realidade.",
                Set.of(GeneroFilme.FICCAO_CIENTIFICA),
                136,
                "Lana Wachowski, Lilly Wachowski",
                "Warner Bros.",
                LocalDate.of(1999, 3, 31),
                "/imagens/matrix.jpg",
                ClassificacaoIndicativa.DEZESSEIS_ANOS,
                Set.of(FormatoFilme.DUAS_D));


        filmeSemId = new Filme(
                filmeRequestDTO.titulo(),
                filmeRequestDTO.sinopse(),
                filmeRequestDTO.generos(),
                filmeRequestDTO.duracao(),
                filmeRequestDTO.diretor(),
                filmeRequestDTO.distribuidora(),
                filmeRequestDTO.dataLancamento(),
                filmeRequestDTO.imagePath(),
                filmeRequestDTO.classificacaoIndicativa(),
                filmeRequestDTO.formatos());

        filmeSalvo = new Filme(
                filmeRequestDTO.titulo(),
                filmeRequestDTO.sinopse(),
                filmeRequestDTO.generos(),
                filmeRequestDTO.duracao(),
                filmeRequestDTO.diretor(),
                filmeRequestDTO.distribuidora(),
                filmeRequestDTO.dataLancamento(),
                filmeRequestDTO.imagePath(),
                filmeRequestDTO.classificacaoIndicativa(),
                filmeRequestDTO.formatos());
        ReflectionTestUtils.setField(filmeSalvo, "id", 1L);

        filmeResponseDTO = new FilmeResponseDTO(
                1L,
                filmeRequestDTO.titulo(),
                filmeRequestDTO.sinopse(),
                filmeRequestDTO.generos(),
                filmeRequestDTO.duracao(),
                filmeRequestDTO.diretor(),
                filmeRequestDTO.distribuidora(),
                filmeRequestDTO.dataLancamento(),
                filmeRequestDTO.imagePath(),
                filmeRequestDTO.classificacaoIndicativa(),
                filmeRequestDTO.formatos());
    }

    // =========================================================================
    // 1. CADASTRO DE FILME (cadastrar)
    // =========================================================================

    @Test
    @DisplayName("Deve cadastrar um filme com sucesso quando os dados forem válidos")
    void deveCadastrarFilmeComSucesso() {
        // ARRANGE
        when(filmeRepository.existsByTituloIgnoreCaseAndDataLancamento(
                filmeRequestDTO.titulo(), filmeRequestDTO.dataLancamento())).thenReturn(false);
        when(filmeMapper.toEntity(filmeRequestDTO)).thenReturn(filmeSemId);
        when(filmeRepository.save(filmeSemId)).thenReturn(filmeSalvo);
        when(filmeMapper.toDTO(filmeSalvo)).thenReturn(filmeResponseDTO);

        // ACT
        FilmeResponseDTO resultado = filmeService.cadastrar(filmeRequestDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.titulo()).isEqualTo("Matrix");
        assertThat(resultado.classificacaoIndicativa()).isEqualTo(ClassificacaoIndicativa.DEZESSEIS_ANOS);
        assertThat(resultado.formatos()).contains(FormatoFilme.DUAS_D);
        verify(filmeRepository, times(1)).save(filmeSemId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar filme duplicado (mesmo título e data de lançamento)")
    void deveLancarExcecaoAoCadastrarFilmeDuplicado() {
        // ARRANGE
        when(filmeRepository.existsByTituloIgnoreCaseAndDataLancamento(
                filmeRequestDTO.titulo(), filmeRequestDTO.dataLancamento())).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> filmeService.cadastrar(filmeRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Filme já cadastrado no catálogo com este título e ano.");

        verify(filmeRepository, never()).save(any());
    }

    // =========================================================================
    // 2. ATUALIZAÇÃO DE FILME (atualizar)
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar um filme com sucesso quando os dados forem válidos")
    void deveAtualizarFilmeComSucesso() {
        // ARRANGE
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filmeSalvo));
        when(filmeRepository.existsByTituloIgnoreCaseAndDataLancamentoAndIdNot(
                filmeRequestDTO.titulo(), filmeRequestDTO.dataLancamento(), 1L)).thenReturn(false);
        when(sessaoRepository.existsByFilmeIdAndDataHoraFimAfterAndAtivoTrue(eq(1L), any(LocalDateTime.class)))
                .thenReturn(false);
        when(filmeMapper.toDTO(filmeSalvo)).thenReturn(filmeResponseDTO);

        // ACT
        FilmeResponseDTO resultado = filmeService.atualizar(1L, filmeRequestDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        verify(filmeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um filme inexistente")
    void deveLancarExcecaoAoAtualizarFilmeInexistente() {
        // ARRANGE
        when(filmeRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> filmeService.atualizar(1L, filmeRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Filme não encontrado com o ID: 1");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar para título e data que pertencem a outro filme")
    void deveLancarExcecaoAoAtualizarParaTituloEDataJaExistentes() {
        // ARRANGE
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filmeSalvo));
        when(filmeRepository.existsByTituloIgnoreCaseAndDataLancamentoAndIdNot(
                filmeRequestDTO.titulo(), filmeRequestDTO.dataLancamento(), 1L)).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> filmeService.atualizar(1L, filmeRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Já existe outro filme cadastrado com este título e data de lançamento.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar a duração de um filme com sessões futuras agendadas")
    void deveLancarExcecaoAoAlterarDuracaoComSessoesFuturas() {
        // ARRANGE
        FilmeRequestDTO dtoComNovaDuracao = new FilmeRequestDTO(
                "Matrix",
                "Sinopse",
                Set.of(GeneroFilme.FICCAO_CIENTIFICA),
                180,
                "Diretor",
                "Distribuidora",
                LocalDate.of(1999, 3, 31),
                "/img.jpg",
                ClassificacaoIndicativa.DEZESSEIS_ANOS,
                Set.of(FormatoFilme.DUAS_D));

        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filmeSalvo));
        when(filmeRepository.existsByTituloIgnoreCaseAndDataLancamentoAndIdNot(
                dtoComNovaDuracao.titulo(), dtoComNovaDuracao.dataLancamento(), 1L)).thenReturn(false);
        when(sessaoRepository.existsByFilmeIdAndDataHoraFimAfterAndAtivoTrue(eq(1L), any(LocalDateTime.class)))
                .thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> filmeService.atualizar(1L, dtoComNovaDuracao))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Não é possível alterar a duração de um filme que possui sessões futuras agendadas.");
    }

    // =========================================================================
    // 3. INATIVAÇÃO E EXCLUSÃO (inativar, deletar)
    // =========================================================================

    @Test
    @DisplayName("Deve inativar filme com sucesso quando não houver sessões futuras")
    void deveInativarFilmeComSucesso() {
        // ARRANGE
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filmeSalvo));
        when(sessaoRepository.existsByFilmeIdAndDataHoraFimAfterAndAtivoTrue(eq(1L), any(LocalDateTime.class)))
                .thenReturn(false);

        // ACT
        filmeService.inativar(1L);

        // ASSERT
        assertThat(filmeSalvo.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar inativar filme que já está inativo")
    void deveLancarExcecaoAoInativarFilmeJaInativo() {
        // ARRANGE
        filmeSalvo.inativar(); // Uso do próprio método de domínio da entidade para mudar seu estado!
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filmeSalvo));

        // ACT & ASSERT
        assertThatThrownBy(() -> filmeService.inativar(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Este filme já está inativo no catálogo.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar inativar filme com sessões futuras agendadas")
    void deveLancarExcecaoAoInativarFilmeComSessoesFuturas() {
        // ARRANGE
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filmeSalvo));
        when(sessaoRepository.existsByFilmeIdAndDataHoraFimAfterAndAtivoTrue(eq(1L), any(LocalDateTime.class)))
                .thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> filmeService.inativar(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Não é possível inativar o filme pois existem sessões futuras agendadas em cinemas da rede.");
    }

    @Test
    @DisplayName("Deve deletar filme permanentemente quando não possuir nenhuma sessão vinculada")
    void deveDeletarFilmeComSucesso() {
        // ARRANGE
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filmeSalvo));
        when(sessaoRepository.existsByFilmeId(1L)).thenReturn(false);

        // ACT
        filmeService.deletar(1L);

        // ASSERT
        verify(filmeRepository, times(1)).delete(filmeSalvo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar filme que possui sessões vinculadas")
    void deveLancarExcecaoAoDeletarFilmeComSessoes() {
        // ARRANGE
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filmeSalvo));
        when(sessaoRepository.existsByFilmeId(1L)).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> filmeService.deletar(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Não é possível excluir o filme permanentemente pois ele possui sessões vinculadas. Utilize a opção de inativação.");

        verify(filmeRepository, never()).delete(any());
    }

    // =========================================================================
    // 4. CONSULTAS E BUSCAS (listarTodos, buscarPorId, listarParaClientes)
    // =========================================================================

    @Test
    @DisplayName("Deve buscar filme por ID com sucesso")
    void deveBuscarFilmePorIdComSucesso() {
        // ARRANGE
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filmeSalvo));
        when(filmeMapper.toDTO(filmeSalvo)).thenReturn(filmeResponseDTO);

        // ACT
        FilmeResponseDTO resultado = filmeService.buscarPorId(1L);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o filme não for encontrado no buscarPorId")
    void deveLancarExcecaoQuandoFilmeNaoEncontradoPorId() {
        // ARRANGE
        when(filmeRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> filmeService.buscarPorId(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Filme não encontrado de id 1");
    }

    @Test
    @DisplayName("Deve listar todos os filmes de forma paginada")
    void deveListarTodosOsFilmesPaginado() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Filme> pageFilme = new PageImpl<>(List.of(filmeSalvo), pageable, 1);

        when(filmeRepository.findAll(pageable)).thenReturn(pageFilme);
        when(filmeMapper.toDTO(filmeSalvo)).thenReturn(filmeResponseDTO);

        // ACT
        Page<FilmeResponseDTO> resultado = filmeService.listarTodos(pageable);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar filmes filtrados para clientes de forma paginada")
    void deveListarParaClientesComFiltro() {
        // ARRANGE
        FilmeFiltroDTO filtro = new FilmeFiltroDTO(
                "Matrix",
                GeneroFilme.FICCAO_CIENTIFICA,
                "Lana Wachowski, Lilly Wachowski",
                "Warner Bros.",
                LocalDate.of(1999, 3, 31),
                ClassificacaoIndicativa.DEZESSEIS_ANOS,
                FormatoFilme.DUAS_D);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Filme> pageFilme = new PageImpl<>(List.of(filmeSalvo), pageable, 1);

        when(filmeRepository.buscarComFiltrosCliente(filtro, pageable)).thenReturn(pageFilme);
        when(filmeMapper.toDTO(filmeSalvo)).thenReturn(filmeResponseDTO);

        // ACT
        Page<FilmeResponseDTO> resultado = filmeService.listarParaClientes(filtro, pageable);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(filmeRepository, times(1)).buscarComFiltrosCliente(filtro, pageable);
    }

    // =========================================================================
    // 5. OPERAÇÃO EM LOTE (cadastrarEmLote)
    // =========================================================================

    @Test
    @DisplayName("Deve cadastrar uma lista de filmes em lote com sucesso")
    void deveCadastrarFilmesEmLoteComSucesso() {
        // ARRANGE
        List<FilmeRequestDTO> dtos = List.of(filmeRequestDTO);
        List<Filme> entidadesEntrada = List.of(filmeSemId);
        List<Filme> entidadesSalvas = List.of(filmeSalvo);

        when(filmeMapper.toEntity(filmeRequestDTO)).thenReturn(filmeSemId);
        when(filmeRepository.saveAll(entidadesEntrada)).thenReturn(entidadesSalvas);
        when(filmeMapper.toDTO(filmeSalvo)).thenReturn(filmeResponseDTO);

        // ACT
        List<FilmeResponseDTO> resultado = filmeService.cadastrarEmLote(dtos);

        // ASSERT
        assertThat(resultado).isNotNull().hasSize(1);
        verify(filmeRepository, times(1)).saveAll(entidadesEntrada);
    }
}