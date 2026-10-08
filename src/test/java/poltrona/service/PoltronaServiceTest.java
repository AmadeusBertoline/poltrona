package poltrona.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.poltrona.PoltronaRequestDTO;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.dto.poltrona.TipoPoltronaRequestDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Gerente;
import poltrona.entity.Poltrona;
import poltrona.entity.Proprietario;
import poltrona.entity.Sala;
import poltrona.entity.Usuario;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.enums.poltrona.TipoPoltrona;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.PoltronaMapper;
import poltrona.repository.IngressoRepository;
import poltrona.repository.PoltronaRepository;
import poltrona.repository.SalaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PoltronaService - Testes Unitários")
class PoltronaServiceTest {

        @Mock
        private PoltronaRepository poltronaRepository;

        @Mock
        private PoltronaMapper poltronaMapper;

        @Mock
        private IngressoRepository ingressoRepository;

        @Mock
        private UsuarioService usuarioService;

        @Mock
        private SalaRepository salaRepository;

        @InjectMocks
        private PoltronaService poltronaService;

        private Sala sala;

        private Proprietario proprietario;
        private Proprietario outroProprietario;

        private Cinema cinema;
        private Cinema outroCinema;

        private Gerente gerente;
        private Gerente outroGerente;

        private Usuario usuarioComum;

        private Poltrona poltronaAtiva;
        private Poltrona poltronaInativa;

        private PoltronaResponseDTO poltronaResponseDTO;

        private TipoPoltronaRequestDTO tipoPoltronaVipDTO;
        private TipoPoltronaRequestDTO tipoPoltronaComumDTO;

        private PoltronaRequestDTO poltronaRequestDTO;

        private List<Poltrona> listaPoltronasExcedentes;
        private List<Poltrona> listaPoltronasExistentes;

        @BeforeEach
        void setUp() {

                proprietario = new Proprietario(
                                "Proprietario Teste",
                                "proprietario@email.com",
                                "senha123",
                                "11111111111",
                                LocalDate.of(1980, 1, 1));
                ReflectionTestUtils.setField(proprietario, "id", 10L);

                outroProprietario = new Proprietario(
                                "Outro Proprietario",
                                "outro.prop@email.com",
                                "senha123",
                                "22222222222",
                                LocalDate.of(1985, 2, 2));
                ReflectionTestUtils.setField(outroProprietario, "id", 99L);

                cinema = new Cinema(
                                "Cine Top",
                                "Cine Top LTDA",
                                "12345678000199",
                                "11999999999",
                                null,
                                proprietario,
                                null);
                ReflectionTestUtils.setField(cinema, "id", 100L);

                outroCinema = new Cinema(
                                "Outro Cine",
                                "Outro Cine LTDA",
                                "98765432000188",
                                "11888888888",
                                null,
                                outroProprietario,
                                null);
                ReflectionTestUtils.setField(outroCinema, "id", 999L);

                gerente = new Gerente(
                                "Gerente Teste",
                                "gerente@email.com",
                                "senha123",
                                "33333333333",
                                LocalDate.of(1990, 3, 3),
                                cinema);
                ReflectionTestUtils.setField(gerente, "id", 20L);

                outroGerente = new Gerente(
                                "Outro Gerente",
                                "outro.gerente@email.com",
                                "senha123",
                                "44444444444",
                                LocalDate.of(1992, 4, 4),
                                outroCinema);
                ReflectionTestUtils.setField(outroGerente, "id", 30L);

                usuarioComum = new Usuario(
                                "Usuario Comum",
                                "comum@email.com",
                                "senha123",
                                "55555555555",
                                LocalDate.of(2000, 5, 5)) {
                };
                ReflectionTestUtils.setField(usuarioComum, "id", 50L);

                Map<Character, Integer> capacidade = Map.of(
                                'A', 10,
                                'B', 10,
                                'C', 8);

                sala = new Sala(
                                1,
                                capacidade,
                                cinema);
                ReflectionTestUtils.setField(sala, "id", 1L);

                poltronaAtiva = new Poltrona(
                                'A',
                                1,
                                sala);
                ReflectionTestUtils.setField(poltronaAtiva, "id", 1000L);

                poltronaInativa = new Poltrona(
                                'A',
                                2,
                                sala);
                ReflectionTestUtils.setField(poltronaInativa, "id", 1001L);
                poltronaInativa.desativar();

                poltronaResponseDTO = new PoltronaResponseDTO(
                                1000L,
                                'A',
                                1,
                                TipoPoltrona.COMUM,
                                1L);

                tipoPoltronaVipDTO = new TipoPoltronaRequestDTO(
                                TipoPoltrona.VIP);

                tipoPoltronaComumDTO = new TipoPoltronaRequestDTO(
                                TipoPoltrona.COMUM);

                poltronaRequestDTO = new PoltronaRequestDTO(
                                Map.of('A', 2));

                listaPoltronasExcedentes = List.of(
                                poltronaAtiva);

                listaPoltronasExistentes = List.of(
                                poltronaInativa);
        }

        // ==============================================
        // Cadastro de Poltronas
        // ==============================================

        @Test
        @DisplayName("Deve cadastrar poltronas com sucesso para as fileiras solicitadas")
        void deveCadastrarPoltronasComSucesso() {

                // Arrange
                when(poltronaRepository.saveAll(anyList()))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                when(poltronaMapper.toDTO(any(Poltrona.class)))
                                .thenReturn(poltronaResponseDTO);

                // Act
                List<PoltronaResponseDTO> resultado = poltronaService.cadastrar(poltronaRequestDTO, sala);

                // Assert
                assertThat(resultado)
                                .isNotNull()
                                .hasSize(2);

                verify(poltronaRepository)
                                .saveAll(anyList());

                verify(poltronaMapper, times(2))
                                .toDTO(any(Poltrona.class));
        }

        // ==============================================
        // Busca de Poltronas
        // ==============================================

        @Test
        @DisplayName("Deve buscar poltrona por ID com sucesso")
        void deveBuscarPoltronaPorIdComSucesso() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(poltronaMapper.toDTO(poltronaAtiva))
                                .thenReturn(poltronaResponseDTO);

                // Act
                PoltronaResponseDTO resultado = poltronaService.buscarPorId(1000L);

                // Assert
                assertThat(resultado)
                                .isNotNull();

                assertThat(resultado.id())
                                .isEqualTo(1000L);

                verify(poltronaRepository)
                                .findById(1000L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar poltrona por ID inexistente")
        void deveLancarExcecaoQuandoPoltronaNaoEncontrada() {

                // Arrange
                when(poltronaRepository.findById(999L))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.buscarPorId(999L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Poltrona não encontrada de id 999");
        }

        // ==============================================
        // Atualização de Tipo
        // ==============================================

        @Test
        @DisplayName("Deve atualizar o tipo da poltrona com sucesso quando autorizado e sem ingressos futuros")
        void deveAtualizarTipoPoltronaComSucesso() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(ingressoRepository.existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                                eq(1000L),
                                any(LocalDateTime.class),
                                eq(StatusIngresso.ATIVO))).thenReturn(false);

                when(poltronaRepository.save(poltronaAtiva))
                                .thenReturn(poltronaAtiva);

                when(poltronaMapper.toDTO(poltronaAtiva))
                                .thenReturn(poltronaResponseDTO);

                // Act
                PoltronaResponseDTO resultado = poltronaService.atualizarTipo(
                                1000L,
                                tipoPoltronaVipDTO);

                // Assert
                assertThat(resultado)
                                .isNotNull();

                assertThat(poltronaAtiva.getTipo())
                                .isEqualTo(TipoPoltrona.VIP);

                verify(poltronaRepository)
                                .save(poltronaAtiva);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar tipo se a poltrona já tiver o mesmo tipo")
        void deveLancarExcecaoQuandoTipoForIgual() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.atualizarTipo(
                                1000L,
                                tipoPoltronaComumDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage("A poltrona já está cadastrada com este tipo.");

                verify(poltronaRepository, never())
                                .save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar tipo se houver ingressos vendidos para sessões futuras")
        void deveLancarExcecaoAoAtualizarTipoComIngressosFuturos() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(ingressoRepository.existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                                eq(1000L),
                                any(LocalDateTime.class),
                                eq(StatusIngresso.ATIVO))).thenReturn(true);

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.atualizarTipo(
                                1000L,
                                tipoPoltronaVipDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Esta poltrona possui ingressos vendidos para sessões futuras e não pode ter seu tipo alterado.");

                verify(poltronaRepository, never())
                                .save(any());
        }

        // ==============================================
        // Gestão de Status (Ativar / Desativar)
        // ==============================================

        @Test
        @DisplayName("Deve desativar poltrona com sucesso quando estiver ativa e sem ingressos futuros")
        void deveDesativarPoltronaComSucesso() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(ingressoRepository.existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                                eq(1000L),
                                any(LocalDateTime.class),
                                eq(StatusIngresso.ATIVO))).thenReturn(false);

                // Act
                poltronaService.desativar(1000L);

                // Assert
                assertThat(poltronaAtiva.getAtiva())
                                .isFalse();

                verify(poltronaRepository)
                                .save(poltronaAtiva);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar desativar poltrona que já está inativa")
        void deveLancarExcecaoAoDesativarPoltronaJaInativa() {

                // Arrange
                when(poltronaRepository.findById(1001L))
                                .thenReturn(Optional.of(poltronaInativa));

                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.desativar(1001L))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Esta poltrona já está inativa, id: 1001");

                verify(poltronaRepository, never())
                                .save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao desativar poltrona com ingressos futuros")
        void deveLancarExcecaoAoDesativarPoltronaComIngressosFuturos() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(usuarioService.usuarioLogado())
                                .thenReturn(proprietario);

                when(ingressoRepository.existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                                eq(1000L),
                                any(LocalDateTime.class),
                                eq(StatusIngresso.ATIVO))).thenReturn(true);

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.desativar(1000L))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage(
                                                "Esta poltrona possui ingressos vendidos para sessões futuras e não pode ser desativada.");

                verify(poltronaRepository, never())
                                .save(any());
        }

        @Test
        @DisplayName("Deve alterar status para inativo com sucesso quando não houver ingressos futuros")
        void deveAlterarStatusParaInativoComSucesso() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(usuarioService.usuarioLogado())
                                .thenReturn(gerente);

                when(ingressoRepository.existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                                eq(1000L),
                                any(LocalDateTime.class),
                                eq(StatusIngresso.ATIVO))).thenReturn(false);

                when(poltronaRepository.save(poltronaAtiva))
                                .thenReturn(poltronaAtiva);

                when(poltronaMapper.toDTO(poltronaAtiva))
                                .thenReturn(poltronaResponseDTO);

                // Act
                PoltronaResponseDTO resultado = poltronaService.alterarStatus(1000L, false);

                // Assert
                assertThat(resultado)
                                .isNotNull();

                assertThat(poltronaAtiva.getAtiva())
                                .isFalse();

                verify(poltronaRepository)
                                .save(poltronaAtiva);
        }

        @Test
        @DisplayName("Deve ativar poltrona sem validar ingressos futuros")
        void deveAtivarPoltronaComSucesso() {

                // Arrange
                when(poltronaRepository.findById(1001L))
                                .thenReturn(Optional.of(poltronaInativa));

                when(usuarioService.usuarioLogado())
                                .thenReturn(gerente);

                when(poltronaRepository.save(poltronaInativa))
                                .thenReturn(poltronaInativa);

                when(poltronaMapper.toDTO(poltronaInativa))
                                .thenReturn(poltronaResponseDTO);

                // Act
                PoltronaResponseDTO resultado = poltronaService.alterarStatus(1001L, true);

                // Assert
                assertThat(resultado)
                                .isNotNull();

                assertThat(poltronaInativa.getAtiva())
                                .isTrue();

                verify(
                                ingressoRepository,
                                never()).existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                                                any(),
                                                any(),
                                                any());

                verify(poltronaRepository)
                                .save(poltronaInativa);
        }

        // ==============================================
        // Gerenciamento de Fileiras e Capacidade
        // ==============================================

        @Test
        @DisplayName("Deve criar poltronas para uma nova fileira")
        void deveCriarPoltronasParaFileira() {

                // Arrange
                when(poltronaRepository.saveAll(anyList()))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                List<Poltrona> criadas = poltronaService.criarPoltronasParaFileira(
                                sala,
                                'B',
                                1,
                                3);

                // Assert
                assertThat(criadas)
                                .hasSize(3)
                                .extracting(Poltrona::getFileira)
                                .containsOnly('B');

                verify(poltronaRepository)
                                .saveAll(anyList());
        }

        @Test
        @DisplayName("Deve aumentar capacidade da fileira reativando inativas e criando novas")
        void deveAumentarCapacidadeFileira() {

                // Arrange
                when(poltronaRepository.saveAll(anyList()))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                List<Poltrona> resultado = poltronaService.aumentarCapacidadeFileira(
                                sala,
                                'A',
                                3,
                                listaPoltronasExistentes);

                // Assert
                assertThat(resultado)
                                .hasSize(3);

                assertThat(poltronaInativa.getAtiva())
                                .isTrue();

                verify(poltronaRepository)
                                .saveAll(anyList());
        }

        @Test
        @DisplayName("Deve inativar poltronas excedentes com sucesso quando sem ingressos futuros")
        void deveInativarPoltronasExcedentesComSucesso() {

                // Arrange
                when(ingressoRepository.existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                                eq(1000L),
                                any(LocalDateTime.class),
                                eq(StatusIngresso.ATIVO))).thenReturn(false);

                // Act
                poltronaService.inativarPoltronasExcedentes(
                                listaPoltronasExcedentes);

                // Assert
                assertThat(poltronaAtiva.getAtiva())
                                .isFalse();

                verify(poltronaRepository)
                                .saveAll(listaPoltronasExcedentes);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar inativar poltronas excedentes com ingressos futuros")
        void deveLancarExcecaoAoInativarExcedentesComIngressoFuturo() {

                // Arrange
                when(ingressoRepository.existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                                eq(1000L),
                                any(LocalDateTime.class),
                                eq(StatusIngresso.ATIVO))).thenReturn(true);

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.inativarPoltronasExcedentes(
                                listaPoltronasExcedentes))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessageContaining(
                                                "possui ingressos vendidos para sessões futuras e não pode ser desativada.");

                verify(
                                poltronaRepository,
                                never()).saveAll(any());
        }

        // ==============================================
        // Listagem e Consultas
        // ==============================================

        @Test
        @DisplayName("Deve listar poltronas por sala com sucesso")
        void deveListarPorSalaComSucesso() {

                // Arrange
                when(salaRepository.existsById(1L))
                                .thenReturn(true);

                when(poltronaRepository.findBySalaId(1L))
                                .thenReturn(List.of(poltronaAtiva));

                when(poltronaMapper.toDTO(poltronaAtiva))
                                .thenReturn(poltronaResponseDTO);

                // Act
                List<PoltronaResponseDTO> resultado = poltronaService.listarPorSala(1L);

                // Assert
                assertThat(resultado)
                                .isNotNull()
                                .hasSize(1);

                verify(salaRepository)
                                .existsById(1L);

                verify(poltronaRepository)
                                .findBySalaId(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao listar poltronas para sala inexistente")
        void deveLancarExcecaoAoListarPorSalaInexistente() {

                // Arrange
                when(salaRepository.existsById(99L))
                                .thenReturn(false);

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.listarPorSala(99L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage(
                                                "Sala não encontrada com o ID: 99");

                verify(
                                poltronaRepository,
                                never()).findBySalaId(any());
        }

        // ==============================================
        // Validação de Permissões
        // ==============================================

        @Test
        @DisplayName("Deve lançar AccessDeniedException quando Proprietário tentar alterar poltrona de outro cinema")
        void deveLancarExcecaoParaProprietarioDeOutroCinema() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(usuarioService.usuarioLogado())
                                .thenReturn(outroProprietario);

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.desativar(1000L))
                                .isInstanceOf(AccessDeniedException.class)
                                .hasMessage(
                                                "Você não tem permissão para alterar poltronas deste cinema.");
        }

        @Test
        @DisplayName("Deve lançar AccessDeniedException quando Gerente tentar alterar poltrona de outro cinema")
        void deveLancarExcecaoParaGerenteDeOutroCinema() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(usuarioService.usuarioLogado())
                                .thenReturn(outroGerente);

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.desativar(1000L))
                                .isInstanceOf(AccessDeniedException.class)
                                .hasMessage(
                                                "Você não tem permissão para alterar poltronas de outro cinema.");
        }

        @Test
        @DisplayName("Deve lançar AccessDeniedException para tipo de usuário não autorizado")
        void deveLancarExcecaoParaUsuarioNaoAutorizado() {

                // Arrange
                when(poltronaRepository.findById(1000L))
                                .thenReturn(Optional.of(poltronaAtiva));

                when(usuarioService.usuarioLogado())
                                .thenReturn(usuarioComum);

                // Act & Assert
                assertThatThrownBy(() -> poltronaService.desativar(1000L))
                                .isInstanceOf(AccessDeniedException.class)
                                .hasMessage(
                                                "Usuário sem permissão para realizar esta operação.");
        }
}