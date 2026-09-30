package poltrona.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Proprietario;
import poltrona.entity.Usuario;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.UsuarioMapper;
import poltrona.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UsuarioResponseDTO usuarioResponseDTO;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    private Pageable pageable;
    private Page<Usuario> paginaUsuario;

    private String emailValido;
    private String emailEmUso;
    private String cpf;

    @BeforeEach
    void setUp() {

        usuario = new Proprietario(
                "Usuario Teste",
                "usuario@email.com",
                "123456",
                "12345678901",
                LocalDate.of(1990, 1, 1));
        ReflectionTestUtils.setField(usuario, "id", 1L);

        pageable = PageRequest.of(0, 10);
        paginaUsuario = new PageImpl<>(List.of(usuario));

        emailValido = "novo@email.com";
        emailEmUso = "existente@email.com";
        cpf = "12345678901";
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    // usuarioLogado
    // =========================================================================

    @Test
    @DisplayName("Deve retornar usuário logado com sucesso quando id do contexto for válido")
    void deveRetornarUsuarioLogadoComSucesso() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("1");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // ACT
        Usuario resultado = usuarioService.usuarioLogado();

        // ASSERT
        assertThat(resultado).isNotNull().isEqualTo(usuario);
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário logado não for encontrado no banco")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoForEncontrado() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("1");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> usuarioService.usuarioLogado())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário logado não encontrado de id 1");

        verify(usuarioRepository, times(1)).findById(1L);
    }

    // =========================================================================
    // listarTodos
    // =========================================================================

    @Test
    @DisplayName("Deve listar todos os usuários paginados com sucesso")
    void deveListarTodosUsuariosPaginados() {
        // ARRANGE
        when(usuarioRepository.findAll(pageable)).thenReturn(paginaUsuario);
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioResponseDTO);

        // ACT
        Page<UsuarioResponseDTO> resultado = usuarioService.listarTodos(pageable);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0)).isEqualTo(usuarioResponseDTO);

        verify(usuarioRepository, times(1)).findAll(pageable);
        verify(usuarioMapper, times(1)).toDTO(usuario);
    }

    // =========================================================================
    // me
    // =========================================================================

    @Test
    @DisplayName("Deve retornar DTO do usuário logado através do método me")
    void deveRetornarUsuarioLogadoComoDTO() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("1");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioResponseDTO);

        // ACT
        UsuarioResponseDTO resultado = usuarioService.me();

        // ASSERT
        assertThat(resultado).isNotNull().isEqualTo(usuarioResponseDTO);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioMapper, times(1)).toDTO(usuario);
    }

    // =========================================================================
    // validarCredenciaisDisponiveis
    // =========================================================================

    @Test
    @DisplayName("Deve retornar false quando e-mail estiver disponível")
    void deveRetornarFalsoQuandoEmailDisponivel() {
        // ARRANGE
        when(usuarioRepository.existsByEmailAndStatus(emailValido, StatusConta.ATIVA)).thenReturn(false);

        // ACT
        boolean resultado = usuarioService.validarCredenciaisDisponiveis(emailValido, cpf);

        // ASSERT
        assertThat(resultado).isFalse();
        verify(usuarioRepository, times(1)).existsByEmailAndStatus(emailValido, StatusConta.ATIVA);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando e-mail já estiver em uso")
    void deveLancarExcecaoQuandoEmailJaEmUso() {
        // ARRANGE
        when(usuarioRepository.existsByEmailAndStatus(emailEmUso, StatusConta.ATIVA)).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> usuarioService.validarCredenciaisDisponiveis(emailEmUso, cpf))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("E-mail já está em uso no sistema.");

        verify(usuarioRepository, times(1)).existsByEmailAndStatus(emailEmUso, StatusConta.ATIVA);
    }
}