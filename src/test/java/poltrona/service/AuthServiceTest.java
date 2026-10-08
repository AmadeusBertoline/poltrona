package poltrona.service;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.login.LoginRequestDTO;
import poltrona.dto.login.LoginResponseDTO;
import poltrona.entity.Admin;
import poltrona.entity.Usuario;
import poltrona.enums.usuario.StatusConta;
import poltrona.repository.UsuarioRepository;
import poltrona.security.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

        @Mock
        private UsuarioRepository usuarioRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtService jwtService;

        @InjectMocks
        private AuthService authService;

        private LoginRequestDTO loginRequestDTO;
        private Usuario usuario;

        @BeforeEach
        void setUp() {
                loginRequestDTO = new LoginRequestDTO("admin@poltrona.com", "Senha123@");

                usuario = new Admin(
                                "Admin Silva",
                                "admin@poltrona.com",
                                "senha_criptografada",
                                "12345678900",
                                LocalDate.of(1990, 5, 15));
                ReflectionTestUtils.setField(usuario, "id", 1L);
        }

        // =========================================================================
        // logar
        // =========================================================================

        @Test
        @DisplayName("Deve realizar login com sucesso quando as credenciais forem válidas e a conta estiver ativa")
        void deveLogarComSucesso() {
                // ARRANGE
                String tokenGerado = "token_jwt_valido";
                String tipoUsuario = "ADMIN";

                when(usuarioRepository.findByEmailOrCpfAndStatus(loginRequestDTO.emailOrCpf(), StatusConta.ATIVA))
                                .thenReturn(Optional.of(usuario));
                when(passwordEncoder.matches(loginRequestDTO.senha(), usuario.getSenha()))
                                .thenReturn(true);
                when(jwtService.gerarToken(usuario))
                                .thenReturn(tokenGerado);
                when(jwtService.extrairTipoUsuario(tokenGerado))
                                .thenReturn(tipoUsuario);

                // ACT
                LoginResponseDTO resposta = authService.logar(loginRequestDTO);

                // ASSERT
                assertThat(resposta).isNotNull();
                assertThat(resposta.token()).isEqualTo(tokenGerado);
                assertThat(resposta.tipo()).isEqualTo("Bearer");
                assertThat(resposta.id()).isEqualTo(1L);
                assertThat(resposta.email()).isEqualTo("admin@poltrona.com");
                assertThat(resposta.role()).isEqualTo(tipoUsuario);

                verify(usuarioRepository, times(1)).findByEmailOrCpfAndStatus(loginRequestDTO.emailOrCpf(),
                                StatusConta.ATIVA);
                verify(passwordEncoder, times(1)).matches(loginRequestDTO.senha(), usuario.getSenha());
                verify(jwtService, times(1)).gerarToken(usuario);
                verify(jwtService, times(1)).extrairTipoUsuario(tokenGerado);
        }

        @Test
        @DisplayName("Deve lançar BadCredentialsException quando o usuário não for encontrado ou não estiver ativo")
        void deveLancarExcecaoQuandoUsuarioNaoEncontradoOuInativo() {
                // ARRANGE
                when(usuarioRepository.findByEmailOrCpfAndStatus(loginRequestDTO.emailOrCpf(), StatusConta.ATIVA))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThatThrownBy(() -> authService.logar(loginRequestDTO))
                                .isInstanceOf(BadCredentialsException.class)
                                .hasMessage("Usuário ou senha inválidos");

                verify(usuarioRepository, times(1)).findByEmailOrCpfAndStatus(loginRequestDTO.emailOrCpf(),
                                StatusConta.ATIVA);
                verifyNoInteractions(passwordEncoder, jwtService);
        }

        @Test
        @DisplayName("Deve lançar BadCredentialsException quando a senha estiver incorreta")
        void deveLancarExcecaoQuandoSenhaForIncorreta() {
                // ARRANGE
                when(usuarioRepository.findByEmailOrCpfAndStatus(loginRequestDTO.emailOrCpf(), StatusConta.ATIVA))
                                .thenReturn(Optional.of(usuario));
                when(passwordEncoder.matches(loginRequestDTO.senha(), usuario.getSenha()))
                                .thenReturn(false);

                // ACT & ASSERT
                assertThatThrownBy(() -> authService.logar(loginRequestDTO))
                                .isInstanceOf(BadCredentialsException.class)
                                .hasMessage("Usuário ou senha inválidos");

                verify(usuarioRepository, times(1)).findByEmailOrCpfAndStatus(loginRequestDTO.emailOrCpf(),
                                StatusConta.ATIVA);
                verify(passwordEncoder, times(1)).matches(loginRequestDTO.senha(), usuario.getSenha());
                verifyNoInteractions(jwtService);
        }
}