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
import poltrona.dto.cliente.AtualizaClienteRequestDTO;
import poltrona.dto.cliente.ClienteRequestDTO;
import poltrona.dto.cliente.ClienteResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.dto.usuario.UsuarioRequestDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Cliente;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.ClienteMapper;
import poltrona.repository.ClienteRepository;
import poltrona.repository.IngressoRepository;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private IngressoRepository ingressoRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteSemId;
    private Cliente clienteSalvo;
    private ClienteRequestDTO clienteRequestDTO;
    private ClienteResponseDTO clienteResponseDTO;
    private UsuarioRequestDTO usuarioRequestDTO;
    private UsuarioResponseDTO usuarioResponseDTO;
    private AtualizaClienteRequestDTO atualizaClienteDTO;
    private AtualizaSenhaRequestDTO atualizaSenhaRequestDTO;
    private ClienteRequestDTO requestComSenhasDiferentes;

    @BeforeEach
    void setUp() {
        String senhaCriptografada = "senha_criptografada";

        usuarioRequestDTO = new UsuarioRequestDTO(
                "Cliente Silva",
                "cliente@poltrona.com",
                "12345678900",
                "Senha@123",
                "Senha@123",
                LocalDate.of(1995, 8, 20));

        clienteRequestDTO = new ClienteRequestDTO(usuarioRequestDTO);

        clienteSemId = new Cliente(
                clienteRequestDTO.usuario().nome(),
                clienteRequestDTO.usuario().email(),
                senhaCriptografada,
                clienteRequestDTO.usuario().cpf(),
                clienteRequestDTO.usuario().dataNascimento());

        clienteSalvo = new Cliente(
                clienteRequestDTO.usuario().nome(),
                clienteRequestDTO.usuario().email(),
                senhaCriptografada,
                clienteRequestDTO.usuario().cpf(),
                clienteRequestDTO.usuario().dataNascimento());

        ReflectionTestUtils.setField(clienteSalvo, "id", 1L);

        usuarioResponseDTO = new UsuarioResponseDTO(
                1L,
                usuarioRequestDTO.nome(),
                usuarioRequestDTO.email(),
                usuarioRequestDTO.cpf(),
                usuarioRequestDTO.dataNascimento(),
                StatusConta.ATIVA,
                LocalDateTime.now());

        clienteResponseDTO = new ClienteResponseDTO(usuarioResponseDTO);

        atualizaClienteDTO = new AtualizaClienteRequestDTO(
                "Cliente Silva Atualizado",
                "cliente.atualizado@poltrona.com",
                LocalDate.of(1995, 8, 20));

        atualizaSenhaRequestDTO = new AtualizaSenhaRequestDTO(
                "Senha@123",
                "SenhaNova123!",
                "SenhaNova123!");

        UsuarioRequestDTO usuarioComSenhasDiferentes = new UsuarioRequestDTO(
                "Cliente Silva",
                "cliente@poltrona.com",
                "12345678900",
                "Senha@123",
                "senhaErrada",
                LocalDate.of(1995, 8, 20));

        requestComSenhasDiferentes = new ClienteRequestDTO(usuarioComSenhasDiferentes);
    }

    // =========================================================================
    // 1. TESTES DE CADASTRO DE CLIENTE (cadastrar)
    // =========================================================================

    @Test
    @DisplayName("Deve cadastrar um cliente com sucesso quando os dados forem válidos")
    void deveCadastrarClienteComSucesso() {
        // ARRANGE
        when(clienteRepository.existsByCpf(clienteRequestDTO.usuario().cpf())).thenReturn(false);
        when(clienteRepository.existsByEmail(clienteRequestDTO.usuario().email())).thenReturn(false);
        when(passwordEncoder.encode(clienteRequestDTO.usuario().confirmarSenha())).thenReturn("senha_criptografada");
        when(clienteMapper.toEntity(clienteRequestDTO, "senha_criptografada")).thenReturn(clienteSemId);
        when(clienteRepository.save(clienteSemId)).thenReturn(clienteSalvo);
        when(clienteMapper.toDTO(clienteSalvo)).thenReturn(clienteResponseDTO);

        // ACT
        ClienteResponseDTO resultado = clienteService.cadastrar(clienteRequestDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.usuario().id()).isEqualTo(1L);
        assertThat(resultado.usuario().status()).isEqualByComparingTo(StatusConta.ATIVA);
        verify(clienteRepository, times(1)).save(clienteSemId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar cliente com CPF existente")
    void deveLancarExcecaoQuandoCpfJaExistir() {
        // ARRANGE
        when(clienteRepository.existsByCpf(clienteRequestDTO.usuario().cpf())).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.cadastrar(clienteRequestDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Já existe uma conta com este CPF");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar cliente com e-mail existente")
    void deveLancarExcecaoQuandoEmailJaExistir() {
        // ARRANGE
        when(clienteRepository.existsByCpf(clienteRequestDTO.usuario().cpf())).thenReturn(false);
        when(clienteRepository.existsByEmail(clienteRequestDTO.usuario().email())).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.cadastrar(clienteRequestDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Já existe uma conta com este e-mail");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha e a confirmação de senha não coincidirem no cadastro")
    void deveLancarExcecaoQuandoSenhasNaoCoincidirem() {
        // ARRANGE
        when(clienteRepository.existsByCpf(requestComSenhasDiferentes.usuario().cpf())).thenReturn(false);
        when(clienteRepository.existsByEmail(requestComSenhasDiferentes.usuario().email())).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.cadastrar(requestComSenhasDiferentes))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("A senha e a confirmação de senha não coincidem");

        verify(clienteRepository, never()).save(any());
    }

    // =========================================================================
    // 2. TESTES DE CONSULTA E LISTAGEM (listarTodos, me, buscarPorId)
    // =========================================================================

    @Test
    @DisplayName("Deve listar todos os clientes com sucesso de forma paginada")
    void deveListarClientesComSucesso() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        List<Cliente> listaClientes = List.of(clienteSalvo);
        Page<Cliente> pageClienteInput = new PageImpl<>(listaClientes, pageable, listaClientes.size());

        when(clienteRepository.findAll(pageable)).thenReturn(pageClienteInput);
        when(clienteMapper.toDTO(clienteSalvo)).thenReturn(clienteResponseDTO);

        // ACT
        Page<ClienteResponseDTO> resultado = clienteService.listarTodos(pageable);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.getContent().get(0).usuario().id()).isEqualTo(1L);

        verify(clienteRepository, times(1)).findAll(pageable);
        verify(clienteMapper, times(1)).toDTO(clienteSalvo);
    }

    @Test
    @DisplayName("Deve retornar os dados do cliente autenticado logado")
    void deveListarDadosClienteLogado() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteSalvo);
        when(clienteMapper.toDTO(clienteSalvo)).thenReturn(clienteResponseDTO);

        // ACT
        ClienteResponseDTO resultado = clienteService.me();

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.usuario().nome()).isEqualTo(clienteSalvo.getNome());
    }

    @Test
    @DisplayName("Deve buscar um cliente pelo ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        // ARRANGE
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteMapper.toDTO(clienteSalvo)).thenReturn(clienteResponseDTO);

        // ACT
        ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.usuario().nome()).isEqualTo(clienteSalvo.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente não for encontrado pelo ID")
    void deveLancarExcecaoQuandoClienteNaoEncontradoPorId() {
        // ARRANGE
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.buscarPorId(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cliente não encontrado de id 1");
    }

    // =========================================================================
    // 3. TESTES DE ATUALIZAÇÃO DE PERFIL (atualizar)
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar os dados do cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteSalvo);
        when(clienteRepository.existsByEmailAndIdNot(atualizaClienteDTO.email(), clienteSalvo.getId()))
                .thenReturn(false);
        when(clienteRepository.save(clienteSalvo)).thenReturn(clienteSalvo);
        when(clienteMapper.toDTO(clienteSalvo)).thenReturn(clienteResponseDTO);

        // ACT
        ClienteResponseDTO resultado = clienteService.atualizar(atualizaClienteDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cliente sem conta ATIVA")
    void deveLancarExcecaoAoAtualizarClienteSemContaAtiva() {
        // ARRANGE
        clienteSalvo.bloquear();
        when(usuarioService.usuarioLogado()).thenReturn(clienteSalvo);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.atualizar(atualizaClienteDTO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Uma conta bloqueada ou encerrada não pode atualizar dados");

        verify(clienteRepository, never()).save(clienteSalvo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar para um e-mail já em uso por outro cliente")
    void deveLancarExcecaoAoAtualizarEmailParaUmJaEmUso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteSalvo);
        when(clienteRepository.existsByEmailAndIdNot(atualizaClienteDTO.email(), clienteSalvo.getId()))
                .thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.atualizar(atualizaClienteDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Já existe uma conta para este e-mail");

        verify(clienteRepository, never()).save(clienteSalvo);
    }

    // =========================================================================
    // 4. TESTES DE ALTERAÇÃO DE SENHA (atualizarSenha)
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar a senha do cliente com sucesso")
    void deveAtualizarSenhaClienteComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteSalvo);
        when(passwordEncoder.matches(atualizaSenhaRequestDTO.senhaAtual(), clienteSalvo.getSenha())).thenReturn(true);
        when(passwordEncoder.encode(atualizaSenhaRequestDTO.confirmarSenha())).thenReturn("senha_nova_criptografada");

        // ACT
        clienteService.atualizarSenha(atualizaSenhaRequestDTO);

        // ASSERT
        assertThat(clienteSalvo.getSenha()).isEqualTo("senha_nova_criptografada");
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao informar senha atual incorreta na alteração de senha")
    void deveLancarExcecaoAoErrarSenhaAtual() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteSalvo);
        when(passwordEncoder.matches(atualizaSenhaRequestDTO.senhaAtual(), clienteSalvo.getSenha())).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.atualizarSenha(atualizaSenhaRequestDTO))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Senha atual incorreta");

        verify(passwordEncoder, never()).encode(any());
        verify(clienteRepository, never()).save(clienteSalvo);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a nova senha for diferente da confirmação")
    void deveLancarExcecaoQuandoNovaSenhaENovaConfirmacaoDiferentes() {
        // ARRANGE
        AtualizaSenhaRequestDTO requestComSenhasDiferentes = new AtualizaSenhaRequestDTO(
                "Senha@123",
                "SenhaNova123!",
                "senhaIncompativel@123");
        when(usuarioService.usuarioLogado()).thenReturn(clienteSalvo);
        when(passwordEncoder.matches(requestComSenhasDiferentes.senhaAtual(), clienteSalvo.getSenha()))
                .thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.atualizarSenha(requestComSenhasDiferentes))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("A senha nova deve ser igual a confirmação de senha");

        verify(passwordEncoder, never()).encode(any());
        verify(clienteRepository, never()).save(clienteSalvo);
    }

    // =========================================================================
    // 5. TESTES DE ENCERRAMENTO DE CONTA (encerrar)
    // =========================================================================

    @Test
    @DisplayName("Deve encerrar a conta do cliente com sucesso quando não houver ingressos ativos em sessões futuras")
    void deveEncerrarContaClienteComSucesso() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteSalvo);
        when(ingressoRepository.existsByStatusAndUsuarioIdAndSessaoDataHoraFimAfter(
                eq(StatusIngresso.ATIVO), eq(clienteSalvo.getId()), any(LocalDateTime.class)))
                .thenReturn(false);

        // ACT
        clienteService.encerrar();

        // ASSERT
        assertThat(clienteSalvo.getStatus()).isEqualByComparingTo(StatusConta.ENCERRADA);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar encerrar conta com ingressos ativos em sessões futuras")
    void deveLancarExcecaoAoEncerrarContaComIngressosAtivosEmSessoesFuturas() {
        // ARRANGE
        when(usuarioService.usuarioLogado()).thenReturn(clienteSalvo);
        when(ingressoRepository.existsByStatusAndUsuarioIdAndSessaoDataHoraFimAfter(
                eq(StatusIngresso.ATIVO), eq(clienteSalvo.getId()), any(LocalDateTime.class)))
                .thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.encerrar())
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Não é possível inativar a conta com sessões futuras que possuem ingressos vendidos.");

        verify(clienteRepository, never()).save(clienteSalvo);
    }
}