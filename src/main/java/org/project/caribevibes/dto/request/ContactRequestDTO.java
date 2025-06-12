package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.*;

/**
 * DTO de request para crear un mensaje de contacto.
 * 
 * Este DTO contiene todos los datos necesarios para
 * que un usuario envíe un mensaje de contacto.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2024
 */
public class ContactRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]+$", message = "El teléfono contiene caracteres inválidos")
    private String phone;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(min = 5, max = 200, message = "El asunto debe tener entre 5 y 200 caracteres")
    private String subject;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 10, max = 2000, message = "El mensaje debe tener entre 10 y 2000 caracteres")
    private String message;

    /**
     * Constructor por defecto.
     */
    public ContactRequestDTO() {}

    /**
     * Constructor completo.
     * 
     * @param name Nombre del remitente
     * @param email Email del remitente
     * @param phone Teléfono del remitente
     * @param subject Asunto del mensaje
     * @param message Contenido del mensaje
     */
    public ContactRequestDTO(String name, String email, String phone, String subject, String message) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.subject = subject;
        this.message = message;
    }

    // Getters y Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ContactRequestDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
