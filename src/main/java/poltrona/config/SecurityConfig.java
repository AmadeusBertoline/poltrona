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

                        //.requestMatchers("/filmes/**").hasAuthority("ADMIN")
                        .requestMatchers("/filmes/**").permitAll()

                        // PROPRIETARIOS
                        .requestMatchers(HttpMethod.POST, "/proprietarios").permitAll()
                        .requestMatchers("/proprietarios/**").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.GET, "/proprietarios").hasAuthority("ADMIN")

                        // CINEMAS
                        .requestMatchers(HttpMethod.POST, "/cinemas/**").hasAnyAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.GET, "/cinemas/**").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.PATCH, "/cinemas/**").hasAuthority("PROPRIETARIO")
                        .requestMatchers(HttpMethod.GET, "/cinemas/me").hasAuthority("PROPRIETARIO")

                        // ADMINS
                        .requestMatchers("/admins/**").hasAuthority("ADMIN")

                        // CLIENTES
                        .requestMatchers(HttpMethod.POST, "/clientes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/clientes").permitAll()
                        .requestMatchers("/clientes/**").hasAuthority("CLIENTE")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // PRECOS
                        .requestMatchers(HttpMethod.GET,"/precos/**").permitAll()


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
