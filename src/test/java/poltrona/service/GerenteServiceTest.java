package poltrona.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import poltrona.dto.gerente.AtualizaGerenteRequestDTO;
import poltrona.dto.gerente.GerenteRequestDTO;
import poltrona.dto.gerente.GerenteResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.dto.usuario.UsuarioRequestDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Gerente;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.GerenteMapper;
import poltrona.repository.GerenteRepository;

@ExtendWith(MockitoExtension.class)
public class GerenteServiceTest {

        @Mock
        private GerenteRepository gerenteRepository;

        @Mock
        private UsuarioService usuarioService;

        @Mock
        private GerenteMapper gerenteMapper;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private GerenteService gerenteService;

        private GerenteRequestDTO gerenteRequestDTO;
        private UsuarioRequestDTO usuarioRequestDTO;
        private Gerente gerenteSalvo;
        private GerenteResponseDTO gerenteResponseDTO;

        @BeforeEach
        void setUp() {
                usuarioRequestDTO = new UsuarioRequestDTO(
                                "Carlos Silva",
                                "12345678901", // 2ª pos: CPF
                                "carlos.gerente@poltrona.com", // 3ª pos: Email
                                "Senha@123",
                                "Senha@123",
                                LocalDate.of(1985, 5, 20));

                gerenteRequestDTO = new GerenteRequestDTO(usuarioRequestDTO);

                // Entidade Gerente: (nome, email, senha, cpf, dataNascimento)
                gerenteSalvo = new Gerente(
                                usuarioRequestDTO.nome(),
                                usuarioRequestDTO.email(),
                                "$2a$10$encodedPasswordHash",
                                usuarioRequestDTO.cpf(),
                                usuarioRequestDTO.dataNascimento());

                ReflectionTestUtils.setField(gerenteSalvo, "id", 1L);

                LocalDateTime dataCriacao = LocalDateTime.now();

                UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                                1L,
                                usuarioRequestDTO.nome(),
                                usuarioRequestDTO.cpf(), // 3ª pos: CPF
                                usuarioRequestDTO.email(), // 4ª pos: Email
                                usuarioRequestDTO.dataNascimento(),
                                StatusConta.ATIVA,
                                dataCriacao);

                gerenteResponseDTO = new GerenteResponseDTO(usuarioResponseDTO);
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        // =========================================================================
        // 1. CADASTRO DE GERENTE (cadastrar)
        // =========================================================================

        @Test
        @DisplayName("Deve cadastrar gerente com sucesso quando dados forem válidos")
        void deveCadastrarGerenteComSucesso() {
                // ARRANGE
                when(gerenteRepository.existsByCpf(usuarioRequestDTO.cpf())).thenReturn(false);
                when(gerenteRepository.existsByEmail(usuarioRequestDTO.email())).thenReturn(false);
                when(passwordEncoder.encode(usuarioRequestDTO.senha())).thenReturn("$2a$10$encodedPasswordHash");
                when(gerenteMapper.toEntity(gerenteRequestDTO, "$2a$10$encodedPasswordHash")).thenReturn(gerenteSalvo);
                when(gerenteRepository.save(gerenteSalvo)).thenReturn(gerenteSalvo);
                when(gerenteMapper.toDTO(gerenteSalvo)).thenReturn(gerenteResponseDTO);

                // ACT
                GerenteResponseDTO resultado = gerenteService.cadastrar(gerenteRequestDTO);

                // ASSERT
                assertThat(resultado).isNotNull();
                assertThat(resultado.usuario().email()).isEqualTo("carlos.gerente@poltrona.com");
                assertThat(resultado.usuario().cpf()).isEqualTo("12345678901");
                assertThat(resultado.usuario().dataCriacao()).isNotNull();
                verify(gerenteRepository, times(1)).save(gerenteSalvo);
        }

        @Test
        @DisplayName("Deve lançar exceção ao cadastrar com CPF já existente")
        void deveLancarExcecaoQuandoCpfJaExistir() {
                // ARRANGE
                when(gerenteRepository.existsByCpf(usuarioRequestDTO.cpf())).thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> gerenteService.cadastrar(gerenteRequestDTO))
                                .isInstanceOf(ResourceAlreadyExistsException.class)
                                .hasMessage("Já existe uma conta com este CPF");

                verify(gerenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao cadastrar com E-mail já existente")
        void deveLancarExcecaoQuandoEmailJaExistir() {
                // ARRANGE
                when(gerenteRepository.existsByCpf(usuarioRequestDTO.cpf())).thenReturn(false);
                when(gerenteRepository.existsByEmail(usuarioRequestDTO.email())).thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> gerenteService.cadastrar(gerenteRequestDTO))
                                .isInstanceOf(ResourceAlreadyExistsException.class)
                                .hasMessage("Já existe uma conta com este e-mail");

                verify(gerenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando a senha e confirmação de senha divergirem")
        void deveLancarExcecaoQuandoSenhasNaoCoincidirem() {
                // ARRANGE
                UsuarioRequestDTO dtoComSenhasDiferentes = new UsuarioRequestDTO(
                                "Carlos Silva",
                                "12345678901",
                                "carlos.gerente@poltrona.com",
                                "Senha@123",
                                "OutraSenha456",
                                LocalDate.of(1985, 5, 20));
                GerenteRequestDTO requestInvalido = new GerenteRequestDTO(dtoComSenhasDiferentes);

                when(gerenteRepository.existsByCpf(dtoComSenhasDiferentes.cpf())).thenReturn(false);
                when(gerenteRepository.existsByEmail(dtoComSenhasDiferentes.email())).thenReturn(false);

                // ACT & ASSERT
                assertThatThrownBy(() -> gerenteService.cadastrar(requestInvalido))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage("A senha e a confirmação de senha não coincidem");

                verify(gerenteRepository, never()).save(any());
        }

        // =========================================================================
        // 2. CONSULTAS E PERFIL (listarTodos, me, buscarPorId)
        // =========================================================================

        @Test
        @DisplayName("Deve listar gerentes de forma paginada")
        void deveListarTodosOsGerentesPaginado() {
                // ARRANGE
                Pageable pageable = PageRequest.of(0, 10);
                Page<Gerente> pageGerente = new PageImpl<>(List.of(gerenteSalvo), pageable, 1);

                when(gerenteRepository.findAll(pageable)).thenReturn(pageGerente);
                when(gerenteMapper.toDTO(gerenteSalvo)).thenReturn(gerenteResponseDTO);

                // ACT
                Page<GerenteResponseDTO> resultado = gerenteService.listarTodos(pageable);

                // ASSERT
                assertThat(resultado).isNotNull();
                assertThat(resultado.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar os dados do gerente autenticado (me)")
        void deveRetornarGerenteLogadoComSucesso() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(gerenteSalvo);
                when(gerenteMapper.toDTO(gerenteSalvo)).thenReturn(gerenteResponseDTO);

                // ACT
                GerenteResponseDTO resultado = gerenteService.me();

                // ASSERT
                assertThat(resultado).isNotNull();
                assertThat(resultado.usuario().cpf()).isEqualTo("12345678901");
        }

        @Test
        @DisplayName("Deve buscar gerente por ID com sucesso")
        void deveBuscarGerentePorIdComSucesso() {
                // ARRANGE
                when(gerenteRepository.findById(1L)).thenReturn(Optional.of(gerenteSalvo));
                when(gerenteMapper.toDTO(gerenteSalvo)).thenReturn(gerenteResponseDTO);

                // ACT
                GerenteResponseDTO resultado = gerenteService.buscarPorId(1L);

                // ASSERT
                assertThat(resultado).isNotNull();
                assertThat(resultado.usuario().id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o gerente não for encontrado por ID")
        void deveLancarExcecaoQuandoGerenteNaoEncontradoPorId() {
                // ARRANGE
                when(gerenteRepository.findById(1L)).thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> gerenteService.buscarPorId(1L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Gerente não encontrado com id 1");
        }

        // =========================================================================
        // 3. ATUALIZAÇÃO DE DADOS (atualizar)
        // =========================================================================

        @Test
        @DisplayName("Deve atualizar dados do gerente logado com sucesso")
        void deveAtualizarGerenteComSucesso() {
                // ARRANGE
                AtualizaGerenteRequestDTO atualizaDTO = new AtualizaGerenteRequestDTO(
                                "Carlos Silva Novo",
                                "carlos.novo@poltrona.com",
                                LocalDate.of(1985, 5, 20));

                when(usuarioService.usuarioLogado()).thenReturn(gerenteSalvo);
                when(gerenteRepository.existsByEmailAndIdNot(atualizaDTO.email(), 1L)).thenReturn(false);
                when(gerenteRepository.save(gerenteSalvo)).thenReturn(gerenteSalvo);
                when(gerenteMapper.toDTO(gerenteSalvo)).thenReturn(gerenteResponseDTO);

                // ACT
                GerenteResponseDTO resultado = gerenteService.atualizar(atualizaDTO);

                // ASSERT
                assertThat(resultado).isNotNull();
                verify(gerenteRepository, times(1)).save(gerenteSalvo);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar atualizar dados de conta bloqueada ou encerrada")
        void deveLancarExcecaoAoAtualizarGerenteNaoAtivo() {
                // ARRANGE
                gerenteSalvo.encerrar(); // Executa a mudança de estado internamente na entidade
                AtualizaGerenteRequestDTO atualizaDTO = new AtualizaGerenteRequestDTO(
                                "Carlos Silva", "carlos@poltrona.com", LocalDate.of(1985, 5, 20));

                when(usuarioService.usuarioLogado()).thenReturn(gerenteSalvo);

                // ACT & ASSERT
                assertThatThrownBy(() -> gerenteService.atualizar(atualizaDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage("Uma conta bloqueada ou encerrada não pode atualizar dados");

                verify(gerenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar para um e-mail que já pertence a outra conta")
        void deveLancarExcecaoAoAtualizarParaEmailJaCadastrado() {
                // ARRANGE
                AtualizaGerenteRequestDTO atualizaDTO = new AtualizaGerenteRequestDTO(
                                "Carlos Silva",
                                "outro.gerente@poltrona.com",
                                LocalDate.of(1985, 5, 20));

                when(usuarioService.usuarioLogado()).thenReturn(gerenteSalvo);
                when(gerenteRepository.existsByEmailAndIdNot(atualizaDTO.email(), 1L)).thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> gerenteService.atualizar(atualizaDTO))
                                .isInstanceOf(ResourceAlreadyExistsException.class)
                                .hasMessage("Já existe uma conta para este e-mail");

                verify(gerenteRepository, never()).save(any());
        }

        // =========================================================================
        // 4. ENCERRAMENTO DE CONTA (encerrar)
        // =========================================================================

        @Test
        @DisplayName("Deve encerrar a conta do gerente e limpar o SecurityContext")
        void deveEncerrarContaELimparSecurityContext() {
                // ARRANGE
                SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken("carlos", "senha"));
                when(usuarioService.usuarioLogado()).thenReturn(gerenteSalvo);

                // ACT
                gerenteService.encerrar();

                // ASSERT
                assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
                assertThat(gerenteSalvo.getStatus()).isEqualTo(StatusConta.ENCERRADA);
                verify(gerenteRepository, times(1)).save(gerenteSalvo);
        }

        // =========================================================================
        // 5. ATUALIZAÇÃO DE SENHA (atualizarSenha)
        // =========================================================================

        @Test
        @DisplayName("Deve atualizar a senha do gerente com sucesso")
        void deveAtualizarSenhaComSucesso() {
                // ARRANGE
                AtualizaSenhaRequestDTO dtoSenha = new AtualizaSenhaRequestDTO(
                                "Senha@123",
                                "NovaSenha@123",
                                "NovaSenha@123");

                when(usuarioService.usuarioLogado()).thenReturn(gerenteSalvo);
                when(passwordEncoder.matches(dtoSenha.senhaAtual(), gerenteSalvo.getSenha())).thenReturn(true);
                when(passwordEncoder.encode(dtoSenha.confirmarSenha())).thenReturn("$2a$10$newEncodedPasswordHash");

                // ACT
                gerenteService.atualizarSenha(dtoSenha);

                // ASSERT
                verify(gerenteRepository, times(1)).save(gerenteSalvo);
        }

        @Test
        @DisplayName("Deve lançar exceção quando a senha atual fornecida for incorreta")
        void deveLancarExcecaoQuandoSenhaAtualEstiverIncorreta() {
                // ARRANGE
                AtualizaSenhaRequestDTO dtoSenha = new AtualizaSenhaRequestDTO(
                                "SenhaIncorreta",
                                "NovaSenha@123",
                                "NovaSenha@123");

                when(usuarioService.usuarioLogado()).thenReturn(gerenteSalvo);
                when(passwordEncoder.matches(dtoSenha.senhaAtual(), gerenteSalvo.getSenha())).thenReturn(false);

                // ACT & ASSERT
                assertThatThrownBy(() -> gerenteService.atualizarSenha(dtoSenha))
                                .isInstanceOf(BadCredentialsException.class)
                                .hasMessage("Senha atual incorreta");

                verify(gerenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando a nova senha e a confirmação não forem iguais")
        void deveLancarExcecaoQuandoNovaSenhaEConfirmacaoNaoCoincidirem() {
                // ARRANGE
                AtualizaSenhaRequestDTO dtoSenha = new AtualizaSenhaRequestDTO(
                                "Senha@123",
                                "NovaSenha@123",
                                "SenhaDiferente@123");

                when(usuarioService.usuarioLogado()).thenReturn(gerenteSalvo);
                when(passwordEncoder.matches(dtoSenha.senhaAtual(), gerenteSalvo.getSenha())).thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> gerenteService.atualizarSenha(dtoSenha))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage("A senha nova deve ser igual a confirmação de senha");

                verify(passwordEncoder, never()).encode(anyString());
                verify(gerenteRepository, never()).save(any());
        }
}