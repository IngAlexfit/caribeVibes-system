package org.project.caribevibes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para las respuestas de autenticación en el sistema Caribe Vibes.
 * 
 * Este DTO contiene la información del token JWT y los datos del usuario
 * autenticado que se envían como respuesta después de un login exitoso.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    /**
     * Token JWT generado para el usuario autenticado
     */
    private String token;

    /**
     * Tipo de token (generalmente "Bearer")
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Tiempo de expiración del token en milisegundos
     */
    private Long expiresIn;

    /**
     * Fecha y hora de expiración del token
     */
    private LocalDateTime expiresAt;

    /**
     * Información básica del usuario autenticado
     */
    private UserInfoDTO user;

    /**
     * DTO anidado para la información del usuario
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDTO {
        
        /**
         * ID único del usuario
         */
        private Long id;
        
        /**
         * Nombre de usuario
         */
        private String username;
        
        /**
         * Correo electrónico del usuario
         */
        private String email;
        
        /**
         * Nombre del usuario
         */
        private String firstName;
        
        /**
         * Apellido del usuario
         */
        private String lastName;
        
        /**
         * Nombre completo del usuario
         */
        private String fullName;
        
        /**
         * Rol del usuario en el sistema
         */
        private String role;
        
        /**
         * Indica si el usuario está activo
         */
        private Boolean isActive;
        
        /**
         * Preferencias del usuario
         */
        private Map<String, Object> preferences;
        
        /**
         * Fecha de creación de la cuenta
         */
        private LocalDateTime createdAt;
    }
}
