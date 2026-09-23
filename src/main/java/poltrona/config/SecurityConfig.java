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

                        // GERENTES
                        .requestMatchers(HttpMethod.POST, "/gerentes").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.GET, "/gerentes").hasAuthority("PROPRIETARIO")
                        .requestMatchers("/gerentes/me").hasAuthority("GERENTE")
                        .requestMatchers(HttpMethod.PATCH, "/gerentes", "/gerentes/*").hasAuthority("GERENTE")
                        .requestMatchers(HttpMethod.DELETE, "/gerentes").hasAuthority("GERENTE")
                        .requestMatchers(HttpMethod.GET, "/gerentes/*").hasAuthority("PROPRIETARIO")

                        // AUTH
                        .requestMatchers("/auth/**").permitAll()

                        // CINEMAS
                        .requestMatchers(HttpMethod.POST, "/cinemas").hasAuthority("PROPRIETARIO")
                        .requestMatchers("/cinemas/me").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.GET, "/cinemas/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/cinemas/**").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.DELETE, "/cinemas/**").hasAuthority("PROPRIETARIO")

                        // FILMES
                        .requestMatchers(HttpMethod.GET, "/filmes", "/filmes/**").permitAll()
                        .requestMatchers("/filmes/**").hasAuthority("ADMIN")

                        // PROPRIETARIOS
                        .requestMatchers(HttpMethod.POST, "/proprietarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/proprietarios").hasAuthority("ADMIN")
                        .requestMatchers("/proprietarios/**").hasAuthority("PROPRIETARIO")

                        // POLTRONAS
                        .requestMatchers(HttpMethod.GET, "/poltronas/**", "/poltronas").permitAll()
                        .requestMatchers("/poltronas").hasAnyAuthority("PROPRIETARIO", "GERENTE")

                        // PRECOS (REGRAS UNIFICADAS E CORRIGIDAS)
                        .requestMatchers(HttpMethod.POST, "/precos").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.GET, "/precos").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/precos/cinema/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/precos/*").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/precos/*").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.DELETE, "/precos/*").hasAuthority("PROPRIETARIO")

                        // SALAS
                        .requestMatchers(HttpMethod.POST, "/salas").hasAnyAuthority("PROPRIETARIO", "GERENTE")
                        .requestMatchers(HttpMethod.PATCH, "/salas/**").hasAnyAuthority("PROPRIETARIO", "GERENTE")
                        .requestMatchers(HttpMethod.DELETE, "/salas/**").hasAnyAuthority("PROPRIETARIO", "GERENTE")
                        .requestMatchers(HttpMethod.GET, "/salas/**", "/salas").permitAll()

                        // SESSAO
                        .requestMatchers(HttpMethod.POST, "/sessoes").hasAnyAuthority("PROPRIETARIO", "GERENTE")
                        .requestMatchers(HttpMethod.GET, "/sessoes", "/sessoes/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/sessoes/**").hasAnyAuthority("PROPRIETARIO", "GERENTE")
                        .requestMatchers(HttpMethod.DELETE, "/sessoes/**").hasAnyAuthority("PROPRIETARIO", "GERENTE")

                        // ADMINS
                        .requestMatchers("/admins/**").hasAuthority("ADMIN")

                        // CLIENTES
                        .requestMatchers(HttpMethod.POST, "/clientes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/clientes/**", "/clientes").hasAuthority("ADMIN")
                        .requestMatchers("/clientes/**", "/clientes").hasAuthority("CLIENTE")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // PRODUTOS
                        .requestMatchers("/produtos").hasAnyAuthority("PROPRIETARIO", "GERENTE")
                        .requestMatchers(HttpMethod.GET, "/produtos/*", "/produtos").permitAll()

                        // VENDAS
                        .requestMatchers(HttpMethod.GET, "/vendas").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/vendas/*").hasAnyAuthority("ADMIN", "CLIENTE")
                        .requestMatchers("/vendas/***", "/vendas", "/vendas/**").hasAuthority("CLIENTE")

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