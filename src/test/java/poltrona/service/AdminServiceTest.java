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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.dto.admin.AtualizaAdminRequestDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.dto.usuario.AtualizaUsuarioRequestDTO;
import poltrona.dto.usuario.UsuarioRequestDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Admin;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.mapper.AdminMapper;
import poltrona.repository.AdminRepository;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

        @Mock
        private AdminRepository adminRepository;

        @Mock
        private AdminMapper adminMapper;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private UsuarioService usuarioService;

        @InjectMocks
        private AdminService adminService;

        private Admin adminSemId;
        private Admin adminSalvo;
        private AdminRequestDTO adminRequestDTO;
        private AdminResponseDTO adminResponseDTO;
        private UsuarioRequestDTO usuarioRequestDTO;
        private UsuarioResponseDTO usuarioResponseDTO;
        private AtualizaAdminRequestDTO atualizaAdminDTO;
        private AtualizaSenhaRequestDTO atualizaSenhaRequestDTO;
        private AdminRequestDTO requestComSenhasDiferentes;
        private UsuarioRequestDTO usuarioComSenhasDiferentes;

        @BeforeEach
        void setUp() {
                String senhaCriptografada = "senha_criptografada";

                usuarioRequestDTO = new UsuarioRequestDTO(
                                "Admin Silva",
                                "admin@poltrona.com",
                                "12345678900",
                                "Senha@123",
                                "Senha@123",
                                LocalDate.of(1990, 5, 15));

                adminRequestDTO = new AdminRequestDTO(usuarioRequestDTO);

                adminSemId = new Admin(
                                adminRequestDTO.usuario().nome(),
                                adminRequestDTO.usuario().email(),
                                senhaCriptografada,
                                adminRequestDTO.usuario().cpf(),
                                adminRequestDTO.usuario().dataNascimento());

                adminSalvo = new Admin(
                                adminRequestDTO.usuario().nome(),
                                adminRequestDTO.usuario().email(),
                                senhaCriptografada,
                                adminRequestDTO.usuario().cpf(),
                                adminRequestDTO.usuario().dataNascimento());

                ReflectionTestUtils.setField(adminSalvo, "id", 1L);

                usuarioResponseDTO = new UsuarioResponseDTO(
                                1L,
                                usuarioRequestDTO.nome(),
                                usuarioRequestDTO.email(),
                                usuarioRequestDTO.cpf(),
                                usuarioRequestDTO.dataNascimento(),
                                StatusConta.ATIVA,
                                LocalDateTime.now());

                adminResponseDTO = new AdminResponseDTO(usuarioResponseDTO);

                AtualizaUsuarioRequestDTO atualizaUsuarioDTO = new AtualizaUsuarioRequestDTO(
                                "Admin Silva Atualizado",
                                "admin.atualizado@poltrona.com",
                                LocalDate.of(1992, 10, 10));

                atualizaAdminDTO = new AtualizaAdminRequestDTO(atualizaUsuarioDTO);

                atualizaSenhaRequestDTO = new AtualizaSenhaRequestDTO(
                                "Senha@123",
                                "SenhaNova123!",
                                "SenhaNova123!");

                usuarioComSenhasDiferentes = new UsuarioRequestDTO(
                                "Admin Silva",
                                "admin@poltrona.com",
                                "12345678900",
                                "Senha@123",
                                "senhaErrada",
                                LocalDate.of(1990, 5, 15));

                requestComSenhasDiferentes = new AdminRequestDTO(usuarioComSenhasDiferentes);
        }

        // =========================================================================
        // 1. TESTES DE CADASTRO DE ADMIN (cadastrar)
        // =========================================================================

        @Test
        @DisplayName("Deve cadastrar um admin com sucesso quando os dados forem válidos")
        void deveCadastrarAdminComSucesso() {
                // ARRANGE
                when(adminRepository.existsByCpf(adminRequestDTO.usuario().cpf())).thenReturn(false);
                when(adminRepository.existsByEmail(adminRequestDTO.usuario().email())).thenReturn(false);
                when(passwordEncoder.encode(adminRequestDTO.usuario().confirmarSenha()))
                                .thenReturn("senha_criptografada");
                when(adminMapper.toEntity(adminRequestDTO, "senha_criptografada")).thenReturn(adminSemId);
                when(adminRepository.save(adminSemId)).thenReturn(adminSalvo);
                when(adminMapper.toDTO(adminSalvo)).thenReturn(adminResponseDTO);

                // ACT
                AdminResponseDTO adminResposta = adminService.cadastrar(adminRequestDTO);

                // ASSERT
                assertThat(adminResposta).isNotNull();
                assertThat(adminResposta.usuario().id()).isEqualTo(1L);
                assertThat(adminResposta.usuario().status()).isEqualByComparingTo(StatusConta.ATIVA);
                verify(adminRepository, times(1)).save(adminSemId);
        }

        @Test
        @DisplayName("Deve lançar exceção ao cadastrar admin com CPF existente")
        void deveLancarExcecaoQuandoCpfJaExistir() {
                // ARRANGE
                when(adminRepository.existsByCpf(adminRequestDTO.usuario().cpf())).thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> adminService.cadastrar(adminRequestDTO))
                                .isInstanceOf(ResourceAlreadyExistsException.class)
                                .hasMessage("Já existe uma conta com este CPF");

                verify(adminRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao cadastrar admin com e-mail já existente")
        void deveLancarExcecaoQuandoEmailJaExistir() {
                // ARRANGE
                when(adminRepository.existsByCpf(adminRequestDTO.usuario().cpf())).thenReturn(false);
                when(adminRepository.existsByEmail(adminRequestDTO.usuario().email())).thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> adminService.cadastrar(adminRequestDTO))
                                .isInstanceOf(ResourceAlreadyExistsException.class)
                                .hasMessage("Já existe uma conta com este e-mail");

                verify(adminRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando a senha e a confirmação de senha não coincidirem")
        void deveLancarExcecaoSenhaOuConfirmacaoDiferentes() {
                // ARRANGE
                when(adminRepository.existsByCpf(requestComSenhasDiferentes.usuario().cpf())).thenReturn(false);
                when(adminRepository.existsByEmail(requestComSenhasDiferentes.usuario().email())).thenReturn(false);

                // ACT & ASSERT
                assertThatThrownBy(() -> adminService.cadastrar(requestComSenhasDiferentes))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("A senha e a confirmação de senha não coincidem");

                verify(adminRepository, never()).save(any());
        }

        // =========================================================================
        // 2. TESTES DE CONSULTA E LISTAGEM (listarTodos, me, buscarPorId)
        // =========================================================================

        @Test
        @DisplayName("Deve listar todos os admins com sucesso de forma paginada")
        void deveListarAdminsComSucesso() {
                // ARRANGE
                Pageable pageable = PageRequest.of(0, 10);
                List<Admin> listaAdmin = List.of(adminSalvo);
                Page<Admin> pageAdminInput = new PageImpl<>(listaAdmin, pageable, listaAdmin.size());

                when(adminRepository.findAll(pageable)).thenReturn(pageAdminInput);
                when(adminMapper.toDTO(adminSalvo)).thenReturn(adminResponseDTO);

                // ACT
                Page<AdminResponseDTO> resultado = adminService.listarTodos(pageable);

                // ASSERT
                assertThat(resultado).isNotNull();
                assertThat(resultado).hasSize(1);
                assertThat(resultado.getContent().get(0).usuario().id()).isEqualTo(1L);
                assertThat(resultado.getTotalElements()).isEqualTo(1);

                verify(adminRepository, times(1)).findAll(pageable);
                verify(adminMapper, times(1)).toDTO(adminSalvo);
        }

        @Test
        @DisplayName("Deve listar os dados do admin autenticado logado")
        void deveListarDadosAdminLogado() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(adminSalvo);
                when(adminMapper.toDTO(adminSalvo)).thenReturn(adminResponseDTO);

                // ACT
                AdminResponseDTO resposta = adminService.me();

                // ASSERT
                assertThat(resposta).isNotNull();
                assertThat(resposta.usuario().nome()).isEqualTo(adminSalvo.getNome());
        }

        @Test
        @DisplayName("Deve buscar um admin pelo ID com sucesso")
        void deveBuscarAdminPeloId() {
                // ARRANGE
                when(adminRepository.findById(1L)).thenReturn(Optional.of(adminSalvo));
                when(adminMapper.toDTO(adminSalvo)).thenReturn(adminResponseDTO);

                // ACT
                AdminResponseDTO resultado = adminService.buscarPorId(1L);

                // ASSERT
                assertThat(resultado).isNotNull();
                assertThat(resultado.usuario().nome()).isEqualTo(adminSalvo.getNome());
        }

        // =========================================================================
        // 3. TESTES DE ATUALIZAÇÃO DE PERFIL (atualizar)
        // =========================================================================

        @Test
        @DisplayName("Deve atualizar os dados do admin com sucesso")
        void deveAtualizarAdminComSucesso() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(adminSalvo);
                when(adminRepository.existsByEmailAndIdNot(atualizaAdminDTO.usuario().email(), adminSalvo.getId()))
                                .thenReturn(false);
                when(adminRepository.save(adminSalvo)).thenReturn(adminSalvo);
                when(adminMapper.toDTO(adminSalvo)).thenReturn(adminResponseDTO);

                // ACT
                AdminResponseDTO resultado = adminService.atualizar(atualizaAdminDTO);

                // ASSERT
                assertThat(resultado).isNotNull();
                verify(adminRepository, times(1)).save(adminSalvo);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar atualizar admin sem conta ATIVA")
        void deveLancarExcecaoAtualizarAdminSemContaAtiva() {
                // ARRANGE
                adminSalvo.bloquear();
                when(usuarioService.usuarioLogado()).thenReturn(adminSalvo);

                // ACT & ASSERT
                assertThatThrownBy(() -> adminService.atualizar(atualizaAdminDTO))
                                .isInstanceOf(RegraNegocioException.class)
                                .hasMessage("Uma conta bloqueada ou encerrada não pode atualizar dados");

                verify(adminRepository, never()).save(adminSalvo);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar atualizar para um e-mail já em uso por outro admin")
        void deveLancarExcecaoAoAtualizarEmailParaUmJaEmUso() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(adminSalvo);
                when(adminRepository.existsByEmailAndIdNot(atualizaAdminDTO.usuario().email(), adminSalvo.getId()))
                                .thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> adminService.atualizar(atualizaAdminDTO))
                                .isInstanceOf(ResourceAlreadyExistsException.class)
                                .hasMessage("Já existe uma conta para este e-mail");

                verify(adminRepository, never()).save(adminSalvo);
        }

        // =========================================================================
        // 4. TESTES DE ALTERAÇÃO DE SENHA (atualizarSenha)
        // =========================================================================

        @Test
        @DisplayName("Deve atualizar a senha do admin com sucesso")
        void deveAtualizarSenhaAdminComSucesso() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(adminSalvo);
                when(passwordEncoder.matches(atualizaSenhaRequestDTO.senhaAtual(), adminSalvo.getSenha()))
                                .thenReturn(true);
                when(passwordEncoder.encode(atualizaSenhaRequestDTO.confirmarSenha()))
                                .thenReturn("senha_nova_criptografada");

                // ACT
                adminService.atualizarSenha(atualizaSenhaRequestDTO);

                // ASSERT
                assertThat(adminSalvo.getSenha()).isEqualTo("senha_nova_criptografada");
                verify(adminRepository, times(1)).save(adminSalvo);
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar senha atual incorreta na alteração de senha")
        void deveLancarExcecaoAoErrarSenhaParaAtualizar() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(adminSalvo);
                when(passwordEncoder.matches(atualizaSenhaRequestDTO.senhaAtual(), adminSalvo.getSenha()))
                                .thenReturn(false);

                // ACT & ASSERT
                assertThatThrownBy(() -> adminService.atualizarSenha(atualizaSenhaRequestDTO))
                                .isInstanceOf(BadCredentialsException.class)
                                .hasMessage("Senha atual incorreta");

                verify(passwordEncoder, never()).encode(atualizaSenhaRequestDTO.confirmarSenha());
                verify(adminRepository, never()).save(adminSalvo);
        }

        @Test
        @DisplayName("Deve lançar exceção quando a nova senha for diferente da confirmação na atualização")
        void deveLancarExcecaoSenhasDiferentesAoAtualizar() {
                // ARRANGE
                AtualizaSenhaRequestDTO requestComSenhasDiferentes = new AtualizaSenhaRequestDTO(
                                "Senha@123",
                                "SenhaNova123!",
                                "senhaNadaAver@123");
                when(usuarioService.usuarioLogado()).thenReturn(adminSalvo);
                when(passwordEncoder.matches(atualizaSenhaRequestDTO.senhaAtual(), adminSalvo.getSenha()))
                                .thenReturn(true);

                // ACT & ASSERT
                assertThatThrownBy(() -> adminService.atualizarSenha(requestComSenhasDiferentes))
                                .isInstanceOf(BadCredentialsException.class)
                                .hasMessage("A senha nova deve ser igual a confirmação de senha");

                verify(passwordEncoder, never()).encode(atualizaSenhaRequestDTO.confirmarSenha());
                verify(adminRepository, never()).save(adminSalvo);
        }

        // =========================================================================
        // 5. TESTES DE ENCERRAMENTO DE CONTA (encerrar)
        // =========================================================================

        @Test
        @DisplayName("Deve encerrar a conta do admin e limpar a autenticação do contexto de segurança")
        void deveEncerrarContaDeAdmin() {
                // ARRANGE
                when(usuarioService.usuarioLogado()).thenReturn(adminSalvo);

                // ACT
                adminService.encerrar();

                // ASSERT
                assertThat(adminSalvo.getStatus()).isEqualByComparingTo(StatusConta.ENCERRADA);
                assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
                verify(adminRepository, times(1)).save(adminSalvo);
        }
}