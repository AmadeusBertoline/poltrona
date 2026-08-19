package poltrona.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import poltrona.dto.cliente.ClienteRequestDTO;
import poltrona.dto.cliente.ClienteResponseDTO;
import poltrona.entity.Cliente;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.mapper.ClienteMapper;
import poltrona.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper,
            PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {

        if (clienteRepository.existsByEmail(dto.usuario().email())) {
            throw new ResourceAlreadyExistsException("Email já cadastrado");
        }

        if (clienteRepository.existsByCpf(dto.usuario().cpf())) {
            throw new ResourceAlreadyExistsException("CPF já cadastrado");
        }

        if (!dto.usuario().senha().equals(dto.usuario().confirmarSenha())) {
            throw new RegraNegocioException("As senhas devem ser iguais");
        }

        Cliente cliente = clienteMapper.toEntity(dto);
        cliente.setSenha(passwordEncoder.encode(dto.usuario().senha()));

        Cliente salvo = clienteRepository.save(cliente);

        return clienteMapper.toDTO(salvo);

    }

}
