package org.project.caribevibes.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Punto de entrada para manejar errores de autenticación JWT en el sistema Caribe Vibes.
 * 
 * Esta clase maneja las excepciones de autenticación y devuelve respuestas JSON
 * estructuradas cuando un usuario intenta acceder a recursos protegidos sin
 * autenticación válida.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Maneja las excepciones de autenticación devolviendo una respuesta JSON estructurada
     * 
     * @param request Petición HTTP que causó la excepción
     * @param response Respuesta HTTP donde se escribirá el error
     * @param authException Excepción de autenticación que se produjo
     * @throws IOException Si ocurre un error escribiendo la respuesta
     * @throws ServletException Si ocurre un error en el servlet
     */
    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {

        log.warn("Acceso no autorizado a: {} - IP: {} - User-Agent: {} - Error: {}", 
                request.getRequestURI(),
                getClientIpAddress(request),
                request.getHeader("User-Agent"),
                authException.getMessage());

        // Configurar la respuesta HTTP
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");

        // Crear el cuerpo de la respuesta de error
        Map<String, Object> errorResponse = createErrorResponse(request, authException);

        // Escribir la respuesta JSON
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Crea la estructura de respuesta de error estándar
     * 
     * @param request Petición HTTP
     * @param authException Excepción de autenticación
     * @return Mapa con la estructura de error
     */
    private Map<String, Object> createErrorResponse(HttpServletRequest request, 
                                                   AuthenticationException authException) {
        Map<String, Object> errorResponse = new HashMap<>();
        
        // Información básica del error
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "No autorizado");
        errorResponse.put("message", determineErrorMessage(authException));
        errorResponse.put("path", request.getRequestURI());
        
        // Información adicional para debugging (solo en desarrollo)
        if (isDevelopmentMode()) {
            errorResponse.put("details", authException.getMessage());
            errorResponse.put("type", authException.getClass().getSimpleName());
        }
        
        // Información de ayuda para el cliente
        errorResponse.put("hint", "Proporcione un token JWT válido en la cabecera Authorization");
        errorResponse.put("support", "Para obtener ayuda, contacte a soporte@caribevibes.com");
        
        return errorResponse;
    }

    /**
     * Determina el mensaje de error apropiado basado en el tipo de excepción
     * 
     * @param authException Excepción de autenticación
     * @return Mensaje de error user-friendly
     */
    private String determineErrorMessage(AuthenticationException authException) {
        String exceptionMessage = authException.getMessage().toLowerCase();
        
        if (exceptionMessage.contains("expired")) {
            return "Su sesión ha expirado. Por favor, inicie sesión nuevamente.";
        } else if (exceptionMessage.contains("malformed") || exceptionMessage.contains("invalid")) {
            return "Token de autenticación inválido. Por favor, inicie sesión nuevamente.";
        } else if (exceptionMessage.contains("signature")) {
            return "Token de autenticación no válido. Acceso denegado.";
        } else if (exceptionMessage.contains("unsupported")) {
            return "Formato de autenticación no soportado.";
        } else {
            return "Acceso denegado. Se requiere autenticación válida.";
        }
    }

    /**
     * Obtiene la dirección IP real del cliente considerando proxies
     * 
     * @param request Petición HTTP
     * @return Dirección IP del cliente
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // En caso de múltiples IPs, tomar la primera
                if (ip.contains(",")) {
                    ip = ip.split(",")[0];
                }
                return ip.trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Verifica si la aplicación está en modo desarrollo
     * 
     * @return true si está en modo desarrollo
     */
    private boolean isDevelopmentMode() {
        String profile = System.getProperty("spring.profiles.active", "");
        return profile.contains("dev") || profile.contains("local");
    }
}
