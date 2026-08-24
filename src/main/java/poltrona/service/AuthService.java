package poltrona.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poltrona.dto.login.LoginRequestDTO;
import poltrona.dto.login.LoginResponseDTO;
import poltrona.entity.Usuario;
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

        System.out.println("1 - iniciando login");

        Usuario usuario = usuarioRepository.findByEmailOrCpf(dto.emailOrCpf())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário ou senha inválidos"));

        System.out.println("2 - usuario encontrado: " + usuario.getId());

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            System.out.println("3 - senha inválida");
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }


        String token = jwtService.gerarToken(usuario);

        System.out.println("5 - token gerado");

        String tipo = jwtService.extrairTipoUsuario(token);

        System.out.println("6 - tipo extraído: " + tipo);

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getEmail(),
                tipo);
    }

}
