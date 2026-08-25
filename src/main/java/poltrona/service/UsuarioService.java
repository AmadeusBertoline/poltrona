package poltrona.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import poltrona.entity.Usuario;
import poltrona.exception.ResourceNotFoundException;
import poltrona.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario usuarioLogado() {

        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado de id " + id));

    }


}
