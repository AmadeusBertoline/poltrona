package poltrona.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import poltrona.dto.page.RespostaPaginadaDTO;
import poltrona.dto.poltrona.MapaPoltronasResponseDTO;
import poltrona.dto.sessao.AtualizaSessaoRequestDTO;
import poltrona.dto.sessao.GradeSessaoRequestDTO;
import poltrona.dto.sessao.SessaoFiltroDTO;
import poltrona.dto.sessao.SessaoRequestDTO;
import poltrona.dto.sessao.SessaoResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Filme;
import poltrona.entity.Gerente;
import poltrona.entity.PoliticaOperacional;
import poltrona.entity.Poltrona;
import poltrona.entity.Preco;
import poltrona.entity.Proprietario;
import poltrona.entity.Sala;
import poltrona.entity.Sessao;
import poltrona.entity.Usuario;
import poltrona.enums.filme.ClassificacaoIndicativa;
import poltrona.enums.filme.FormatoFilme;
import poltrona.enums.filme.GeneroFilme;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.SessaoMapper;
import poltrona.repository.FilmeRepository;
import poltrona.repository.IngressoRepository;
import poltrona.repository.PoltronaRepository;
import poltrona.repository.PrecoRepository;
import poltrona.repository.SalaRepository;
import poltrona.repository.SessaoRepository;

@ExtendWith(MockitoExtension.class)
public class SessaoServiceTest {

        @Mock
        private SessaoRepository sessaoRepository;

        @Mock
        private FilmeRepository filmeRepository;

        @Mock
        private SalaRepository salaRepository;

        @Mock
        private SessaoMapper sessaoMapper;

        @Mock
        private PrecoRepository precoRepository;

        @Mock
        private PoltronaRepository poltronaRepository;

        @Mock
        private IngressoRepository ingressoRepository;

        @Mock
        private UsuarioService usuarioService;

        @Mock
        private SessaoResponseDTO sessaoResponseDTO;

        @Mock
        private Gerente gerenteOutroCinema;

        @Mock
        private Usuario usuarioSemPermissao;

        @Mock
        private Poltrona poltrona1;

        @Mock
        private Poltrona poltrona2;

        @InjectMocks
        private SessaoService sessaoService;

        // Entidades
        private Sala sala;
        private Cinema cinema;
        private Cinema outroCinema;
        private Proprietario proprietario;
        private Proprietario outroProprietario;
        private PoliticaOperacional politicaOperacional;
        private Filme filme;
        private Filme novoFilme;
        private Preco preco;
        private Sessao sessao;

        // DTOs & Auxiliares
        private SessaoFiltroDTO sessaoFiltroDTO;
        private SessaoRequestDTO sessaoRequestDTO;
        private SessaoRequestDTO sessaoRequestDtoFormatoInvalido;
        private SessaoRequestDTO sessaoRequestDtoPassado;
        private SessaoRequestDTO sessaoRequestDtoSalaInexistente;

        private GradeSessaoRequestDTO gradeSessaoRequestDTO;
        private GradeSessaoRequestDTO gradeDtoFilmeInexistente;
        private GradeSessaoRequestDTO gradeDtoPrecoNaoEncontrado;
        private GradeSessaoRequestDTO gradeDtoConflito;

        private AtualizaSessaoRequestDTO atualizaSessaoRequestDTO;
        private AtualizaSessaoRequestDTO atualizaDtoFilmeInexistente;
        private AtualizaSessaoRequestDTO atualizaDtoConflito;

        private Pageable pageable;
        private Page<Sessao> paginaSessao;

        @BeforeEach
        void setUp() {
                LocalDateTime inicioFuturo = LocalDateTime.now().plusDays(1);

                // =====================================================================
                // ENTIDADES
                // =====================================================================

                proprietario = new Proprietario(
                                "Proprietario Teste",
                                "proprietario@email.com",
                                "123456",
                                "12345678901",
                                LocalDate.of(1985, 5, 20));

                ReflectionTestUtils.setField(proprietario, "id", 1L);

                outroProprietario = new Proprietario(
                                "Outro Proprietario",
                                "outro@email.com",
                                "123456",
                                "98765432100",
                                LocalDate.of(1990, 1, 1));

                ReflectionTestUtils.setField(outroProprietario, "id", 2L);

                politicaOperacional = new PoliticaOperacional(15, 30, 15);

                cinema = new Cinema(
                                "Cinema Central",
                                "Cinema Central LTDA",
                                "12345678000199",
                                "11999999999",
                                null,
                                proprietario,
                                politicaOperacional);

                ReflectionTestUtils.setField(cinema, "id", 10L);

                outroCinema = new Cinema(
                                "Outro Cinema",
                                "Outro Cinema LTDA",
                                "98765432000199",
                                "11888888888",
                                null,
                                outroProprietario,
                                politicaOperacional);

                ReflectionTestUtils.setField(outroCinema, "id", 99L);

                sala = new Sala(
                                1,
                                Map.of('A', 10),
                                cinema);

                ReflectionTestUtils.setField(sala, "id", 1L);

                filme = new Filme(
                                "Matrix",
                                "Sinopse do filme Matrix",
                                Set.of(GeneroFilme.ACAO),
                                120,
                                "Wachowski",
                                "Warner",
                                LocalDate.of(1999, 3, 31),
                                "/capas/matrix.jpg",
                                ClassificacaoIndicativa.DOZE_ANOS,
                                Set.of(
                                                FormatoFilme.DUAS_D,
                                                FormatoFilme.TRES_D));

                ReflectionTestUtils.setField(filme, "id", 1L);

                novoFilme = new Filme(
                                "Matrix Reloaded",
                                "Sinopse Matrix Reloaded",
                                Set.of(GeneroFilme.ACAO),
                                138,
                                "Wachowski",
                                "Warner",
                                LocalDate.of(2003, 5, 15),
                                "/capas/reloaded.jpg",
                                ClassificacaoIndicativa.DOZE_ANOS,
                                Set.of(FormatoFilme.TRES_D));

                ReflectionTestUtils.setField(novoFilme, "id", 2L);

                preco = new Preco(
                                FormatoFilme.DUAS_D,
                                BigDecimal.valueOf(30.00),
                                cinema);

                ReflectionTestUtils.setField(preco, "id", 1L);

                sessao = new Sessao(
                                inicioFuturo,
                                filme,
                                sala,
                                FormatoFilme.DUAS_D,
                                preco,
                                politicaOperacional);

                ReflectionTestUtils.setField(sessao, "id", 100L);

                // =====================================================================
                // DTOS
                // =====================================================================

                sessaoFiltroDTO = new SessaoFiltroDTO(
                                1L,
                                LocalDate.now().plusDays(1),
                                1L,
                                true);

                sessaoRequestDTO = new SessaoRequestDTO(
                                inicioFuturo,
                                1L,
                                1L,
                                FormatoFilme.DUAS_D);

                sessaoRequestDtoFormatoInvalido = new SessaoRequestDTO(
                                inicioFuturo,
                                1L,
                                1L,
                                FormatoFilme.IMAX);

                sessaoRequestDtoPassado = new SessaoRequestDTO(
                                LocalDateTime.now().minusHours(1),
                                1L,
                                1L,
                                FormatoFilme.DUAS_D);

                sessaoRequestDtoSalaInexistente = new SessaoRequestDTO(
                                inicioFuturo,
                                1L,
                                99L,
                                FormatoFilme.DUAS_D);

                gradeSessaoRequestDTO = new GradeSessaoRequestDTO(
                                1L,
                                1L,
                                FormatoFilme.DUAS_D,
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                List.of(
                                                LocalTime.of(14, 0),
                                                LocalTime.of(18, 0)));

                gradeDtoFilmeInexistente = new GradeSessaoRequestDTO(
                                99L,
                                1L,
                                FormatoFilme.DUAS_D,
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                List.of(LocalTime.of(14, 0)));

                gradeDtoPrecoNaoEncontrado = new GradeSessaoRequestDTO(
                                1L,
                                1L,
                                FormatoFilme.DUAS_D,
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                List.of(LocalTime.of(14, 0)));

                gradeDtoConflito = new GradeSessaoRequestDTO(
                                1L,
                                1L,
                                FormatoFilme.DUAS_D,
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                List.of(LocalTime.of(14, 0)));

                atualizaSessaoRequestDTO = new AtualizaSessaoRequestDTO(
                                LocalDateTime.now().plusDays(2),
                                2L,
                                1L,
                                BigDecimal.valueOf(30.00),
                                FormatoFilme.TRES_D,
                                15);

                atualizaDtoFilmeInexistente = new AtualizaSessaoRequestDTO(
                                null,
                                99L,
                                null,
                                null,
                                FormatoFilme.DUAS_D,
                                null);

                atualizaDtoConflito = new AtualizaSessaoRequestDTO(
                                LocalDateTime.now().plusDays(2),
                                null,
                                null,
                                null,
                                FormatoFilme.DUAS_D,
                                null);

                pageable = PageRequest.of(0, 10);
                paginaSessao = new PageImpl<>(List.of(sessao));
        }

        // =========================================================================
        // cadastrar
        // =========================================================================

        @Test
        @DisplayName("Deve cadastrar sessão com sucesso quando os dados forem válidos")
        void deveCadastrarSessaoComSucesso() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(sessaoRequestDTO.idSala()))
                                .thenReturn(Optional.of(sala));

                when(filmeRepository.findById(sessaoRequestDTO.idFilme()))
                                .thenReturn(Optional.of(filme));

                when(precoRepository.findByCinemaIdAndFormato(
                                cinema.getId(),
                                sessaoRequestDTO.formato()))
                                .thenReturn(Optional.of(preco));

                when(sessaoMapper.toEntity(
                                sessaoRequestDTO,
                                filme,
                                sala,
                                preco))
                                .thenReturn(sessao);

                when(sessaoRepository.existeConflitoDeHorario(
                                eq(sala.getId()),
                                eq(null),
                                any(),
                                any()))
                                .thenReturn(false);

                when(sessaoRepository.save(sessao))
                                .thenReturn(sessao);

                when(sessaoMapper.toDTO(sessao))
                                .thenReturn(sessaoResponseDTO);

                // ACT
                SessaoResponseDTO resposta = sessaoService.cadastrar(sessaoRequestDTO);

                // ASSERT
                assertThat(resposta)
                                .isNotNull()
                                .isEqualTo(sessaoResponseDTO);

                verify(sessaoRepository, times(1)).save(sessao);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o filme não for encontrado no cadastro")
        void deveLancarExcecaoQuandoFilmeNaoEncontradoAoCadastrar() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(sessaoRequestDTO.idSala()))
                                .thenReturn(Optional.of(sala));

                when(filmeRepository.findById(sessaoRequestDTO.idFilme()))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrar(sessaoRequestDTO))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Filme não encontrado");

                verify(sessaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando o filme não estiver disponível no formato informado")
        void deveLancarExcecaoQuandoFilmeNaoPossuirFormato() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(sessaoRequestDtoFormatoInvalido.idSala()))
                                .thenReturn(Optional.of(sala));

                when(filmeRepository.findById(sessaoRequestDtoFormatoInvalido.idFilme()))
                                .thenReturn(Optional.of(filme));

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrar(sessaoRequestDtoFormatoInvalido))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage("O filme 'Matrix' não está disponível no formato IMAX");

                verify(sessaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando o cinema não tiver preço cadastrado para o formato")
        void deveLancarExcecaoQuandoPrecoNaoCadastrado() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(sessaoRequestDTO.idSala()))
                                .thenReturn(Optional.of(sala));

                when(filmeRepository.findById(sessaoRequestDTO.idFilme()))
                                .thenReturn(Optional.of(filme));

                when(precoRepository.findByCinemaIdAndFormato(
                                cinema.getId(),
                                sessaoRequestDTO.formato()))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrar(sessaoRequestDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "O cinema não possui um preço cadastrado para o formato "
                                                                + sessaoRequestDTO.formato());

                verify(sessaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando a data/horário de início for no passado")
        void deveLancarExcecaoQuandoDataHoraInicioForNoPassado() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(sessaoRequestDtoPassado.idSala()))
                                .thenReturn(Optional.of(sala));

                when(filmeRepository.findById(sessaoRequestDtoPassado.idFilme()))
                                .thenReturn(Optional.of(filme));

                when(precoRepository.findByCinemaIdAndFormato(
                                cinema.getId(),
                                sessaoRequestDtoPassado.formato()))
                                .thenReturn(Optional.of(preco));

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrar(sessaoRequestDtoPassado))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage("A data e horário da sessão devem ser no futuro");

                verify(sessaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando houver conflito de horário no cadastro")
        void deveLancarExcecaoQuandoHouverConflitoDeHorario() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(sessaoRequestDTO.idSala()))
                                .thenReturn(Optional.of(sala));

                when(filmeRepository.findById(sessaoRequestDTO.idFilme()))
                                .thenReturn(Optional.of(filme));

                when(precoRepository.findByCinemaIdAndFormato(
                                cinema.getId(),
                                sessaoRequestDTO.formato()))
                                .thenReturn(Optional.of(preco));

                when(sessaoMapper.toEntity(
                                sessaoRequestDTO,
                                filme,
                                sala,
                                preco))
                                .thenReturn(sessao);

                when(sessaoRepository.existeConflitoDeHorario(
                                eq(sala.getId()),
                                eq(null),
                                any(),
                                any()))
                                .thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrar(sessaoRequestDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "O horário da sessão cadastrada está em conflito com outra sessão nesta sala");

                verify(sessaoRepository, never()).save(any());
        }

        // =========================================================================
        // listar
        // =========================================================================

        @Test
        @DisplayName("Deve listar sessões paginadas com filtros")
        void deveListarSessoesComFiltroEPaginacao() {
                // ARRANGE
                when(sessaoRepository.buscarComFiltros(
                                sessaoFiltroDTO,
                                pageable))
                                .thenReturn(paginaSessao);

                when(sessaoMapper.toDTO(sessao))
                                .thenReturn(sessaoResponseDTO);

                // ACT
                RespostaPaginadaDTO<SessaoResponseDTO> resposta = sessaoService.listar(sessaoFiltroDTO, pageable);

                // ASSERT
                assertThat(resposta).isNotNull();
                assertThat(resposta.conteudo()).hasSize(1);
                assertThat(resposta.conteudo().get(0))
                                .isEqualTo(sessaoResponseDTO);

                verify(sessaoRepository, times(1))
                                .buscarComFiltros(sessaoFiltroDTO, pageable);
        }

        // =========================================================================
        // buscarPorId
        // =========================================================================

        @Test
        @DisplayName("Deve buscar sessão por id com sucesso")
        void deveBuscarSessaoPorIdComSucesso() {
                // ARRANGE
                when(sessaoRepository.findById(100L))
                                .thenReturn(Optional.of(sessao));

                when(sessaoMapper.toDTO(sessao))
                                .thenReturn(sessaoResponseDTO);

                // ACT
                SessaoResponseDTO resposta = sessaoService.buscarPorId(100L);

                // ASSERT
                assertThat(resposta)
                                .isNotNull()
                                .isEqualTo(sessaoResponseDTO);

                verify(sessaoRepository, times(1))
                                .findById(100L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a sessão não for encontrada por id")
        void deveLancarExcecaoQuandoSessaoNaoEncontrada() {
                // ARRANGE
                when(sessaoRepository.findById(999L))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.buscarPorId(999L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Sessão não encontrada de id 999");
        }

        // =========================================================================
        // deletar
        // =========================================================================

        @Test
        @DisplayName("Deve deletar sessão com sucesso")
        void deveDeletarSessaoComSucesso() {
                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(sessaoRepository.findByIdWithLock(100L))
                                .thenReturn(Optional.of(sessao));

                when(ingressoRepository.countBySessaoId(100L))
                                .thenReturn(0L);

                // ACT
                sessaoService.deletar(100L);

                // ASSERT
                verify(sessaoRepository, times(1))
                                .delete(sessao);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar sessão inexistente")
        void deveLancarExcecaoQuandoSessaoNaoEncontradaAoDeletar() {
                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(sessaoRepository.findByIdWithLock(999L))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.deletar(999L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Sessão não encontrada de id 999");

                verify(sessaoRepository, never()).delete(any());
        }

        // =========================================================================
        // obterMapaPoltronas
        // =========================================================================

        @Test
        @DisplayName("Deve obter mapa de poltronas apontando corretamente livres e ocupadas")
        void deveObterMapaPoltronasComSucesso() {
                // ARRANGE
                when(poltrona1.getId()).thenReturn(101L);
                when(poltrona1.getNumero()).thenReturn("A1");

                when(poltrona2.getId()).thenReturn(102L);
                when(poltrona2.getNumero()).thenReturn("A2");

                when(sessaoRepository.findById(100L))
                                .thenReturn(Optional.of(sessao));

                when(poltronaRepository.findBySalaId(sala.getId()))
                                .thenReturn(List.of(poltrona1, poltrona2));

                when(ingressoRepository.findPoltronaIdsBySessaoId(100L))
                                .thenReturn(Set.of(101L));

                // ACT
                MapaPoltronasResponseDTO mapa = sessaoService.obterMapaPoltronas(100L);

                // ASSERT
                assertThat(mapa).isNotNull();
                assertThat(mapa.sessaoId()).isEqualTo(100L);
                assertThat(mapa.salaId()).isEqualTo(sala.getId());
                assertThat(mapa.poltronas()).hasSize(2);
                assertThat(mapa.poltronas().get(0).ocupada()).isTrue();
                assertThat(mapa.poltronas().get(1).ocupada()).isFalse();

                verify(poltronaRepository, times(1))
                                .findBySalaId(sala.getId());

                verify(ingressoRepository, times(1))
                                .findPoltronaIdsBySessaoId(100L);
        }

        // =========================================================================
        // cadastrarGrade
        // =========================================================================

        @Test
        @DisplayName("Deve cadastrar grade de sessões com sucesso")
        void deveCadastrarGradeComSucesso() {
                // ARRANGE
                when(filmeRepository.findById(gradeSessaoRequestDTO.filmeId()))
                                .thenReturn(Optional.of(filme));

                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(gradeSessaoRequestDTO.salaId()))
                                .thenReturn(Optional.of(sala));

                when(precoRepository.findByCinemaIdAndFormato(
                                cinema.getId(),
                                gradeSessaoRequestDTO.formato()))
                                .thenReturn(Optional.of(preco));

                when(sessaoRepository.existeConflitoDeHorario(
                                eq(sala.getId()),
                                eq(null),
                                any(),
                                any()))
                                .thenReturn(false);

                when(sessaoRepository.saveAll(any()))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                when(sessaoMapper.toDTO(any()))
                                .thenReturn(sessaoResponseDTO);

                // ACT
                List<SessaoResponseDTO> resultado = sessaoService.cadastrarGrade(gradeSessaoRequestDTO);

                // ASSERT
                assertThat(resultado)
                                .isNotNull()
                                .hasSize(2);

                verify(sessaoRepository, times(1))
                                .saveAll(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao cadastrar grade se filme não for encontrado")
        void deveLancarExcecaoQuandoFilmeNaoEncontradoAoCadastrarGrade() {
                // ARRANGE
                when(filmeRepository.findById(gradeDtoFilmeInexistente.filmeId()))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrarGrade(gradeDtoFilmeInexistente))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Filme não encontrado");

                verify(sessaoRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException ao cadastrar grade se cinema não tiver preço cadastrado")
        void deveLancarExcecaoQuandoPrecoNaoEncontradoAoCadastrarGrade() {
                // ARRANGE
                when(filmeRepository.findById(gradeDtoPrecoNaoEncontrado.filmeId()))
                                .thenReturn(Optional.of(filme));

                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(gradeDtoPrecoNaoEncontrado.salaId()))
                                .thenReturn(Optional.of(sala));

                when(precoRepository.findByCinemaIdAndFormato(
                                cinema.getId(),
                                gradeDtoPrecoNaoEncontrado.formato()))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrarGrade(gradeDtoPrecoNaoEncontrado))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Cinema sem preço cadastrado para o formato "
                                                                + gradeDtoPrecoNaoEncontrado.formato());

                verify(sessaoRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException ao cadastrar grade quando houver conflito de horário no banco")
        void deveLancarExcecaoQuandoHouverConflitoNoBancoAoCadastrarGrade() {
                // ARRANGE
                when(filmeRepository.findById(gradeDtoConflito.filmeId()))
                                .thenReturn(Optional.of(filme));

                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(gradeDtoConflito.salaId()))
                                .thenReturn(Optional.of(sala));

                when(precoRepository.findByCinemaIdAndFormato(
                                cinema.getId(),
                                gradeDtoConflito.formato()))
                                .thenReturn(Optional.of(preco));

                when(sessaoRepository.existeConflitoDeHorario(
                                eq(sala.getId()),
                                eq(null),
                                any(),
                                any()))
                                .thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrarGrade(gradeDtoConflito))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessageContaining("Conflito de horário na sala");

                verify(sessaoRepository, never()).saveAll(any());
        }

        // =========================================================================
        // atualizar
        // =========================================================================

        @Test
        @DisplayName("Deve atualizar sessão com sucesso")
        void deveAtualizarSessaoComSucesso() {
                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(sessaoRepository.findByIdWithLock(100L))
                                .thenReturn(Optional.of(sessao));

                when(ingressoRepository.countBySessaoId(100L))
                                .thenReturn(0L);

                when(filmeRepository.findById(atualizaSessaoRequestDTO.filmeId()))
                                .thenReturn(Optional.of(novoFilme));

                when(salaRepository.findByIdWithLock(atualizaSessaoRequestDTO.salaId()))
                                .thenReturn(Optional.of(sala));

                when(sessaoRepository.existeConflitoDeHorario(
                                eq(sala.getId()),
                                eq(100L),
                                any(),
                                any()))
                                .thenReturn(false);

                when(sessaoMapper.toDTO(sessao))
                                .thenReturn(sessaoResponseDTO);

                // ACT
                SessaoResponseDTO resposta = sessaoService.atualizar(100L, atualizaSessaoRequestDTO);

                // ASSERT
                assertThat(resposta)
                                .isNotNull()
                                .isEqualTo(sessaoResponseDTO);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar com filme inexistente")
        void deveLancarExcecaoQuandoFilmeNaoEncontradoAoAtualizar() {
                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(sessaoRepository.findByIdWithLock(100L))
                                .thenReturn(Optional.of(sessao));

                when(ingressoRepository.countBySessaoId(100L))
                                .thenReturn(0L);

                when(filmeRepository.findById(atualizaDtoFilmeInexistente.filmeId()))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.atualizar(100L, atualizaDtoFilmeInexistente))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Filme não encontrado.");
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException ao atualizar se houver conflito de horário")
        void deveLancarExcecaoQuandoHouverConflitoDeHorarioAoAtualizar() {
                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(sessaoRepository.findByIdWithLock(100L))
                                .thenReturn(Optional.of(sessao));

                when(ingressoRepository.countBySessaoId(100L))
                                .thenReturn(0L);

                when(sessaoRepository.existeConflitoDeHorario(
                                eq(sala.getId()),
                                eq(100L),
                                any(),
                                any()))
                                .thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.atualizar(100L, atualizaDtoConflito))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "O horário da sessão cadastrada está em conflito com outra sessão nesta sala");
        }

        // =========================================================================
        // buscarSalaEValidarAcesso
        // =========================================================================

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a sala não for encontrada")
        void deveLancarExcecaoQuandoSalaNaoEncontrada() {
                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(salaRepository.findByIdWithLock(99L))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrar(sessaoRequestDtoSalaInexistente))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Sala não encontrada");
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando proprietário tentar acessar sala de outro proprietário")
        void deveLancarExcecaoQuandoProprietarioNaoTiverPermissaoNaSala() {
                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(outroProprietario);

                when(salaRepository.findByIdWithLock(1L))
                                .thenReturn(Optional.of(sala));

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrar(sessaoRequestDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Você não tem permissão para acessar salas de um cinema que não lhe pertence.");
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando gerente tentar acessar sala de outro cinema")
        void deveLancarExcecaoQuandoGerenteNaoTiverPermissaoNaSala() {
                // ARRANGE
                when(gerenteOutroCinema.getCinema())
                                .thenReturn(outroCinema);

                when(usuarioService.usuarioLogado())
                                .thenReturn(gerenteOutroCinema);

                when(salaRepository.findByIdWithLock(1L))
                                .thenReturn(Optional.of(sala));

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.cadastrar(sessaoRequestDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Você não tem permissão para acessar salas de um cinema que não opera.");
        }

        // =========================================================================
        // buscarSessaoEValidarAcesso
        // =========================================================================

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando proprietário tentar acessar sessão de outro proprietário")
        void deveLancarExcecaoQuandoProprietarioNaoTiverPermissaoNaSessaoAoDeletar() {
                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(outroProprietario);

                when(sessaoRepository.findByIdWithLock(100L))
                                .thenReturn(Optional.of(sessao));

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.deletar(100L))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Você não tem permissão para acessar ou alterar sessões de outro proprietário.");

                verify(ingressoRepository, never()).countBySessaoId(any());
                verify(sessaoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando gerente tentar acessar sessão de outro cinema")
        void deveLancarExcecaoQuandoGerenteNaoTiverPermissaoNaSessaoAoDeletar() {
                // ARRANGE
                when(gerenteOutroCinema.getCinema())
                                .thenReturn(outroCinema);

                when(usuarioService.usuarioLogado())
                                .thenReturn(gerenteOutroCinema);

                when(sessaoRepository.findByIdWithLock(100L))
                                .thenReturn(Optional.of(sessao));

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.deletar(100L))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Você não tem permissão para acessar ou alterar sessões de outro cinema.");

                verify(ingressoRepository, never()).countBySessaoId(any());
                verify(sessaoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Deve lançar RegraNegocioException quando usuário não tiver permissão para acessar sessão")
        void deveLancarExcecaoQuandoUsuarioSemPermissaoAoDeletar() {
                // ARRANGE
                when(usuarioService.usuarioLogado())
                                .thenReturn(usuarioSemPermissao);

                when(sessaoRepository.findByIdWithLock(100L))
                                .thenReturn(Optional.of(sessao));

                // ACT & ASSERT
                assertThatThrownBy(() -> sessaoService.deletar(100L))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage("Usuário sem permissão para realizar esta operação.");

                verify(ingressoRepository, never()).countBySessaoId(any());
                verify(sessaoRepository, never()).delete(any());
        }
}