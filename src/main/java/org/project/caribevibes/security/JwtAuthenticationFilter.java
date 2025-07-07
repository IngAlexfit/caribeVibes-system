package org.project.caribevibes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT para el sistema Caribe Vibes.
 * 
 * Este filtro intercepta todas las peticiones HTTP y valida los tokens JWT
 * presentes en la cabecera Authorization, estableciendo el contexto de
 * seguridad para usuarios autenticados.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Procesa cada petición HTTP para validar tokens JWT
     * 
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param filterChain Cadena de filtros
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Obtener la cabecera Authorization
        final String authHeader = request.getHeader("Authorization");
        
        // Si no hay cabecera o no empieza con "Bearer ", continuar con la cadena
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No se encontró token JWT en la petición: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraer el token JWT
            final String jwt = jwtTokenUtil.extractTokenFromHeader(authHeader);
            
            if (jwt == null) {
                log.warn("Token JWT vacío en la petición: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // Extraer el username del token
            final String username = jwtTokenUtil.getUsernameFromToken(jwt);

            // Si hay username y no hay autenticación en el contexto actual
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Cargar los detalles del usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validar el token
                if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                    
                    // Crear el token de autenticación
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    
                    // Establecer detalles adicionales de la petición
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("Usuario autenticado exitosamente: {} para URI: {}", 
                             username, request.getRequestURI());
                    
                } else {
                    log.warn("Token JWT inválido para usuario: {} en URI: {}", 
                            username, request.getRequestURI());
                }
            }
        } catch (Exception e) {
            log.error("Error procesando token JWT en URI: {} - Error: {}", 
                     request.getRequestURI(), e.getMessage());
            
            // Limpiar el contexto de seguridad en caso de error
            SecurityContextHolder.clearContext();
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Determina si este filtro debe procesar la petición
     * 
     * @param request Petición HTTP
     * @return true si el filtro debe procesar la petición
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // No filtrar endpoints públicos específicos
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/public/") ||
               path.startsWith("/api/health") ||
               path.startsWith("/api/actuator/") ||
               path.startsWith("/api/docs/") ||
               path.startsWith("/api/swagger-ui/") ||
               path.startsWith("/api/v3/api-docs/") ||
               path.equals("/api/destinations") ||
               path.equals("/api/hotels") ||
               path.equals("/api/experiences") ||
               (path.equals("/api/contact/create") && "POST".equals(request.getMethod())) ||
               // No filtrar recursos estáticos del frontend Angular
               path.equals("/") ||
               path.equals("/index.html") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/assets/") ||
               path.startsWith("/static/") ||
               path.matches(".*\\.(js|css|ico|png|svg|woff|woff2|ttf|eot|html)$") ||
               // No filtrar rutas de Angular SPA (que no son APIs)
               (!path.startsWith("/api/") && !path.contains("."));
    }
}
