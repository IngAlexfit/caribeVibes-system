package org.project.caribevibes.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entidad que representa un usuario en el sistema Caribe Vibes.
 * 
 * Esta clase maneja la información de autenticación y perfil de los usuarios,
 * incluyendo sus preferencias de viaje y datos personales.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * Identificador único del usuario
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario único en el sistema
     */
    @Column(unique = true, nullable = false, length = 80)
    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 80, message = "El nombre de usuario debe tener entre 3 y 80 caracteres")
    private String username;

    /**
     * Correo electrónico del usuario (único)
     */
    @Column(unique = true, nullable = false, length = 120)
    @Email(message = "Debe proporcionar un email válido")
    @NotBlank(message = "El email es requerido")
    private String email;

    /**
     * Contraseña encriptada del usuario
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    @JsonIgnore
    private String password;

    /**
     * Nombre del usuario
     */
    @Column(name = "first_name", length = 80)
    private String firstName;

    /**
     * Apellido del usuario
     */
    @Column(name = "last_name", length = 80)
    private String lastName;

    /**
     * Fecha y hora de creación del usuario
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Indica si el usuario está activo en el sistema
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Preferencias del usuario almacenadas como JSON
     * Incluye preferencias de destinos, tipos de experiencias, etc.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferences", columnDefinition = "JSON")
    private Map<String, Object> preferences;

    /**
     * Roles del usuario en el sistema (relación muchos a muchos)
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // Implementación de UserDetails para Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Retornar email como username para Spring Security
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    /**
     * Obtiene el nombre completo del usuario
     * @return Nombre completo combinando nombre y apellido
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return firstName != null ? firstName : username;
    }

    /**
     * Obtiene el username real del usuario (no el email)
     * @return Username real del usuario
     */
    public String getRealUsername() {
        return username;
    }

    /**
     * Obtiene el nombre del rol del usuario
     */
    public String getRoleName() {
        return roles.stream().findFirst().map(Role::getName).orElse(null);
    }

    /**
     * Obtiene los nombres de los roles del usuario
     */
    public List<String> getRoleNames() {
        return roles.stream().map(Role::getName).toList();
    }
}
