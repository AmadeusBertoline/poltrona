package poltrona.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import poltrona.dto.proprietario.AtualizaProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.dto.usuario.AtualizaUsuarioRequestDTO;
import poltrona.dto.usuario.UsuarioRequestDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;

import poltrona.entity.Cliente; // Importando entidade Cliente para o teste de tipo
import poltrona.entity.Proprietario;
import poltrona.entity.Usuario;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.ProprietarioMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.IngressoRepository;
import poltrona.repository.ProprietarioRepository;
import poltrona.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProprietarioService - Testes Unitários")
class ProprietarioServiceTest {

    @Mock
    private ProprietarioRepository proprietarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProprietarioMapper proprietarioMapper;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private IngressoRepository ingressoRepository;

    @Mock
    private CinemaRepository cinemaRepository;

    @InjectMocks
    private ProprietarioService proprietarioService;

    private Proprietario proprietario;
    private Usuario usuarioComum;
    private ProprietarioResponseDTO proprietarioResponseDTO;
    private ProprietarioRequestDTO proprietarioRequestDTO;

    private AtualizaProprietarioRequestDTO atualizaProprietarioMesmoEmailDTO;
    private AtualizaProprietarioRequestDTO atualizaProprietarioNovoEmailDTO;

    private AtualizaSenhaRequestDTO atualizaSenhaValidaDTO;
    private AtualizaSenhaRequestDTO atualizaSenhaIncorretaDTO;
    private AtualizaSenhaRequestDTO atualizaSenhaConfirmacaoDiferenteDTO;

    @BeforeEach
    void setUp() {

        proprietario = new Proprietario("Carlos Silva", "carlos@email.com", "senha123Encrypted", "12345678901",
                LocalDate.of(1985, 5, 20));
        ReflectionTestUtils.setField(proprietario, "id", 1L);


        usuarioComum = new Cliente("Cliente Comum", "cliente@email.com", "senha123", "11122233344",
                LocalDate.of(2000, 1, 1));

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                1L,
                "Carlos Silva",
                "carlos@email.com",
                "44199496009",
                LocalDate.of(1985, 5, 20),
                StatusConta.ATIVA,
                LocalDateTime.of(2010, 1, 1, 0, 0));
        proprietarioResponseDTO = new ProprietarioResponseDTO(usuarioResponseDTO, Collections.emptyList());

        UsuarioRequestDTO usuarioRequestDTO = new UsuarioRequestDTO(
                "Carlos Silva",
                "carlos@email.com",
                "12345678901",
                "senha123",
                "senha123",
                LocalDate.of(1985, 5, 20));
        proprietarioRequestDTO = new ProprietarioRequestDTO(usuarioRequestDTO);

        AtualizaUsuarioRequestDTO atualizaMesmoEmail = new AtualizaUsuarioRequestDTO(
                "Carlos Silva Alterado",
                "carlos@email.com",
                LocalDate.of(1985, 5, 20));
        atualizaProprietarioMesmoEmailDTO = new AtualizaProprietarioRequestDTO(atualizaMesmoEmail);

        AtualizaUsuarioRequestDTO atualizaNovoEmail = new AtualizaUsuarioRequestDTO(
                "Carlos Silva",
                "novo.email@email.com",
                LocalDate.of(1985, 5, 20));
        atualizaProprietarioNovoEmailDTO = new AtualizaProprietarioRequestDTO(atualizaNovoEmail);

        atualizaSenhaValidaDTO = new AtualizaSenhaRequestDTO("senha123Encrypted", "NovaSenha123!", "NovaSenha123!");
        atualizaSenhaIncorretaDTO = new AtualizaSenhaRequestDTO("senhaErrada", "NovaSenha123!", "NovaSenha123!");
        atualizaSenhaConfirmacaoDiferenteDTO = new AtualizaSenhaRequestDTO("senha123Encrypted", "NovaSenha123!",
                "SenhaDiferente123!");
    }

    // ==============================================
    // CADASTRAR
    // ==============================================

    @Test
    @DisplayName("Deve cadastrar proprietário com sucesso quando credenciais forem válidas")
    void deveCadastrarProprietarioComSucesso() {
        // ARRANGE
        when(passwordEncoder.encode("senha123")).thenReturn("senha123Encrypted");
        when(proprietarioMapper.toEntity(proprietarioRequestDTO, "senha123Encrypted")).thenReturn(proprietario);
        when(proprietarioRepository.save(proprietario)).thenReturn(proprietario);
        when(proprietarioMapper.toDTO(proprietario)).thenReturn(proprietarioResponseDTO);

        // ACT
        ProprietarioResponseDTO resultado = proprietarioService.cadastrar(proprietarioRequestDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        verify(usuarioService).validarCredenciaisDisponiveis("carlos@email.com", "12345678901");
        verify(passwordEncoder).encode("senha123");
        verify(proprietarioRepository).save(proprietario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar quando credenciais já estiverem em uso")
    void deveLancarExcecaoQuandoCredenciaisInvalidasNoCadastro() {
        // ARRANGE
        doThrow(new RegraNegocioException("E-mail ou CPF já cadastrado"))
                .when(usuarioService).validarCredenciaisDisponiveis("carlos@email.com", "12345678901");

        // ACT + ASSERT
        assertThatThrownBy(() -> proprietarioService.cadastrar(proprietarioRequestDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("E-mail ou CPF já cadastrado");

        verify(proprietarioRepository, never()).save(any());
    }

    // ==============================================
    // LISTAR TODOS
    // ==============================================

    @Test
    @DisplayName("Deve listar todos os proprietários paginados com sucesso")
    void deveListarTodosOsProprietariosPaginados() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Proprietario> pagina = new PageImpl<>(List.of(proprietario));

        when(proprietarioRepository.findAll(pageable)).thenReturn(pagina);
        when(proprietarioMapper.toDTO(proprietario)).thenReturn(proprietarioResponseDTO);

        // ACT
        Page<ProprietarioResponseDTO> resultado = proprietarioService.listarTodos(pageable);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(proprietarioRepository).findAll(pageable);
    }

    // ==============================================
    // ME (USUÁRIO LOGADO)
    // ==============================================

    @Test
    @DisplayName("Deve retornar os dados do proprietário logado com sucesso")
    void deveRetornarProprietarioLogadoComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(proprietarioMapper.toDTO(proprietario)).thenReturn(proprietarioResponseDTO);

        // ACT
        ProprietarioResponseDTO resultado = proprietarioService.me();

        // ASSERT
        assertThat(resultado).isNotNull();
        verify(proprietarioMapper).toDTO(proprietario);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando o usuário logado não for do tipo Proprietario")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoForProprietario() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(usuarioComum);

        // ACT + ASSERT
        assertThatThrownBy(() -> proprietarioService.me())
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Apenas proprietários podem realizar esta operação.");
    }

    // ==============================================
    // ATUALIZAR
    // ==============================================

    @Test
    @DisplayName("Deve atualizar proprietário com sucesso quando o e-mail não for alterado")
    void deveAtualizarProprietarioComSucessoQuandoEmailNaoMudou() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(proprietarioRepository.save(proprietario)).thenReturn(proprietario);
        when(proprietarioMapper.toDTO(proprietario)).thenReturn(proprietarioResponseDTO);

        // ACT
        ProprietarioResponseDTO resultado = proprietarioService.atualizar(atualizaProprietarioMesmoEmailDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        verify(usuarioRepository, never()).existsByEmail(anyString());
        verify(proprietarioRepository).save(proprietario);
    }

    @Test
    @DisplayName("Deve atualizar proprietário com sucesso quando o e-mail for alterado e estiver disponível")
    void deveAtualizarProprietarioComSucessoQuandoEmailMudouEDisponivel() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(usuarioRepository.existsByEmail("novo.email@email.com")).thenReturn(false);
        when(proprietarioRepository.save(proprietario)).thenReturn(proprietario);
        when(proprietarioMapper.toDTO(proprietario)).thenReturn(proprietarioResponseDTO);

        // ACT
        ProprietarioResponseDTO resultado = proprietarioService.atualizar(atualizaProprietarioNovoEmailDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        verify(usuarioRepository).existsByEmail("novo.email@email.com");
        verify(proprietarioRepository).save(proprietario);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar atualizar para um e-mail já existente")
    void deveLancarExcecaoQuandoNovoEmailJaEstiverEmUso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(usuarioRepository.existsByEmail("novo.email@email.com")).thenReturn(true);

        // ACT + ASSERT
        assertThatThrownBy(() -> proprietarioService.atualizar(atualizaProprietarioNovoEmailDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("O e-mail informado já está em uso por outro usuário.");

        verify(proprietarioRepository, never()).save(any());
    }

    // ==============================================
    // ENCERRAR CONTA
    // ==============================================

    @Test
    @DisplayName("Deve encerrar conta e inativar cinemas quando não houver sessões futuras com ingressos vendidos")
    void deveEncerrarContaComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(ingressoRepository.existsBySessaoSalaCinemaProprietarioIdAndSessaoDataHoraFimAfter(
                eq(1L), any(LocalDateTime.class))).thenReturn(false);

        // ACT
        proprietarioService.encerrar();

        // ASSERT
        verify(cinemaRepository).inativarPorProprietario(1L);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar encerrar conta com ingressos vendidos para sessões futuras")
    void deveLancarExcecaoQuandoHouverSessoesFuturasComIngressos() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(ingressoRepository.existsBySessaoSalaCinemaProprietarioIdAndSessaoDataHoraFimAfter(
                eq(1L), any(LocalDateTime.class))).thenReturn(true);

        // ACT + ASSERT
        assertThatThrownBy(() -> proprietarioService.encerrar())
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Não é possível inativar a conta com sessões futuras que possuem ingressos vendidos.");

        verify(cinemaRepository, never()).inativarPorProprietario(anyLong());
    }

    // ==============================================
    // ATUALIZAR SENHA
    // ==============================================

    @Test
    @DisplayName("Deve atualizar senha com sucesso quando a senha atual estiver correta e as novas senhas coincidirem")
    void deveAtualizarSenhaComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(passwordEncoder.matches("senha123Encrypted", "senha123Encrypted")).thenReturn(true);
        when(passwordEncoder.encode("NovaSenha123!")).thenReturn("novaSenhaCriptografada");

        // ACT
        proprietarioService.atualizarSenha(atualizaSenhaValidaDTO);

        // ASSERT
        verify(passwordEncoder).encode("NovaSenha123!");
        verify(proprietarioRepository).save(proprietario);
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando a senha atual fornecida for incorreta")
    void deveLancarExcecaoQuandoSenhaAtualEstiverIncorreta() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(passwordEncoder.matches("senhaErrada", "senha123Encrypted")).thenReturn(false);

        // ACT + ASSERT
        assertThatThrownBy(() -> proprietarioService.atualizarSenha(atualizaSenhaIncorretaDTO))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Senha atual incorreta");

        verify(proprietarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando a confirmação de senha não coincidir com a nova senha")
    void deveLancarExcecaoQuandoConfirmacaoDeSenhaNaoCoincidir() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(proprietario);
        when(passwordEncoder.matches("senha123Encrypted", "senha123Encrypted")).thenReturn(true);

        // ACT + ASSERT
        assertThatThrownBy(() -> proprietarioService.atualizarSenha(atualizaSenhaConfirmacaoDiferenteDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("A senha nova deve ser igual a confirmação de senha");

        verify(proprietarioRepository, never()).save(any());
    }

    // ==============================================
    // BUSCAR POR ID
    // ==============================================

    @Test
    @DisplayName("Deve buscar proprietário por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        // ARRANGE
        when(proprietarioRepository.findById(1L)).thenReturn(Optional.of(proprietario));
        when(proprietarioMapper.toDTO(proprietario)).thenReturn(proprietarioResponseDTO);

        // ACT
        ProprietarioResponseDTO resultado = proprietarioService.buscarPorId(1L);

        // ASSERT
        assertThat(resultado).isNotNull();
        verify(proprietarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o proprietário não for encontrado pelo ID")
    void deveLancarExcecaoQuandoProprietarioNaoEncontradoPorId() {
        // ARRANGE
        when(proprietarioRepository.findById(99L))
                .thenThrow(new ResourceNotFoundException("Proprietario não encontrado de id 99"));

        // ACT + ASSERT
        assertThatThrownBy(() -> proprietarioService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Proprietario não encontrado de id 99");
    }
}