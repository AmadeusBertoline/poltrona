package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.cliente.AtualizaClienteRequestDTO;
import poltrona.dto.cliente.ClienteRequestDTO;
import poltrona.dto.cliente.ClienteResponseDTO;
import poltrona.entity.Cliente;
import poltrona.enums.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.mapper.ClienteMapper;
import poltrona.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper,
            PasswordEncoder passwordEncoder, UsuarioService usuarioService) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.passwordEncoder = passwordEncoder;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {

        if (clienteRepository.existsByCpf(dto.usuario().cpf())) {
            throw new ResourceAlreadyExistsException("Já existe uma conta com este CPF");
        }

        if (clienteRepository.existsByEmail(dto.usuario().email())) {
            throw new ResourceAlreadyExistsException("Já existe uma conta com este e-mail");
        }

        if (!dto.usuario().senha().equals(dto.usuario().confirmarSenha())) {
            throw new RegraNegocioException("A senha e a confirmação de senha não coincidem");
        }

        String senha = passwordEncoder.encode(dto.usuario().confirmarSenha());

        Cliente cliente = clienteMapper.toEntity(dto, senha);

        Cliente salvo = clienteRepository.save(cliente);

        return clienteMapper.toDTO(salvo);

    }

    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarTodos(Pageable pageable) {

        return clienteRepository.findAll(pageable).map(clienteMapper::toDTO);

    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO me() {

        Cliente cliente = (Cliente) usuarioService.usuarioLogado();

        return clienteMapper.toDTO(cliente);

    }

    @Transactional
    public ClienteResponseDTO atualizar(AtualizaClienteRequestDTO dto) {

        Cliente cliente = (Cliente) usuarioService.usuarioLogado();

        if (cliente.getStatus() != StatusConta.ATIVA) {
            throw new RegraNegocioException("Uma conta bloqueada ou encerrada não pode atualizar dados");
        }

        if (dto.email() != null && !dto.email().isBlank() && !dto.email().equalsIgnoreCase(cliente.getEmail())) {
            if (clienteRepository.existsByEmailAndIdNot(dto.email(), cliente.getId())) {
                throw new ResourceAlreadyExistsException("Já existe uma conta para este e-mail");
            }
        }

        cliente.atualizar(dto.nome(), dto.email(), dto.dataNascimento());

        return clienteMapper.toDTO(cliente);
    }

}
