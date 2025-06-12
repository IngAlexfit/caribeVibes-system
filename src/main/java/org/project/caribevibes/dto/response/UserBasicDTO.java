package org.project.caribevibes.dto.response;

/**
 * DTO de respuesta básico para información de usuarios.
 * 
 * Este DTO contiene información esencial de un usuario
 * para ser usado en respuestas anidadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2024
 */
public class UserBasicDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    /**
     * Constructor por defecto.
     */
    public UserBasicDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único del usuario
     * @param firstName Nombre del usuario
     * @param lastName Apellido del usuario
     * @param email Email del usuario
     * @param phone Teléfono del usuario
     */
    public UserBasicDTO(Long id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    /**
     * Obtiene el nombre completo del usuario.
     * 
     * @return Nombre completo
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "UserBasicDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
