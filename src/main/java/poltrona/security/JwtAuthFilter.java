package poltrona.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import poltrona.entity.Usuario;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.ContaEncerradaException;
import poltrona.exception.CustomAuthenticationEntryPoint;
import poltrona.exception.ResourceNotFoundException;
import poltrona.repository.UsuarioRepository;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository,
            CustomAuthenticationEntryPoint authenticationEntryPoint) {

        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (jwtService.tokenValido(token)) {

            Long id = jwtService.extrairId(token);
            String tipo = jwtService.extrairTipoUsuario(token);

            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuário não encontrado de id " + id));

            if (usuario.getStatus() == StatusConta.ENCERRADA) {

                SecurityContextHolder.clearContext();

                authenticationEntryPoint.commence(
                        request,
                        response,
                        new ContaEncerradaException("Esta conta foi encerrada."));

                return;
            }

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    id,
                    null,
                    List.of(new SimpleGrantedAuthority(tipo)));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

}
