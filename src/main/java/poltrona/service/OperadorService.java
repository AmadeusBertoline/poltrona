package poltrona.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poltrona.dto.operador.OperadorRequestDTO;
import poltrona.dto.operador.OperadorResponseDTO;
import poltrona.entity.Operador;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.mapper.OperadorMapper;
import poltrona.repository.OperadorRepository;

@Service
public class OperadorService {

    private final OperadorRepository operadorRepository;
    private final OperadorMapper operadorMapper;
    private final PasswordEncoder passwordEncoder;

    public OperadorService(OperadorRepository operadorRepository, OperadorMapper operadorMapper,
            PasswordEncoder passwordEncoder) {
        this.operadorRepository = operadorRepository;
        this.operadorMapper = operadorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public OperadorResponseDTO cadastrar(OperadorRequestDTO dto) {

        if (operadorRepository.existsByEmail(dto.usuario().email())) {
            throw new ResourceAlreadyExistsException("Email já cadastrado");
        }

        if (operadorRepository.existsByCpf(dto.usuario().cpf())) {
            throw new ResourceAlreadyExistsException("CPF já cadastrado");
        }

        if (!dto.usuario().senha().equals(dto.usuario().confirmarSenha())) {
            throw new RegraNegocioException("As senhas devem ser iguais");
        }

        Operador operador = operadorMapper.toEntity(dto);
        operador.setSenha(passwordEncoder.encode(dto.usuario().senha()));
        Operador salvo = operadorRepository.save(operador);

        return operadorMapper.toDTO(salvo);

    }

}
