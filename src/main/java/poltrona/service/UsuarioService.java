package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Usuario;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.UsuarioMapper;
import poltrona.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public Usuario usuarioLogado() {

        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado de id " + id));

    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarTodos(Pageable pageable) {

        return usuarioRepository.findAll(pageable).map(usuarioMapper::toDTO);

    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO me() {

        Usuario ususario = usuarioLogado();

        return usuarioMapper.toDTO(ususario);

    }

    public boolean validarCredenciaisDisponiveis(String email, String cpf) {

        if (usuarioRepository.existsByEmailAndStatus(email, StatusConta.ATIVA)) {
            throw new RegraNegocioException("E-mail já está em uso no sistema.");
        }

        if (usuarioRepository.existsByCpfAndStatus(cpf, StatusConta.ATIVA)) {
            throw new RegraNegocioException("CPF já está em uso no sistema.");
        }

        return false;

    }

}
