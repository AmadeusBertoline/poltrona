package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poltrona.dto.proprietario.ProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioResponseDTO;
import poltrona.entity.Proprietario;
import poltrona.mapper.ProprietarioMapper;
import poltrona.repository.ProprietarioRepository;

@Service
public class ProprietarioService {

    private final ProprietarioRepository proprietarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProprietarioMapper proprietarioMapper;

    public ProprietarioService(ProprietarioRepository proprietarioRepository, PasswordEncoder passwordEncoder,
            ProprietarioMapper proprietarioMapper) {
        this.proprietarioRepository = proprietarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.proprietarioMapper = proprietarioMapper;
    }

    @Transactional
    public ProprietarioResponseDTO cadastrar(ProprietarioRequestDTO dto) {

        String senha = passwordEncoder.encode(dto.usuario().senha());

        Proprietario proprietario = proprietarioMapper.toEntity(dto, senha);

        Proprietario salvo = proprietarioRepository.save(proprietario);

        return proprietarioMapper.toDTO(salvo);

    }

    @Transactional(readOnly = true)
    public Page<ProprietarioResponseDTO> listarTodos(Pageable pageable) {

        return proprietarioRepository.findAll(pageable).map(proprietarioMapper::toDTO);

    }

}
