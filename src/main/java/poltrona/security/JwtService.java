package poltrona.security;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import poltrona.entity.Usuario;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String gerarToken(Usuario usuario) {

        String tipoUsuario = usuario.getClass().getSimpleName().toUpperCase();

        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("email", usuario.getEmail())
                .claim("nome", usuario.getNome())
                .claim("tipo", tipoUsuario)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getChave())
                .compact();

    }

    private SecretKey getChave() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private Claims extrairClaims(String token) {

        return Jwts.parser()
                .verifyWith(getChave())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public Long extrairId(String token) {
        return Long.parseLong(extrairClaims(token).getSubject());
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).get("email", String.class);
    }

    public String extrairTipoUsuario(String token) {
        return extrairClaims(token).get("tipo", String.class);
    }

}
