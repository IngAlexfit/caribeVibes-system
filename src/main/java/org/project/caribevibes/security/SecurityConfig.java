package org.project.caribevibes.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;



/**
 * Configuración de seguridad para el sistema Caribe Vibes.
 * 
 * Esta clase configura la seguridad de la aplicación utilizando Spring Security
 * con autenticación JWT, CORS, y definición de endpoints públicos y protegidos.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configuración del filtro de seguridad principal
     * 
     * @param http Configurador de seguridad HTTP
     * @return Cadena de filtros de seguridad configurada
     * @throws Exception Si ocurre un error en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            // Deshabilitar CSRF ya que usamos JWT
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // Configurar manejo de excepciones de autenticación
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            
            // Configurar gestión de sesiones como stateless
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configurar autorización de endpoints
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos - no requieren autenticación
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/destinations/**").permitAll()
                .requestMatchers("/api/hotels/**").permitAll()
                .requestMatchers("/api/experiences/**").permitAll()
                .requestMatchers("/api/contact/create").permitAll()
                
                // Endpoints de documentación (Swagger/OpenAPI)
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                
                // Endpoints de health check
                .requestMatchers("/api/health", "/api/actuator/health").permitAll()
                
                // Recursos estáticos
                .requestMatchers("/api/static/**", "/api/assets/**", "/api/images/**").permitAll()
                
                // Endpoints protegidos para usuarios autenticados
                .requestMatchers("/api/bookings/**").hasRole("USER")
                .requestMatchers("/api/users/profile/**").hasRole("USER")
                
                // Endpoints de administración
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/management/**").hasAnyRole("ADMIN", "OPERATOR")
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            );

        // Agregar filtro JWT antes del filtro de autenticación por username/password
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración del codificador de contraseñas
     * 
     * @return Codificador BCrypt para contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Fuerza 12 para mayor seguridad
    }

    /**
     * Configuración del proveedor de autenticación
     * 
     * @return Proveedor de autenticación configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    /**
     * Configuración del gestor de autenticación
     * 
     * @param config Configuración de autenticación
     * @return Gestor de autenticación
     * @throws Exception Si ocurre un error en la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
            throws Exception {
        return config.getAuthenticationManager();
    }
}
