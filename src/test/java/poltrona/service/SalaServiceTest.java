package poltrona.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.poltrona.PoltronaRequestDTO;
import poltrona.dto.sala.AtualizaSalaRequestDTO;
import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Cliente;
import poltrona.entity.Gerente;
import poltrona.entity.Poltrona;
import poltrona.entity.Proprietario;
import poltrona.entity.Sala;
import poltrona.enums.cinema.StatusCinema;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.SalaMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.IngressoRepository;
import poltrona.repository.SalaRepository;
import poltrona.repository.SessaoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalaService - Testes Unitários")
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private SalaMapper salaMapper;

    @Mock
    private PoltronaService poltronaService;

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private IngressoRepository ingressoRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @InjectMocks
    private SalaService salaService;

    private Proprietario proprietario;
    private Gerente gerente;
    private Cliente clienteComum;

    private Cinema cinemaAtivo;
    private Cinema cinemaInativo;
    private Cinema outroCinema;

    private Sala sala;
    private Sala salaOutroCinema;

    private PoltronaRequestDTO poltronaRequestDTO;
    private PoltronaRequestDTO poltronaRequestDTOAumentar;
    private PoltronaRequestDTO poltronaRequestDTOReduzir;

    private SalaRequestDTO salaRequestDTO;
    private SalaRequestDTO salaRequestDTOCinemaInativo;
    private SalaRequestDTO salaRequestDTOOutroCinema;

    private SalaResponseDTO salaResponseDTO;

    private AtualizaSalaRequestDTO atualizaSalaRequestDTO_NovoNumero;
    private AtualizaSalaRequestDTO atualizaSalaRequestDTO_NumeroExistente;
    private AtualizaSalaRequestDTO atualizaSalaRequestDTO_AumentarCapacidade;
    private AtualizaSalaRequestDTO atualizaSalaRequestDTO_ReduzirCapacidade;

    private Poltrona poltronaA1;
    private Poltrona poltronaA2;
    private Poltrona poltronaNovaA2;

    @BeforeEach
    void setUp() {

        proprietario = new Proprietario(
                "Carlos Silva",
                "carlos@email.com",
                "senha123",
                "12345678901",
                LocalDate.of(1985, 5, 20));
        ReflectionTestUtils.setField(proprietario, "id", 1L);

        clienteComum = new Cliente(
                "Cliente Comum",
                "cliente@email.com",
                "senha123",
                "11122233344",
                LocalDate.of(2000, 1, 1));
        ReflectionTestUtils.setField(clienteComum, "id", 3L);

        cinemaAtivo = new Cinema(
                "Cinema Central",
                "Razao Central",
                "12345678000199",
                "11999999999",
                null,
                proprietario,
                null);
        ReflectionTestUtils.setField(cinemaAtivo, "id", 10L);
        ReflectionTestUtils.setField(
                cinemaAtivo,
                "status",
                StatusCinema.ATIVO);

        cinemaInativo = new Cinema(
                "Cinema Inativo",
                "Razao Inativa",
                "12345678000188",
                "11888888888",
                null,
                proprietario,
                null);
        ReflectionTestUtils.setField(cinemaInativo, "id", 11L);
        ReflectionTestUtils.setField(
                cinemaInativo,
                "status",
                StatusCinema.INATIVO);

        outroCinema = new Cinema(
                "Outro Cinema",
                "Razao Outra",
                "12345678000177",
                "11777777777",
                null,
                proprietario,
                null);
        ReflectionTestUtils.setField(outroCinema, "id", 99L);

        gerente = new Gerente(
                "Gerente Teste",
                "gerente@email.com",
                "senha123",
                "98765432100",
                LocalDate.of(1990, 1, 1),
                cinemaAtivo);
        ReflectionTestUtils.setField(gerente, "id", 2L);

        Map<Character, Integer> fileirasMap = new HashMap<>();
        fileirasMap.put('A', 10);

        poltronaRequestDTO = new PoltronaRequestDTO(fileirasMap);

        sala = new Sala(
                1,
                fileirasMap,
                cinemaAtivo);
        ReflectionTestUtils.setField(sala, "id", 100L);
        ReflectionTestUtils.setField(
                sala,
                "poltronas",
                new ArrayList<>());

        salaOutroCinema = new Sala(
                1,
                Map.of('A', 10),
                outroCinema);
        ReflectionTestUtils.setField(
                salaOutroCinema,
                "id",
                200L);

        salaRequestDTO = new SalaRequestDTO(
                10L,
                1,
                poltronaRequestDTO);

        salaRequestDTOCinemaInativo = new SalaRequestDTO(
                11L,
                1,
                poltronaRequestDTO);

        salaRequestDTOOutroCinema = new SalaRequestDTO(
                99L,
                1,
                poltronaRequestDTO);

        salaResponseDTO = new SalaResponseDTO(
                100L,
                1,
                10,
                Collections.emptyList());

        atualizaSalaRequestDTO_NovoNumero = new AtualizaSalaRequestDTO(2, null);

        atualizaSalaRequestDTO_NumeroExistente = new AtualizaSalaRequestDTO(2, null);

        Map<Character, Integer> fileirasAumentar = new HashMap<>();
        fileirasAumentar.put('A', 2);

        poltronaRequestDTOAumentar = new PoltronaRequestDTO(fileirasAumentar);

        atualizaSalaRequestDTO_AumentarCapacidade = new AtualizaSalaRequestDTO(
                null,
                poltronaRequestDTOAumentar);

        Map<Character, Integer> fileirasReduzir = new HashMap<>();
        fileirasReduzir.put('A', 1);

        poltronaRequestDTOReduzir = new PoltronaRequestDTO(fileirasReduzir);

        atualizaSalaRequestDTO_ReduzirCapacidade = new AtualizaSalaRequestDTO(
                null,
                poltronaRequestDTOReduzir);

        poltronaA1 = new Poltrona(
                'A',
                1,
                sala);

        poltronaA2 = new Poltrona(
                'A',
                2,
                sala);

        poltronaNovaA2 = new Poltrona(
                'A',
                2,
                sala);
    }

    // ==============================================
    // CADASTRAR
    // ==============================================

    @Test
    @DisplayName("Deve cadastrar sala com sucesso quando logado como Proprietario")
    void deveCadastrarSalaComSucessoComoProprietario() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(cinemaRepository.findByIdAndProprietarioId(10L, 1L))
                .thenReturn(Optional.of(cinemaAtivo));

        when(salaRepository.existsByCinemaIdAndNumero(10L, 1))
                .thenReturn(false);

        when(salaMapper.toEntity(salaRequestDTO, cinemaAtivo))
                .thenReturn(sala);

        when(salaRepository.save(sala))
                .thenReturn(sala);

        when(salaMapper.toDTO(sala))
                .thenReturn(salaResponseDTO);

        SalaResponseDTO resultado = salaService.cadastrar(salaRequestDTO);

        assertThat(resultado)
                .isNotNull();

        verify(poltronaService)
                .cadastrar(poltronaRequestDTO, sala);

        verify(salaRepository)
                .save(sala);
    }

    @Test
    @DisplayName("Deve cadastrar sala com sucesso quando logado como Gerente do cinema")
    void deveCadastrarSalaComSucessoComoGerente() {

        when(usuarioService.usuarioLogado())
                .thenReturn(gerente);

        when(salaRepository.existsByCinemaIdAndNumero(10L, 1))
                .thenReturn(false);

        when(salaMapper.toEntity(salaRequestDTO, cinemaAtivo))
                .thenReturn(sala);

        when(salaRepository.save(sala))
                .thenReturn(sala);

        when(salaMapper.toDTO(sala))
                .thenReturn(salaResponseDTO);

        SalaResponseDTO resultado = salaService.cadastrar(salaRequestDTO);

        assertThat(resultado)
                .isNotNull();

        verify(poltronaService)
                .cadastrar(poltronaRequestDTO, sala);

        verify(salaRepository)
                .save(sala);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o cinema não for encontrado para o proprietário")
    void deveLancarExcecaoQuandoCinemaNaoEncontradoParaProprietario() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(cinemaRepository.findByIdAndProprietarioId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaService.cadastrar(salaRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        "Cinema não encontrado ou não pertence a este proprietário");

        verify(salaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando gerente tentar cadastrar sala em cinema que não opera")
    void deveLancarExcecaoQuandoGerenteTentarCadastrarEmOutroCinema() {

        when(usuarioService.usuarioLogado())
                .thenReturn(gerente);

        assertThatThrownBy(() -> salaService.cadastrar(salaRequestDTOOutroCinema))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Você não pode cadastrar salas em um cinema que não opera.");

        verify(salaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando usuário comum tentar cadastrar sala")
    void deveLancarExcecaoQuandoUsuarioSemPermissao() {

        when(usuarioService.usuarioLogado())
                .thenReturn(clienteComum);

        assertThatThrownBy(() -> salaService.cadastrar(salaRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Usuário sem permissão para realizar esta operação.");

        verify(salaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando o cinema estiver inativo")
    void deveLancarExcecaoQuandoCinemaEstiverInativo() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(cinemaRepository.findByIdAndProprietarioId(11L, 1L))
                .thenReturn(Optional.of(cinemaInativo));

        assertThatThrownBy(() -> salaService.cadastrar(salaRequestDTOCinemaInativo))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Não é possível cadastrar salas para um cinema inativo.");

        verify(salaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando o número da sala já existir no cinema")
    void deveLancarExcecaoQuandoNumeroDaSalaJaExistir() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(cinemaRepository.findByIdAndProprietarioId(10L, 1L))
                .thenReturn(Optional.of(cinemaAtivo));

        when(salaRepository.existsByCinemaIdAndNumero(10L, 1))
                .thenReturn(true);

        assertThatThrownBy(() -> salaService.cadastrar(salaRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Esse cinema já possui uma sala com o número 1");

        verify(salaRepository, never())
                .save(any());
    }

    // ==============================================
    // LISTAR SALAS
    // ==============================================

    @Test
    @DisplayName("Deve listar salas paginadas com sucesso sem filtro de cinema")
    void deveListarSalasPaginadasSemFiltroDeCinema() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Sala> paginaSalas = new PageImpl<>(List.of(sala));

        when(salaRepository.buscarSalas(
                null,
                true,
                pageable)).thenReturn(paginaSalas);

        when(salaMapper.toDTO(sala))
                .thenReturn(salaResponseDTO);

        Page<SalaResponseDTO> resultado = salaService.listar(
                null,
                true,
                pageable);

        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.getContent())
                .hasSize(1);

        verify(salaRepository)
                .buscarSalas(
                        null,
                        true,
                        pageable);
    }

    @Test
    @DisplayName("Deve listar salas paginadas com filtro de cinema existente")
    void deveListarSalasPaginadasComFiltroDeCinemaExistente() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Sala> paginaSalas = new PageImpl<>(List.of(sala));

        when(cinemaRepository.existsById(10L))
                .thenReturn(true);

        when(salaRepository.buscarSalas(
                10L,
                true,
                pageable)).thenReturn(paginaSalas);

        when(salaMapper.toDTO(sala))
                .thenReturn(salaResponseDTO);

        Page<SalaResponseDTO> resultado = salaService.listar(
                10L,
                true,
                pageable);

        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.getContent())
                .hasSize(1);

        verify(cinemaRepository)
                .existsById(10L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao listar com id de cinema inexistente")
    void deveLancarExcecaoAoListarComCinemaInexistente() {

        Pageable pageable = PageRequest.of(0, 10);

        when(cinemaRepository.existsById(99L))
                .thenReturn(false);

        assertThatThrownBy(() -> salaService.listar(
                99L,
                true,
                pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Cinema não encontrado com o ID: 99");
    }

    // ==============================================
    // BUSCAR POR ID
    // ==============================================

    @Test
    @DisplayName("Deve buscar sala por id com sucesso como Proprietario")
    void deveBuscarPorIdComSucessoComoProprietario() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(salaRepository.findByIdAndCinemaProprietarioId(
                100L,
                1L)).thenReturn(Optional.of(sala));

        when(salaMapper.toDTO(sala))
                .thenReturn(salaResponseDTO);

        SalaResponseDTO resultado = salaService.buscarPorId(100L);

        assertThat(resultado)
                .isNotNull();

        verify(salaRepository)
                .findByIdAndCinemaProprietarioId(
                        100L,
                        1L);
    }

    @Test
    @DisplayName("Deve buscar sala por id com sucesso como Gerente do mesmo cinema")
    void deveBuscarPorIdComSucessoComoGerente() {

        when(usuarioService.usuarioLogado())
                .thenReturn(gerente);

        when(salaRepository.findById(100L))
                .thenReturn(Optional.of(sala));

        when(salaMapper.toDTO(sala))
                .thenReturn(salaResponseDTO);

        SalaResponseDTO resultado = salaService.buscarPorId(100L);

        assertThat(resultado)
                .isNotNull();

        verify(salaRepository)
                .findById(100L);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando Gerente tentar buscar sala de outro cinema")
    void deveLancarExcecaoQuandoGerenteBuscarSalaDeOutroCinema() {

        when(usuarioService.usuarioLogado())
                .thenReturn(gerente);

        when(salaRepository.findById(200L))
                .thenReturn(Optional.of(salaOutroCinema));

        assertThatThrownBy(() -> salaService.buscarPorId(200L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Você não tem permissão para acessar ou alterar salas de outro cinema.");
    }

    // ==============================================
    // ATUALIZAR
    // ==============================================

    @Test
    @DisplayName("Deve atualizar número da sala com sucesso quando número não estiver em uso")
    void deveAtualizarNumeroDaSalaComSucesso() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(salaRepository.findByIdAndCinemaProprietarioId(
                100L,
                1L)).thenReturn(Optional.of(sala));

        when(salaRepository.existsByCinemaIdAndNumero(
                10L,
                2)).thenReturn(false);

        when(salaRepository.save(sala))
                .thenReturn(sala);

        when(salaMapper.toDTO(sala))
                .thenReturn(salaResponseDTO);

        SalaResponseDTO resultado = salaService.atualizar(
                100L,
                atualizaSalaRequestDTO_NovoNumero);

        assertThat(resultado)
                .isNotNull();

        assertThat(sala.getNumero())
                .isEqualTo(2);

        verify(salaRepository)
                .save(sala);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar atualizar para número de sala já existente no mesmo cinema")
    void deveLancarExcecaoQuandoNovoNumeroJaExiste() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(salaRepository.findByIdAndCinemaProprietarioId(
                100L,
                1L)).thenReturn(Optional.of(sala));

        when(salaRepository.existsByCinemaIdAndNumero(
                10L,
                2)).thenReturn(true);

        assertThatThrownBy(() -> salaService.atualizar(
                100L,
                atualizaSalaRequestDTO_NumeroExistente))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Esse cinema já possui uma sala com o número 2");

        verify(salaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Deve aumentar capacidade das poltronas com sucesso")
    void deveAumentarCapacidadePoltronasComSucesso() {

        sala.getPoltronas().clear();
        sala.getPoltronas().add(poltronaA1);

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(salaRepository.findByIdAndCinemaProprietarioId(
                100L,
                1L)).thenReturn(Optional.of(sala));

        when(poltronaService.aumentarCapacidadeFileira(
                eq(sala),
                eq('A'),
                eq(2),
                anyList())).thenReturn(List.of(poltronaNovaA2));

        when(salaRepository.save(sala))
                .thenReturn(sala);

        when(salaMapper.toDTO(sala))
                .thenReturn(salaResponseDTO);

        SalaResponseDTO resultado = salaService.atualizar(
                100L,
                atualizaSalaRequestDTO_AumentarCapacidade);

        assertThat(resultado)
                .isNotNull();

        assertThat(sala.getPoltronas())
                .contains(poltronaNovaA2);

        verify(poltronaService)
                .aumentarCapacidadeFileira(
                        eq(sala),
                        eq('A'),
                        eq(2),
                        anyList());

        verify(salaRepository)
                .save(sala);
    }

    @Test
    @DisplayName("Deve inativar poltronas excedentes quando nova capacidade for menor que a atual")
    void deveInativarPoltronasExcedentesComSucesso() {

        sala.getPoltronas().clear();

        sala.getPoltronas().addAll(
                List.of(
                        poltronaA1,
                        poltronaA2));

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(salaRepository.findByIdAndCinemaProprietarioId(
                100L,
                1L)).thenReturn(Optional.of(sala));

        when(salaRepository.save(sala))
                .thenReturn(sala);

        when(salaMapper.toDTO(sala))
                .thenReturn(salaResponseDTO);

        SalaResponseDTO resultado = salaService.atualizar(
                100L,
                atualizaSalaRequestDTO_ReduzirCapacidade);

        assertThat(resultado)
                .isNotNull();

        verify(poltronaService)
                .inativarPoltronasExcedentes(
                        List.of(poltronaA2));

        verify(salaRepository)
                .save(sala);
    }

    // ==============================================
    // DESATIVAR
    // ==============================================

    @Test
    @DisplayName("Deve desativar sala com sucesso quando não houver ingressos futuros para a sala")
    void deveDesativarSalaComSucesso() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(salaRepository.findByIdAndCinemaProprietarioId(
                100L,
                1L)).thenReturn(Optional.of(sala));

        when(ingressoRepository
                .existsBySessaoSalaIdAndSessaoDataHoraInicioAfterAndStatus(
                        eq(100L),
                        any(LocalDateTime.class),
                        eq(StatusIngresso.ATIVO)))
                .thenReturn(false);

        salaService.desativar(100L);

        verify(salaRepository)
                .save(sala);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao desativar sala que possui ingressos vendidos para sessões futuras")
    void deveLancarExcecaoAoDesativarSalaComIngressosFuturos() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(salaRepository.findByIdAndCinemaProprietarioId(
                100L,
                1L)).thenReturn(Optional.of(sala));

        when(ingressoRepository
                .existsBySessaoSalaIdAndSessaoDataHoraInicioAfterAndStatus(
                        eq(100L),
                        any(LocalDateTime.class),
                        eq(StatusIngresso.ATIVO)))
                .thenReturn(true);

        assertThatThrownBy(() -> salaService.desativar(100L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Não é possível desativar a sala pois ela possui ingressos vendidos para sessões futuras.");

        verify(salaRepository, never())
                .save(any());
    }

    // ==============================================
    // DELETAR
    // ==============================================

    @Test
    @DisplayName("Deve deletar sala com sucesso quando não houver ingressos relacionados")
    void deveDeletarSalaComSucesso() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(salaRepository.findByIdAndCinemaProprietarioId(
                100L,
                1L)).thenReturn(Optional.of(sala));

        when(sessaoRepository.existsBySalaId(100L))
                .thenReturn(false);

        salaService.deletar(100L);

        verify(salaRepository)
                .delete(sala);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar deletar sala com sessões relacionadas")
    void deveLancarExcecaoAoDeletarSalaComIngressosRelacionados() {

        when(usuarioService.usuarioLogado())
                .thenReturn(proprietario);

        when(salaRepository.findByIdAndCinemaProprietarioId(
                100L,
                1L)).thenReturn(Optional.of(sala));

        when(sessaoRepository.existsBySalaId(100L))
                .thenReturn(true);

        assertThatThrownBy(() -> salaService.deletar(100L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Não é possível deletar a sala pois ela está relacionada a uma sessão.");

        verify(salaRepository, never())
                .delete(any());
    }
}