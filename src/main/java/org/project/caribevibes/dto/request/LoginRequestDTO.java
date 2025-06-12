package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para las solicitudes de inicio de sesión en el sistema Caribe Vibes.
 * 
 * Este DTO contiene las credenciales necesarias para autenticar un usuario
 * en el sistema, con validaciones básicas de entrada.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /**
     * Correo electrónico del usuario
     * Se utiliza como identificador principal para el login
     */
    @NotBlank(message = "El email es requerido")
    @Email(message = "Debe proporcionar un email válido")
    private String email;

    /**
     * Contraseña del usuario
     */
    @NotBlank(message = "La contraseña es requerida")
    private String password;

    /**
     * Indica si el usuario desea mantener la sesión activa por más tiempo
     * (opcional, por defecto es false)
     */
    @Builder.Default
    private Boolean rememberMe = false;

    /**
     * Token de captcha para verificación de seguridad (opcional)
     */
    private String captchaToken;

    /**
     * Dirección IP del cliente (se puede completar automáticamente)
     */
    private String clientIp;

    /**
     * User Agent del navegador (se puede completar automáticamente)
     */
    private String userAgent;

    /**
     * Normaliza los datos de entrada
     */
    public void normalize() {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
    }
}
