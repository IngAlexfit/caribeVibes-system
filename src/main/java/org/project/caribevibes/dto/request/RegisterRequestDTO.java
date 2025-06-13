package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO para las solicitudes de registro de usuario en el sistema Caribe Vibes.
 * 
 * Este DTO contiene toda la información necesaria para crear una nueva
 * cuenta de usuario, incluyendo validaciones de entrada.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    /**
     * Nombre de usuario único en el sistema
     */
    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 80, message = "El nombre de usuario debe tener entre 3 y 80 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", 
             message = "El nombre de usuario solo puede contener letras, números, puntos, guiones y guiones bajos")
    private String username;

    /**
     * Correo electrónico del usuario
     */
    @NotBlank(message = "El email es requerido")
    @Email(message = "Debe proporcionar un email válido")
    @Size(max = 120, message = "El email no puede exceder 120 caracteres")
    private String email;

    /**
     * Contraseña del usuario
     */
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "La contraseña debe contener al menos una letra minúscula, una mayúscula y un número")
    private String password;

    /**
     * Confirmación de la contraseña
     */
    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String confirmPassword;

    /**
     * Nombre del usuario (opcional)
     */
    @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
    private String firstName;

    /**
     * Apellido del usuario (opcional)
     */
    @Size(max = 80, message = "El apellido no puede exceder 80 caracteres")
    private String lastName;

    /**
     * Número de teléfono del usuario (opcional)
     */
    @Pattern(regexp = "^[\\+]?[0-9\\s\\-\\(\\)]{10,15}$", 
             message = "El número de teléfono debe tener un formato válido")
    private String phoneNumber;

    /**
     * Fecha de nacimiento del usuario (formato: YYYY-MM-DD)
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", 
             message = "La fecha de nacimiento debe tener el formato YYYY-MM-DD")
    private String birthDate;

    /**
     * País de residencia del usuario
     */
    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String country;

    /**
     * Ciudad de residencia del usuario
     */
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String city;

    /**
     * Preferencias iniciales del usuario (opcional)
     */
    private Map<String, Object> preferences;

    /**
     * Indica si el usuario acepta los términos y condiciones
     */
    @NotNull(message = "Debe aceptar los términos y condiciones")
    private Boolean acceptTerms;

    /**
     * Indica si el usuario acepta recibir comunicaciones de marketing
     */
    @Builder.Default
    private Boolean acceptMarketing = false;

    /**
     * Código de promoción o referido (opcional)
     */
    @Size(max = 50, message = "El código promocional no puede exceder 50 caracteres")
    private String promoCode;

    /**
     * Valida que las contraseñas coincidan
     * 
     * @return true si las contraseñas son iguales
     */
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Obtiene el nombre completo del usuario
     * 
     * @return Nombre completo combinando nombre y apellido
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName.trim() + " " + lastName.trim();
        } else if (firstName != null) {
            return firstName.trim();
        } else if (lastName != null) {
            return lastName.trim();
        }
        return null;
    }

    /**
     * Limpia y normaliza los datos del usuario
     */
    public void normalize() {
        if (username != null) {
            username = username.trim().toLowerCase();
        }
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        if (country != null) {
            country = country.trim();
        }
        if (city != null) {
            city = city.trim();
        }
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.trim();
        }
    }
}
