package poltrona.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import poltrona.exception.CustomAccessDeniedHandler;
import poltrona.exception.CustomAuthenticationEntryPoint;
import poltrona.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAccessDeniedHandler customAccessDeniedHandler,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint)
            throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // AUTH
                        .requestMatchers("/auth/**").permitAll()

                        // CINEMAS
                        .requestMatchers(HttpMethod.POST, "/cinemas").hasAuthority("PROPRIETARIO")
                        .requestMatchers("/cinemas/me").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.GET, "/cinemas/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/cinemas/**").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.DELETE, "/cinemas/**").hasAuthority("PROPRIETARIO")

                        // FILMES
                        .requestMatchers(HttpMethod.GET, "/filmes").permitAll()
                        .requestMatchers("/filmes/**").hasAuthority("ADMIN")

                        // PROPRIETARIOS
                        .requestMatchers(HttpMethod.POST, "/proprietarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/proprietarios").hasAuthority("ADMIN")
                        .requestMatchers("/proprietarios/**").hasAuthority("PROPRIETARIO")

                        // POLTRONAS
                        .requestMatchers("/poltronas").hasAuthority("PROPRIETARIO")

                        // PRECOS
                        .requestMatchers(HttpMethod.GET, "/precos").hasAuthority("ADMIN")
                        .requestMatchers("/precos", "/precos/**").hasAuthority("PROPRIETARIO")

                        // SALAS
                        .requestMatchers(HttpMethod.POST, "/salas").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.PATCH, "/salas/**").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.DELETE, "/salas/**").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.GET, "/salas").permitAll()

                        // SESSAO
                        .requestMatchers(HttpMethod.POST, "/sessoes").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.GET, "/sessoes").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/sessoes/**").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.DELETE, "/sessoes/**").hasAuthority("PROPRIETARIO")

                        // ADMINS
                        .requestMatchers("/admins/**").hasAuthority("ADMIN")

                        // CLIENTES
                        .requestMatchers(HttpMethod.POST, "/clientes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/clientes").hasAuthority("ADMIN")
                        .requestMatchers("/clientes/**", "/clientes").hasAuthority("CLIENTE")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // PRECOS
                        .requestMatchers(HttpMethod.GET, "/precos/**").permitAll()

                        // PRODUTOS
                        .requestMatchers(HttpMethod.POST, "/produtos").hasAuthority("PROPRIETARIO")

                        // VENDAS
                        .requestMatchers(HttpMethod.GET, "/vendas").hasAuthority("ADMIN")
                        .requestMatchers("/vendas", "/vendas/**").hasAuthority("CLIENTE")

                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception

                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
