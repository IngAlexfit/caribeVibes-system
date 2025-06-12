package org.project.caribevibes.repository.user;

import org.project.caribevibes.entity.user.User;
import org.project.caribevibes.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de usuarios en el sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad User, incluyendo búsquedas por email, username y estado.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico
     * 
     * @param email Correo electrónico del usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario activo por su dirección de correo electrónico
     * 
     * @param email Correo electrónico del usuario
     * @return Optional con el usuario activo encontrado o vacío si no existe
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);

    /**
     * Busca un usuario por su nombre de usuario
     * 
     * @param username Nombre de usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por email o username
     * 
     * @param email Correo electrónico
     * @param username Nombre de usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<User> findByEmailOrUsername(String email, String username);

    /**
     * Verifica si existe un usuario con el email especificado
     * 
     * @param email Correo electrónico a verificar
     * @return true si existe un usuario con ese email
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con el username especificado
     * 
     * @param username Nombre de usuario a verificar
     * @return true si existe un usuario con ese username
     */
    boolean existsByUsername(String username);    /**
     * Obtiene todos los usuarios activos del sistema
     * 
     * @return Lista de usuarios activos
     */
    List<User> findByIsActiveTrue();

    /**
     * Obtiene usuarios por rol específico
     * 
     * @param role Rol a buscar
     * @return Lista de usuarios con el rol especificado
     */
    List<User> findByRole(UserRole role);

    /**
     * Obtiene usuarios creados después de una fecha específica
     * 
     * @param date Fecha de corte
     * @return Lista de usuarios creados después de la fecha
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Busca usuarios por nombre o apellido (búsqueda parcial)
     * 
     * @param searchTerm Término de búsqueda
     * @return Lista de usuarios que coinciden con el término
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findByNameContaining(@Param("searchTerm") String searchTerm);

    /**
     * Cuenta el número total de usuarios activos
     * 
     * @return Número de usuarios activos
     */
    long countByIsActiveTrue();

    /**
     * Obtiene usuarios con preferencias específicas
     * 
     * @param preferenceKey Clave de la preferencia
     * @param preferenceValue Valor de la preferencia
     * @return Lista de usuarios con la preferencia especificada
     */
    @Query("SELECT u FROM User u WHERE JSON_EXTRACT(u.preferences, :preferenceKey) = :preferenceValue")
    List<User> findByPreference(@Param("preferenceKey") String preferenceKey, 
                               @Param("preferenceValue") String preferenceValue);
}
