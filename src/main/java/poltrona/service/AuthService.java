package poltrona.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poltrona.dto.login.LoginRequestDTO;
import poltrona.dto.login.LoginResponseDTO;
import poltrona.entity.Usuario;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.ResourceNotFoundException;
import poltrona.repository.UsuarioRepository;
import poltrona.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO logar(LoginRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByEmailOrCpfAndStatus(dto.emailOrCpf(), StatusConta.ATIVA)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário ou senha inválidos"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }

        String token = jwtService.gerarToken(usuario);

        String tipo = jwtService.extrairTipoUsuario(token);

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getEmail(),
                tipo);
    }

}
