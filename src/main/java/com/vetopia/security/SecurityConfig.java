package com.vetopia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * CONFIGURACIÓN - Seguridad
 * Define una API REST stateless protegida con JWT: CSRF desactivado,
 * sesiones deshabilitadas, CORS centralizado para Angular y filtro JWT
 * antes del filtro estándar de usuario/contraseña. El login y la
 * documentación quedan públicos; el resto exige autenticación.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** Filtro que valida el token de cada petición. */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** Respuesta JSON para peticiones sin autenticar (401). */
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    /** Respuesta JSON para roles sin permiso (403). */
    private final RestAccessDeniedHandler accessDeniedHandler;

    /** Carga de cuentas y roles desde la tabla unificada. */
    private final CustomUserDetailsService userDetailsService;

    /** Codificador BCrypt compartido con la creación de cuentas. */
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler,
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sesion -> sesion
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(autorizacion -> autorizacion
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/login", "/error", "/h2", "/h2/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(excepciones -> excepciones
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                // La consola de H2 se renderiza dentro de un frame.
                .headers(cabeceras -> cabeceras
                        .frameOptions(opciones -> opciones.sameOrigin()))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** Autenticación con la cuenta unificada y BCrypt. */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider proveedor =
                new DaoAuthenticationProvider(userDetailsService);
        proveedor.setPasswordEncoder(passwordEncoder);
        return proveedor;
    }

    /** AuthenticationManager que usa el controller de login. */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuracion) throws Exception {
        return configuracion.getAuthenticationManager();
    }
}
